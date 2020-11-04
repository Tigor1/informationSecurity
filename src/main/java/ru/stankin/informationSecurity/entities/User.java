package ru.stankin.informationSecurity.entities;

import javafx.scene.control.CheckBox;
import lombok.*;

import java.util.Objects;

@Getter @Setter
public class User {
    private String name;
    private String password = "qwerty";
    private Status status = Status.FIRST;
    private boolean restricted = false;
    private CheckBox blockCheck = new CheckBox();
    private CheckBox restrictedCheck = new CheckBox();

    public User(String name, String password, User.Status status, boolean restricted) {
        this.name = name;
        this.password = password;
        this.status = status;
        blockCheck.setSelected(status.equals(Status.BLOCK) || status.equals(Status.FIRST_AND_BLOCK));
        this.restricted = restricted;
        restrictedCheck.setSelected(restricted);

        blockCheck.setOnAction(e-> {
            if (this.status.equals(Status.FIRST)){
                this.status = Status.FIRST_AND_BLOCK;
            } else if (this.status.equals(Status.FIRST_AND_BLOCK)) {
                this.status = Status.FIRST;
            }else if (this.status.equals(Status.USER)) {
                this.status = Status.BLOCK;
            } else if (this.status.equals(Status.BLOCK)) {
                this.status = Status.USER;
            }
        });
        restrictedCheck.setOnAction(e->{
            this.restricted = !this.restricted;
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public void setStatus(Status status) {
        this.status = status;
        blockCheck.setSelected(status.equals(Status.BLOCK));
    }

    public void setRestricted(boolean restricted) {
        this.restricted = restricted;
        restrictedCheck.setSelected(restricted);
    }

    public enum Status {
        ADMIN,
        USER,
        FIRST,
        BLOCK,
        FIRST_AND_BLOCK;
    }
}
