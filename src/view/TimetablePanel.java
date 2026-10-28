package view;

import algorithm.BacktrackingScheduler;
import model.Subject;
import model.ExamSlot;
import model.Conflict;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class TimetablePanel extends JPanel {

    private final SubjectPanel subjectPanel;
    private final SlotPanel slotPanel;
    private final ConflictPanel conflictPanel;

    private final Color navy = new Color(25, 42, 86);
    private final Color blue = new Color(55, 105, 190);
    private final Color background = new Color(243, 246, 251);

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[]{
                "Subject Code",
                "Subject Name",
                "Exam Date",
                "Start Time",
                "End Time"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable table = new JTable(model);

    private final JLabel status = new JLabel(
        "Add subjects, time slots and conflict rules first."
    );

    public TimetablePanel(
        SubjectPanel subjectPanel,
        SlotPanel slotPanel,
        ConflictPanel conflictPanel
    ) {
        this.subjectPanel = subjectPanel;
        this.slotPanel = slotPanel;
        this.conflictPanel = conflictPanel;

        setLayout(new BorderLayout(15, 15));
        setBackground(background);

        setBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        // ================= HEADER =================

        JLabel heading = new JLabel("Generated Timetable");
        heading.setFont(
            new Font("SansSerif", Font.BOLD, 25)
        );
        heading.setForeground(navy);

        JLabel subtitle = new JLabel(
            "Generate a clash-free examination timetable automatically."
        );
        subtitle.setFont(
            new Font("SansSerif", Font.PLAIN, 14)
        );

        JPanel header = new JPanel(
            new GridLayout(2, 1, 5, 5)
        );
        header.setOpaque(false);
        header.add(heading);
        header.add(subtitle);

        // ================= TABLE =================

        table.setRowHeight(32);
        table.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );

        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        table.setShowGrid(true);
        table.setGridColor(new Color(220, 226, 238));

        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(navy);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
            BorderFactory.createLineBorder(
                new Color(220, 226, 238)
            )
        );

        // ================= BUTTONS =================

        JButton generateButton =
            createButton("Generate Timetable", blue);

        JButton exportButton =
            createButton("Export CSV", new Color(25, 135, 85));

        JButton resetButton =
            createButton("Clear Timetable", new Color(190, 65, 65));

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 5)
        );
        buttons.setOpaque(false);

        buttons.add(resetButton);
        buttons.add(exportButton);
        buttons.add(generateButton);

        // ================= FOOTER / STATUS =================

        status.setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );
        status.setForeground(navy);

        JPanel footer = new JPanel(new BorderLayout(10, 10));
        footer.setOpaque(false);
        footer.add(status, BorderLayout.CENTER);
        footer.add(buttons, BorderLayout.SOUTH);

        // ================= ADD COMPONENTS =================

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        // ================= ACTION LISTENERS =================

        generateButton.addActionListener(e -> generateTimetable());

        exportButton.addActionListener(e -> exportCSV());

        resetButton.addActionListener(e -> clearTimetable());
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(
            new Font("SansSerif", Font.BOLD, 12)
        );
        button.setFocusPainted(false);
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
    }

    // ================= GENERATE TIMETABLE =================

    private void generateTimetable() {

        List<Subject> subjects = subjectPanel.getSubjects();
        List<ExamSlot> slots = slotPanel.getSlots();
        List<Conflict> conflicts = conflictPanel.getConflicts();

        model.setRowCount(0);

        if (subjects.isEmpty()) {
            showError("Please add subjects first.");
            return;
        }

        if (slots.isEmpty()) {
            showError("Please add available time slots first.");
            return;
        }

        BacktrackingScheduler scheduler =
            new BacktrackingScheduler(
                subjects,
                slots,
                conflicts
            );

        Map<String, ExamSlot> result =
            scheduler.generateTimetable();

        if (result == null) {
            status.setText("No valid timetable found.");
            status.setForeground(Color.RED);

            JOptionPane.showMessageDialog(
                this,
                "A valid timetable could not be generated.\n"
                + "Try adding more time slots or reviewing "
                + "the conflict rules.",
                "Scheduling Failed",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        for (Subject subject : subjects) {

            ExamSlot slot = result.get(subject.getCode());

            if (slot != null) {
                model.addRow(new Object[]{
                    subject.getCode(),
                    subject.getName(),
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime()
                });
            }
        }

        status.setForeground(new Color(25, 135, 85));
        status.setText(
            "Success! " + model.getRowCount()
            + " subjects scheduled without conflicts."
        );

        JOptionPane.showMessageDialog(
            this,
            "Timetable generated successfully!",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================= EXPORT CSV =================

    private void exportCSV() {

        if (model.getRowCount() == 0) {
            showError(
                "Please generate the timetable before exporting."
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Save Timetable as CSV");

        chooser.setSelectedFile(
            new File("timetable.csv")
        );

        int choice = chooser.showSaveDialog(this);

        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (PrintWriter writer =
                 new PrintWriter(new FileWriter(file))) {

            // Write column headings
            for (int col = 0; col < model.getColumnCount(); col++) {

                if (col > 0) {
                    writer.print(",");
                }

                writer.print(csvValue(
                    model.getColumnName(col)
                ));
            }

            writer.println();

            // Write timetable data
            for (int row = 0; row < model.getRowCount(); row++) {

                for (int col = 0;
                     col < model.getColumnCount();
                     col++) {

                    if (col > 0) {
                        writer.print(",");
                    }

                    writer.print(csvValue(
                        String.valueOf(
                            model.getValueAt(row, col)
                        )
                    ));
                }

                writer.println();
            }

            status.setForeground(new Color(25, 135, 85));
            status.setText("CSV exported: " + file.getName());

            JOptionPane.showMessageDialog(
                this,
                "Timetable exported successfully!\n\n"
                + file.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Unable to export CSV:\n" + ex.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Escape commas, quotes and other CSV-sensitive values
    private String csvValue(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    // ================= CLEAR TIMETABLE =================

    private void clearTimetable() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Clear the generated timetable?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            model.setRowCount(0);

            status.setForeground(navy);
            status.setText(
                "Timetable cleared. Generate it again when ready."
            );
        }
    }

    // ================= ERROR MESSAGE =================

    private void showError(String message) {

        status.setForeground(Color.RED);
        status.setText(message);

        JOptionPane.showMessageDialog(
            this,
            message,
            "Input Required",
            JOptionPane.WARNING_MESSAGE
        );
    }
}