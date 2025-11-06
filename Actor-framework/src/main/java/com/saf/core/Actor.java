package com.saf.core;

public interface Actor {

    void onReceive(Message message, ActorContext context);

    default void preStart() {
    }

    default void postStop() {
    }

    default void preRestart(Throwable reason, Message optionalMessage) {
    }

    default void postRestart(Throwable reason) {
    }
}