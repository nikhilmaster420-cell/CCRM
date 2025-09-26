package edu.ccrm.cli;

// --- THIS IS THE MISSING BLOCK OF IMPORTS ---
import edu.ccrm.config.AppConfig;
import edu.ccrm.config.CourseBuilder;
import edu.ccrm.domain.*;
import edu.ccrm.exception.CourseNotFoundException;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.io.BackupService;
import edu.ccrm.io.ImportExportService;
import edu.ccrm.service.*;
import edu.ccrm.util.ConsoleColors;
import edu.ccrm.util.RecursiveFileUtil;
import edu.ccrm.service.TranscriptService;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
// --- END OF IMPORTS ---

public class ConsoleManager {
    private final Scanner scanner = new Scanner(System.in);
    private final IStudentService studentService = new StudentServiceImpl();
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService(studentService, courseService);
    private final ImportExportService importExportService = new ImportExportService(studentService, courseService);
    private final BackupService backupService = new BackupService();
    private final TranscriptService transcriptService = new TranscriptService();
    private final AtomicInteger studentIdCounter = new AtomicInteger(1);

    public void start() {
        printWelcomeMessage();

        mainLoop:
        while (true) {
            printMainMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> manageStudents();
                    case 2 -> manageCourses();
                    case 3 -> manageEnrollmentAndGrades();
                    case 4 -> handleFileOperations();
                    case 5 -> handleReports();
                    case 0 -> {
                        System.out.println(ConsoleColors.CYAN + "Exiting V-TOP Records Manager. Goodbye!" + ConsoleColors.RESET);
                        break mainLoop;
                    }
                    default -> System.out.println(ConsoleColors.RED + "Invalid choice. Please try again." + ConsoleColors.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
            }
        }
        scanner.close();
    }

    private void manageStudents() {
        while (true) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Deactivate Student");
            System.out.println("4. List Students Sorted by Name (Anonymous Class Demo)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> listAllStudents();
                    case 3 -> deactivateStudent();
                    case 4 -> listStudentsSortedByName();
                    case 0 -> { return; }
                    default -> System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid input." + ConsoleColors.RESET);
            }
        }
    }

    private void addStudent() {
        try {
            System.out.print("Enter full name: ");
            String name = scanner.nextLine();
            System.out.print("Enter VIT email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Registration Number (e.g., 23BCY10172): ");
            String regNo = scanner.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            LocalDate dob = LocalDate.parse(scanner.nextLine());

            Student student = new Student(studentIdCounter.getAndIncrement(), name, email, dob, regNo);
            studentService.addStudent(student);
            System.out.println(ConsoleColors.GREEN + "Student added successfully!" + ConsoleColors.RESET);
        } catch (DateTimeParseException e) {
            System.out.println(ConsoleColors.RED + "Invalid date format. Please use YYYY-MM-DD." + ConsoleColors.RESET);
        }
    }

    private void listAllStudents() {
        System.out.println("\n--- List of All Students ---");
        List<Student> students = studentService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            students.forEach(s -> System.out.println(s.getProfileDetails()));
        }
    }

    private void deactivateStudent() {
        System.out.print("Enter Student ID to deactivate: ");
        try {
            int studentId = Integer.parseInt(scanner.nextLine());
            studentService.findStudentById(studentId).ifPresentOrElse(
                student -> student.getStatusUpdater().deactivate(),
                () -> System.out.println(ConsoleColors.RED + "Student not found." + ConsoleColors.RESET)
            );
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid ID." + ConsoleColors.RESET);
        }
    }
    
    private void listStudentsSortedByName() {
        System.out.println("\n--- Students Sorted by Name ---");
        List<Student> students = studentService.getAllStudents();
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getFullName().compareTo(s2.getFullName());
            }
        });
        students.forEach(s -> System.out.println(s.getFullName() + " (Reg No: " + s.getRegNo() + ")"));
    }
    
    private void manageCourses() {
         while (true) {
            System.out.println("\n--- Course Management ---");
            System.out.println("1. Add Course");
            System.out.println("2. List All Courses");
            System.out.println("3. Search Courses by School");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch(choice) {
                    case 1 -> addCourse();
                    case 2 -> listAllCourses();
                    case 3 -> searchCourses();
                    case 0 -> { return; }
                    default -> System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid input." + ConsoleColors.RESET);
            }
        }
    }

    private void addCourse() {
        try {
            System.out.print("Enter course code (e.g., CSE1007): ");
            String code = scanner.nextLine();
            System.out.print("Enter course title: ");
            String title = scanner.nextLine();
            System.out.print("Enter credits: ");
            int credits = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter School (SCOPE, SENSE, SMEC, VISH, LAW): ");
            School school = School.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Enter semester (FALL, WINTER, SUMMER): ");
            Semester semester = Semester.valueOf(scanner.nextLine().toUpperCase());

            Course course = new CourseBuilder()
                .withCode(code)
                .withTitle(title)
                .withCredits(credits)
                .withSchool(school)
                .withSemester(semester)
                .build();

            courseService.addCourse(course);
            System.out.println(ConsoleColors.GREEN + "Course added successfully!" + ConsoleColors.RESET);

        } catch (NumberFormatException e) {
             System.out.println(ConsoleColors.RED + "Invalid credits. Please enter a number." + ConsoleColors.RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.RED + "Invalid School or Semester. Please use the options provided." + ConsoleColors.RESET);
        }
    }

    private void listAllCourses() {
        System.out.println("\n--- List of All Courses ---");
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            courses.forEach(System.out::println);
        }
    }

    private void searchCourses() {
        try {
            System.out.print("Enter School to search for (SCOPE, SENSE, etc.): ");
            School school = School.valueOf(scanner.nextLine().toUpperCase());
            System.out.println("\n--- Courses in " + school + " ---");
            List<Course> results = courseService.findCoursesBySchool(school);
            if (results.isEmpty()) {
                System.out.println("No courses found for this school.");
            } else {
                results.forEach(System.out::println);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.RED + "Invalid School." + ConsoleColors.RESET);
        }
    }
    
    private void manageEnrollmentAndGrades() {
        while (true) {
            System.out.println("\n--- Enrollment & Grading ---");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Assign Grade");
            System.out.println("3. View Student Transcript");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> enrollStudent();
                    case 2 -> assignGrade();
                    case 3 -> printTranscript();
                    case 0 -> { return; }
                    default -> System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid input. Please enter a number." + ConsoleColors.RESET);
            }
        }
    }
    
    private void enrollStudent() {
        try {
            System.out.print("Enter Student ID to enroll: ");
            int studentId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Course Code to enroll in: ");
            String courseCode = scanner.nextLine();
            enrollmentService.enrollStudent(studentId, courseCode);
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid ID. Please enter a number." + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "Enrollment Failed: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    private void assignGrade() {
        try {
            System.out.print("Enter Enrollment ID to assign grade: ");
            int enrollmentId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Grade (S, A, B, C, D, E, F): ");
            Grade grade = Grade.valueOf(scanner.nextLine().toUpperCase());
            enrollmentService.assignGrade(enrollmentId, grade);
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid ID. Please enter a number." + ConsoleColors.RESET);
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.RED + "Invalid Grade. Please use S, A, B, etc." + ConsoleColors.RESET);
        }
    }
    
    private void printTranscript() {
        System.out.print("Enter Student ID to view transcript: ");
        try {
            int studentId = Integer.parseInt(scanner.nextLine());
            studentService.findStudentById(studentId).ifPresentOrElse(
                student -> {
                    System.out.println("\n--- Transcript ---");
                    Person person = student;
                    System.out.println(person.getProfileDetails());
                    System.out.println("--------------------");
                    if (student.getEnrolledCourses().isEmpty()) {
                        System.out.println("No courses enrolled.");
                    } else {
                        System.out.printf("%-10s %-40s %-8s %-5s%n", "Code", "Title", "Credits", "Grade");
                        student.getEnrolledCourses().forEach(e -> {
                            Course c = e.getCourse();
                            System.out.printf("%-10s %-40s %-8d %-5s%n", c.getCode(), c.getTitle(), c.getCredits(), e.getGrade());
                        });
                        double gpa = transcriptService.calculateGpa(student);
                        System.out.printf("%nGPA: %.2f%n", gpa);
                    }
                },
                () -> System.out.println(ConsoleColors.RED + "Student not found." + ConsoleColors.RESET)
            );
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid ID." + ConsoleColors.RESET);
        }
    }

    private void handleReports() {
        System.out.println("\n[INFO] Reports module is not fully implemented in this demo.");
    }

    private void handleFileOperations() {
        System.out.println("\n--- File Utilities ---");
        System.out.println("1. Import from CSV");
        System.out.println("2. Export All Data to CSV");
        System.out.println("3. Create a Backup");
        System.out.println("4. Calculate Size of Backup Directory");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch(choice) {
                case 1 -> importExportService.importDataFromCsv("test-data/students.csv", "test-data/courses.csv");
                case 2 -> importExportService.exportDataToCsv();
                case 3 -> backupService.performBackup();
                case 4 -> {
                    long size = RecursiveFileUtil.getDirectorySize(Path.of(AppConfig.getInstance().getBackupDirectory()));
                    System.out.println("Total size of backup directory: " + size + " bytes.");
                }
                case 0 -> { return; }
                default -> System.out.println(ConsoleColors.RED + "Invalid choice." + ConsoleColors.RESET);
            }
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.RED + "Invalid input." + ConsoleColors.RESET);
        }
    }

    private void printWelcomeMessage() {
        System.out.println(ConsoleColors.BLUE_BOLD + "=======================================================");
        System.out.println(" Welcome to VIT Bhopal Course & Records Manager (V-TOP)");
        System.out.println("=======================================================" + ConsoleColors.RESET);
        System.out.println("App data will be stored in: " + AppConfig.getInstance().getDataDirectory());
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollment & Grades");
        System.out.println("4. File Utilities");
        System.out.println("5. View Reports");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }
}