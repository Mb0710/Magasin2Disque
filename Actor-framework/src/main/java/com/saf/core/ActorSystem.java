package com.saf.core;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class ActorSystem implements ActorContext {

    private final ConcurrentMap<String, BlockingQueue<Message>> mailboxes = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors
            .newScheduledThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
    private final ConcurrentMap<String, Supplier<Actor>> actorSuppliers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Actor> liveActors = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ActorRef> actorRefs = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Future<?>> actorTasks = new ConcurrentHashMap<>();

    private final Scheduler scheduler;
    private final EventBus eventBus;
    private final ConcurrentMap<String, String> parentChildMap = new ConcurrentHashMap<>();

    private final ThreadLocal<String> currentActorName = new ThreadLocal<>();

    public ActorSystem() {
        this.scheduler = new Scheduler(executor);
        this.eventBus = new EventBus();
    }

    private class LocalActorRef implements ActorRef {
        private final String name;
        private final BlockingQueue<Message> mailbox;

        LocalActorRef(String name) {
            this.name = name;
            this.mailbox = mailboxes.get(name);
        }

        @Override
        public void send(Object payload, ActorRef sender) {
            sendMessage(new Message(payload, sender));
        }

        @Override
        public void sendMessage(Message message) {
            if (mailbox != null) {
                mailbox.offer(message);
            } else {
                System.err.println(" Acteur " + name + " introuvable (mailbox null)");
                if (message.expectsResponse()) {
                    message.replyWithError(new ActorNotFoundException("Acteur " + name + " non trouvé"));
                }
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public CompletableFuture<Object> ask(Object payload, Duration timeout) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            Message msg = new Message(payload, this, future);
            sendMessage(msg);

            if (timeout != null && !timeout.isZero() && !timeout.isNegative()) {
                executor.schedule(() -> {
                    if (!future.isDone()) {
                        future.completeExceptionally(new TimeoutException("Ask timeout after " + timeout));
                    }
                }, timeout.toMillis(), TimeUnit.MILLISECONDS);
            }

            return future;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LocalActorRef) {
                return name.equals(((LocalActorRef) obj).name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return "ActorRef(" + name + ")";
        }
    }

    @Override
    public ActorRef createActor(String name, Supplier<Actor> actorSupplier) {
        if (actorSuppliers.containsKey(name)) {
            System.err.println("  Un acteur '" + name + "' existe déjà !");
            return actorRefs.get(name);
        }

        mailboxes.put(name, new LinkedBlockingQueue<>());
        LocalActorRef ref = new LocalActorRef(name);
        actorRefs.put(name, ref);
        actorSuppliers.put(name, actorSupplier);

        String currentParent = currentActorName.get();
        if (currentParent != null) {
            parentChildMap.put(name, currentParent);
        }

        Future<?> task = executor.submit(() -> runActor(name, actorSupplier));
        actorTasks.put(name, task);

        System.out.println(" Acteur créé : " + name +
                (currentParent != null ? " (parent: " + currentParent + ")" : ""));
        return ref;
    }

    private void runActor(String name, Supplier<Actor> actorSupplier) {
        BlockingQueue<Message> mailbox = mailboxes.get(name);
        Actor actor = null;

        try {
            actor = actorSupplier.get();
            liveActors.put(name, actor);

            currentActorName.set(name);

            actor.preStart();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = mailbox.take();

                    actor.onReceive(message, this);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;

                } catch (Exception e) {
                    System.err.println(" Erreur dans " + name + ": " + e.getMessage());
                    e.printStackTrace();

                    actor.preRestart(e, null);
                    actor = actorSupplier.get();
                    liveActors.put(name, actor);
                    actor.postRestart(e);
                }
            }
        } catch (Exception e) {
            System.err.println(" Erreur fatale dans " + name + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (actor != null) {
                actor.postStop();
            }
            liveActors.remove(name);
            currentActorName.remove();
        }
    }

    @Override
    public ActorRef getActor(String name) {
        return actorRefs.get(name);
    }

    @Override
    public void stopActor(ActorRef ref) {
        if (ref == null)
            return;

        String name = ref.getName();
        System.out.println(" Arrêt de l'acteur : " + name);

        Future<?> task = actorTasks.remove(name);
        if (task != null) {
            task.cancel(true);
        }

        mailboxes.remove(name);
        actorSuppliers.remove(name);
        actorRefs.remove(name);
    }

    @Override
    public ActorRef getSelf() {
        String name = currentActorName.get();
        return name != null ? actorRefs.get(name) : null;
    }

    @Override
    public ActorRef getParent() {
        String name = currentActorName.get();
        if (name == null)
            return null;

        String parentName = parentChildMap.get(name);
        return parentName != null ? actorRefs.get(parentName) : null;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ActorRef createActor(String name, ActorProps props) {
        return createActor(name, props.getActorSupplier());
    }

    public void shutdown() {
        System.out.println(" Arrêt du système d'acteurs...");

        for (ActorRef ref : actorRefs.values()) {
            stopActor(ref);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println(" Système d'acteurs arrêté");
    }

    public static class ActorNotFoundException extends RuntimeException {
        public ActorNotFoundException(String message) {
            super(message);
        }
    }
}