package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulateView extends JPanel implements ThemeManager.ThemeObserver {
    private JComboBox<String> dataGenDropdown, algoDropdown, speedDropdown;
    private JSpinner frameSizeSpinner;
    private JTextField pageRefField;
    private JButton actionBtn, redoBtn, resetBtn, outputBtn, backBtn;
    private SimulationBoard simulationBoard;
    private JLabel frameLabel, speedLabel, totalFaultsLabel, stepsLabel;
    private JScrollPane scrollPane;

    // Track states locally so html strings can re-evaluate on theme swap
    private int currentFaults = 0;
    private int currentSteps = 0;

    public SimulateView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();       // 1. Build physical layout first
        applyTheme();           // 2. Apply explicit colors & fonts

        // Listen for SettingsDialog updates
        ThemeManager.addObserver(this);
    }

    private void initComponents() {
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel g1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        String[] dataGenOptions = {"User-Defined Input", "Random", "From a .txt file"};
        dataGenDropdown = new JComboBox<>(dataGenOptions);
        g1.add(dataGenDropdown);

        JPanel g2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        String[] algos = {
            "FIFO: First-In, First-Out", "LRU: Least Recently Used",
            "OPT: Optimal", "SC: Second Chance", "ESC: Enhanced Second Chance",
            "LFU: Least Frequently Used", "MFU: Most Frequently Used"
        };
        algoDropdown = new JComboBox<>(algos);
        g2.add(algoDropdown);

        JPanel g3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        frameLabel = new JLabel("Page Frames: ");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 3, 10, 1);
        frameSizeSpinner = new JSpinner(spinnerModel);
        g3.add(frameLabel);
        g3.add(frameSizeSpinner);

        row1.add(g1, BorderLayout.WEST);
        row1.add(g2, BorderLayout.CENTER);
        row1.add(g3, BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout(5, 0));
        pageRefField = new PlaceholderTextField("e.g., 7,0,1,2,0,3,0,4,2,3");
        applyRealTimeFilter(pageRefField);

        actionBtn = new JButton("▶");
        actionBtn.setFocusPainted(false);

        row2.add(pageRefField, BorderLayout.CENTER);
        row2.add(actionBtn, BorderLayout.EAST);

        topWrapper.add(row1);
        topWrapper.add(row2);
        topWrapper.add(Box.createVerticalStrut(15));
        add(topWrapper, BorderLayout.NORTH);

        simulationBoard = new SimulationBoard();
        scrollPane = new JScrollPane(simulationBoard);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        totalFaultsLabel = new JLabel();
        stepsLabel = new JLabel();
        speedLabel = new JLabel();

        String[] speeds = {"0.5x", "1.0x", "2.0x", "3.0x", "4.0x", "5.0x"};
        speedDropdown = new JComboBox<>(speeds);
        speedDropdown.setSelectedItem("1.0x");

        bottomLeft.add(totalFaultsLabel);
        bottomLeft.add(stepsLabel);
        bottomLeft.add(speedLabel);
        bottomLeft.add(speedDropdown);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        redoBtn = createIconButton("/images/redo.png", "Redo");
        resetBtn = createIconButton("/images/reset.png", "Reset");
        outputBtn = createIconButton("/images/export.png", "Output Algorithm");
        backBtn = createIconButton("/images/back.png", "Back to Dashboard");

        bottomRight.add(redoBtn);
        bottomRight.add(resetBtn);
        bottomRight.add(outputBtn);
        bottomRight.add(backBtn);

        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(bottomRight, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * This method is called by the ThemeManager's observer pattern.
     */
    @Override
    public void onThemeChanged() {
        applyTheme();
    }

    /**
     * Applies Backgrounds, Fonts, Borders, and HTML texts dynamically based on Settings
     */
    private void applyTheme() {
        // Recursively update all inner JPanels backgrounds
        updatePanelBackgrounds(this, ThemeManager.getBackgroundColor());

        // Fonts
        Font primaryFont = new Font("Arial", Font.BOLD, ThemeManager.getPrimaryFontSize());
        dataGenDropdown.setFont(primaryFont);
        algoDropdown.setFont(primaryFont);
        frameLabel.setFont(primaryFont);
        frameSizeSpinner.setFont(primaryFont);
        pageRefField.setFont(primaryFont);
        actionBtn.setFont(primaryFont);

        Font secondaryFont = new Font("Arial", Font.PLAIN, ThemeManager.getSecondaryFontSize());
        speedDropdown.setFont(secondaryFont);
        totalFaultsLabel.setFont(secondaryFont);
        stepsLabel.setFont(secondaryFont);
        speedLabel.setFont(secondaryFont);

        Font tertiaryFont = new Font("Arial", Font.BOLD, ThemeManager.getTertiaryFontSize());
        redoBtn.setFont(tertiaryFont);
        resetBtn.setFont(tertiaryFont);
        outputBtn.setFont(tertiaryFont);
        backBtn.setFont(tertiaryFont);

        // Colors & Custom Borders
        frameLabel.setForeground(ThemeManager.getTextColor());
        dataGenDropdown.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2));
        algoDropdown.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentBlue(), 2));
        frameSizeSpinner.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2));
        pageRefField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        actionBtn.setBackground(ThemeManager.getAccentBlue());
        actionBtn.setForeground(Color.WHITE); // Play Icon is always white
        actionBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2));
        scrollPane.getViewport().setBackground(ThemeManager.getBackgroundColor());

        redoBtn.setForeground(ThemeManager.getTextColor());
        resetBtn.setForeground(ThemeManager.getTextColor());
        outputBtn.setForeground(ThemeManager.getTextColor());
        backBtn.setForeground(ThemeManager.getTextColor());

        // Refresh HTML string labels
        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        speedLabel.setText("<html><b style='color:" + textHex + ";'>Speed:</b></html>");
        updateFaults(currentFaults);
        updateSteps(currentSteps);

        // Re-paint simulation board
        simulationBoard.setBackground(ThemeManager.getBackgroundColor());
        simulationBoard.repaint();
    }

    // Helper to deeply change JPanel backgrounds instantly
    private void updatePanelBackgrounds(Container container, Color bg) {
        if (container instanceof JPanel) {
            container.setBackground(bg);
        }
        for (Component c : container.getComponents()) {
            if (c instanceof Container) updatePanelBackgrounds((Container) c, bg);
        }
    }

    public void updateFaults(int faults) {
        this.currentFaults = faults;
        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        String redHex = ThemeManager.colorToHex(ThemeManager.getErrorRed());
        totalFaultsLabel.setText("<html><b style='color:" + textHex + ";'>Total Page Fault:</b> <span style='color:" + redHex + ";'>" + faults + "</span></html>");
    }

    public void updateSteps(int steps) {
        this.currentSteps = steps;
        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        stepsLabel.setText("<html><b style='color:" + textHex + ";'>Steps:</b> <span style='color:" + textHex + ";'>" + steps + "</span></html>");
    }

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
                String sanitized = newText.replaceAll("\\s+", "");
                if (!sanitized.matches("[0-9,]*")) { Toolkit.getDefaultToolkit().beep(); return; }
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String simulatedResult = currentText.substring(0, offset) + sanitized + currentText.substring(offset + length);
                long commaCount = simulatedResult.chars().filter(ch -> ch == ',').count();
                if (commaCount > 39) { Toolkit.getDefaultToolkit().beep(); return; }
                fb.replace(offset, length, sanitized, attrs);
            }
        });
    }

    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        public PlaceholderTextField(String placeholder) { this.placeholder = placeholder; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ThemeManager.getPlaceholderColor());
                g2d.setFont(new Font("Arial", Font.ITALIC, ThemeManager.getPrimaryFontSize()));
                int textY = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
                g2d.drawString(placeholder, getInsets().left, textY);
            }
        }
    }

    private JButton createIconButton(String imagePath, String text) {
        JButton btn = new JButton(text);
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
            }
        } catch (Exception e) {}
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    public static class SimulationBoard extends JPanel {
        private final List<StepData> steps = new ArrayList<>();
        private int frameSize = 4;

        public SimulationBoard() {
            setBackground(ThemeManager.getBackgroundColor());
        }

        public void clear() {
            steps.clear();
            revalidate();
            repaint();
        }

        public void setFrameSize(int size) { this.frameSize = size; }

        public void addStep(int page, int[] frames, boolean isHit) {
            steps.add(new StepData(page, frames, isHit));
            int requiredWidth = Math.max(800, steps.size() * 35 + 40);
            int requiredHeight = 100 + (frameSize * 35);
            setPreferredSize(new Dimension(requiredWidth, requiredHeight));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int startX = 20;
            int startY = 30;
            int colWidth = 35;
            int rowHeight = 35;

            for (int i = 0; i < steps.size(); i++) {
                StepData step = steps.get(i);
                int currentX = startX + i * colWidth;

                g2d.setColor(ThemeManager.getTextColor());
                g2d.setFont(new Font("Arial", Font.BOLD, 22));
                FontMetrics fm = g2d.getFontMetrics();
                String pageStr = String.valueOf(step.page);
                int textX = currentX + (30 - fm.stringWidth(pageStr)) / 2;
                g2d.drawString(pageStr, textX, startY);

                g2d.setFont(new Font("Arial", Font.PLAIN, 20));
                for (int f = 0; f < frameSize; f++) {
                    int boxY = startY + 15 + f * rowHeight;
                    g2d.setColor(ThemeManager.getAccentBlue());
                    g2d.drawRect(currentX, boxY, 30, 30);

                    if (step.frames[f] != -1) {
                        g2d.setColor(ThemeManager.getAccentOrange());
                        String frameStr = String.valueOf(step.frames[f]);
                        int fTextX = currentX + (30 - fm.stringWidth(frameStr)) / 2;
                        g2d.drawString(frameStr, fTextX, boxY + 23);
                    }
                }

                int statusY = startY + 15 + frameSize * rowHeight + 20;
                g2d.setStroke(new BasicStroke(3));
                if (step.isHit) {
                    g2d.setColor(ThemeManager.getAccentBlue());
                    g2d.drawOval(currentX + 5, statusY, 20, 20);
                } else {
                    g2d.setColor(ThemeManager.getErrorRed());
                    g2d.drawLine(currentX + 5, statusY, currentX + 25, statusY + 20);
                    g2d.drawLine(currentX + 25, statusY, currentX + 5, statusY + 20);
                }
                g2d.setStroke(new BasicStroke(1));
            }
        }
    }

    private static class StepData {
        int page;
        int[] frames;
        boolean isHit;
        public StepData(int page, int[] frames, boolean isHit) {
            this.page = page;
            this.frames = frames.clone();
            this.isHit = isHit;
        }
    }

    // --- GETTERS for MainController ---
    public JComboBox<String> getDataGenDropdown() { return dataGenDropdown; }
    public JComboBox<String> getAlgoDropdown() { return algoDropdown; }
    public JTextField getPageRefField() { return pageRefField; }
    public JButton getActionBtn() { return actionBtn; }
    public JSpinner getFrameSizeSpinner() { return frameSizeSpinner; }
    public JComboBox<String> getSpeedDropdown() { return speedDropdown; }
    public JButton getRedoBtn() { return redoBtn; }
    public JButton getResetBtn() { return resetBtn; }
    public JButton getOutputBtn() { return outputBtn; }
    public JButton getBackBtn() { return backBtn; }
    public SimulationBoard getSimulationBoard() { return simulationBoard; }
    public JScrollPane getScrollPane() { return scrollPane; }
}