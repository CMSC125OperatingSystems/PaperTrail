package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulateView extends JPanel {
    private JComboBox<String> dataGenDropdown, algoDropdown, speedDropdown;
    private JSpinner frameSizeSpinner;
    private JTextField pageRefField;
    private JButton actionBtn, redoBtn, resetBtn, outputBtn, backBtn;
    private SimulationBoard simulationBoard;
    private JLabel totalFaultsLabel, timerLabel;
    private JScrollPane scrollPane;

    public SimulateView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(ThemeManager.getBackgroundColor());

        initComponents();
    }

    private void initComponents() {
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));
        topWrapper.setBackground(ThemeManager.getBackgroundColor());

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(ThemeManager.getBackgroundColor());
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel g1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        g1.setBackground(ThemeManager.getBackgroundColor());
        String[] dataGenOptions = {"User-Defined Input", "Random", "From a .txt file"};
        dataGenDropdown = new JComboBox<>(dataGenOptions);
        dataGenDropdown.setFont(new Font("Arial", Font.BOLD, 18));
        dataGenDropdown.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2));
        g1.add(dataGenDropdown);

        JPanel g2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        g2.setBackground(ThemeManager.getBackgroundColor());
        String[] algos = {
            "FIFO: First-In, First-Out", "LRU: Least Recently Used",
            "OPT: Optimal", "SC: Second Chance", "ESC: Enhanced Second Chance",
            "LFU: Least Frequently Used", "MFU: Most Frequently Used"
        };
        algoDropdown = new JComboBox<>(algos);
        algoDropdown.setFont(new Font("Arial", Font.BOLD, 18));
        algoDropdown.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentBlue(), 2));
        g2.add(algoDropdown);

        JPanel g3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        g3.setBackground(ThemeManager.getBackgroundColor());
        JLabel frameLabel = new JLabel("Page Frames: ");
        frameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frameLabel.setForeground(ThemeManager.getTextColor());
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(4, 3, 10, 1);
        frameSizeSpinner = new JSpinner(spinnerModel);
        frameSizeSpinner.setFont(new Font("Arial", Font.BOLD, 18));
        frameSizeSpinner.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2));
        g3.add(frameLabel);
        g3.add(frameSizeSpinner);

        row1.add(g1, BorderLayout.WEST);
        row1.add(g2, BorderLayout.CENTER);
        row1.add(g3, BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout(5, 0));
        row2.setBackground(ThemeManager.getBackgroundColor());
        pageRefField = new PlaceholderTextField("e.g., 7,0,1,2,0,3,0,4,2,3");
        pageRefField.setFont(new Font("Arial", Font.BOLD, 18));
        pageRefField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        applyRealTimeFilter(pageRefField);

        actionBtn = new JButton("▶");
        actionBtn.setFont(new Font("Arial", Font.BOLD, 18));
        actionBtn.setBackground(ThemeManager.getAccentBlue());
        actionBtn.setForeground(Color.WHITE); // The play icon is always white
        actionBtn.setFocusPainted(false);
        actionBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getAccentOrange(), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        row2.add(pageRefField, BorderLayout.CENTER);
        row2.add(actionBtn, BorderLayout.EAST);

        topWrapper.add(row1);
        topWrapper.add(row2);
        topWrapper.add(Box.createVerticalStrut(15));
        add(topWrapper, BorderLayout.NORTH);

        simulationBoard = new SimulationBoard();
        scrollPane = new JScrollPane(simulationBoard);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(ThemeManager.getBackgroundColor());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(ThemeManager.getBackgroundColor());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomLeft.setBackground(ThemeManager.getBackgroundColor());

        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        String redHex = ThemeManager.colorToHex(ThemeManager.getErrorRed());

        totalFaultsLabel = new JLabel("<html><b style='color:" + textHex + ";'>Total Page Fault:</b> <span style='color:" + redHex + ";'>0</span></html>");
        totalFaultsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        timerLabel = new JLabel("<html><b style='color:" + textHex + ";'>Timer:</b> <span style='color:" + textHex + ";'>0s</span></html>");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel speedLabel = new JLabel("<html><b style='color:" + textHex + ";'>Speed:</b></html>");
        speedLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        String[] speeds = {"0.5x", "1.0x", "2.0x", "3.0x", "4.0x", "5.0x"};
        speedDropdown = new JComboBox<>(speeds);
        speedDropdown.setSelectedItem("1.0x");

        bottomLeft.add(totalFaultsLabel);
        bottomLeft.add(timerLabel);
        bottomLeft.add(speedLabel);
        bottomLeft.add(speedDropdown);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomRight.setBackground(ThemeManager.getBackgroundColor());

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

    public void updateFaults(int faults) {
        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        String redHex = ThemeManager.colorToHex(ThemeManager.getErrorRed());
        totalFaultsLabel.setText("<html><b style='color:" + textHex + ";'>Total Page Fault:</b> <span style='color:" + redHex + ";'>" + faults + "</span></html>");
    }

    public void updateTimer(int ticks) {
        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        timerLabel.setText("<html><b style='color:" + textHex + ";'>Timer:</b> <span style='color:" + textHex + ";'>" + ticks + "s</span></html>");
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
                g2d.setFont(new Font("Arial", Font.ITALIC, 16));
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
        btn.setFont(new Font("Arial", Font.BOLD, 11));
        btn.setForeground(ThemeManager.getTextColor());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }

    public static class SimulationBoard extends JPanel {
        private final List<StepData> steps = new ArrayList<>();
        private int frameSize = 4;

        public SimulationBoard() { setBackground(ThemeManager.getBackgroundColor()); }

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