package gradesystem;

/** A student with marks and a grade calculated from those marks. */
public class Student extends Person {
    private double marks;

    public Student(String name, int id, double marks) {
        super(name, id);
        setMarks(marks);
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        if (!Double.isFinite(marks) || marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100.");
        }
        this.marks = marks;
    }

    @Override
    public char calculateGrade() {
        if (marks >= 90) return 'A';
        if (marks >= 80) return 'B';
        if (marks >= 70) return 'C';
        if (marks >= 60) return 'D';
        return 'F';
    }
}
