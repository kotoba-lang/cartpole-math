(ns kotoba.cartpole-math
  "Facade re-exporting `kami.cartpole-math` (SSoT, ADR-2607102200 addendum 9)."
  (:require [kami.cartpole-math :as impl]))

(def clamp           impl/clamp)
(def step            impl/step)
(def canonical-input impl/canonical-input)
(def canonical-step  impl/canonical-step)
(def output-bytes    impl/output-bytes)
(def output-hash     impl/output-hash)
