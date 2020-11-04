package ru.stankin.informationSecurity.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.event.Event;
import javafx.scene.text.Text;
import ru.stankin.informationSecurity.entities.User;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class MainController implements Initializable {
    private final String restriction = "(?=.*[A-Za-z])(?=.*[ёЁА-Яа-я])(?=.*[.,!?;:])(?=\\S+$).{0,15}";

    @FXML
    Text errorText;
    @FXML
    AnchorPane loginPane;
    @FXML
    AnchorPane mainPane;
    @FXML
    AnchorPane adminPane;

    @FXML
    TextField loginText;
    @FXML
    PasswordField passwordText;
    @FXML
    RadioButton userRBtn;
    @FXML
    RadioButton adminRBtn;
    @FXML
    Button loginBtn;

    @FXML
    TableView listUsers;

    @FXML
    TableColumn<String, User> nameT;
    @FXML
    TableColumn<Boolean, User> blockT;
    @FXML
    TableColumn<Boolean, User> restrictions;

    @FXML
    TextField nameAddT;
    @FXML
    PasswordField passwordAddT;
    @FXML
    CheckBox blockAddC;
    @FXML
    CheckBox psswrdRAddC;


    //change password pane
    @FXML
    AnchorPane changePasswdPane;
    @FXML
    PasswordField changePasswd1;
    @FXML
    PasswordField changePasswd2;
    @FXML
    Button changePasswdBtn;

    //navigation btns
    @FXML
    Button mainSignOutBtn;
    @FXML
    Button mainAdminBtn;
    @FXML
    Button adminSignOutBtn;
    @FXML
    Button adminHomeBtn;

    @FXML Button editUserBtn;
    @FXML Text adminErrorT;
    @FXML Text changePasswdInfoT;
    @FXML Text changePasswdErrorT;

    private List<User> users;

    public MainController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Files.exists(Paths.get("users"))) {
            List<String> admin = Arrays.asList("admin", "admin", User.Status.ADMIN.name(), "false");

            try {
                Files.write(Paths.get("users"),
                        admin,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ToggleGroup group = new ToggleGroup();

        userRBtn.setToggleGroup(group);
        adminRBtn.setToggleGroup(group);

        changePasswdBtn.setOnAction(e -> {
            if (changePasswd1.getText().isEmpty() || changePasswd2.getText().isEmpty()) return;

            if (!changePasswd1.getText().equals(changePasswd2.getText())) {
                changePasswdErrorT.setText("passwords don't match!");
                return;
            }
            changePasswdErrorT.setText("");

            User currentUser = getByName(loginText.getText());

            if (currentUser.isRestricted() && !changePasswd1.getText().matches(restriction)) {
                changePasswdInfoT.setVisible(true);
                return;
            }
            changePasswdInfoT.setVisible(false);

            currentUser.setPassword(changePasswd1.getText());
            currentUser.setStatus(User.Status.USER);
            updateUser();
            changePasswd1.clear();
            changePasswd2.clear();
            loginText.clear();
            passwordText.clear();
            adminRBtn.setSelected(false);
            userRBtn.setSelected(false);
            changePasswdPane.setVisible(false);
            loginPane.setVisible(true);
        });

        adminSignOutBtn.setOnAction(e -> {
            nameAddT.clear();
            passwordAddT.clear();
            updateUser();
            blockAddC.setSelected(false);
            psswrdRAddC.setSelected(false);
            adminPane.setVisible(false);
            loginPane.setVisible(true);
        });

        mainSignOutBtn.setOnAction(e -> {
            mainPane.setVisible(false);
            loginPane.setVisible(true);
        });

        adminHomeBtn.setOnAction(e -> {
            nameAddT.clear();
            passwordAddT.clear();
            blockAddC.setSelected(false);
            psswrdRAddC.setSelected(false);
            adminPane.setVisible(false);
            mainPane.setVisible(true);
        });

        users = getUsers();

        nameT.setCellValueFactory(new PropertyValueFactory<>("name"));
        blockT.setCellValueFactory(new PropertyValueFactory<>("blockCheck"));
        restrictions.setCellValueFactory(new PropertyValueFactory<>("restrictedCheck"));
        listUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    public void saveUsers() {
        updateUser();
    }

    @FXML
    public void login(Event event) {
        if (loginText.getText().isEmpty() || passwordText.getText().isEmpty()) {
            errorText.setText("Fill login and password!");
            return;
        }

        if (!userRBtn.isSelected() && !adminRBtn.isSelected()) {
            errorText.setText("Select user or admin");
            return;
        }

        User currentUser = getByName(loginText.getText());

        if (currentUser == null || !currentUser.getPassword().equals(passwordText.getText())) {
            errorText.setText("Error!");
            return;
        }

        if (currentUser.getStatus().equals(User.Status.BLOCK) || currentUser.getStatus().equals(User.Status.FIRST_AND_BLOCK)) {
            errorText.setText("User is block!");
            return;
        }

        if (currentUser.getStatus().equals(User.Status.FIRST) || (currentUser.isRestricted() && !currentUser.getPassword().matches(restriction))) {
            if (currentUser.isRestricted() && !currentUser.getPassword().matches(restriction))
                changePasswdInfoT.setVisible(true);
            loginPane.setVisible(false);
            changePasswdPane.setVisible(true);
            return;
        }
        errorText.setText("");

        if (adminRBtn.isSelected()) {
            if (!currentUser.getStatus().equals(User.Status.ADMIN)) {
                errorText.setText("User isn't admin");
                return;
            }
            listUsers.getItems().clear();
            listUsers.getItems().addAll(users);

            loginPane.setVisible(false);
            mainPane.setVisible(false);
            adminPane.setVisible(true);
        } else if (userRBtn.isSelected()) {
            loginPane.setVisible(false);
            adminPane.setVisible(false);
            mainPane.setVisible(true);
        }
    }

    public void updateUser() {
        List<String> lines = new ArrayList<>();

        users.stream()
                .forEach(user -> {
                    lines.add(user.getName());
                    lines.add(user.getPassword());
                    lines.add(user.getStatus().name());
                    lines.add(String.valueOf(user.isRestricted()));
                });

        try {
            Files.delete(Paths.get("users"));
            Files.write(Paths.get("users"),
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> getUsers() {
        List<User> result = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get("users"));

            for (int i = 0; i < lines.size(); i++) {
                result.add(new User(
                        lines.get(i++),
                        lines.get(i++),
                        User.Status.valueOf(lines.get(i++)),
                        lines.get(i).equals("true")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @FXML
    public void addUser() {
        if (users.stream().anyMatch(u->u.getName().equals(nameAddT.getText()))) {
            adminErrorT.setText("User with this name is exist!");
            return;
        }
        adminErrorT.setText("");

        User.Status status = blockAddC.isSelected() ? User.Status.FIRST_AND_BLOCK : User.Status.FIRST;
        User newUser = new User(nameAddT.getText(), passwordAddT.getText(), status, psswrdRAddC.isSelected());
        users.add(newUser);
        listUsers.getItems().add(newUser);
    }

    public User getByName(String name) {
        return users.stream()
                .reduce(null, (result, current) -> result = (current.getName().equals(name)) ? current : result);
    }
}
