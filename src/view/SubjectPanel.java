package view;

import database.SubjectDAO;
import model.Subject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SubjectPanel extends JPanel {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    private final JTextField codeField = new JTextField(12);
    private final JTextField nameField = new JTextField(18);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Subject Code", "Subject Name"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable subjectTable = new JTable(tableModel);

    public SubjectPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel heading = new JLabel("Manage Subjects");
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        add(heading, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Subject Details"));

        formPanel.add(new JLabel("Subject Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Subject Name:"));
        formPanel.add(nameField);

        JButton addButton = new JButton("Add Subject");
        JButton editButton = new JButton("Edit Subject");
        JButton deleteButton = new JButton("Delete Subject");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        formPanel.add(buttonPanel);
        formPanel.add(new JLabel("Select a row to edit or delete."));

        subjectTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        subjectTable.setRowHeight(25);
        subjectTable.getTableHeader().setReorderingAllowed(false);

        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = subjectTable.getSelectedRow();

                if (row >= 0) {
                    codeField.setText(
                            tableModel.getValueAt(row, 0).toString());
                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString());
                }
            }
        });

        add(formPanel, BorderLayout.CENTER);
        add(new JScrollPane(subjectTable), BorderLayout.SOUTH);

        // Make the table occupy the main available area.
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        remove(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> addSubject());
        editButton.addActionListener(e -> editSubject());
        deleteButton.addActionListener(e -> deleteSubject());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadSubjects());

        loadSubjects();
    }

    private void addSubject() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter both subject code and name.");
            return;
        }

        boolean success = subjectDAO.addSubject(new Subject(code, name));

        if (success) {
            JOptionPane.showMessageDialog(
                    this, "Subject added successfully!");
            clearFields();
            loadSubjects();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Subject could not be added. Check the database connection "
                            + "and ensure the subject code is unique.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSubject() {
        int row = subjectTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Please select a subject to edit.");
            return;
        }

        String oldCode = tableModel.getValueAt(row, 0).toString();
        String newCode = codeField.getText().trim();
        String newName = nameField.getText().trim();

        if (newCode.isEmpty() || newName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Subject code and name cannot be empty.");
            return;
        }

        boolean success =
                subjectDAO.updateSubject(oldCode, newCode, newName);

        if (success) {
            JOptionPane.showMessageDialog(
                    this, "Subject updated successfully!");
            clearFields();
            loadSubjects();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Subject could not be updated. Check the DAO and database.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int row = subjectTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this, "Please select a subject to delete.");
            return;
        }

        String code = tableModel.getValueAt(row, 0).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete subject " + code + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            boolean success = subjectDAO.deleteSubject(code);

            if (success) {
                JOptionPane.showMessageDialog(
                        this, "Subject deleted successfully!");
                clearFields();
                loadSubjects();
            } else {
                JOptionPane.showMessageDialog(
                        this, "Subject could not be deleted.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);

        List<Subject> subjects = subjectDAO.getAllSubjects();

        for (Subject subject : subjects) {
            tableModel.addRow(new Object[]{
                    subject.getCode(),
                    subject.getName()
            });
        }
    }

    private void clearFields() {
        codeField.setText("");
        nameField.setText("");
        subjectTable.clearSelection();
    }

    public List<Subject> getSubjects() {
        return subjectDAO.getAllSubjects();
    }
}