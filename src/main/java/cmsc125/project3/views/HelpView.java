package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class HelpView extends JPanel implements ThemeManager.ThemeObserver {
    private JPanel stepsContainer;
    private CardLayout stepCardLayout;
    private JButton prevBtn, nextBtn, backBtn;
    private int currentStepIndex = 1;
    private final int TOTAL_STEPS = 5;

    public HelpView() {
        initComponents();
        applyTheme();
        setupInternalNavigation();
        ThemeManager.addObserver(this);
    }

    @Override
    public void onThemeChanged() {
        applyTheme();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        stepCardLayout = new CardLayout();
        stepsContainer = new JPanel(stepCardLayout);

        // Panels are created here but styled in applyTheme()
        for (int i = 1; i <= TOTAL_STEPS; i++) {
            stepsContainer.add(createStepPanel(i), "Step " + i);
        }
        add(stepsContainer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        prevBtn = new JButton("◄ Previous");
        prevBtn.setEnabled(false);
        prevBtn.setFocusPainted(false);
        nextBtn = new JButton("Next ►");
        nextBtn.setFocusPainted(false);
        paginationPanel.add(prevBtn);
        paginationPanel.add(nextBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        backBtn = new JButton("Back to Dashboard");
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(backBtn);

        bottomPanel.add(paginationPanel, BorderLayout.CENTER);
        bottomPanel.add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyTheme() {
        setBackground(ThemeManager.getBackgroundColor());
        stepsContainer.setBackground(ThemeManager.getBackgroundColor());
        // Apply theme to all child components recursively
        updateComponentTheme(this);
    }

    private void updateComponentTheme(Container container) {
        container.setBackground(ThemeManager.getBackgroundColor());
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) c.setForeground(ThemeManager.getTextColor());
            if (c instanceof JTextArea) {
                c.setForeground(ThemeManager.getSecondaryTextColor());
                c.setBackground(ThemeManager.getBackgroundColor());
            }
            if (c instanceof JButton) {
                c.setForeground(ThemeManager.getTextColor());
                c.setBackground(ThemeManager.getPanelColor());
            }
            if (c instanceof Container) {
                updateComponentTheme((Container) c);
            }
        }
    }

    private JPanel createStepPanel(int stepNum) {
        String title = getTitleForStep(stepNum);
        String description = getDescriptionForStep(stepNum);
        String imagePath = "/images/step" + stepNum + "_placeholder.png";

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        JPanel textPanel = new JPanel(new BorderLayout(0, 15));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setHighlighter(null);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        JLabel imageLabel = new JLabel("Image placeholder: " + imagePath, SwingConstants.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        imageLabel.setPreferredSize(new Dimension(800, 400));
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                imageLabel.setIcon(new ImageIcon(imgURL));
                imageLabel.setText("");
            }
        } catch (Exception e) {}

        panel.add(textPanel, BorderLayout.NORTH);
        panel.add(imageLabel, BorderLayout.CENTER);
        return panel;
    }

    // ... (rest of HelpView remains the same: setupInternalNavigation, updateButtonStates, getBackBtn, etc.) ...
    private void setupInternalNavigation() {
        nextBtn.addActionListener(e -> {
            if (currentStepIndex < TOTAL_STEPS) {
                currentStepIndex++;
                stepCardLayout.next(stepsContainer);
                updateButtonStates();
            }
        });

        prevBtn.addActionListener(e -> {
            if (currentStepIndex > 1) {
                currentStepIndex--;
                stepCardLayout.previous(stepsContainer);
                updateButtonStates();
            }
        });
    }

    private void updateButtonStates() {
        prevBtn.setEnabled(currentStepIndex > 1);
        nextBtn.setEnabled(currentStepIndex < TOTAL_STEPS);
    }

    private String getTitleForStep(int step) {
        switch(step) {
            case 1: return "Step 1: Choose Your Data";
            case 2: return "Step 2: Configure the Simulation";
            case 3: return "Step 3: Run the Algorithms";
            case 4: return "Step 4: Understanding the Visuals";
            case 5: return "Step 5: Controls & Exporting Results";
            default: return "";
        }
    }

    private String getDescriptionForStep(int step) {
        switch(step) {
            case 1: return "First, use the 'Data Generation' dropdown to decide how you want to create your page reference string. You can let the computer generate random numbers, type them in manually, or upload a formatted .txt file.";
            case 2: return "Next, select which Page Replacement Algorithm you want to simulate. Then, set your desired 'Frame Size' (between 3 and 10). The frame size represents how much memory is available.";
            case 3: return "Click the '▶' button to start the simulation! You can watch the algorithm execute step-by-step. Use the 'Speed' dropdown at the bottom left to make the animation play faster or slower.";
            case 4: return "The simulator shows the memory frames progressing one-by-one over time. \n\n" +
                "• Top Number: The specific page number currently being requested.\n" +
                "• Bottom Symbol 'O' (Hit): The page was already in memory! No replacement needed.\n" +
                "• Bottom Symbol 'X' (Miss): The page was not in memory, causing a Page Fault.\n\n" +
                "At the bottom, you will see the 'Total Page Faults', which tells you how many times the algorithm had to load a new page.";
            case 5: return "Use the buttons at the bottom right to manage your session:\n\n" +
                "• Redo: Replays the animation from the beginning using your current exact inputs.\n" +
                "• Reset: Completely clears all text fields and dropdowns so you can start completely fresh.\n" +
                "• Output Algorithm: Saves your final, finished algorithm results as either a PNG image or a PDF document so you can review them later.";
            default: return "";
        }
    }

    public JButton getBackBtn() {
        return backBtn;
    }
}