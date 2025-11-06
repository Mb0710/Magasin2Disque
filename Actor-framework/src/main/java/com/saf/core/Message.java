package com.saf.core;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Message {
    private final String id;
    private final Object payload;
    private final ActorRef sender;
    private final CompletableFuture<Object> responseFuture;
    private final boolean expectsResponse;

    public Message(Object payload, ActorRef sender) {
        this.id = UUID.randomUUID().toString();
        this.payload = payload;
        this.sender = sender;
        this.responseFuture = null;
        this.expectsResponse = false;
    }

    public Message(Object payload, ActorRef sender, CompletableFuture<Object> responseFuture) {
        this.id = UUID.randomUUID().toString();
        this.payload = payload;
        this.sender = sender;
        this.responseFuture = responseFuture;
        this.expectsResponse = true;
    }

    public String getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }

    public ActorRef getSender() {
        return sender;
    }

    public CompletableFuture<Object> getResponseFuture() {
        return responseFuture;
    }

    public boolean expectsResponse() {
        return expectsResponse;
    }

    public void reply(Object response) {
        if (expectsResponse && responseFuture != null) {
            responseFuture.complete(response);
        }
    }

    public void replyWithError(Throwable error) {
        if (expectsResponse && responseFuture != null) {
            responseFuture.completeExceptionally(error);
        }
    }

    @Override
    public String toString() {
        return "Message{id='" + id + "', payload=" + payload + ", sender=" +
                (sender != null ? sender.getName() : "null") + ", expectsResponse=" + expectsResponse + "}";
    }
}