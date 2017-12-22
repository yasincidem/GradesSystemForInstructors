package Entity;

/**
 * Created by yasin_000 on 21.12.2017.
 */
public class Student {
    private Integer studentID;
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private String username;
    private String password;
    private Integer grade;


    public Integer getGrade() {
        return grade;
    }

    public Student(Integer studentID, String name, String surname, String email, String telephone, String username, String password) {
        this.studentID = studentID;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.telephone = telephone;
        this.username = username;
        this.password = password;
    }

    public Student(Integer studentID, String name, String surname, Integer grade) {
        this.studentID = studentID;
        this.name = name;
        this.surname = surname;
        this.grade = grade;
    }

    public Integer getStudentID() {
        return studentID;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
