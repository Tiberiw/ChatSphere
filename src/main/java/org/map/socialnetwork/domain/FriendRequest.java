package org.map.socialnetwork.domain;

import java.util.Objects;
import java.util.StringJoiner;

public class FriendRequest extends Entity<Long>{

    private final User firstUser;
    private final User secondUser;
    private String status;

    public FriendRequest(User firstUser, User secondUser, String status) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.status = status;
        this.id = null;
    }

    public FriendRequest(Long id, User firstUser, User secondUser, String status) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.status = status;
        this.id = id;
    }

    public User getFirstUser() {
        return firstUser;
    }
    public User getSecondUser() {
        return secondUser;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(firstUser, that.firstUser) && Objects.equals(secondUser, that.secondUser) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser, status);
    }

    @Override
    public String toString() {
        return new StringJoiner("| ")
                .add("From: " + firstUser)
                .add("To: " + secondUser)
                .add("Status: " + status)
                .toString();
    }
}
