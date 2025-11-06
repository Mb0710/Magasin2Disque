package com.saf.core;

public class DeadLetter {
    private final Message message;
    private final ActorRef recipient;
    private final String reason;

    public DeadLetter(Message message, ActorRef recipient, String reason) {
        this.message = message;
        this.recipient = recipient;
        this.reason = reason;
    }

    public Message getMessage() {
        return message;
    }

    public ActorRef getRecipient() {
        return recipient;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "DeadLetter{" +
                "message=" + message +
                ", recipient=" + (recipient != null ? recipient.getName() : "null") +
                ", reason='" + reason + '\'' +
                '}';
    }
}
