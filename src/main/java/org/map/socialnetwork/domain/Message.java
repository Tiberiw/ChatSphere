package org.map.socialnetwork.domain;

import org.map.socialnetwork.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private Message replyTo;

    public Message(Long id, User from, List<User> to, String message, LocalDateTime date, Message reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyTo = reply;
    }

    public Message(User from, List<User> to, String message, LocalDateTime date, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyTo = reply;
        this.id = null;
    }

    public Message(User from, List<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyTo = null;
        this.id = null;
    }

    public User getSender() {
        return from;
    }

    public void setSender(User from) {
        this.from = from;
    }

    public List<User> getReceivers() {
        return to;
    }

    public void setReceivers(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return date.format(Utils.formatter);
    }

    public Message getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Message reply) {
        this.replyTo = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date) && Objects.equals(replyTo, message1.replyTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from, to, message, date, replyTo);
    }

    @Override
    public String toString() {
        return new StringJoiner(" | ")
                .add("" + id)
                .add("" + from.getID())
                .add("" + to)
                .add(message)
                .add(date.format(Utils.formatter))
                .add("" + replyTo)
                .toString();
    }
}
