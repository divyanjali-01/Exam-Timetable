package view;

import model.Subject;
import model.Conflict;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ConflictPanel extends JPanel {

    private final SubjectPanel subjectPanel;

    private final JComboBox<Subject> subjectA = new JComboBox<>();
    private final JComboBox<Subject> subjectB = new JComboBox<>();

    private final List<Conflict> conflicts = new ArrayList<>();

    private final DefaultTableModel tableModel =
        new DefaultTableModel(new String[]{"Conflict Rules"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable table = new JTable(tableModel);

    public ConflictPanel(SubjectPanel subjectPanel) {
        this.subjectPanel = subjectPanel;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(243, 246, 251));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("Conflict Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(new Color(25, 42, 86));

        JLabel subtitle = new JLabel(
            "Select two subjects that must not share an exam slot."
        );

        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));
        top.setOpaque(false);
        top.add(heading);
        top.add(subtitle);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            "Define Subject Conflict"
        ));

        JButton refreshButton = createButton("Refresh Subjects");
        JButton addButton = createButton("Add Conflict");
        JButton deleteButton = createButton("Delete Selected");

        form.add(new JLabel("Subject A:"));
        form.add(subjectA);
        form.add(new JLabel("Subject B:"));
        form.add(subjectB);
        form.add(refreshButton);
        form.add(addButton);
        form.add(deleteButton);

        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);

        JPanel center = new JPanel(new BorderLayout(12, 15));
        center.setOpaque(false);
        center.add(form, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> refreshSubjects());
        addButton.addActionListener(e -> addConflict());
        deleteButton.addActionListener(e -> deleteConflict());

        refreshSubjects();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(55, 105, 190));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void refreshSubjects() {
        Subject selectedA = (Subject) subjectA.getSelectedItem();
        Subject selectedB = (Subject) subjectB.getSelectedItem();

        subjectA.removeAllItems();
        subjectB.removeAllItems();

        for (Subject subject : subjectPanel.getSubjects()) {
            subjectA.addItem(subject);
            subjectB.addItem(subject);
        }

        if (selectedA != null) {
            selectSubject(subjectA, selectedA.getCode());
        }

        if (selectedB != null) {
            selectSubject(subjectB, selectedB.getCode());
        }
    }

    private void selectSubject(
        JComboBox<Subject> combo, String code
    ) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Subject subject = combo.getItemAt(i);

            if (subject.getCode().equalsIgnoreCase(code)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void addConflict() {
        Subject a = (Subject) subjectA.getSelectedItem();
        Subject b = (Subject) subjectB.getSelectedItem();

        if (a == null || b == null) {
            JOptionPane.showMessageDialog(
                this, "Please add at least two subjects first."
            );
            return;
        }

        if (a.getCode().equalsIgnoreCase(b.getCode())) {
            JOptionPane.showMessageDialog(
                this, "Select two different subjects."
            );
            return;
        }

        for (Conflict conflict : conflicts) {
            boolean samePair =
                (conflict.getSubjectA().getCode()
                    .equalsIgnoreCase(a.getCode())
                && conflict.getSubjectB().getCode()
                    .equalsIgnoreCase(b.getCode()))
                ||
                (conflict.getSubjectA().getCode()
                    .equalsIgnoreCase(b.getCode())
                && conflict.getSubjectB().getCode()
                    .equalsIgnoreCase(a.getCode()));

            if (samePair) {
                JOptionPane.showMessageDialog(
                    this, "This conflict rule already exists."
                );
                return;
            }
        }

        Conflict conflict = new Conflict(a, b);
        conflicts.add(conflict);
        tableModel.addRow(new Object[]{conflict.toString()});
    }

    private void deleteConflict() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                this, "Please select a conflict to delete."
            );
            return;
        }

        conflicts.remove(row);
        tableModel.removeRow(row);
    }

    public List<Conflict> getConflicts() {
        return new ArrayList<>(conflicts);
    }
}