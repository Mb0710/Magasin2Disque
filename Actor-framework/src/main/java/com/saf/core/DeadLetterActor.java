package com.saf.core;

public class DeadLetterActor implements Actor {

    @Override
    public void onReceive(Message message, ActorContext context) {
        Object payload = message.getPayload();

        if (payload instanceof DeadLetter) {
            DeadLetter deadLetter = (DeadLetter) payload;
            System.err.println(" Dead Letter: " + deadLetter);
        } else {
            System.err.println(" Message non délivré: " + message);
        }
    }

    @Override
    public void preStart() {
        System.out.println(" DeadLetterActor démarré");
    }
}
