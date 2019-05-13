import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;

class Student {

    private static final int MAXIMUM_NUMBER_OF_DELETE_REQUESTS_UNITS = 3;
    private static ArrayList<Student> students = new ArrayList<>();
    private int studentID;
    private ArrayList<Course> studentCourses = new ArrayList<>();
    private double average = 0;
    private int numberOfDeletedCourses = 0;
    private int units = 0;

    Student(int studentID) {
        this.studentID = studentID;
    }

    static Comparator<Student> studentIDComparator() {
        return Comparator.comparingInt(o -> o.studentID);
    }

    static Comparator<Student> rankComparator() {
        return (o1, o2) -> {

            if (o1.getAverage() > o2.getAverage())
                return -1;

            if (o1.getAverage() < o2.getAverage())
                return 1;

            if (o1.getStudentID() < o2.getStudentID())
                return -1;
            return 1;
        };
    }

    static Comparator<Student> courseRankComparator(int courseID) {
        return (o1, o2) -> {
            Course firstCourse = o1.getCourse(courseID);
            Course secondCourse = o2.getCourse(courseID);

            if (firstCourse.getScore() > secondCourse.getScore())
                return -1;

            if (firstCourse.getScore() < secondCourse.getScore())
                return 1;

            if (o1.getStudentID() < o2.getStudentID())
                return -1;

            return 1;
        };
    }

    static void addNewStudent(Student student) {
        if (Student.getStudent(student.getStudentID()) == null)
            students.add(student);
    }

    static void registerCourse(int studentID, int courseID) {
        Student currentStudent = getStudent(studentID);

        if (currentStudent != null) {
            Course currentCourse = currentStudent.getCourse(courseID);
            if (currentCourse == null) {
                Course newCourse = Course.getLecturerCourse(courseID);

                if (newCourse != null && newCourse.getNumberOfRegisteredStudents() < newCourse.getCapacity()) {
                    newCourse = new Course(newCourse);
                    newCourse.setScore(Course.STUDENT_COURSE);
                    currentStudent.studentCourses.add(newCourse);
                    currentStudent.addCourse(newCourse);
                    currentStudent.units += newCourse.getUnit();
                }
            }
        }
    }

    static Student getStudent(int studentID) {
        Student currentStudent = null;
        for (Student student : Student.getStudents())

            if (currentStudent == null && student.getStudentID() == studentID)
                currentStudent = student;
        return currentStudent;
    }

    static void deleteCourse(int studentID, int courseID) {
        Student currentStudent = getStudent(studentID);

        if (currentStudent != null) {
            Course currentCourse = currentStudent.getCourse(courseID);

            if (currentCourse != null) {
                currentStudent.numberOfDeletedCourses += currentCourse.getUnit();
                if (currentStudent.numberOfDeletedCourses <= MAXIMUM_NUMBER_OF_DELETE_REQUESTS_UNITS) {
                    currentStudent.studentCourses.remove(currentCourse);
                    Course.removeCourse(currentCourse);
                    for (Course course : Course.getCourses())

                        if (course.getCourseID() == courseID)
                            course.decrementNumberOfRegisteredStudents();
                }
            }
        }
    }

    static void setAverages() {
        for (Student student : Student.getStudents()) {
            int unitSum = 0;
            for (Course course : student.getStudentCourses()) {
                student.average += course.getScore() * course.getUnit();
                unitSum += course.getUnit();
            }

            if (unitSum != 0)
                student.average /= unitSum;
        }
    }

    static ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    static void deleteStudent(int studentID) {
        Student currentStudent = getStudent(studentID);

        if (currentStudent != null) {
            for (Course studentCourse : currentStudent.getStudentCourses()) {
                for (Course course : Course.getCourses())
                    if (course.getCourseID() == studentCourse.getCourseID())
                        course.decrementNumberOfRegisteredStudents();
                Course.removeCourse(studentCourse);
            }
            Student.students.remove(currentStudent);
        }
    }

    static void showAverage(int studentID) {
        Student currentStudent = getStudent(studentID);
        if (currentStudent != null) {
            double average = Math.round(currentStudent.getAverage() * 10) / 10.0;
            if (average - Math.floor(average) < 0.05)
                System.out.println((int) (average));
            else
                System.out.println(average);
        }
        else
            System.out.println("shoma daneshjoo nistid");
    }

    static void showRanks() {
        ArrayList<Student> ranks = new ArrayList<>(Student.getStudents());
        Collections.sort(ranks, Student.rankComparator());
        showFirstThree(ranks);
    }

    static void showFirstThree(ArrayList<Student> ranks) {
        for (int i = 0; i < Integer.min(3, ranks.size()) - 1; i++)
            System.out.print(ranks.get(i).getStudentID() + " ");
        if (!ranks.isEmpty())
            System.out.println(ranks.get(Integer.min(3, ranks.size()) - 1).getStudentID());
    }

    static void showRanks(int courseID) {
        ArrayList<Student> courseRanks = new ArrayList<>();
        for (Student student : getStudents())
            for (Course course : student.getStudentCourses())

                if (!courseRanks.contains(student) && course.getCourseID() == courseID)
                    courseRanks.add(student);
        Collections.sort(courseRanks, Student.courseRankComparator(courseID));
        showFirstThree(courseRanks);
    }

    Course getCourse(int courseID) {
        for (Course course : getStudentCourses())
            if (course.getCourseID() == courseID)
                return course;
        return null;
    }

    double getAverage() {
        return average;
    }

    void addCourse(Course course) {
        for (Course course1 : Course.getCourses())

            if (course1.getCourseID() == course.getCourseID())
                course1.incrementNumberOfRegisteredStudents();
        course.incrementNumberOfRegisteredStudents();
        course.setScore(Course.STUDENT_COURSE);
        Course.addNewCourse(course);
    }

    void setMark(int courseID, double mark) {
        for (Course course : getStudentCourses())
            if (course.getCourseID() == courseID)
                course.setScore(mark);
    }

    int getStudentID() {
        return studentID;
    }

    ArrayList<Course> getStudentCourses() {
        return new ArrayList<>(studentCourses);
    }

    void removeCourse(Course course) {
        if (studentCourses.contains(course))
            units -= course.getUnit();
        studentCourses.remove(course);
    }

    int getUnits() {
        return units;
    }
}