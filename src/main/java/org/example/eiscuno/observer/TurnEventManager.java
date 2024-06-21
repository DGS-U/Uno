package org.example.eiscuno.observer;

import java.util.ArrayList;
import java.util.List;

public class TurnEventManager {
    private TurnEventManager instance;
    List<EventListener> eventListeners = new ArrayList<>();

    public void subscribe(EventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void unsubscribe(EventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    public void notify(boolean message) {
        for (EventListener e : eventListeners) {
            e.update(message);
        }
    }
}
