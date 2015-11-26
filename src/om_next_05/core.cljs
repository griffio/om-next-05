(ns ^:figwheel-always om-next-05.core
  (:import [goog.net XhrIo])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.pprint]
    [goog.dom :as gdom]
    [cognitect.transit :as tt]
    [om.next :as om :refer-macros [defui]]
    [om.dom :as dom]
    [clojure.test.check :as ck]
    [clojure.test.check.generators :as ckgs]
    [clojure.test.check.properties :as ckps]
    [cljs.core.async :refer [chan put!]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn people-send []
  "makes a query to the remote and the result takes a callback to receive json response."
  (fn [remotes callback]                                    ;; remote expression keyed by remote target e.g {:list1 [{:name :points :age}]}
    (doseq [[remote] remotes]                               ;; for each remote
      (let [url (str "/" (name remote) ".json")]
        (.send XhrIo url
               (fn [_]
                 (this-as this
                   (let [data (tt/read (tt/reader :json) (.getResponseText this))]
                     (callback data)))))))))

;(def init-data
;  {:list/one [{:name "Yoda" :points 0 :age 800}
;              {:name "Mary" :points 0}
;              {:name "Bob" :points 0}]
;   :list/two [{:name "Mary" :points 0 :age 27}
;              {:name "Gwen" :points 0}
;              {:name "Jeff" :points 0}
;              {:name "Yoda" :points 0}]})

(defmulti read-people om/dispatch)

(defn get-people [state key]
  (let [st @state]
    (into [] (map #(get-in st %)) (get st key))))

(defmethod read-people :list/one
  [{:keys [state]} key]
  (println state)
  {:value (get-people state key)
   :list1 true})

(defmethod read-people :list/two
  [{:keys [state]} key]
  {:value (get-people state key)
   :list2 true})

(defmulti mutate-points om/dispatch)

(defmethod mutate-points 'points/increment
  [{:keys [state]} _ {:keys [name]}]
  {:action
   (fn []
     (swap! state update-in
            [:person/by-name name :points]
            inc))})

(defmethod mutate-points 'points/decrement
  [{:keys [state]} _ {:keys [name]}]
  {:action
   (fn []
     (swap! state update-in
            [:person/by-name name :points]
            #(let [n (dec %)] (if (neg? n) 0 n))))})

(defui Person
       static om/Ident
       (ident [this {:keys [name]}]
              [:person/by-name name])
       static om/IQuery
       (query [this]
              '[:name :points :age])
       Object
       (render [this]
               (println "Render Person" (-> this om/props :name))
               (let [{:keys [points name] :as props} (om/props this)]
                 (dom/li nil
                         (dom/label nil (str name ", points: " points))
                         (dom/button
                           #js {:onClick
                                (fn [_] (om/transact! this `[(points/increment ~props)]))}
                           "+")
                         (dom/button
                           #js {:onClick
                                (fn [_] (om/transact! this `[(points/decrement ~props)]))}
                           "-")))))

(def person (om/factory Person {:keyfn :name}))

(defui ListView
       Object
       (render [this]
               (println "Render ListView" (-> this om/path first))
               (let [list (om/props this)]
                 (apply dom/ul nil
                        (map person list)))))

(def list-view (om/factory ListView))

(defui RootView
       static om/IQuery
       (query [this]
              (let [subquery (om/get-query Person)]
                `[{:list/one ~subquery} {:list/two ~subquery}]))
       Object
       (render [this]
               (println "Render RootView")
               (let [{:keys [list/one list/two]} (om/props this)]
                 (apply dom/div nil
                        [(dom/h3 nil "List A")
                         (list-view one)
                         (dom/h3 nil "List B")
                         (list-view two)]))))

(defn merge-list-people [one two]
  (merge-with into one two)) ;; squash together

(def reconciler
  (om/reconciler
    {:state      {}                                         ;; state will normalized
     :remotes    [:list1 :list2]                            ;; the two remote lists
     :merge-tree merge-list-people                          ;; manual merge
     :send       (people-send)                              ;; get the remote data
     :parser     (om/parser {:read read-people :mutate mutate-points})}))

(om/add-root! reconciler
              RootView (gdom/getElement "people"))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )