package org.map.socialnetwork.domain;

import java.util.Objects;
import java.util.StringJoiner;

public class UserComponent_DTO extends User{
    private String firstName;
    private String lastName;
    private Long componentNumber;


    public UserComponent_DTO(String firstName, String lastName, Long componentNumber) {
        super(firstName, lastName);
        this.componentNumber = componentNumber;
    }

    public UserComponent_DTO(Long id, String firstName, String lastName, Long componentNumber) {
        super(id, firstName, lastName);
        this.componentNumber = componentNumber;
    }

//    @Override
//    public String getFirstName() {
//        return firstName;
//    }
//
//    @Override
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    @Override
//    public String getLastName() {
//        return lastName;
//    }
//
//    @Override
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }

    public Long getComponentNumber() {
        return componentNumber;
    }

    public void setComponentNumber(Long componentNumber) {
        this.componentNumber = componentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserComponent_DTO that = (UserComponent_DTO) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(componentNumber, that.componentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName, componentNumber);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserComponent_DTO.class.getSimpleName() + "[", "]")
                .add("firstName='" + firstName + "'")
                .add("lastName='" + lastName + "'")
                .add("componentNumber=" + componentNumber)
                .toString();
    }
}
