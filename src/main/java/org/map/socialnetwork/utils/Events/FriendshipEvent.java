package org.map.socialnetwork.utils.Events;

import org.map.socialnetwork.domain.Friendship;

public class FriendshipEvent implements Event{
    private EventType eventType;
    private Friendship oldValue;

    private Friendship newValue;

    public FriendshipEvent(EventType eventType, Friendship oldValue, Friendship newValue) {
        this.eventType = eventType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Friendship getOldValue() {
        return oldValue;
    }

    public Friendship getNewValue() {
        return newValue;
    }
}
