package algorithm;

import model.Subject;
import model.ExamSlot;
import model.Conflict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BacktrackingScheduler {

    private final List<Subject> subjects;
    private final List<ExamSlot> slots;
    private final List<Conflict> conflicts;

    private final Map<String, Integer> assignment;
    private final Map<String, Subject> subjectMap;

    public BacktrackingScheduler(
            List<Subject> subjects,
            List<ExamSlot> slots,
            List<Conflict> conflicts) {

        this.subjects = subjects;
        this.slots = slots;
        this.conflicts = conflicts;

        this.assignment = new HashMap<>();
        this.subjectMap = new HashMap<>();

        for (Subject subject : subjects) {
            subjectMap.put(subject.getCode(), subject);
        }
    }

    public Map<String, ExamSlot> generateTimetable() {

        if (subjects == null || slots == null || conflicts == null
                || subjects.isEmpty() || slots.isEmpty()) {
            return null;
        }

        // Every subject needs a distinct slot.
        if (slots.size() < subjects.size()) {
            return null;
        }

        assignment.clear();

        if (!solve()) {
            return null;
        }

        Map<String, ExamSlot> result = new LinkedHashMap<>();

        for (Subject subject : subjects) {
            Integer slotIndex = assignment.get(subject.getCode());

            if (slotIndex == null) {
                return null;
            }

            result.put(subject.getCode(), slots.get(slotIndex));
        }

        return result;
    }

    private boolean solve() {

        if (assignment.size() == subjects.size()) {
            return true;
        }

        Subject current = selectNextSubject();

        if (current == null) {
            return false;
        }

        for (int slot = 0; slot < slots.size(); slot++) {

            if (isSafe(current, slot)) {

                assignment.put(current.getCode(), slot);

                if (solve()) {
                    return true;
                }

                // Backtrack if this assignment does not work.
                assignment.remove(current.getCode());
            }
        }

        return false;
    }

    private Subject selectNextSubject() {

        Subject best = null;
        int maxDegree = -1;

        for (Subject subject : subjects) {

            if (assignment.containsKey(subject.getCode())) {
                continue;
            }

            int degree = 0;

            for (Conflict conflict : conflicts) {

                if (conflict.getSubjectA().getCode()
                        .equalsIgnoreCase(subject.getCode())
                        || conflict.getSubjectB().getCode()
                        .equalsIgnoreCase(subject.getCode())) {

                    degree++;
                }
            }

            if (degree > maxDegree) {
                maxDegree = degree;
                best = subject;
            }
        }

        return best;
    }

    private boolean isSafe(Subject subject, int slot) {

        // Assign a unique slot to every subject.
        for (Map.Entry<String, Integer> entry : assignment.entrySet()) {

            if (entry.getValue() == slot) {
                return false;
            }
        }

        // Check conflicts between subjects.
        for (Conflict conflict : conflicts) {

            String a = conflict.getSubjectA().getCode();
            String b = conflict.getSubjectB().getCode();

            String other = null;

            if (a.equalsIgnoreCase(subject.getCode())) {
                other = b;
            } else if (b.equalsIgnoreCase(subject.getCode())) {
                other = a;
            }

            if (other != null
                    && assignment.containsKey(other)
                    && assignment.get(other) == slot) {

                return false;
            }
        }

        return true;
    }
}