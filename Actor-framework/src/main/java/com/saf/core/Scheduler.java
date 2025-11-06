package com.saf.core;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private final ScheduledExecutorService executor;

    public Scheduler(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public ScheduledFuture<?> scheduleOnce(Duration delay, ActorRef target, Object message, ActorRef sender) {
        return executor.schedule(
                () -> target.send(message, sender),
                delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(
            Duration initialDelay,
            Duration interval,
            ActorRef target,
            Object message,
            ActorRef sender) {
        return executor.scheduleAtFixedRate(
                () -> target.send(message, sender),
                initialDelay.toMillis(),
                interval.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(
            Duration initialDelay,
            Duration delay,
            ActorRef target,
            Object message,
            ActorRef sender) {
        return executor.scheduleWithFixedDelay(
                () -> target.send(message, sender),
                initialDelay.toMillis(),
                delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }
}
