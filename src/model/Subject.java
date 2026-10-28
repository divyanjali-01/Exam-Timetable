package model;

public class Subject {

    private String code;
    private String name;

    // Default constructor
    public Subject() {
    }

    // Parameterized constructor
    public Subject(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Get subject code
    public String getCode() {
        return code;
    }

    // Set subject code
    public void setCode(String code) {
        this.code = code;
    }

    // Get subject name
    public String getName() {
        return name;
    }

    // Set subject name
    public void setName(String name) {
        this.name = name;
    }

    // Display subject details
    @Override
    public String toString() {
        return code + " - " + name;
    }
}