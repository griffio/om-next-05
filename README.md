# om-next-05

Simple example based around [om-next Components,-Identity-&-Normalization](https://github.com/omcljs/om/wiki/Components,-Identity-&-Normalization)

This only demonstrates that data keys can be loaded and merged and is not a correct usage of remote hosts.

The merge-tree function is overridden to merge the two data keys into one state.

Two list remotes :list1 :list2

 [public/list1.json](https://github.com/griffio/om-next-05/blob/master/resources/public/list1.json)

 [public/list2.json](https://github.com/griffio/om-next-05/blob/master/resources/public/list2.json)

List data is merged together - normalized by om.next to present view

[1.0.0-alpha24](https://clojars.org/org.omcljs/om)

## Overview

![om-next-05.gif](https://raw.githubusercontent.com/griffio/griffio.github.io/master/public/om-next-05.gif)

## Setup

Intellij - Cursive - REPL

![Figwheel Idea Cursive](https://raw.githubusercontent.com/griffio/griffio.github.io/master/public/figwheel-idea.png)

Open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2015 

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
