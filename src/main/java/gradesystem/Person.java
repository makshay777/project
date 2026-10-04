package gradesystem;

/** Common person details shared by everyone in the grading system. */
public abstract class Person {
    private String name;
    private int id;

    protected Person(String name, int id) {
        setName(name);
        setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student ID must be a positive number.");
        }
        this.id = id;
    }

    public abstract char calculateGrade();
}
