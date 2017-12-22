package Entity;

/**
 * Created by yasin_000 on 20.12.2017.
 */
public class Instructor {
    private Integer instructorID;
    private String name;
    private String surname;
    private String email;
    private String telephone;
    private String username;
    private String password;

    public Instructor(Integer instructorID, String name, String surname, String email, String telephone, String username, String password) {
        this.instructorID = instructorID;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.telephone = telephone;
        this.username = username;
        this.password = password;
    }

    public Integer getInstructorID() {
        return instructorID;
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
