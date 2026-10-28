package view;

import model.ExamSlot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class SlotPanel extends JPanel {

    private final JTextField dateField = new JTextField(10);
    private final JTextField startField = new JTextField(7);
    private final JTextField endField = new JTextField(7);

    private final ArrayList<ExamSlot> slots = new ArrayList<>();

    private final DefaultTableModel model =
        new DefaultTableModel(
            new String[]{"Date", "Start Time", "End Time"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable table = new JTable(model);

    public SlotPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(243, 246, 251));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel("Time Slot Management");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(new Color(25, 42, 86));

        JLabel subtitle = new JLabel(
            "Add available examination dates and timings."
        );

        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));
        top.setOpaque(false);
        top.add(heading);
        top.add(subtitle);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Add Time Slot"));

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Start (HH:mm):"));
        form.add(startField);

        form.add(new JLabel("End (HH:mm):"));
        form.add(endField);

        JButton addButton = createButton("Add Slot");
        JButton deleteButton = createButton("Delete Selected");
        JButton clearButton = createButton("Clear Fields");

        form.add(addButton);
        form.add(deleteButton);
        form.add(clearButton);

        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(25, 42, 86));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13)
        );

        JPanel center = new JPanel(new BorderLayout(12, 15));
        center.setOpaque(false);
        center.add(form, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        addButton.addActionListener(e -> addSlot());
        deleteButton.addActionListener(e -> deleteSlot());
        clearButton.addActionListener(e -> clearFields());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(55, 105, 190));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void addSlot() {
        String date = dateField.getText().trim();
        String start = startField.getText().trim();
        String end = endField.getText().trim();

        if (date.isEmpty() || start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Please fill in all fields."
            );
            return;
        }

        try {
            LocalDate.parse(date);
            LocalTime startTime = LocalTime.parse(start);
            LocalTime endTime = LocalTime.parse(end);

            if (!startTime.isBefore(endTime)) {
                JOptionPane.showMessageDialog(
                    this, "End time must be after start time."
                );
                return;
            }

            for (ExamSlot slot : slots) {
                if (slot.getDate().equals(date)
                    && startTime.isBefore(LocalTime.parse(slot.getEndTime()))
                    && endTime.isAfter(LocalTime.parse(slot.getStartTime()))) {

                    JOptionPane.showMessageDialog(
                        this, "This time overlaps with an existing slot."
                    );
                    return;
                }
            }

            ExamSlot slot = new ExamSlot(date, start, end);
            slots.add(slot);
            model.addRow(new Object[]{date, start, end});
            clearFields();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid format. Date: YYYY-MM-DD, Time: HH:mm."
            );
        }
    }

    private void deleteSlot() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                this, "Please select a slot to delete."
            );
            return;
        }

        slots.remove(row);
        model.removeRow(row);
        clearFields();
    }

    private void clearFields() {
        dateField.setText("");
        startField.setText("");
        endField.setText("");
        table.clearSelection();
    }
    public java.util.List<model.ExamSlot> getSlots() {
    return new java.util.ArrayList<>(slots);
}
}