package com.saf.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {

    private final ConcurrentHashMap<Class<?>, List<Subscription>> subscriptions = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, ActorRef subscriber, Consumer<T> handler) {
        subscriptions.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(new Subscription(subscriber, handler));
    }

    public void unsubscribe(Class<?> eventType, ActorRef subscriber) {
        List<Subscription> subs = subscriptions.get(eventType);
        if (subs != null) {
            subs.removeIf(sub -> sub.subscriber.equals(subscriber));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        Class<?> eventClass = event.getClass();
        List<Subscription> subs = subscriptions.get(eventClass);

        if (subs != null) {
            for (Subscription sub : subs) {
                try {
                    ((Consumer<T>) sub.handler).accept(event);
                } catch (Exception e) {
                    System.err.println(
                            " Erreur lors de la publication à " + sub.subscriber.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    private static class Subscription {
        final ActorRef subscriber;
        final Consumer<?> handler;

        Subscription(ActorRef subscriber, Consumer<?> handler) {
            this.subscriber = subscriber;
            this.handler = handler;
        }
    }
}
