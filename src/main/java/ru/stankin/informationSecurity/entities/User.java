package ru.stankin.informationSecurity.entities;

import javafx.scene.control.CheckBox;
import lombok.*;

import java.util.Objects;

@Getter @Setter
public class User {
    private String name;
    private String password = "qwerty";
    private boolean isAdmin = false;
    private boolean isBlocked = false;
    private boolean isLimited = false;
    private CheckBox blockCheck = new CheckBox();
    private CheckBox limitedCheck = new CheckBox();
    private boolean NeedChange = true;

    public User(String name, String password, boolean isAdmin, boolean isBlocked, boolean isLimited) {
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
        blockCheck.setSelected(isBlocked);
        this.isLimited = isLimited;
        limitedCheck.setSelected(isLimited);
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

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
        blockCheck.setSelected(blocked);
    }

    public void setLimited(boolean limited) {
        isLimited = limited;
        limitedCheck.setSelected(!limited);
    }
}
