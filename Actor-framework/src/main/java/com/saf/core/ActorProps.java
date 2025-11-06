package com.saf.core;

import java.util.function.Supplier;

public class ActorProps {
    private final Supplier<Actor> actorSupplier;
    private SupervisionStrategy supervisionStrategy;
    private int mailboxCapacity;

    private ActorProps(Supplier<Actor> actorSupplier) {
        this.actorSupplier = actorSupplier;
        this.supervisionStrategy = SupervisionStrategy.alwaysRestart();
        this.mailboxCapacity = 1000;
    }

    public static ActorProps create(Supplier<Actor> actorSupplier) {
        return new ActorProps(actorSupplier);
    }

    public ActorProps withSupervisionStrategy(SupervisionStrategy strategy) {
        this.supervisionStrategy = strategy;
        return this;
    }

    public ActorProps withMailboxCapacity(int capacity) {
        this.mailboxCapacity = capacity;
        return this;
    }

    public Supplier<Actor> getActorSupplier() {
        return actorSupplier;
    }

    public SupervisionStrategy getSupervisionStrategy() {
        return supervisionStrategy;
    }

    public int getMailboxCapacity() {
        return mailboxCapacity;
    }
}
