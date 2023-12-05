package org.map.socialnetwork.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class FriendRequest extends Entity<Long>{

    User firstUser;
    User secondUser;
    String status;

    private final StringProperty firstUserFirstName = new SimpleStringProperty();
    private final StringProperty firstUserLastName = new SimpleStringProperty();


    public StringProperty firstUserFirstNameProperty() {
        return firstUserFirstName;
    }

    public final String getFirstUserFirstName() {
        return firstUserFirstNameProperty().get();
    }

    public final void setFirstUserFirstName(String firstUserFirstName) {
        firstUserFirstNameProperty().set(firstUserFirstName);
    }

    public StringProperty firstUserLastNameProperty() {
        return firstUserLastName;
    }
    private String getFirstUserLastName() {
        return firstUserLastNameProperty().get();
    }

    public final void setFirstUserLastName(String firstUserLastName) {
        firstUserLastNameProperty().set(firstUserLastName);
    }

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

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
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
