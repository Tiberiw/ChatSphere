package org.map.socialnetwork.utils.Observer;

import org.map.socialnetwork.utils.Events.Event;
import org.map.socialnetwork.utils.Events.EventInterface;

public interface Observable{

    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
