package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Color navy = new Color(25, 42, 86);
    private final Color blue = new Color(55, 105, 190);
    private final Color background = new Color(243, 246, 251);

    public MainFrame() {

        setTitle("Smart Timetable Generator");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(background);

        // ================= HEADER =================

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(navy);
        header.setBorder(
            BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        JLabel title = new JLabel("SMART TIMETABLE GENERATOR");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
            "AOA Major Project | Graph Coloring & Backtracking"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 230, 250));

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        add(header, BorderLayout.NORTH);

        // ================= DASHBOARD =================

        JPanel dashboard = createDashboard();

        // ================= FEATURE PANELS =================

        SubjectPanel subjectPanel = new SubjectPanel();

        SlotPanel slotPanel = new SlotPanel();

        ConflictPanel conflictPanel =
            new ConflictPanel(subjectPanel);

        TimetablePanel timetablePanel =
            new TimetablePanel(
                subjectPanel,
                slotPanel,
                conflictPanel
            );

        // ================= NAVIGATION TABS =================

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Dashboard", dashboard);
        tabs.addTab("Subjects", subjectPanel);
        tabs.addTab("Time Slots", slotPanel);
        tabs.addTab("Conflicts", conflictPanel);
        tabs.addTab("Timetable", timetablePanel);

        add(tabs, BorderLayout.CENTER);

        // ================= FOOTER =================

        JLabel footer = new JLabel(
            "Smart Timetable Generator | Java Swing | AOA"
        );

        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        footer.setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );

        add(footer, BorderLayout.SOUTH);
    }

    // ================= DASHBOARD DESIGN =================

    private JPanel createDashboard() {

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBackground(background);
        panel.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JPanel welcome = new JPanel();
        welcome.setLayout(new BoxLayout(welcome, BoxLayout.Y_AXIS));
        welcome.setOpaque(false);

        JLabel heading = new JLabel("Welcome!");
        heading.setFont(new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(navy);

        JLabel description = new JLabel(
            "Create a conflict-free examination timetable automatically."
        );
        description.setFont(new Font("SansSerif", Font.PLAIN, 15));

        welcome.add(heading);
        welcome.add(Box.createVerticalStrut(10));
        welcome.add(description);

        panel.add(welcome, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 18));
        cards.setOpaque(false);

        cards.add(createCard(
            "01",
            "Manage Subjects",
            "Add and manage examination subjects."
        ));

        cards.add(createCard(
            "02",
            "Manage Time Slots",
            "Set exam dates and timings."
        ));

        cards.add(createCard(
            "03",
            "Generate Timetable",
            "Resolve scheduling conflicts automatically."
        ));

        panel.add(cards, BorderLayout.CENTER);

        JLabel algorithm = new JLabel(
            "Algorithm: Graph Coloring + Recursive Backtracking"
        );

        algorithm.setFont(new Font("SansSerif", Font.BOLD, 14));
        algorithm.setForeground(navy);

        panel.add(algorithm, BorderLayout.SOUTH);

        return panel;
    }

    // ================= DASHBOARD CARDS =================

    private JPanel createCard(
        String number,
        String heading,
        String description
    ) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(220, 226, 238)
            ),
            BorderFactory.createEmptyBorder(25, 18, 25, 18)
        ));

        JLabel numberLabel = new JLabel(number);
        numberLabel.setFont(
            new Font("SansSerif", Font.BOLD, 30)
        );
        numberLabel.setForeground(blue);

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(
            new Font("SansSerif", Font.BOLD, 17)
        );
        headingLabel.setForeground(navy);

        JLabel descriptionLabel = new JLabel(
            "<html><div style='width:190px;'>"
            + description
            + "</div></html>"
        );

        descriptionLabel.setFont(
            new Font("SansSerif", Font.PLAIN, 13)
        );
        descriptionLabel.setForeground(Color.DARK_GRAY);

        card.add(numberLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(headingLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(descriptionLabel);

        return card;
    }
}