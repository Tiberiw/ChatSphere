package org.map.socialnetwork.domain;

import java.util.Objects;
import java.util.StringJoiner;

public class UserCredentials extends Entity<User>{

    private String email;
    private String phoneNumber;
    private String password;

    public UserCredentials(User user, String email, String phoneNumber, String password) {
        this.id = user;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCredentials that = (UserCredentials) o;
        return Objects.equals(this.id, that.id) && Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, email, phoneNumber, password);
    }

    @Override
    public String toString() {
        return new StringJoiner("|")
                .add("user=" + this.id)
                .add(email)
                .add(phoneNumber)
                .add(password)
                .toString();
    }
}
