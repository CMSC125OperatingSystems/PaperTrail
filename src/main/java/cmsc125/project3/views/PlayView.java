package cmsc125.project3.views;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class PlayView extends JPanel {
    private JButton algorithmMenuBtn, actionBtn, restartBtn, resetBtn, exportBtn, backBtn;
    private JPopupMenu algorithmPopupMenu;
    private JCheckBoxMenuItem[] algoCheckboxes;
    private JComboBox<String> dataGenDropdown, speedDropdown;
    private JSpinner frameSizeSpinner;
    private JTextField pageRefField; // Will use our custom PlaceholderTextField
    private JPanel renderingContainer;
    private JLabel timerLabel;

    public PlayView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // Top Panel (Settings & Inputs)
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));

        // Row 1: Dropdown inputs
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel dataGenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        String[] dataGenOptions = {"Random", "User-defined input", "User-defined input from a .txt file"};
        dataGenDropdown = new JComboBox<>(dataGenOptions);
        dataGenPanel.add(new JLabel("Data Generation: "));
        dataGenPanel.add(dataGenDropdown);

        // Setup Algorithm Button Menu
        algorithmMenuBtn = new JButton("Select Algorithms ▼");
        algorithmPopupMenu = new JPopupMenu();
        String[] algos = {"All", "First-In-First-Out", "Least Recently Used", "Optimal", "Second Chance", "Enhanced Second Chance", "Least Frequently Used", "Most Frequently Used"};
        algoCheckboxes = new JCheckBoxMenuItem[algos.length];

        for (int i = 0; i < algos.length; i++) {
            algoCheckboxes[i] = new JCheckBoxMenuItem(algos[i]);
            algorithmPopupMenu.add(algoCheckboxes[i]);
        }

        algorithmMenuBtn.addActionListener(e -> algorithmPopupMenu.show(algorithmMenuBtn, 0, algorithmMenuBtn.getHeight()));

        JPanel frameSizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(3, 3, 10, 1);
        frameSizeSpinner = new JSpinner(spinnerModel);
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) frameSizeSpinner.getEditor();
        spinnerEditor.getTextField().setEditable(false);
        spinnerEditor.getTextField().setColumns(2);
        frameSizePanel.add(new JLabel("Frame Size: "));
        frameSizePanel.add(frameSizeSpinner);

        // Evenly space group rows
        row1.add(dataGenPanel);
        row1.add(Box.createHorizontalGlue());
        row1.add(algorithmMenuBtn);
        row1.add(Box.createHorizontalGlue());
        row1.add(frameSizePanel);

        // Row 2: Page reference string input
        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        pageRefField = new PlaceholderTextField("e.g., 7,0,1,2,0,3,0,4,2,3");
        applyRealTimeFilter(pageRefField);

        actionBtn = new JButton("Run");
        actionBtn.setBackground(new Color(0xFF8405));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFocusPainted(false);

        row2.add(new JLabel("Page Reference String:"), BorderLayout.WEST);
        row2.add(pageRefField, BorderLayout.CENTER);
        row2.add(actionBtn, BorderLayout.EAST);

        topWrapper.add(row1);
        topWrapper.add(row2);
        topWrapper.add(Box.createVerticalStrut(15));
        add(topWrapper, BorderLayout.NORTH);

        // Center Panel (Algorithm Rendering)
        renderingContainer = new JPanel();
        renderingContainer.setLayout(new BoxLayout(renderingContainer, BoxLayout.Y_AXIS));
        renderingContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(renderingContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createTitledBorder("Algorithm Execution"));
        centerWrapper.add(scrollPane, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);

        // Bottom Panel (Controls)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timerLabel = new JLabel("Timer: 0.0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        String[] speeds = {"1x", "2x", "3x", "4x", "5x"};
        speedDropdown = new JComboBox<>(speeds);

        bottomLeft.add(timerLabel);
        bottomLeft.add(new JLabel("  Speed:"));
        bottomLeft.add(speedDropdown);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        restartBtn = createIconButton("/images/restart.png", "Restart");
        resetBtn = createIconButton("/images/reset.png", "Reset");
        exportBtn = createIconButton("/images/export.png", "Export");
        backBtn = createIconButton("/images/back.png", "Back to Dashboard");

        bottomRight.add(restartBtn);
        bottomRight.add(resetBtn);
        bottomRight.add(exportBtn);
        bottomRight.add(backBtn);

        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(bottomRight, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Apply real-time DocumentFilter to strictly allow only numbers and commas, block spaces, and prevent exceeding 40 entries.
     */
    private void applyRealTimeFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                processInput(fb, offset, 0, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                processInput(fb, offset, length, text, attrs);
            }

            private void processInput(FilterBypass fb, int offset, int length, String newText, AttributeSet attrs) throws BadLocationException {
                // Strictly remove all spaces from incoming text
                String sanitized = newText.replaceAll("\\s+", "");

                // Block if it contains anything other than digits and commas
                if (!sanitized.matches("[0-9,]*")) {
                    Toolkit.getDefaultToolkit().beep(); // Play error sound
                    return;
                }

                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String simulatedResult = currentText.substring(0, offset) + sanitized + currentText.substring(offset + length);

                // 40 entries maximum means 39 commas maximum
                long commaCount = simulatedResult.chars().filter(ch -> ch == ',').count();
                if (commaCount > 39) {
                    Toolkit.getDefaultToolkit().beep();
                    return; // Reject input if it exceeds 40 entries
                }

                // If all checks pass, allow text insertion
                fb.replace(offset, length, sanitized, attrs);
            }
        });
    }

    // Placeholder Text Field
    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Draw normal text field first

            // If field is empty, draw gray placeholder text
            if (getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.ITALIC, 12));

                // Vertically align text inside text field
                int textY = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g2d.drawString(placeholder, getInsets().left, textY);
            }
        }
    }

    private JButton createIconButton(String imagePath, String tooltip) {
        JButton btn = new JButton();
        btn.setToolTipText(tooltip);
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            } else {
                btn.setText(tooltip);
            }
        } catch (Exception e) {
            btn.setText(tooltip);
        }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    public JComboBox<String> getDataGenDropdown() { return dataGenDropdown; }
    public JTextField getPageRefField() { return pageRefField; }
    public JButton getActionBtn() { return actionBtn; }
    public JCheckBoxMenuItem[] getAlgoCheckboxes() { return algoCheckboxes; }
    public JSpinner getFrameSizeSpinner() { return frameSizeSpinner; }
    public JComboBox<String> getSpeedDropdown() { return speedDropdown; }
    public JButton getRestartBtn() { return restartBtn; }
    public JButton getResetBtn() { return resetBtn; }
    public JButton getExportBtn() { return exportBtn; }
    public JButton getBackBtn() { return backBtn; }
}