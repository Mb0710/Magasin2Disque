package com.saf.core;

public interface SupervisionStrategy {

    enum Directive {
        RESUME,
        RESTART,
        STOP,
        ESCALATE
    }

    Directive decide(Throwable error);

    static SupervisionStrategy alwaysRestart() {
        return error -> Directive.RESTART;
    }

    static SupervisionStrategy alwaysStop() {
        return error -> Directive.STOP;
    }

    static SupervisionStrategy alwaysResume() {
        return error -> Directive.RESUME;
    }

    static SupervisionStrategy custom() {
        return error -> {
            if (error instanceof IllegalArgumentException) {
                return Directive.RESUME;
            } else if (error instanceof IllegalStateException) {
                return Directive.RESTART;
            } else {
                return Directive.STOP;
            }
        };
    }
}
