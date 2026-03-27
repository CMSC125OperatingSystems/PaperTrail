package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulateView extends JPanel implements ThemeManager.ThemeObserver {
    private JPopupMenu algorithmPopupMenu;
    private JCheckBoxMenuItem[] algoCheckboxes;
    private JComboBox<String> dataGenDropdown, speedDropdown;
    private JSpinner frameSizeSpinner;
    private JTextField pageRefField;
    private JButton algorithmMenuBtn, runBtn, randomBtn, browseBtn, resetBtn, backBtn, outputBtn;
    private JLabel frameLabel, speedLabel;

    private JPanel resultsContainer; // Main container for all dynamic panels
    private JLabel infoLabel; // Displays Ref String and Frame Size

    public SimulateView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        applyTheme();
        ThemeManager.addObserver(this);
    }

    private void initComponents() {
        // --- TOP INPUT PANEL ---
        JPanel topWrapper = new JPanel();
        topWrapper.setLayout(new BoxLayout(topWrapper, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 5, 0, 5);

        dataGenDropdown = new JComboBox<>(new String[]{"User-defined input", "User-defined input from a .txt file"});
        algorithmMenuBtn = new JButton("Select Algorithms ▼");
        algorithmPopupMenu = new JPopupMenu();
        String[] algos = {"All", "FIFO", "LRU", "OPT", "SC", "ESC", "LFU", "MFU"};
        algoCheckboxes = new JCheckBoxMenuItem[algos.length];
        for (int i = 0; i < algos.length; i++) {
            algoCheckboxes[i] = new JCheckBoxMenuItem(algos[i]);
            algorithmPopupMenu.add(algoCheckboxes[i]);
        }
        algorithmMenuBtn.addActionListener(e -> algorithmPopupMenu.show(algorithmMenuBtn, 0, algorithmMenuBtn.getHeight()));

        JPanel frameSizePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        frameLabel = new JLabel("Frame Size: ");
        frameSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 3, 10, 1));
        frameSizePanel.add(frameLabel);
        frameSizePanel.add(frameSizeSpinner);

        row1.add(dataGenDropdown, gbc);
        row1.add(algorithmMenuBtn, gbc);
        row1.add(frameSizePanel, gbc);

        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        row2.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        pageRefField = new PlaceholderTextField("e.g., 7,0,1,2,0,3,0,4,2,3");
        applyRealTimeFilter(pageRefField);
        JPanel buttonSet = new JPanel(new GridLayout(1, 3, 5, 0));
        runBtn = new JButton("Run Simulation");
        randomBtn = new JButton("Generate Random");
        browseBtn = new JButton("Browse File");
        buttonSet.add(runBtn);
        buttonSet.add(randomBtn);
        buttonSet.add(browseBtn);

        row2.add(pageRefField, BorderLayout.CENTER);
        row2.add(buttonSet, BorderLayout.EAST);

        topWrapper.add(row1);
        topWrapper.add(row2);
        add(topWrapper, BorderLayout.NORTH);

        // --- CENTER RESULTS PANEL ---
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        infoLabel = new JLabel(" ", SwingConstants.CENTER); // Placeholder

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.add(infoPanel, BorderLayout.NORTH);
        mainContentPanel.add(resultsContainer, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // --- BOTTOM CONTROLS PANEL ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        speedLabel = new JLabel();
        speedDropdown = new JComboBox<>(new String[]{"0.5x", "1.0x", "2.0x", "3.0x", "4.0x", "5.0x"});
        speedDropdown.setSelectedItem("1.0x");
        bottomLeft.add(speedLabel);
        bottomLeft.add(speedDropdown);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        resetBtn = createIconButton("/images/reset.png", "Reset All");
        outputBtn = createIconButton("/images/export.png", "Export Results");
        backBtn = createIconButton("/images/back.png", "Back to Dashboard");
        bottomRight.add(resetBtn);
        bottomRight.add(outputBtn);
        bottomRight.add(backBtn);

        bottomPanel.add(bottomLeft, BorderLayout.WEST);
        bottomPanel.add(bottomRight, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onThemeChanged() {
        applyTheme();
        // Force redraw of all child components
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyTheme() {
        updatePanelBackgrounds(this, ThemeManager.getBackgroundColor());

        Font primaryFont = new Font("Arial", Font.BOLD, ThemeManager.getPrimaryFontSize());
        dataGenDropdown.setFont(primaryFont);
        algorithmMenuBtn.setFont(primaryFont);
        frameLabel.setFont(primaryFont);
        pageRefField.setFont(primaryFont);
        infoLabel.setFont(new Font("Monospaced", Font.BOLD, ThemeManager.getSecondaryFontSize()));

        Font secondaryFont = new Font("Arial", Font.PLAIN, ThemeManager.getSecondaryFontSize());
        speedDropdown.setFont(secondaryFont);
        speedLabel.setFont(secondaryFont);
        runBtn.setFont(secondaryFont);
        randomBtn.setFont(secondaryFont);
        browseBtn.setFont(secondaryFont);

        Font tertiaryFont = new Font("Arial", Font.BOLD, ThemeManager.getTertiaryFontSize());
        resetBtn.setFont(tertiaryFont);
        backBtn.setFont(tertiaryFont);
        outputBtn.setFont(tertiaryFont);

        frameLabel.setForeground(ThemeManager.getTextColor());
        infoLabel.setForeground(ThemeManager.getTextColor());

        runBtn.setBackground(ThemeManager.getAccentBlue());
        runBtn.setForeground(Color.WHITE);

        String textHex = ThemeManager.colorToHex(ThemeManager.getTextColor());
        speedLabel.setText("<html><b style='color:" + textHex + ";'>Speed:</b></html>");
    }

    private void updatePanelBackgrounds(Container container, Color bg) {
        container.setBackground(bg);
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) updatePanelBackgrounds((Container) c, bg);
        }
    }

    public void clearResults() {
        resultsContainer.removeAll();
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    public void setInfo(String refString, int frameSize) {
        String infoText = String.format("Reference String: %s  |  Frame Size: %d", refString, frameSize);
        infoLabel.setText(infoText);
    }

    public void addAlgorithmPanel(String name) {
        resultsContainer.add(new AlgorithmResultPanel(name));
        resultsContainer.revalidate();
    }

    public void addStepToAlgorithm(int index, int page, int[] frames, boolean isHit) {
        if (index < resultsContainer.getComponentCount()) {
            ((AlgorithmResultPanel) resultsContainer.getComponent(index)).addStep(page, frames, isHit);
        }
    }

    public void addSummaryToAlgorithm(int index, int hits, int faults, double faultRate) {
        if (index < resultsContainer.getComponentCount()) {
            ((AlgorithmResultPanel) resultsContainer.getComponent(index)).addSummary(hits, faults, faultRate);
        }
    }

    // --- NESTED CLASSES FOR DYNAMIC UI ---

    private static class AlgorithmResultPanel extends JPanel {
        private final SimulationBoard board;

        public AlgorithmResultPanel(String name) {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.getAccentBlue()), name
            ));

            board = new SimulationBoard();
            JScrollPane scrollPane = new JScrollPane(board);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

            add(scrollPane, BorderLayout.CENTER);
        }

        public void addStep(int page, int[] frames, boolean isHit) {
            board.addStep(page, frames, isHit);
        }

        public void addSummary(int hits, int faults, double faultRate) {
            JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
            summaryPanel.setBackground(ThemeManager.getPanelColor());

            String hitsText = String.format("Total Hits: %d", hits);
            String faultsText = String.format("Total Faults: %d", faults);
            String rateText = String.format("Fault Rate: %.2f%%", faultRate * 100);

            summaryPanel.add(new JLabel(hitsText));
            summaryPanel.add(new JLabel(faultsText));
            summaryPanel.add(new JLabel(rateText));

            add(summaryPanel, BorderLayout.SOUTH);
            revalidate();
        }
    }

    private static class SimulationBoard extends JPanel {
        private final List<StepData> steps = new ArrayList<>();
        private int frameSize = 4;

        public void clear() { steps.clear(); repaint(); }
        public void setFrameSize(int size) { this.frameSize = size; }

        public void addStep(int page, int[] frames, boolean isHit) {
            steps.add(new StepData(page, frames, isHit));
            int requiredWidth = Math.max(600, steps.size() * 45 + 40);
            int requiredHeight = 120 + (frameSize * 35);
            setPreferredSize(new Dimension(requiredWidth, requiredHeight));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(ThemeManager.getBackgroundColor());
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < steps.size(); i++) {
                StepData step = steps.get(i);
                int currentX = 20 + i * 45;

                // Draw Page
                g2d.setColor(ThemeManager.getTextColor());
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString(String.valueOf(step.page), currentX + 10, 25);

                // Draw Frames
                for (int f = 0; f < frameSize; f++) {
                    int boxY = 35 + f * 35;
                    g2d.setColor(ThemeManager.getAccentBlue());
                    g2d.drawRect(currentX, boxY, 30, 30);
                    if (step.frames[f] != -1) {
                        g2d.setColor(ThemeManager.getAccentOrange());
                        g2d.drawString(String.valueOf(step.frames[f]), currentX + 10, boxY + 22);
                    }
                }

                // Draw Hit/Miss
                int statusY = 40 + frameSize * 35;
                if (step.isHit) {
                    g2d.setColor(ThemeManager.getAccentBlue());
                    g2d.drawString("O", currentX + 10, statusY + 15);
                } else {
                    g2d.setColor(ThemeManager.getErrorRed());
                    g2d.drawString("X", currentX + 10, statusY + 15);
                }

                // Draw Step Number
                g2d.setColor(ThemeManager.getSecondaryTextColor());
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString("S" + (i + 1), currentX + 8, statusY + 35);
            }
        }
    }

    private static class StepData {
        int page; int[] frames; boolean isHit;
        public StepData(int p, int[] f, boolean h) { page = p; frames = f.clone(); isHit = h; }
    }

    // ... (Getters and other private methods remain) ...
    private JButton createIconButton(String imagePath, String text) {
        JButton btn = new JButton(text);
        btn.setForeground(ThemeManager.getTextColor());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        return btn;
    }
    private void applyRealTimeFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException { if (string == null) return; processInput(fb, offset, 0, string); }
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException { if (text == null) return; processInput(fb, offset, length, text); }
            private void processInput(FilterBypass fb, int offset, int length, String newText) throws BadLocationException {
                String sanitized = newText.replaceAll("\\s+", "");
                if (!sanitized.matches("[0-9,]*")) { Toolkit.getDefaultToolkit().beep(); return; }
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String simulatedResult = currentText.substring(0, offset) + sanitized + currentText.substring(offset + length);
                if (simulatedResult.chars().filter(ch -> ch == ',').count() > 39) { Toolkit.getDefaultToolkit().beep(); return; }
                fb.replace(offset, length, sanitized, null);
            }
        });
    }
    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        public PlaceholderTextField(String placeholder) { this.placeholder = placeholder; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(ThemeManager.getPlaceholderColor());
                g2d.setFont(new Font("Arial", Font.ITALIC, ThemeManager.getPrimaryFontSize()));
                g2d.drawString(placeholder, getInsets().left, (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent());
            }
        }
    }

    public JComboBox<String> getDataGenDropdown() { return dataGenDropdown; }
    public JTextField getPageRefField() { return pageRefField; }
    public JButton getRunBtn() { return runBtn; }
    public JButton getRandomBtn() { return randomBtn; }
    public JButton getBrowseBtn() { return browseBtn; }
    public JCheckBoxMenuItem[] getAlgoCheckboxes() { return algoCheckboxes; }
    public JSpinner getFrameSizeSpinner() { return frameSizeSpinner; }
    public JComboBox<String> getSpeedDropdown() { return speedDropdown; }
    public JButton getResetBtn() { return resetBtn; }
    public JButton getBackBtn() { return backBtn; }
    public JButton getOutputBtn() { return outputBtn; }
}