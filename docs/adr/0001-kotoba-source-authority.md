# ADR 0001: Kotoba owns the cartpole source

- Status: accepted
- Date: 2026-07-20

## Decision

`src/kotoba/cartpole_math.kotoba` is the sole production source for cartpole
physics. The former `kami.cartpole-math` CLJC oracle and its Kotoba facade are
removed. `kami` remains the engine/domain layer; this repository supplies a
language-owned scalar physics kernel that an engine may call.

The source compiles with `kotoba-lang/compiler` to restricted JavaScript and
typed Wasm. JVM Clojure is permitted only as a build/test host. ClojureScript is
not a production compiler or runtime dependency.

## Safety and verification

- Configuration and state cross the public ABI as bounded `:vector-f64`
  values; action and each component result are explicitly `:f64`.
- Force is deterministically clamped before integration.
- Bounded Kotoba trigonometric operations enforce the language floating-point
  policy instead of importing host `Math.sin` or `Math.cos`.
- Reference, JavaScript, and Wasm outputs must agree semantically for canonical
  and non-canonical inputs, including an input that exercises force clamping.
- Short vectors and forged Wasm `externref` values must be rejected rather than
  being interpreted as partially valid physics state.
- Wasm must validate and instantiate. Binary byte identity is not required;
  only the fleet's representative structured fixture remains a byte sentinel.
- CI rejects any production `.cljc` source under `src`.

## Consequences

The generic CLJC map/vector facade and JVM-only output hashing are no longer
part of production. Hosts assemble the four scalar outputs into their preferred
state representation. A future structured f64 ABI may add a convenience export
without weakening the scalar contract.
