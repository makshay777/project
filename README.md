# Student Gradebook

A Java Swing desktop application based on the student grading program in the supplied images. It keeps the original grade boundaries and supports up to five students per report.

## Features

- Enter a student ID, name, and marks (0–100).
- Automatically calculate grades: A (90+), B (80+), C (70+), D (60+), and F (below 60).
- View the class report, average, passing count, and highest grade.
- Reject invalid marks and duplicate student IDs; reset to begin a new report.
- Uses the original object-oriented model: abstract `Person` and derived `Student`.

## Study notes

See [docs/Swing-Study-Notes.pdf](docs/Swing-Study-Notes.pdf) for a printable guide to the Swing and AWT classes, layout managers, event handling, table rendering, and gradebook model used in this project. The Markdown source is [docs/Swing-Study-Notes.md](docs/Swing-Study-Notes.md).

## Run with Maven

Requires JDK 11 or newer.

```text
mvn clean package
java -jar target/student-gradebook-1.0.0.jar
```

## Run without Maven

Compile the Java files from the project root, then launch `gradesystem.GradeSystemApp`:

```text
javac -d out src/main/java/gradesystem/*.java
java -cp out gradesystem.GradeSystemApp
```
