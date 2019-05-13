import java.util.ArrayList;

class Lecturer {
    private static ArrayList<Lecturer> lecturers = new ArrayList<>();
    private int lecturerID;
    private ArrayList<Course> courses = new ArrayList<>();

    Lecturer(int lecturerID) {
        this.lecturerID = lecturerID;
    }

    static ArrayList<Lecturer> getLecturers() {
        return new ArrayList<>(lecturers);
    }

    static void addNewLecturer(Lecturer lecturer) {
        if (Lecturer.getLecturer(lecturer.lecturerID) == null)
            lecturers.add(lecturer);
    }

    static void addCapacity(int lecturerID, int courseID, int number) {
        Lecturer currentLecturer = getLecturer(lecturerID);
        if (currentLecturer != null) {
            Course currentCourse = Course.getLecturerCourse(courseID);
            if (currentCourse != null && currentLecturer.getCourses().contains(currentCourse)) {
                for (Course course : Course.getCourses())
                    if (course.getCourseID() == courseID)
                        course.addCapacity(number);
            }
        }
    }

    static Lecturer getLecturer(int lecturerID) {
        for (Lecturer lecturer : getLecturers())
            if (lecturer.getLecturerID() == lecturerID)
                return lecturer;
        return null;
    }

    static void setMark(int lecturerID, int courseID, double mark, int studentID) {
        Lecturer currentLecturer = Lecturer.getLecturer(lecturerID);
        if (currentLecturer != null) {
            Course currentCourse = Course.getLecturerCourse(courseID);
            if (currentCourse != null && currentLecturer.getCourses().contains(currentCourse)) {
                if (studentID == -1)
                    for (Student student : Student.getStudents())
                        student.setMark(courseID, mark);
                else {
                    Student currentStudent = Student.getStudent(studentID);

                    if (currentStudent != null)
                        currentStudent.setMark(courseID, mark);
                }
            }
        }
    }

    void addCourse(int courseID) {
        Course course = Course.getRawCourse(courseID);
        if (course != null) {
            course.setScore(Course.LECTURER_COURSE);
            courses.add(course);
        }
    }

    ArrayList<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    int getLecturerID() {
        return lecturerID;
    }

    void removeCourse(Course course) {
        courses.remove(course);
    }
}