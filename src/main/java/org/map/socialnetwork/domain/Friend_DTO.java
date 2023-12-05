package org.map.socialnetwork.domain;

import org.map.socialnetwork.utils.Utils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class Friend_DTO {

    private User user;
    private LocalDateTime date;

    public Friend_DTO(User user, LocalDateTime date) {
        this.user = user;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend_DTO friendDto = (Friend_DTO) o;
        return Objects.equals(user, friendDto.user) && Objects.equals(date, friendDto.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, date);
    }

    @Override
    public String toString() {
        return new StringJoiner(" | ")
                .add(user.getFirstName())
                .add(user.getLastName())
                .add(date.format(Utils.formatter))
                .toString();
    }
}
