package model;

public class Conflict {

    private final Subject subjectA;
    private final Subject subjectB;

    public Conflict(Subject subjectA, Subject subjectB) {
        this.subjectA = subjectA;
        this.subjectB = subjectB;
    }

    public Subject getSubjectA() {
        return subjectA;
    }

    public Subject getSubjectB() {
        return subjectB;
    }

    @Override
    public String toString() {
        return subjectA.getCode() + " - " + subjectA.getName()
            + "  cannot overlap with  "
            + subjectB.getCode() + " - " + subjectB.getName();
    }
}