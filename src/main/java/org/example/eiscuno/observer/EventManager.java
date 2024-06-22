package org.example.eiscuno.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
    private EventManager instance;
    HashMap<String, List<EventListener>> eventListeners = new HashMap<>();

    public void subscribe(String key, EventListener eventListener) {
        if (!eventListeners.containsKey(key)) {
            eventListeners.put(key, new ArrayList<>(){{add(eventListener);}});
            return;
        }
        eventListeners.get(key).add(eventListener);
    }

    public void unsubscribe(String key, EventListener eventListener) {
        if (eventListeners.containsKey(key)) {
            eventListeners.get(key).remove(eventListener);
        }
    }

    public void notify(String key, boolean message) {
        if(eventListeners.containsKey(key)){
            for(EventListener listener : eventListeners.get(key)){
                listener.update(key, message);
            }
        }
    }
}
