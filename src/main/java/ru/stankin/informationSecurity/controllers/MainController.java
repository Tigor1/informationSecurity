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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    private String restriction = "(?=.*[A-Za-z])(?=.*[ёЁА-Яа-я])(?=.*[.,!?;:])(?=\\S+$).{0,15}";

    @FXML Text errorText;
    @FXML AnchorPane loginPane;
    @FXML AnchorPane mainPane;
    @FXML AnchorPane adminPane;

    @FXML TextField loginText;
    @FXML PasswordField passwordText;
    @FXML RadioButton userRBtn;
    @FXML RadioButton adminRBtn;
    @FXML Button loginBtn;

    @FXML TableView listUsers;

    @FXML TableColumn<String, User> nameT;
    @FXML TableColumn<Boolean, User> blockT;
    @FXML TableColumn<Boolean, User> restrictions;

    @FXML TextField nameAddT;
    @FXML PasswordField passwordAddT;
    @FXML CheckBox blockAddC;
    @FXML CheckBox psswrdRAddC;

    private List<User> users;

    public MainController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!Files.exists(Paths.get("users"))) {
            List<String> admin = Arrays.asList("admin", "admin", "true", "true", "false");

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

        nameT.setCellValueFactory(new PropertyValueFactory<>("name"));
        blockT.setCellValueFactory(new PropertyValueFactory<>("blockCheck"));
        restrictions.setCellValueFactory(new PropertyValueFactory<>("limitedCheck"));
        listUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    public void login(Event event) {

        User currentUser = getByName(loginText.getText());

        if (loginText.getText().isEmpty() || passwordText.getText().isEmpty()) {
            errorText.setText("Fill login and password!");
            return;
        }

        if (!userRBtn.isSelected() && !adminRBtn.isSelected()) {
            errorText.setText("Select user or admin");
            return;
        }

        if (currentUser == null || !currentUser.getPassword().equals(passwordText.getText())) {
            errorText.setText("Error!");
            return;
        }
        errorText.setText("");

        loginPane.setVisible(false);
        if (adminRBtn.isSelected()) {

            mainPane.setVisible(false);
            adminPane.setVisible(true);
            getUsers().forEach(user -> listUsers.getItems().add(user));
        } else if (userRBtn.isSelected()) {

            adminPane.setVisible(false);
            mainPane.setVisible(true);
        }

    }

    private List<User> getUsers() {
        try {
            if (users == null) {
                users = new ArrayList<>();
                List<String> lines = Files.readAllLines(Paths.get("users"));

                for (int i = 0; i < lines.size(); i++) {
                    users.add(new User(
                            lines.get(i++),
                            lines.get(i++),
                            lines.get(i++).equals("true"),
                            lines.get(i++).equals("true"),
                            lines.get(i).equals("true")));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return users;
    }

    @FXML
    public void addUser() {
        User newUser = new User(nameAddT.getText(), passwordAddT.getText(), false, blockAddC.isSelected(), psswrdRAddC.isSelected());
        List<String> values = Arrays.asList(
                            nameAddT.getText(),
                            passwordAddT.getText(),
                            "false",
                            String.valueOf(blockAddC.isSelected()),
                            String.valueOf(psswrdRAddC.isSelected()));

        listUsers.getItems().add(newUser);

        try {
            Files.write(Paths.get("users"),
                                        values,
                                        StandardCharsets.UTF_8,
                                        StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getByName(String name) {
        getUsers();
        return users.stream()
                .reduce(null, (result, current)->result = (current.getName().equals(name))? current : result);
    }
}
