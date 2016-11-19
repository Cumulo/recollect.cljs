
Recollect
----

Syncing library for Cumulo with caches in Respo DOM diffing algorithms.

### Usage

```clojure
(recollect.bunch/create-twig twig-name renderer)
(recollect.bunch/render-bunch twig-data old-data-bunch)
(recollect.bunch/conceal data-bunch)
(recollect.diff/diff-bunch a b)
(recollect.diff/patch-bunch a changes)
```

Terms:

* Twig: data wrapped with a renderer to caching purpose
* Bunch: the whole date tree rendered with Twigs

### Diff Operations

```clojure
[:m/!   coord x]      # reset data
[:m/-   coord k]      # remove key from map
[:v/+!  coord xs]     # append to vector
[:v/-!  coord k]      # remove after index k
[:st/++ coord xs]     # add to set
[:st/-- coord xs]     # remove from set
[:sq/-+ coord [k xs]] # drop k items and add sequence
```

For vectors, data is supposed to be manipulated from the tail.
Items in the new vector are mapped to its old ones by index.

For sequences, it's always considered changed totally.

### Related

For record parsing http://stackoverflow.com/a/29133350/883571

### Develop

Workflow https://github.com/mvc-works/stack-workflow

### License

MIT
