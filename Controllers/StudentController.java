package Controllers;

import DBUtil.DBUtil;
import Entity.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by yasin_000 on 21.12.2017.
 */
public class StudentController implements Initializable {

    @FXML
    TableView<Course> tableViewCourses;
    @FXML
    TableColumn<Course, String> idColumnCourse;
    @FXML
    TableColumn<Course, String> nameColumnCourse;
    @FXML
    TableColumn<Course, Integer> quotaColumnCourse;
    //////////////////////////
    @FXML
    ListView<String> listViewForCourses;



    private String currentStudentID;

    public void setCurrentStudentID(String currentStudentID) {
        this.currentStudentID = currentStudentID;
    }

    private String selectedCourseID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("courseID"));
        nameColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
        quotaColumnCourse.setCellValueFactory(new PropertyValueFactory<Course, Integer>("quota"));
        listCoursesCurrentStudentEnrolled();
        showCoursesNotButtonClicked();

        tableViewCourses.setRowFactory( tv -> {
            TableRow<Course> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    Course rowData = row.getItem();
                    selectedCourseID = rowData.getCourseID();
                    registerTheCourse();
                    listCoursesCurrentStudentEnrolled();
                }
            });
            return row ;
        });

        listViewForCourses.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                showChartForSelectedCourse(listViewForCourses.getSelectionModel().getSelectedItem());
            }
        });

        listCoursesCurrentStudentEnrolled();
    }

    private void showCoursesNotButtonClicked(){
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

        tableViewCourses.setItems(courses);
    }
    private void registerTheCourse(){
        boolean isQuoteFull = false;
        PreparedStatement preparedStatement = null;

        try (final Connection conn = DBUtil.connectDB()) {
            String query0 = "select Quote from courses WHERE ID = '" + selectedCourseID + "'";
            preparedStatement = conn.prepareStatement(query0);
            final ResultSet resultSet = preparedStatement.executeQuery();
           while (resultSet.next()){
               if (resultSet.getInt(1) <= 0){
                   isQuoteFull = true;
                   Alert alert = new Alert(Alert.AlertType.WARNING);
                   alert.setTitle("Warning Dialog");
                   alert.setHeaderText("Sorry, Quota of the course is full");
                   alert.showAndWait();
               }
           }
           if (!isQuoteFull){

               String query2 = "select count(*) from grades WHERE CourseID = '" + selectedCourseID + "' and StudentID = '" + currentStudentID + "'" ;
               PreparedStatement preparedStatement2 = conn.prepareStatement(query2);
               ResultSet resultSet2 = preparedStatement2.executeQuery();
               int count = 0;
               while (resultSet2.next()){
                   count = resultSet2.getInt(1);
               }


               if (count < 1) {
                   String query = "insert into grades (StudentID, CourseID) values (?, ?)";

                   preparedStatement = conn.prepareStatement(query);
                   preparedStatement.setString(1, currentStudentID);
                   preparedStatement.setString(2, selectedCourseID);

                   preparedStatement.execute();

                   String query3 = "UPDATE courses SET Quote = Quote - 1 WHERE ID = '" + selectedCourseID + "'";
                   preparedStatement = conn.prepareStatement(query3);
                   preparedStatement.execute();

                   showCoursesNotButtonClicked();

                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                   alert.setTitle("Success Dialog");
                   alert.setHeaderText("You registered the course successfully");
                   alert.showAndWait();
               }else {
                   Alert alert = new Alert(Alert.AlertType.WARNING);
                   alert.setTitle("Warning Dialog");
                   alert.setHeaderText("You already registered the course");
                   alert.showAndWait();
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
            DBUtil.close();
        }
    }
    private void listCoursesCurrentStudentEnrolled(){
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select CourseID from grades where StudentID = " + currentStudentID;

            preparedStatement = conn.prepareStatement(query);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String str = rs.getString(1);
                list.add(str);
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
        ObservableList<String> observableList = FXCollections.observableList(list);
        final List<String> collect = observableList.stream().distinct().collect(Collectors.toList());

        listViewForCourses.setItems(FXCollections.observableList(collect));
    }

    @FXML
    LineChart<String, Number> lineChart;

    private void showChartForSelectedCourse(String selectedCourseID){
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Integer> list = new ArrayList<>();
        try (final Connection conn = DBUtil.connectDB()) {

            String query = "select Grade from grades where CourseID = '" + selectedCourseID + "'";

            preparedStatement = conn.prepareStatement(query);

            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Integer i = rs.getInt(1);
                list.add(i);
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

        ObservableList<Integer> observableList = FXCollections.observableList(list);

        int between0and20 = getGradesQuantityBetween(0, 20, observableList);
        int between20and40 = getGradesQuantityBetween(21, 40, observableList);
        int between40and60 = getGradesQuantityBetween(41, 60, observableList);
        int between60and80 = getGradesQuantityBetween(61, 80, observableList);
        int between80and100 = getGradesQuantityBetween(81, 100, observableList);

        XYChart.Series series = new XYChart.Series();

        series.getData().add(new XYChart.Data("0-20", between0and20));
        series.getData().add(new XYChart.Data("20-40", between20and40));
        series.getData().add(new XYChart.Data("40-60", between40and60));
        series.getData().add(new XYChart.Data("60-80", between60and80));
        series.getData().add(new XYChart.Data("80-100", between80and100));

        lineChart.setAnimated(false);

        lineChart.getData().clear();
        lineChart.getData().addAll(series);
    }

    public int getGradesQuantityBetween(int lower, int higher, ObservableList<Integer> observableList){
        int count = 0;
        for (int i = 0; i < observableList.size(); i++) {
            final int grade = observableList.get(i);
            if (grade >= lower && grade <= higher)
                count++;
        }
        return count;
    }

    public void goLogin(MouseEvent mouseEvent) throws IOException {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("../Res/login.fxml"));

        stage.setScene(new Scene(root, 370, 235));
        stage.setTitle("Login Page");
        stage.show();
    }
}

