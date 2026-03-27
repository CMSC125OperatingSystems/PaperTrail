package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

    private JPanel resultsContainer;
    private JLabel infoLabel;

    public SimulateView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        applyTheme();
        ThemeManager.addObserver(this);
    }

    private void initComponents() {
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

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));

        infoLabel = new JLabel(" ", SwingConstants.CENTER);
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

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        speedLabel = new JLabel();
        speedDropdown = new JComboBox<>(new String[]{"0.5x", "1x", "2x", "4x", "8x", "16x"});
        speedDropdown.setSelectedItem("1x");
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
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyTheme() {
        // 1. Structural update
        updatePanelBackgrounds(this, ThemeManager.getBackgroundColor());

        Color textFg = ThemeManager.getTextColor();
        Color panelBg = ThemeManager.getPanelColor();

        // 2. Explicitly target the bottom-right buttons
        // Sometimes text on a Light background stays White because of the OS theme
        for (JButton btn : new JButton[]{resetBtn, outputBtn, backBtn}) {
            btn.setForeground(textFg);
            btn.setBackground(ThemeManager.getBackgroundColor()); // Match the bottom bar
            btn.setOpaque(false); // Helps text visibility on custom backgrounds
        }

        // 3. Update Input Fields & Dropdowns
        dataGenDropdown.setBackground(panelBg);
        dataGenDropdown.setForeground(textFg);
        algorithmMenuBtn.setBackground(panelBg);
        algorithmMenuBtn.setForeground(textFg);
        speedDropdown.setBackground(panelBg);
        speedDropdown.setForeground(textFg);
        pageRefField.setBackground(panelBg);
        pageRefField.setForeground(textFg);
        pageRefField.setCaretColor(textFg);
        randomBtn.setBackground(panelBg);
        randomBtn.setForeground(textFg);
        browseBtn.setBackground(panelBg);
        browseBtn.setForeground(textFg);

        if (frameSizeSpinner.getEditor() instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) frameSizeSpinner.getEditor()).getTextField();
            tf.setBackground(panelBg);
            tf.setForeground(textFg);
        }

        // 4. Update Fonts
        Font primaryFont = new Font("Arial", Font.BOLD, ThemeManager.getPrimaryFontSize());
        dataGenDropdown.setFont(primaryFont);
        algorithmMenuBtn.setFont(primaryFont);
        frameLabel.setFont(primaryFont);
        pageRefField.setFont(primaryFont);

        Font secondaryFont = new Font("Arial", Font.PLAIN, ThemeManager.getSecondaryFontSize());
        infoLabel.setFont(new Font("Monospaced", Font.BOLD, ThemeManager.getSecondaryFontSize()));
        speedDropdown.setFont(secondaryFont);
        speedLabel.setFont(secondaryFont);
        runBtn.setFont(secondaryFont);
        randomBtn.setFont(secondaryFont);
        browseBtn.setFont(secondaryFont);

        Font tertiaryFont = new Font("Arial", Font.BOLD, ThemeManager.getTertiaryFontSize());
        resetBtn.setFont(tertiaryFont);
        backBtn.setFont(tertiaryFont);
        outputBtn.setFont(tertiaryFont);

        frameLabel.setForeground(textFg);
        infoLabel.setForeground(textFg);

        runBtn.setBackground(ThemeManager.getAccentBlue());
        runBtn.setForeground(Color.WHITE);

        String textHex = ThemeManager.colorToHex(textFg);
        speedLabel.setText("<html><b style='color:" + textHex + ";'>Speed:</b></html>");

        // 5. Update Algorithm Result Panels
        for (Component c : resultsContainer.getComponents()) {
            if (c instanceof AlgorithmResultPanel) {
                ((AlgorithmResultPanel) c).applyDynamicTheme();
            }
        }

        if (algorithmPopupMenu != null) {
            SwingUtilities.updateComponentTreeUI(algorithmPopupMenu);
        }
    }

    private void updatePanelBackgrounds(Container container, Color bg) {
        // Apply strictly to structural containers
        if (container instanceof JPanel || container instanceof JScrollPane || container instanceof JViewport) {
            // Ignore if it's explicitly named "SummaryPanel" so it keeps contrast
            if (!"SummaryPanel".equals(container.getName())) {
                container.setBackground(bg);
            }
        }
        for (Component c : container.getComponents()) {
            if (c instanceof Container) {
                updatePanelBackgrounds((Container) c, bg);
            }
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

    public void addAlgorithmPanel(String name, int frameSize) {
        resultsContainer.add(new AlgorithmResultPanel(name, frameSize));
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
        private final int requiredHeight;
        private final String algorithmName; // Store name to prevent "Algorithm" generic label

        public AlgorithmResultPanel(String name, int frameSize) {
            this.algorithmName = name; // Save the name immediately
            setLayout(new BorderLayout());

            board = new SimulationBoard(frameSize);
            JScrollPane scrollPane = new JScrollPane(board);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

            int cellSize = Math.max(35, ThemeManager.getSecondaryFontSize() + 15);
            int boardHeight = 80 + (frameSize * cellSize);
            this.requiredHeight = boardHeight + 80; // Adjusted for labels

            scrollPane.setPreferredSize(new Dimension(800, boardHeight));
            add(scrollPane, BorderLayout.CENTER);

            applyDynamicTheme();
        }

        /**
         * Refreshes the border and sub-labels using the stored algorithmName
         */
        public void applyDynamicTheme() {
            // Fix: Use this.algorithmName instead of parsing the border title
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.getAccentBlue()),
                algorithmName,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, ThemeManager.getSecondaryFontSize()),
                ThemeManager.getTextColor()
            ));

            setBackground(ThemeManager.getBackgroundColor());

            // Force child labels (Summary stats) to update
            for (Component c : getComponents()) {
                if (c instanceof JPanel && "SummaryPanel".equals(c.getName())) {
                    c.setBackground(ThemeManager.getPanelColor());
                    for (Component label : ((JPanel) c).getComponents()) {
                        label.setForeground(ThemeManager.getTextColor());
                        label.setFont(new Font("Arial", Font.BOLD, ThemeManager.getSecondaryFontSize()));
                    }
                }
            }
        }

        @Override
        public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, requiredHeight); }
        @Override
        public Dimension getMinimumSize() { return new Dimension(400, requiredHeight); }
        @Override
        public Dimension getPreferredSize() { return new Dimension(800, requiredHeight); }

        public void addStep(int page, int[] frames, boolean isHit) {
            board.addStep(page, frames, isHit);
            JScrollPane scroll = (JScrollPane) getComponent(0);
            JScrollBar horizontalBar = scroll.getHorizontalScrollBar();
            horizontalBar.setValue(horizontalBar.getMaximum());
        }

        public void addSummary(int hits, int faults, double faultRate) {
            JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
            summaryPanel.setName("SummaryPanel");
            summaryPanel.setBackground(ThemeManager.getPanelColor());

            Font font = new Font("Arial", Font.BOLD, ThemeManager.getSecondaryFontSize());

            JLabel hitsLabel = new JLabel(String.format("Total Hits: %d", hits));
            JLabel faultsLabel = new JLabel(String.format("Total Faults: %d", faults));
            JLabel rateLabel = new JLabel(String.format("Fault Rate: %.2f%%", faultRate * 100));

            hitsLabel.setFont(font); faultsLabel.setFont(font); rateLabel.setFont(font);
            hitsLabel.setForeground(ThemeManager.getTextColor());
            faultsLabel.setForeground(ThemeManager.getTextColor());
            rateLabel.setForeground(ThemeManager.getTextColor());

            summaryPanel.add(hitsLabel);
            summaryPanel.add(faultsLabel);
            summaryPanel.add(rateLabel);

            add(summaryPanel, BorderLayout.SOUTH);
            revalidate();
        }
    }

    private static class SimulationBoard extends JPanel {
        private final List<StepData> steps = new ArrayList<>();
        private final int frameSize;

        public SimulationBoard(int frameSize) {
            this.frameSize = frameSize;
            setBackground(ThemeManager.getBackgroundColor());
        }

        public void addStep(int page, int[] frames, boolean isHit) {
            steps.add(new StepData(page, frames, isHit));
            int cellSize = Math.max(35, ThemeManager.getSecondaryFontSize() + 15);
            int requiredWidth = Math.max(600, steps.size() * (cellSize + 10) + 40);
            int requiredHeight = 80 + (frameSize * cellSize);
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

            int fontSize = ThemeManager.getSecondaryFontSize();
            int cellSize = Math.max(35, fontSize + 15);
            int colWidth = cellSize + 10;
            int rowHeight = cellSize;

            int startX = 20;
            int startY = 30;

            for (int i = 0; i < steps.size(); i++) {
                StepData step = steps.get(i);
                int currentX = startX + i * colWidth;

                // Draw Requested Page
                g2d.setColor(ThemeManager.getTextColor());
                g2d.setFont(new Font("Arial", Font.BOLD, fontSize + 4));
                FontMetrics fmPrimary = g2d.getFontMetrics();
                String pageStr = String.valueOf(step.page);
                g2d.drawString(pageStr, currentX + (cellSize - fmPrimary.stringWidth(pageStr)) / 2, startY);

                // Draw Memory Frames
                g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
                FontMetrics fmSec = g2d.getFontMetrics();

                for (int f = 0; f < frameSize; f++) {
                    int boxY = startY + 10 + f * rowHeight;

                    boolean isModifiedFrame = (step.frames[f] == step.page);

                    if (isModifiedFrame) {
                        g2d.setColor(ThemeManager.getAccentBlue());
                        g2d.fillRect(currentX, boxY, cellSize, cellSize);

                        g2d.setColor(ThemeManager.getAccentOrange());
                        g2d.drawRect(currentX, boxY, cellSize, cellSize);

                        g2d.setColor(Color.WHITE);
                        String frameStr = String.valueOf(step.frames[f]);
                        g2d.drawString(frameStr, currentX + (cellSize - fmSec.stringWidth(frameStr)) / 2, boxY + (cellSize/2) + (fontSize/2) - 2);
                    } else {
                        g2d.setColor(ThemeManager.getAccentBlue());
                        g2d.drawRect(currentX, boxY, cellSize, cellSize);

                        if (step.frames[f] != -1) {
                            g2d.setColor(ThemeManager.getAccentOrange());
                            String frameStr = String.valueOf(step.frames[f]);
                            g2d.drawString(frameStr, currentX + (cellSize - fmSec.stringWidth(frameStr)) / 2, boxY + (cellSize/2) + (fontSize/2) - 2);
                        }
                    }
                }

                // Draw Status (Hit / Miss)
                int statusY = startY + 20 + frameSize * rowHeight;
                if (step.isHit) {
                    g2d.setColor(ThemeManager.getAccentBlue());
                    g2d.drawString("O", currentX + (cellSize - fmSec.stringWidth("O")) / 2, statusY);
                } else {
                    g2d.setColor(ThemeManager.getErrorRed());
                    g2d.drawString("X", currentX + (cellSize - fmSec.stringWidth("X")) / 2, statusY);
                }

                // Draw Step Number
                g2d.setColor(ThemeManager.getSecondaryTextColor());
                g2d.setFont(new Font("Arial", Font.PLAIN, ThemeManager.getTertiaryFontSize()));
                String stepNum = "S" + (i + 1);
                FontMetrics fmTert = g2d.getFontMetrics();
                g2d.drawString(stepNum, currentX + (cellSize - fmTert.stringWidth(stepNum)) / 2, statusY + ThemeManager.getTertiaryFontSize() + 5);
            }
        }
    }

    private static class StepData {
        int page; int[] frames; boolean isHit;
        public StepData(int p, int[] f, boolean h) { page = p; frames = f.clone(); isHit = h; }
    }

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