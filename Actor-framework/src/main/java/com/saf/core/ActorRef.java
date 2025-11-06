package com.saf.core;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface ActorRef {

    void send(Object message, ActorRef sender);

    void sendMessage(Message message);

    String getName();

    CompletableFuture<Object> ask(Object message, Duration timeout);
}