package ru.nsu.worksdonttouch.cardholder.kotlinclient.data.listener;

import java.lang.reflect.Method;

public class ListenerEventRunner {

    private final Method method;

    private final EventListener eventListener;

    public ListenerEventRunner(Method method, EventListener eventListener) {
        this.method = method;
        this.eventListener = eventListener;
    }

    public void run(Event event) throws Exception {
        method.invoke(eventListener, event);
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    @Override
    public String toString() {
        return "ListenerEventRunner{" +
                "method=" + method.getName() +
                ", eventListener=" + eventListener.getClass() +
                '}';
    }
}
