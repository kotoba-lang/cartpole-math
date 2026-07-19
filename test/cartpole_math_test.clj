(ns cartpole-math-test
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [kami.cartpole-math :as cm]
            [kotoba.compiler.core :as compiler]
            [kotoba.compiler.ir :as ir]))

(deftest canonical-step-stable
  (let [out (cm/canonical-step)]
    (is (= 4 (count out)))
    (is (every? number? out))
    (is (string? (cm/output-hash out)))))

(deftest kotoba-reference-js-and-wasm-match-the-cljc-golden
  (let [source (slurp "src/kotoba/cartpole_step.kotoba")
        names ['next-x 'next-x-dot 'next-theta 'next-theta-dot]
        expected (cm/canonical-step)
        js-artifact (compiler/compile-source source :js-kotoba-v1)
        wasm-artifact (compiler/compile-source source :wasm32-browser-kotoba-v1)
        reference (mapv #(ir/execute (:kir js-artifact) % []) names)
        js64 (.encodeToString (java.util.Base64/getEncoder)
                              (.getBytes ^String (:source js-artifact) "UTF-8"))
        wasm64 (.encodeToString (java.util.Base64/getEncoder) (:bytes wasm-artifact))
        node-source
        (str "const expected=[" (str/join "," (map #(Double/toString (double %)) expected)) "];"
             "const close=(a,b)=>Math.abs(a-b)<=1e-14;"
             "Promise.all([import('data:text/javascript;base64," js64 "'),"
             "WebAssembly.instantiate(Buffer.from('" wasm64 "','base64'),{})]).then(([j,w])=>{"
             "const a=j.instantiateKotoba({}),b=w.instance.exports;"
             "const js=[a['next-x'](),a['next-x-dot'](),a['next-theta'](),a['next-theta-dot']()];"
             "const wa=[b['next-x'](),b['next-x-dot'](),b['next-theta'](),b['next-theta-dot']()];"
             "if(!js.every((v,i)=>close(v,expected[i])&&Object.is(v,wa[i])))process.exit(2);"
             "}).catch(e=>{console.error(e);process.exit(99)})")
        node-result (shell/sh "node" "--input-type=module" "-e" node-source)]
    (is (every? true? (map #(< (Math/abs (- %1 %2)) 1.0e-14) reference expected)))
    (is (zero? (:exit node-result)) (:err node-result))
    (is (= :kotoba.floating-point/ieee-754-f32-f64-v7
           (:floating-point-policy js-artifact)))
    (is (= #{} (set (:effects (:kir js-artifact)))))))
