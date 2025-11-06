package com.saf.core;

import java.util.function.Supplier;

public interface ActorContext {

    ActorRef createActor(String name, Supplier<Actor> actorSupplier);

    ActorRef createActor(String name, ActorProps props);

    ActorRef getActor(String name);

    void stopActor(ActorRef ref);

    ActorRef getSelf();

    ActorRef getParent();

    Scheduler getScheduler();

    EventBus getEventBus();

    default String getSelfName() {
        ActorRef self = getSelf();
        return self != null ? self.getName() : null;
    }
}