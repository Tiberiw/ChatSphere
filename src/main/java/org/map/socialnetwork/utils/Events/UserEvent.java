package org.map.socialnetwork.utils.Events;

import org.map.socialnetwork.domain.User;

public class UserEvent implements Event {

    private EventType eventType;
    private User oldUser;
    private User newUser;

    public UserEvent(EventType eventType, User oldUser, User newUser) {
        this.eventType = eventType;
        this.oldUser = oldUser;
        this.newUser = newUser;
    }

    public EventType getEventType() {
        return eventType;
    }

    public User getOldUser() {
        return oldUser;
    }

    public User getNewUser() {
        return newUser;
    }
}
