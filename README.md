# VIT Bhopal Course & Records Manager (V-TOP)

## 1. Project Overview

The VIT Bhopal Course & Records Manager (V-TOP) is a comprehensive, console-based Java SE application designed to manage student and course records for the university. It showcases a wide range of core and advanced Java features, from fundamental Object-Oriented Programming (OOP) principles to modern APIs like NIO.2, Streams, and the Date/Time API.

### Key Features:
- **Student Management:** Add, list, and view student profiles and transcripts.
- **Course Management:** Add and list courses offered by various schools at VIT Bhopal.
- **Enrollment & Grading:** A robust system to enroll students in courses, enforce business rules like credit limits, assign grades, and generate transcripts.
- **File Utilities:** Export data to CSV files and perform timestamped backups of application data.

---

## 2. How to Run the Project

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher.
- **IDE:** VS Code with the "Extension Pack for Java" or Eclipse IDE.

### Steps to Run
1.  **Clone/Download:** Place the project source code in a folder on your computer.
2.  **Open in IDE:** Open the project folder in VS Code or import it into Eclipse.
3.  **Enable Assertions (Required):** Assertions are used to check for internal invariants (e.g., credit totals can't be negative). You must enable them.
    -   **In VS Code:** Create a `.vscode/launch.json` file and add `"vmArgs": "-ea"`.
    -   **In Eclipse:** Go to `Run -> Run Configurations... -> Arguments` and add `-ea` to the VM arguments.
4.  **Run the Main Class:** Locate and run the `CCRMApplication.java` file.

---

## 3. Core Java Concepts Explained

### 3.1. Evolution of Java
- **1995:** Java 1.0 is released by Sun Microsystems.
- **1998:** J2SE 1.2 introduces the Collections Framework.
- **2004:** J2SE 5.0 (Tiger) brings major features like Generics, Enums, and Annotations.
- **2014:** Java SE 8, a landmark release, introduces Lambda Expressions, the Stream API, and a new Date/Time API.
- **2018:** Java SE 11 is released as a Long-Term Support (LTS) version.
- **2021:** Java SE 17 becomes the latest LTS version, introducing features like Sealed Classes.

### 3.2. Java ME vs. SE vs. EE

| Platform | Target Environment | Key Use Case |
| :--- | :--- | :--- |
| **Java ME** (Micro) | Resource-constrained devices (IoT, old phones) | Embedded systems, legacy mobile apps. |
| **Java SE** (Standard) | Desktops, servers, consoles | General-purpose programming. **This project uses SE.** |
| **Java EE** (Enterprise) | Large-scale, distributed systems | Web servers, enterprise applications (JSP, Servlets). |

### 3.3. Java Architecture: JDK vs. JRE vs. JVM

- **JVM (Java Virtual Machine):** An abstract machine that executes Java bytecode. It's the component that makes Java "platform-independent."
- **JRE (Java Runtime Environment):** The software package needed to *run* Java applications. It contains the JVM and core libraries.
- **JDK (Java Development Kit):** The full development kit needed to *write* and *run* Java applications. It includes the JRE, the compiler (`javac`), the debugger (`jdb`), and other tools.

**Interaction:** A developer uses the **JDK** to write and compile `.java` source code into `.class` bytecode. A user only needs the **JRE** to run that bytecode, which is executed by the **JVM**.

---

## 4. Setup and Installation Screenshots

*(This is where you will insert your own screenshots)*

### 4.1. Java Installation Verification
****

### 4.2. IDE Project Setup
****

---

## 5. Topic to Code Mapping (For Evaluation)

| Syllabus Topic | File / Method Where Demonstrated |
| :--- | :--- |
| **Packages** | Entire project structure, e.g., `edu.ccrm.domain`, `edu.ccrm.service`. |
| **OOP: Encapsulation** | `Person.java` (private/protected fields with public getters/setters). |
| **OOP: Inheritance** | `Student.java` and `Instructor.java` extend the abstract `Person.java`. |
| **OOP: Abstraction**| `Person.java` is an `abstract` class with an `abstract` method `getProfileDetails()`. |
| **OOP: Polymorphism**| `ConsoleManager.printTranscript()`: A `Person` reference is used to call `getProfileDetails()` on a `Student` object, invoking the subclass's overridden method. |
| **Design Pattern: Singleton** | `AppConfig.java`: Eagerly initialized Singleton for global configuration. |
| **Design Pattern: Builder** | `CourseBuilder.java`: Fluent builder for constructing `Course` objects. |
| **Exception: Custom** | `MaxCreditLimitExceededException.java`, `DuplicateEnrollmentException.java`. |
| **Exception: Handling** | `ConsoleManager.enrollStudent()`: `try-catch` block handles multiple custom exceptions. |
| **File I/O: NIO.2** | `BackupService.java`: Uses `Path`, `Paths`, `Files.copy`, `Files.createDirectories`. |
| **Streams API** | `EnrollmentService.enrollStudent()`: Uses a stream pipeline with `anyMatch()` to check for duplicates. |
| **Date/Time API** | `Enrollment.java` uses `LocalDate.now()` for the enrollment date. |
| **Interfaces** | `IStudentService.java` defines the contract for student operations. |
| **Enums with Fields** | `Grade.java`: Enum with a `gradePoint` field and a constructor. |
| **Lambdas** | `EnrollmentService.assignGrade()`: Uses a lambda expression with `ifPresentOrElse`. |
| **Recursion** | `RecursiveFileUtil.java`: The `getDirectorySize` method recursively calculates directory size using `Files.walkFileTree`. |
| **Arrays & Utilities** | `ConsoleManager.printTranscript()`: Demonstrates `Arrays.sort()` and `Arrays.toString()` on an array of course codes. |
| **Assertions** | `EnrollmentService.enrollStudent()`: `assert currentCredits >= 0` checks for an impossible internal state. |
| **Labeled Jump** | `ConsoleManager.start()`: A `break mainLoop;` is used to exit the main application loop. |