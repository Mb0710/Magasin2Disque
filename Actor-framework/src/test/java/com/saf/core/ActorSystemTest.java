package com.saf.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ActorSystemTest {

    private ActorSystem system;

    @BeforeEach
    void setUp() {
        system = new ActorSystem();
    }

    @AfterEach
    void tearDown() {
        if (system != null) {
            system.shutdown();
        }
    }

    @Test
    void testCreateAndSendMessage() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);

        ActorRef actor = system.createActor("test-actor", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                counter.incrementAndGet();
            }
        });

        actor.send("Hello", null);

        Thread.sleep(100);

        assertEquals(1, counter.get());
    }

    @Test
    void testAskPattern() throws Exception {
        ActorRef echo = system.createActor("echo", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                if (message.expectsResponse()) {
                    message.reply("Echo: " + message.getPayload());
                }
            }
        });

        CompletableFuture<Object> future = echo.ask("Hello", Duration.ofSeconds(1));
        Object response = future.get(2, TimeUnit.SECONDS);

        assertEquals("Echo: Hello", response);
    }

    @Test
    void testParentChildHierarchy() throws Exception {
        AtomicInteger parentMessages = new AtomicInteger(0);
        AtomicInteger childMessages = new AtomicInteger(0);

        ActorRef parent = system.createActor("parent", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                parentMessages.incrementAndGet();

                if (message.getPayload().equals("create-child")) {
                    context.createActor("child", () -> new Actor() {
                        @Override
                        public void onReceive(Message message, ActorContext context) {
                            childMessages.incrementAndGet();
                        }
                    });
                }
            }
        });

        parent.send("create-child", null);
        Thread.sleep(100);

        ActorRef child = system.getActor("child");
        assertNotNull(child);
        child.send("test", null);
        Thread.sleep(100);

        assertEquals(1, childMessages.get());
    }

    @Test
    void testScheduler() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);

        ActorRef actor = system.createActor("scheduled-actor", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                counter.incrementAndGet();
            }
        });

        system.getScheduler().scheduleOnce(Duration.ofMillis(200), actor, "delayed", null);

        Thread.sleep(100);
        assertEquals(0, counter.get());

        Thread.sleep(150);
        assertEquals(1, counter.get());
    }

    @Test
    void testEventBus() throws Exception {
        AtomicInteger subscriber1Count = new AtomicInteger(0);
        AtomicInteger subscriber2Count = new AtomicInteger(0);

        ActorRef sub1 = system.createActor("sub1", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                subscriber1Count.incrementAndGet();
            }
        });

        ActorRef sub2 = system.createActor("sub2", () -> new Actor() {
            @Override
            public void onReceive(Message message, ActorContext context) {
                subscriber2Count.incrementAndGet();
            }
        });

        EventBus eventBus = system.getEventBus();
        eventBus.subscribe(String.class, sub1, event -> sub1.send(event, null));
        eventBus.subscribe(String.class, sub2, event -> sub2.send(event, null));

        eventBus.publish("Test Event");
        Thread.sleep(100);

        assertEquals(1, subscriber1Count.get());
        assertEquals(1, subscriber2Count.get());
    }

    @Test
    void testActorLifecycle() throws Exception {
        AtomicInteger preStartCalls = new AtomicInteger(0);
        AtomicInteger postStopCalls = new AtomicInteger(0);

        ActorRef actor = system.createActor("lifecycle-actor", () -> new Actor() {
            @Override
            public void preStart() {
                preStartCalls.incrementAndGet();
            }

            @Override
            public void onReceive(Message message, ActorContext context) {
            }

            @Override
            public void postStop() {
                postStopCalls.incrementAndGet();
            }
        });

        Thread.sleep(100);
        assertEquals(1, preStartCalls.get());
        assertEquals(0, postStopCalls.get());

        system.stopActor(actor);
        Thread.sleep(100);
        assertEquals(1, postStopCalls.get());
    }
}
