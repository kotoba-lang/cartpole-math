# kotoba-lang/cartpole-math

**SSoT for `kami.cartpole-math`** — the CLJC compute oracle plus a native
Kotoba policy-v7 cartpole golden. `src/kotoba/cartpole_step.kotoba` expresses
the canonical semi-implicit step with explicit f64 operations and qualified
bounded sine/cosine; it compiles independently to restricted JavaScript and
typed Wasm without JVM runtime semantics or host transcendental imports.

The parameterized engine-facing CLJC function remains the oracle until a
bounded input-validation and structured f64 state ABI is qualified. The
`.kotoba` golden deliberately owns the fixed canonical physics vector only;
it is not yet a blanket engine cutover.

## Test

```sh
clojure -M:test
```
