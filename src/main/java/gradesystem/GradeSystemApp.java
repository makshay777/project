package gradesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Desktop interface for entering students and viewing their grade report. */
public class GradeSystemApp extends JFrame {
    private static final int MAX_STUDENTS = 5;
    private static final Color BACKGROUND = new Color(245, 247, 251);
    private static final Color NAVY = new Color(24, 39, 75);
    private static final Color MUTED = new Color(111, 123, 145);
    private static final Color BLUE = new Color(63, 105, 225);
    private static final Color BORDER = new Color(229, 233, 241);

    private final List<Student> students = new ArrayList<>();
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField marksField = new JTextField();
    private final JLabel statusLabel = new JLabel("Enter student details to get started.");
    private final JButton addButton = new JButton("Add student");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] {"STUDENT ID", "STUDENT NAME", "MARKS / 100", "GRADE"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private GradeSystemApp() {
        super("Student Gradebook");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(780, 680));
        setSize(980, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());

        JPanel page = new JPanel();
        page.setBackground(BACKGROUND);
        page.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));
        page.setLayout(new javax.swing.BoxLayout(page, javax.swing.BoxLayout.Y_AXIS));
        page.add(createHeader());
        page.add(BoxGap.vertical(22));
        page.add(createEntryCard());
        page.add(BoxGap.vertical(22));
        page.add(createReportCard());
        add(page, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new javax.swing.BoxLayout(titles, javax.swing.BoxLayout.Y_AXIS));
        JLabel eyebrow = new JLabel("ACADEMIC OVERVIEW");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 11));
        eyebrow.setForeground(BLUE);
        JLabel title = new JLabel("Student Gradebook");
        title.setFont(new Font("SansSerif", Font.BOLD, 29));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("Add student marks and calculate grades.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        titles.add(eyebrow);
        titles.add(BoxGap.vertical(5));
        titles.add(title);
        titles.add(BoxGap.vertical(5));
        titles.add(subtitle);

        header.add(titles, BorderLayout.CENTER);
        return header;
    }

    private JPanel createEntryCard() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(19, 21, 17, 21)));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Add a student");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(NAVY);
        JLabel hint = new JLabel("Up to five students");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hint.setForeground(MUTED);
        heading.add(title, BorderLayout.WEST);
        heading.add(hint, BorderLayout.EAST);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addField(fields, "STUDENT ID", idField, 0);
        addField(fields, "FULL NAME", nameField, 1);
        addField(fields, "MARKS (0–100)", marksField, 2);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);
        JButton clearButton = new JButton("Reset all");
        styleSecondary(clearButton);
        addButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        addButton.setForeground(Color.BLACK);
        addButton.setBackground(BLUE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        addButton.addActionListener(event -> addStudent());
        clearButton.addActionListener(event -> resetAll());
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(addButton);
        actions.add(statusLabel, BorderLayout.CENTER);
        actions.add(buttons, BorderLayout.EAST);

        card.add(heading, BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void addField(JPanel parent, String labelText, JTextField field, int column) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 7));
        wrapper.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(NAVY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, column == 0 ? 0 : 10, 0, column == 2 ? 0 : 10);
        parent.add(wrapper, constraints);
    }

    private JPanel createReportCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(19, 21, 19, 21)));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel("Student grade report");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(NAVY);
        JLabel gradeScale = new JLabel("A ≥ 90   B ≥ 80   C ≥ 70   D ≥ 60   F < 60");
        gradeScale.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gradeScale.setForeground(MUTED);
        heading.add(title, BorderLayout.WEST);
        heading.add(gradeScale, BorderLayout.EAST);

        JTable table = new JTable(tableModel);
        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(NAVY);
        table.setGridColor(new Color(239, 242, 247));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setSelectionBackground(new Color(235, 241, 255));
        table.setSelectionForeground(NAVY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setBackground(new Color(248, 250, 253));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        table.getColumnModel().getColumn(0).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setPreferredWidth(310);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(new ReportCellRenderer(column));
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 220));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(heading, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void addStudent() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            double marks = Double.parseDouble(marksField.getText().trim());
            if (students.size() >= MAX_STUDENTS) {
                setStatus("The class list is full. Reset to start another report.", new Color(190, 104, 24));
                return;
            }
            for (Student student : students) {
                if (student.getId() == id) {
                    throw new IllegalArgumentException("That student ID is already in the report.");
                }
            }
            Student student = new Student(name, id, marks);
            students.add(student);
            tableModel.addRow(new Object[] {
                student.getId(), student.getName(), String.format("%.2f", student.getMarks()), student.calculateGrade()
            });
            clearFields();
            setStatus("Added " + student.getName() + " to the grade report.", new Color(25, 133, 105));
            if (students.size() == MAX_STUDENTS) {
                addButton.setEnabled(false);
            }
            idField.requestFocusInWindow();
        } catch (NumberFormatException exception) {
            setStatus("Enter a whole-number ID and valid numeric marks.", new Color(190, 66, 66));
        } catch (IllegalArgumentException exception) {
            setStatus(exception.getMessage(), new Color(190, 66, 66));
        }
    }

    private void resetAll() {
        students.clear();
        tableModel.setRowCount(0);
        addButton.setEnabled(true);
        clearFields();
        setStatus("Report cleared. Enter student details to get started.", MUTED);
        idField.requestFocusInWindow();
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        marksField.setText("");
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void styleSecondary(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(NAVY);
        button.setBackground(new Color(246, 248, 252));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
    }

    private static class ReportCellRenderer extends DefaultTableCellRenderer {
        private final int column;

        private ReportCellRenderer(int column) {
            this.column = column;
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            if (column == 0 || column == 2 || column == 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                boolean focused, int row, int columnIndex) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, columnIndex);
            if (!selected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 251, 253));
                setForeground(NAVY);
            }
            if (column == 3 && value != null) {
                setFont(new Font("SansSerif", Font.BOLD, 13));
                if (!selected) {
                    char grade = value.toString().charAt(0);
                    setForeground(grade == 'F' ? new Color(196, 68, 68) : new Color(32, 142, 105));
                }
            } else {
                setFont(new Font("SansSerif", column == 1 ? Font.BOLD : Font.PLAIN, 13));
            }
            return this;
        }
    }

    private static class BoxGap {
        private static Component vertical(int height) {
            return javax.swing.Box.createRigidArea(new Dimension(0, height));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep Swing's default look and feel if the system theme is unavailable.
            }
            new GradeSystemApp().setVisible(true);
        });
    }
}
