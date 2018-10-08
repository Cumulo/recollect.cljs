
Recollect
----

Cached rendering and diff/patch library designed for Cumulo project.

### API [![Clojars Project](https://img.shields.io/clojars/v/cumulo/recollect.svg)](https://clojars.org/cumulo/recollect)

```clojure
[cumulo/recollect "0.4.4"]
```

```clojure
(recollect.macros/deftwig twig-name [param] body)
(recollect.twig/render-twig twig-data old-data-twig)
(recollect.twig/conceal data-twig)
(recollect.diff/diff-twig a b {:key :id})
(recollect.diff/patch-twig a changes)
```

Terms:

* Twig: data wrapped with a renderer to caching purpose
* `:key`: to help diff maps

### Purpose

Rendering data tree and doing diffing would be slow.
It's a simlar to the problem of React DOM diffing.

This library is using the algorithm developed in Respo DOM diffing.
It's like data rendering, with keeps reusing last result of data tree.

It's not tested yet, but is trying to trade memory and performance with caching.

### Diff Operations

number | name | meaning
--- | --- | ---
0 | tree-op-assoc | `assoc-in`
1 | tree-op-dissoc | `dissoc-in`
2 | tree-op-vec-append | append items to vector
3 | tree-op-vec-drop | pop items from vector
4 | tree-op-set-splice | remove and add to set
5 | tree-op-seq-splice | remove and add to sequence

For vectors, data is supposed to be manipulated from the tail.
Items in the new vector are mapped to its old ones by index.

For sequences, unchanged values since the tail is kept.
Changed elements in the front will be replaced directly.

For function arguments in `deftwig`, changes are ignored.

### Related

For record parsing http://stackoverflow.com/a/29133350/883571

### Develop

Workflow https://github.com/mvc-works/calcit-workflow

To run tests:

```bash
yarn test
```

### License

MIT
