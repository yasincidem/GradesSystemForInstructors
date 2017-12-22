package Controllers;

import DBUtil.DBUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

    ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    RadioButton admin;
    @FXML
    RadioButton instructor;
    @FXML
    RadioButton student;
    @FXML
    TextField id;
    @FXML
    PasswordField password;
    @FXML
    Label errorUserType;
    @FXML
    Label errorId;
    @FXML
    Label errorPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        admin.setToggleGroup(toggleGroup);
        instructor.setToggleGroup(toggleGroup);
        student.setToggleGroup(toggleGroup);


    }

    @FXML
    public void login(ActionEvent actionEvent) {
        if (toggleGroup.getSelectedToggle() == null)
            errorUserType.setText("You must choose a user type");
        else if (id.getText().equals("")) {
            errorUserType.setText("");
            errorId.setText("You must enter your ID correctly");
        }
        else if (password.getText().equals("")) {
            errorId.setText("");
            errorPassword.setText("You must enter your password");
        }else {
            errorPassword.setText("");
            if (toggleGroup.getSelectedToggle() == admin)
                if (id.getText().equals("admin") && password.getText().equals("123456"))
                    goAdminPage(actionEvent);
                else
                    errorPassword.setText("You need to enter ID and password correctly");
            else if (toggleGroup.getSelectedToggle() == instructor){

                PreparedStatement preparedStatement = null;
                ResultSet rs = null;

                try (final Connection conn = DBUtil.connectDB()) {

                    String sql = "Select * from instructor Where ID=" + id.getText() + " and Password='" + password.getText() + "'";

                    preparedStatement = conn.prepareStatement(sql);
                    rs = preparedStatement.executeQuery(sql);
                    if (rs.next()) {
                        //in this case enter when at least one result comes it means user is valid
                        goInstructorPage(actionEvent, id.getText());
                    } else {
                        //in this case enter when  result size is zero  it means user is invalid
                        errorPassword.setText("You need to enter ID and password correctly");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else if (toggleGroup.getSelectedToggle() == student){
                PreparedStatement preparedStatement = null;
                ResultSet rs = null;

                try (final Connection conn = DBUtil.connectDB()) {

                    String sql = "Select * from student Where ID=" + id.getText() + " and Password='" + password.getText() + "'";

                    preparedStatement = conn.prepareStatement(sql);
                    rs = preparedStatement.executeQuery(sql);
                    if (rs.next()) {
                        //in this case enter when at least one result comes it means user is valid
                        goStudentPage(actionEvent, id.getText());
                    } else {
                        //in this case enter when  result size is zero  it means user is invalid
                        errorPassword.setText("You need to enter ID and password correctly");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void goAdminPage(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Res/admin.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root, 700, 591));
        stage.setTitle("Admin Page");
        stage.show();
    }

    private void goInstructorPage(ActionEvent actionEvent, String currentInstructorID) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../Res/instructor.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("Instructor Page");
        stage.show();
    }


    private void goStudentPage(ActionEvent actionEvent, String currentStudentID) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Res/student.fxml"));
        Parent parent = null;
        try {
            parent = (Parent) loader.load();
            StudentController studentController = loader.getController();
            studentController.setCurrentStudentID(currentStudentID);
        } catch (IOException e) {
            e.printStackTrace();
        }


        stage.setScene(new Scene(parent, 800, 600));
        stage.setTitle("Student Page");
        stage.show();
    }

}
