# user.clj footgun

This is a fairly simple demonstration of how you can shoot
yourself in the foot by accidentally including `user.clj`
in your build.

## The situation

I've created a websocket-based chat! The app is a ring-based
web-server that only accepts websocket-connections. Any message
received will be sent to other connected users.

I've also decided that **it sure would be handy if I could connect
to the application and send messages to all users via the REPL!**

That's why I've added the following code to the `user`-namespace:

```clojure
(ns user
  (:require [ring.websocket :as ws]
            [chat :as chat]))

(comment
  ;; Send to all consumers!
  (doseq [[id socket] @chat/listeners]
    (try
      (ws/send socket "Hello!")
      (catch Exception e (println e)))))

```

While testing locally I've found that this works as expected.
Time to build a jar and get this thing deployed!

```sh
> clj -X:build
Exception in thread "main" Syntax error macroexpanding at (user.clj:1:1).
	at clojure.lang.Compiler.load(Compiler.java:8177)
	at clojure.lang.RT.loadResourceScript(RT.java:401)
	at clojure.lang.RT.loadResourceScript(RT.java:388)
	at clojure.lang.RT.maybeLoadResourceScript(RT.java:384)
	at clojure.lang.RT.doInit(RT.java:506)
	at clojure.lang.RT.init(RT.java:487)
	at clojure.main.main(main.java:38)
Caused by: java.io.FileNotFoundException: Could not locate ring/websocket__init.class, ring/websocket.clj or ring/websocket.cljc on classpath.
```

Oh no! For some reason the websocket-classes are missing.
What's happened here? Well, the `user` namespace is special,
and [will load too early for its required namespaces to be
reloaded during compilation](https://ask.clojure.org/index.php/13770/warn-about-user-clj-during-build-compilation).

So be careful when including a `user`-namespace in your app!

## Solutions?

### Dev alias

One solution is to place the `user`-namespace in another folder that
is never loaded during compilation:

```clojure
{...;deps.edn-stuff
 :aliases {:dev {:extra-paths ["dev"]}}}
```

### Rename your "user"-namespace

Another solution is to simply put all your convenience-functions
in another namespace that doesn't carry the historical baggage of
the `user` namespace.
