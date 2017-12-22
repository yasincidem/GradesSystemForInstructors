package Controllers;

import DBUtil.DBUtil;
import Entity.Course;
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
 * Created by yasin_000 on 21.12.2017.
 */
public class InstructorController implements Initializable{
    @FXML
    TextField courseIDTextField;
    @FXML
    TextField courseNameTextField;
    @FXML
    TextField quotaTextField;

    ////////////////////////
    @FXML
    TableView<Course> tableViewCourse;
    @FXML
    TableColumn<Course, String> idColumnCourse;
    @FXML
    TableColumn<Course, String> nameColumnCourse;
    @FXML
    TableColumn<Course, Integer> quotaColumnCourse;
    /////////////////////////

    @FXML
    TableView<Student> tableViewStudent;
    @FXML
    TableColumn<Student, Integer> idColumnStudent;
    @FXML
    TableColumn<Student, String> nameColumnStudent;
    @FXML
    TableColumn<Student, String> surnameColumnStudent;
    @FXML
    TableColumn<Student, String> gradeColumnStudent;
    ///////////////////////////

    Integer selectedStudentIDFromStudentTable = 0;
    String courseIDToRememberForStudent = "";
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("courseID"));
        nameColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        quotaColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, Integer>("quota"));

        idColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, Integer>("studentID"));
        nameColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("name"));
        surnameColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("surname"));
        gradeColumnStudent.setCellValueFactory(new PropertyValueFactory<Student, String>("grade"));

        showCoursesNotButtonClicked();

        tableViewCourse.setRowFactory( tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Course rowData = row.getItem();
                    tableViewStudent.setItems(getStudentListUsingCourse(rowData.getCourseID()));
                    courseIDToRememberForStudent = rowData.getCourseID();
                }
            });
            return row ;
        });

        tableViewStudent.setRowFactory( tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Student rowData = row.getItem();
                    selectedStudentIDFromStudentTable = rowData.getStudentID();
                }
            });
            return row ;
        });


    }

    @FXML
    public void addCourse(ActionEvent actionEvent) {
        if (courseIDTextField.getText().isEmpty() || courseNameTextField.getText().isEmpty() || quotaTextField.getText().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("You must enter all information");
            alert.showAndWait();
        }else {

            showCoursesNotButtonClicked();
            PreparedStatement preparedStatement = null;

            try (final Connection conn = DBUtil.connectDB()) {

                String query = "insert into courses (ID, Name, Quote) values (?, ?, ?)";

                preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, courseIDTextField.getText());
                preparedStatement.setString(2, courseNameTextField.getText());
                preparedStatement.setString(3, quotaTextField.getText());

                preparedStatement.execute();

                showCoursesNotButtonClicked();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success Dialog");
                alert.setHeaderText("The record successfully created");
                alert.showAndWait();

            } catch (SQLIntegrityConstraintViolationException e) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("The course already exists");
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
        }
    }

    public void showCoursesNotButtonClicked(){
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Course> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select * from courses";

            preparedStatement = conn.prepareStatement(query);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String s1 = rs.getString(1);
                String s2 = rs.getString(2);
                Integer i = (Integer) rs.getObject(3);

                Course course = new Course(s1, s2, i);

                list.add(course);
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

        final ObservableList<Course> courses = FXCollections.observableList(list);

        tableViewCourse.setItems(courses);
    }


    public ObservableList<Student> getStudentListUsingCourse(String selectedCourseID){
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Student> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select StudentID, Grade from grades where CourseID = '" + selectedCourseID + "'";
            preparedStatement = conn.prepareStatement(query);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Integer studentID = rs.getInt(1);
                Integer grade = rs.getInt(2);
                System.out.println(rs.getInt(2));

                String query2 = "select * from student where ID = " +  studentID ;
                PreparedStatement preparedStatement2 = conn.prepareStatement(query2);
                ResultSet resultSet = preparedStatement2.executeQuery();

                while (resultSet.next()){
                    String name = resultSet.getString(2);
                    String surname = resultSet.getString(3);
                    Student student = new Student(studentID, name, surname, grade);

                    list.add(student);
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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
            DBUtil.close();
        }

        return FXCollections.observableList(list);
    }


    @FXML
    TextField gradeTextField;
    @FXML
    public void enterGrade(ActionEvent actionEvent) {
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try (final Connection conn = DBUtil.connectDB()) {
            System.out.println(selectedStudentIDFromStudentTable);
            System.out.println(courseIDToRememberForStudent);

            String query = "UPDATE grades SET Grade = " + Integer.parseInt(gradeTextField.getText()) + " WHERE StudentID = " + selectedStudentIDFromStudentTable
                     + " and CourseID = '" + courseIDToRememberForStudent + "'" ;

            preparedStatement = conn.prepareStatement(query);

            preparedStatement.execute();

        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
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
            DBUtil.close();
        }
        showCoursesNotButtonClicked();
        tableViewStudent.setItems(getStudentListUsingCourse(courseIDToRememberForStudent));
        gradeTextField.clear();

    }

    public void goLogin(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("../Res/login.fxml"));

        stage.setScene(new Scene(root, 370, 235));
        stage.setTitle("Login Page");
        stage.show();
    }
}
