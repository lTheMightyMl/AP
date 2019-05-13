import java.util.ArrayList;
import java.util.Scanner;

public class Interactor {
    private static final String PERSON_ID = "\\d{5}";
    private static final String NUMBER = "\\d+";
    private static final String COURSE_ID = NUMBER;
    private static final String ADD_STUDENT = "addStudent " + PERSON_ID;
    private static final String ADD_LECTURER = "addLecturer " + PERSON_ID + "( " + COURSE_ID + ")*";
    private static final String DELETE = "W " + COURSE_ID + " " + PERSON_ID;
    private static final String REGISTER = PERSON_ID + " register( " + COURSE_ID + ")*";
    private static final String CAPACITY = PERSON_ID + " capacitate " + COURSE_ID + " " + NUMBER;
    private static final String MARK = NUMBER + "(\\." + NUMBER + ")?";
    private static final String STUDENT_MARK = PERSON_ID + " " + MARK;
    private static final String ADD_MARK = PERSON_ID + " mark " + COURSE_ID + "( " + STUDENT_MARK + ")*";
    private static final String ADD_MARK_ALL = PERSON_ID + " mark " + COURSE_ID + " " + MARK + " -all";
    private static final String ADD_COURSE = "addCourse " + COURSE_ID + " " + NUMBER;
    private static final String SHOW_COURSE = "showCourse " + COURSE_ID + " \\D+";
    private static final String SHOW_RANKS = "showRanks " + COURSE_ID;
    private static final String SHOW_RANKS_ALL = "showRanks -all";
    private static final String SHOW_AVERAGE = "showAverage " + PERSON_ID;
    private static final String START_SEMESTER = "start semester";
    private static final String END_REGISTRATION = "end registration";
    private static final String END_SEMESTER = "end semester";
    private static final String END_SHOW = "endShow";
    private static final int MINIMUM_NUMBER_OF_STUDENTS = 3;
    private static final int MINIMUM_NUMBER_OF_UNITS = 12;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        boolean semesterHasStarted = false;
        boolean registration = false;
        boolean semesterHasEnded = false;
        while (true) {
            command = command.trim();
            if (command.matches(ADD_STUDENT) && !semesterHasStarted)
                Student.addNewStudent(new Student(Integer.parseInt(command.split(" ")[1])));
            else if (command.matches(ADD_LECTURER) && !semesterHasStarted)
                addLecturer(command);
            else if (command.matches(DELETE) && semesterHasStarted && !registration && !semesterHasEnded)
                deleteCourse(command);
            else if (command.matches(REGISTER) && registration)
                registerCourse(command);
            else if (command.matches(CAPACITY) && (registration || !semesterHasStarted))
                addCapacity(command);
            else if (command.matches(ADD_MARK))
                addMark(command);
            else if (command.matches(ADD_MARK_ALL))
                markAll(command);
            else if (command.equals(END_REGISTRATION) && semesterHasStarted)
                registration = endRegistration();
            else if (command.matches(ADD_COURSE))
                addCourse(command);
            else if (command.matches(SHOW_COURSE))
                showCourse(command);
            else if (command.matches(SHOW_RANKS))
                Student.showRanks(Integer.parseInt(command.split(" ")[1]));
            else if (command.matches(SHOW_RANKS_ALL))
                Student.showRanks();
            else if (command.matches(SHOW_AVERAGE))
                Student.showAverage(Integer.parseInt(command.split(" ")[1]));
            else if (command.matches(START_SEMESTER) && !semesterHasEnded)
                semesterHasStarted = registration = true;
            else if (command.matches(END_SEMESTER) && semesterHasStarted && !semesterHasEnded)
                semesterHasEnded = endSemester();
            else if (command.matches(END_SHOW) && semesterHasEnded)
                return;
            command = scanner.nextLine();
        }
    }

    static void showCourse(String command) {
        String[] commandSplit = command.split(" ");
        Course.showCourse(Integer.parseInt(commandSplit[1]), commandSplit[2]);
    }

    static void addCourse(String command) {
        String[] commandSplit = command.split(" ");
        Course.addNewRawCourse(new Course(Integer.parseInt(commandSplit[1]), Integer.parseInt(commandSplit[2])));
    }

    static boolean endRegistration() {
        removeCourses(getEmptyCourses());
        deleteIneligibleStudents();
        return false;
    }

    static void deleteIneligibleStudents() {
        ArrayList<Student> ineligibleStudents = new ArrayList<>();
        for (Student student : Student.getStudents())
            if (student.getUnits() < MINIMUM_NUMBER_OF_UNITS)
                ineligibleStudents.add(student);
        for (Student student : ineligibleStudents)
            Student.deleteStudent(student.getStudentID());
    }

    static void removeCourses(ArrayList<Course> courses) {
        for (Course course : courses) {
            for (Student student : Student.getStudents())
                student.removeCourse(course);
            for (Lecturer lecturer : Lecturer.getLecturers())
                lecturer.removeCourse(course);
            Course.removeCourse(course);
        }
    }

    static ArrayList<Course> getEmptyCourses() {
        ArrayList<Course> emptyCourses = new ArrayList<>();
        for (Course course : Course.getCourses())
            if (course.getNumberOfRegisteredStudents() < MINIMUM_NUMBER_OF_STUDENTS)
                emptyCourses.add(course);
        return emptyCourses;
    }

    static void markAll(String command) {
        String[] commandSplit = command.split(" ");
        int lecturerID = Integer.parseInt(commandSplit[0]);
        int courseID = Integer.parseInt(commandSplit[2]);
        Lecturer.setMark(lecturerID, courseID, Double.parseDouble(commandSplit[3]), -1);
    }

    static void addMark(String command) {
        String[] commandSplit = command.split(" ");
        int lecturerID = Integer.parseInt(commandSplit[0]);
        int courseID = Integer.parseInt(commandSplit[2]);
        for (int i = 3; i < commandSplit.length; i += 2) {
            double mark = Double.parseDouble(commandSplit[i + 1]);
            Lecturer.setMark(lecturerID, courseID, mark, Integer.parseInt(commandSplit[i]));
        }
    }

    static void addCapacity(String command) {
        String[] commandSplit = command.split(" ");
        int lecturerID = Integer.parseInt(commandSplit[0]);
        Lecturer.addCapacity(lecturerID, Integer.parseInt(commandSplit[2]), Integer.parseInt(commandSplit[3]));
    }

    static void registerCourse(String command) {
        String[] commandSplit = command.split(" ");
        int studentID = Integer.parseInt(commandSplit[0]);
        for (int i = 2; i < commandSplit.length; i++)
            Student.registerCourse(studentID, Integer.parseInt(commandSplit[i]));
    }

    static void deleteCourse(String command) {
        String[] commandSplit = command.split(" ");
        Student.deleteCourse(Integer.parseInt(commandSplit[2]), Integer.parseInt(commandSplit[1]));
    }

    static void addLecturer(String command) {
        String[] commandSplit = command.split(" ");
        int lecturerID = Integer.parseInt(commandSplit[1]);
        if (Lecturer.getLecturer(lecturerID) == null) {
            Lecturer newLecturer = new Lecturer(lecturerID);
            Lecturer.addNewLecturer(newLecturer);
            for (int i = 2; i < commandSplit.length; i++)
                newLecturer.addCourse(Integer.parseInt(commandSplit[i]));
        }
    }

    static boolean endSemester() {
        Student.setAverages();
        return true;
    }
}