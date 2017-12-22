package Entity;

/**
 * Created by yasin_000 on 21.12.2017.
 */
public class Course {
    private String courseID;
    private String name;
    private int quota;

    public String getCourseID() {
        return courseID;
    }

    public String getName() {
        return name;
    }

    public int getQuota() {
        return quota;
    }

    public Course(String courseID, String name, int quota) {

        this.courseID = courseID;
        this.name = name;
        this.quota = quota;
    }
}
