package Controllers;

import DBUtil.DBUtil;
import Entity.Instructor;
import Entity.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by yasin_000 on 20.12.2017.
 */
public class AdminController implements Initializable {

    //TextFields
    @FXML
    TextField idTextField;
    @FXML
    TextField nameTextField;
    @FXML
    TextField surnameTextField;
    @FXML
    TextField emailTextField;
    @FXML
    TextField usernameField;
    @FXML
    TextField telephoneField;
    @FXML
    TextField passwordField;

    //TableView & TableColumns for Instructor
    @FXML
    TableView<Instructor> tableView;
    @FXML
    TableColumn<Instructor, Integer> idColumn;
    @FXML
    TableColumn<Instructor, String> nameColumn;
    @FXML
    TableColumn<Instructor, String> surnameColumn;
    @FXML
    TableColumn<Instructor, String> emailColumn;
    @FXML
    TableColumn<Instructor, String> usernameColumn;
    @FXML
    TableColumn<Instructor, String> telephoneColumn;
    @FXML
    TableColumn<Instructor, String> passwordColumn;


    //TableView & TableColumns for Student
    @FXML
    TableView<Student> tableViewStudent;
    @FXML
    TableColumn<Student, Integer> idColumnStudent;
    @FXML
    TableColumn<Student, String> nameColumnStudent;
    @FXML
    TableColumn<Student, String> surnameColumnStudent;
    @FXML
    TableColumn<Student, String> emailColumnStudent;
    @FXML
    TableColumn<Student, String> usernameColumnStudent;
    @FXML
    TableColumn<Student, String> telephoneColumnStudent;
    @FXML
    TableColumn<Student, String> passwordColumnStudent;

    //ToggleGroup to get RadioButtons together
    ToggleGroup toggleGroup = new ToggleGroup();

    //RadioButtons
    @FXML
    RadioButton instructorRadioButton;
    @FXML
    RadioButton studentRadioButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instructorRadioButton.setToggleGroup(toggleGroup);
        studentRadioButton.setToggleGroup(toggleGroup);

    }

    @FXML
    public void addUser(ActionEvent actionEvent) {
        if (toggleGroup.getSelectedToggle() == instructorRadioButton)
            addUserToDB("instructor", "student");
        else if (toggleGroup.getSelectedToggle() == studentRadioButton)
            addUserToDB("student", "instructor");
        else if (toggleGroup.getSelectedToggle() == null){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("You must select Instructor or Student");
            alert.showAndWait();
        }

    }

    private void addUserToDB(String currentTable, String otherTable) {
        if (idTextField.getText().isEmpty() || nameTextField.getText().isEmpty() || surnameTextField.getText().isEmpty() ||
               emailTextField.getText().isEmpty() ||  telephoneField.getText().isEmpty() || usernameField.getText().isEmpty() ||
                passwordField.getText().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("You must enter all information");
            alert.showAndWait();
        }else {

            int count = 0;
            final String id = idTextField.getText();
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try(Connection conn = DBUtil.connectDB()){
                final String queryCheck = "select count(*) from " + otherTable  + " WHERE ID = " + Integer.parseInt(id);
                statement = conn.prepareStatement(queryCheck);
                resultSet = statement.executeQuery();
                if(resultSet.next()) {
                    count = resultSet.getInt(1);
                    System.out.println(count);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                if (statement != null){
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (resultSet != null){
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (count == 0){
                PreparedStatement preparedStatement = null;

                try (final Connection conn = DBUtil.connectDB()) {

                    String query = "insert into " + currentTable + " (ID, Name, Surname, Email, Telephone, Username, Password)"
                            + " values (?, ?, ?, ?, ?, ?, ?)";

                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, idTextField.getText());
                    preparedStatement.setString(2, nameTextField.getText());
                    preparedStatement.setString(3, surnameTextField.getText());
                    preparedStatement.setString(4, emailTextField.getText());
                    preparedStatement.setString(5, telephoneField.getText());
                    preparedStatement.setString(6, usernameField.getText());
                    preparedStatement.setString(7, passwordField.getText());

                    preparedStatement.execute();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success Dialog");
                    alert.setHeaderText("The record successfully created");
                    alert.showAndWait();

                } catch (SQLIntegrityConstraintViolationException e) {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText("The record already exists");
                    alert.showAndWait();

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    DBUtil.close();
                }
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("There already exists a " + otherTable  + " record with same ID");
                alert.showAndWait();
            }

        }
    }


    public void showInstructors(ActionEvent actionEvent) {
        idColumn.setCellValueFactory(new PropertyValueFactory<Instructor, Integer>("instructorID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("surname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("email"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("telephone"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<Instructor, String>("password"));

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Instructor> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select * from instructor";

            preparedStatement = conn.prepareStatement(query);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Integer i = (Integer) rs.getObject(1);
                String s1 = rs.getString(2);
                String s2 = rs.getString(3);
                String s3 = rs.getString(4);
                String s4 = rs.getString(5);
                String s5 = rs.getString(6);
                String s6 = rs.getString(7);

                Instructor instructor = new Instructor(i, s1, s2, s3, s4, s5, s6);

                list.add(instructor);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        final ObservableList<Instructor> instructors = FXCollections.observableList(list);

        tableView.setItems(instructors);
    }

    @FXML
    public void showStudents(ActionEvent actionEvent) {
        idColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, Integer>("studentID"));
        nameColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("name"));
        surnameColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("surname"));
        emailColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        usernameColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("telephone"));
        telephoneColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("username"));
        passwordColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("password"));

        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Student> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select * from student";

            preparedStatement = conn.prepareStatement(query);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Integer i = (Integer) rs.getObject(1);
                String s1 = rs.getString(2);
                String s2 = rs.getString(3);
                String s3 = rs.getString(4);
                String s4 = rs.getString(5);
                String s5 = rs.getString(6);
                String s6 = rs.getString(7);

                Student student = new Student(i, s1, s2, s3, s4, s5, s6);
                System.out.println(s1);
                list.add(student);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        final ObservableList<Student> students = FXCollections.observableList(list);

        tableViewStudent.setItems(students);
    }


    public void goLogin(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("../Res/login.fxml"));

        stage.setScene(new Scene(root, 370, 235));
        stage.setTitle("Login Page");
        stage.show();
    }
}
