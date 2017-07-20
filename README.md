
Recollect
----

Cached rendering and diff/patch library designed for Cumulo project.

### Usage

[![Clojars Project](https://img.shields.io/clojars/v/cumulo/recollect.svg)](https://clojars.org/cumulo/recollect)

```clojure
[cumulo/recollect "0.1.8"]
```

```clojure
(recollect.bunch/create-twig twig-name renderer)
(recollect.bunch/render-bunch twig-data old-data-bunch)
(recollect.bunch/conceal data-bunch)
(recollect.diff/diff-bunch a b {:key :id})
(recollect.diff/patch-bunch a changes)
```

Terms:

* Twig: data wrapped with a renderer to caching purpose
* Bunch: the whole date tree rendered with Twigs
* `:key`: to help diff maps

### Purpose

Rendering data tree and doing diffing would be slow.
It's a simlar to the problem of React DOM diffing.

This library is using the algorithm developed in Respo DOM diffing.
It's like data rendering, with keeps reusing last result of data tree.

It's not tested yet, but is trying to trade memory and performance with caching.

### Diff Operations

```clojure
[:m/!   coord x]      ; reset data
[:m/-   coord k]      ; remove key from map
[:v/+!  coord xs]     ; append to vector
[:v/-!  coord k]      ; remove after index k
[:st/++ coord xs]     ; add to set
[:st/-- coord xs]     ; remove from set
[:sq/-+ coord [k xs]] ; drop k items and add sequence
```

For vectors, data is supposed to be manipulated from the tail.
Items in the new vector are mapped to its old ones by index.

For sequences, unchanged values since the tail is kept.
Changed elements in the front will be replaced directly.

### Related

For record parsing http://stackoverflow.com/a/29133350/883571

### Develop

Workflow https://github.com/mvc-works/coworkflow

To run tests:

```bash
yarn test
```

### License

MIT
