import java.util.ArrayList;
import java.util.Collections;

class Course {
    static final double LECTURER_COURSE = -5;
    static final double STUDENT_COURSE = 0;
    private static final double RAW_COURSE = -10;
    private static ArrayList<Course> courses = new ArrayList<>();
    private int courseID;
    private int unit;
    private int capacity = 15;
    private int numberOfRegisteredStudents = 0;
    private double score = RAW_COURSE;

    Course(Course course) {
        courseID = course.getCourseID();
        unit = course.getUnit();
        capacity = course.getCapacity();
        numberOfRegisteredStudents = course.getNumberOfRegisteredStudents();
        score = course.getScore();
    }

    Course(int courseID, int unit) {
        this.courseID = courseID;
        this.unit = unit;
    }

    static void addNewRawCourse(Course course) {
        int courseID = course.getCourseID();
        if (Course.getRawCourse(courseID) == null && Course.getLecturerCourse(courseID) == null)
            courses.add(course);
    }

    static void addNewCourse(Course course) {
        courses.add(course);
    }

    static ArrayList<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    static void showCourse(int courseID, String string) {
        Course currentCourse = getLecturerCourse(courseID);
        if (currentCourse == null)
            System.out.println("shoma daneshjoo nistid");
        else if (string.equals("students"))
            showCourseStudents(courseID);
        else if (string.equals("lecturer"))
            showCourseLecturer(currentCourse);
        else if (string.equals("capacity"))
            System.out.println(currentCourse.getCapacity());
        else if (string.equals("average"))
            showCourseAverage(courseID);
    }

    static void showCourseAverage(int courseID) {
        double markSum = 0;
        int numberOfStudents = 0;
        for (Student student : Student.getStudents()) {
            Course course = student.getCourse(courseID);
            if (course != null) {
                numberOfStudents++;
                markSum += course.getScore();
            }
        }
        if (numberOfStudents != 0) {
            markSum /= numberOfStudents;
            markSum = Math.round(markSum * 10) / 10.0;
            if (markSum - Math.floor(markSum) < .05)
                System.out.println((int) (markSum));
            else
                System.out.println(markSum);
        }
    }

    static void showCourseLecturer(Course currentCourse) {
        Lecturer currentLecturer = null;
        for (Lecturer lecturer : Lecturer.getLecturers())
            if (currentLecturer == null && lecturer.getCourses().contains(currentCourse))
                currentLecturer = lecturer;
        if (currentLecturer != null)
            System.out.println(currentLecturer.getLecturerID());
    }

    static void showCourseStudents(int courseID) {
        ArrayList<Student> students = new ArrayList<>();
        for (Student student : Student.getStudents())
            if (student.getCourse(courseID) != null)
                students.add(student);
        Collections.sort(students, Student.studentIDComparator());
        for (int i = 0; i < students.size() - 1; i++)
            System.out.print(students.get(i).getStudentID() + " ");
        if (!students.isEmpty())
            System.out.println(students.get(students.size() - 1).getStudentID());
    }

    static Course getRawCourse(int courseID) {
        for (Course course : courses)
            if (course.courseID == courseID && course.getScore() < LECTURER_COURSE)
                return course;
        return null;
    }

    static Course getLecturerCourse(int courseID) {
        for (Course course : courses) {
            double score = course.score;
            if (course.courseID == courseID && score < STUDENT_COURSE && score > RAW_COURSE)
                return course;
        }
        return null;
    }

    static void removeCourse(Course course) {
        Course.courses.remove(course);
    }

    int getNumberOfRegisteredStudents() {
        return numberOfRegisteredStudents;
    }

    public void setNumberOfRegisteredStudents(int number) {
        numberOfRegisteredStudents = number;
    }

    void incrementNumberOfRegisteredStudents() {
        numberOfRegisteredStudents++;
    }

    void decrementNumberOfRegisteredStudents() {
        if (numberOfRegisteredStudents > 0)
            numberOfRegisteredStudents--;
    }

    int getCourseID() {
        return courseID;
    }

    void addCapacity(int number) {
        capacity += number;
    }

    double getScore() {
        return score;
    }

    void setScore(double score) {
        this.score = score;
    }

    int getUnit() {
        return unit;
    }

    int getCapacity() {
        return capacity;
    }
}