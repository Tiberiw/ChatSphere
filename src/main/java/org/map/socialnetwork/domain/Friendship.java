package org.map.socialnetwork.domain;

import org.map.socialnetwork.utils.Utils;

import java.time.LocalDateTime;
import java.util.Objects;

public class Friendship extends Entity<Pair<Long,Long>>{

    private User firstUser;
    private User secondUser;
    private LocalDateTime date;

    public Friendship(User firstUser, User secondUser, LocalDateTime date) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.date = date;
        this.id = createId(firstUser.getID(),secondUser.getID());
    }

    public Friendship(Pair<Long,Long> id, User firstUser, User secondUser, LocalDateTime date) {
        this.id = id;
        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.date = date;
    }

    private static Pair<Long,Long> createId(Long userId1, Long userId2) {
        return userId1 < userId2 ? new Pair<>(userId1,userId2) : new Pair<>(userId2,userId1);
    }

    public User getFirstUser() {

        return firstUser;
    }

    public User getSecondUser() {

        return secondUser;
    }

    public LocalDateTime getDate() {

        return date;
    }

    public void setfirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public void setsecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return date.format(Utils.formatter);
    }

    @Override
    public String toString() {
        return String.format("firstUser: " + firstUser.toString() +
                "secondUser: " + secondUser.toString() +
                "Date: " + date.format(Utils.formatter));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(firstUser, that.firstUser) && Objects.equals(secondUser, that.secondUser) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstUser, secondUser, date);
    }
}
