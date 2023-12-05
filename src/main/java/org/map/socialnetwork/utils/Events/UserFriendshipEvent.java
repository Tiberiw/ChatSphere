package org.map.socialnetwork.utils.Events;

import org.map.socialnetwork.domain.Friendship;
import org.map.socialnetwork.domain.User;

public class UserFriendshipEvent implements Event{

    private EventType eventType;
    private User oldUser;
    private User newUser;

    public UserFriendshipEvent() {

    }
    public UserFriendshipEvent(EventType eventType, User oldUser, User newUser) {
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
