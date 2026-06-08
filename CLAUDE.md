# ts-learning

A project for simultaneously learning Clojure and time series analysis.

## Project purpose

Dual learning track:
- **Clojure** — language fundamentals, functional patterns, idioms
- **Time series analysis** — statistical concepts, signal processing, implemented in Clojure

## Project structure

```
core.clj                    # Root-level REPL scratch file (evaluated interactively)
src/ts_learning/core.clj    # Leiningen entry point (standard -main)
test/ts_learning/core_test.clj
project.clj                 # Dependencies and build config
```

The root `core.clj` is the primary learning file — it is evaluated form-by-form in the Calva REPL, not run as a program. New concepts go here first, then can be organized into `src/` once stable.

## Toolchain

- **Leiningen** — dependency management and build (`lein repl`, `lein run`)
- **Emacs** with **clojure-mode** and **CIDER** — primary editor and REPL integration
- **REPL-driven workflow** — evaluate forms interactively; results appear in the `*cider-repl*` buffer

### Common CIDER keybindings

| Action | Key |
|---|---|
| Jack-in (start REPL) | `C-c M-j` |
| Evaluate expression at point | `C-c C-c` |
| Evaluate buffer | `C-c C-k` |
| Load/switch to REPL | `C-c C-z` |
| Lookup docs | `C-c C-d C-d` |
| Run tests | `C-c C-t C-t` |

## Key dependencies

| Library | Purpose |
|---|---|
| `uncomplicate/neanderthal` | High-performance vectors and matrices (BLAS/LAPACK-backed) |
| `org.bytedeco/mkl-platform-redist` | Intel MKL native backend for Neanderthal |
| `org.apache.commons/commons-math3` | Statistical distributions, random number generation |
| `aerial.hanami` | Vega-Lite based data visualization |

### Neanderthal notes
- Uses native memory — vectors/matrices must be created with `dv`, `dge`, etc. from `uncomplicate.neanderthal.native`
- Requires MKL or OpenBLAS on the classpath; `mkl-platform-redist` handles this
- Not interchangeable with plain Clojure vectors — wrap/unwrap at boundaries

### Hanami notes
- Produces Vega-Lite specs as Clojure maps
- Render via a browser viewer or notebook integration

## Clojure conventions in this project

- Prefer `defn` over anonymous functions for anything reusable
- Use `mapv` (eager) over `map` (lazy) when the full result is always needed
- `reduce` with a partial is idiomatic for applying an operation across a collection of collections
- Clojure data is immutable — operations return new values, they do not mutate

## Time series concepts being explored

- Vectors and sequences as time series
- Lag operators
- Statistical distributions (Normal via Commons Math)
- (Planned) autocorrelation, rolling statistics, spectral analysis
