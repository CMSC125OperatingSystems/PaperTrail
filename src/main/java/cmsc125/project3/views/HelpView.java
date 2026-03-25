package cmsc125.project3.views;

import javax.swing.*;
import java.awt.*;

public class HelpView extends JPanel {
    private JPanel stepsContainer;
    private CardLayout stepCardLayout;
    private JButton prevBtn, nextBtn, backBtn;
    private int currentStepIndex = 1;
    private final int TOTAL_STEPS = 5;

    public HelpView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initComponents();
        setupInternalNavigation();
    }

    private void initComponents() {
        stepCardLayout = new CardLayout();
        stepsContainer = new JPanel(stepCardLayout);
        stepsContainer.setBackground(Color.WHITE);

        stepsContainer.add(createStepPanel(
                "Step 1: Choose Your Data",
                "First, use the 'Data Generation' dropdown to decide how you want to create your page reference string. You can let the computer generate random numbers, type them in manually, or upload a formatted .txt file.",
                "/images/step1_placeholder.png"
        ), "Step 1");

        stepsContainer.add(createStepPanel(
                "Step 2: Configure the Simulation",
                "Next, click 'Select Algorithms' to choose which Page Replacement Algorithms you want to compare. Then, set your desired 'Frame Size' (between 3 and 10). The frame size represents how much memory is available.",
                "/images/step2_placeholder.png"
        ), "Step 2");

        stepsContainer.add(createStepPanel(
                "Step 3: Run the Algorithms",
                "Click the 'Run' button to start the simulation! You can watch the algorithm execute step-by-step. Use the 'Speed' dropdown at the bottom left to make the animation play faster or slower.",
                "/images/step3_placeholder.png"
        ), "Step 3");

        stepsContainer.add(createStepPanel(
                "Step 4: Understanding the Visuals",
                "The simulator shows the memory frames progressing one-by-one over time. \n\n" +
                        "• Top Number: The specific page number currently being requested.\n" +
                        "• Bottom Symbol 'O' (Hit): The page was already in memory! No replacement needed.\n" +
                        "• Bottom Symbol 'X' (Miss): The page was not in memory, causing a Page Fault.\n\n" +
                        "At the end of the row, you will see the 'Total Page Faults', which tells you how many times the algorithm had to load a new page.",
                "/images/step4_placeholder.png"
        ), "Step 4");

        stepsContainer.add(createStepPanel(
                "Step 5: Controls & Exporting Results",
                "Use the buttons at the bottom right to manage your session:\n\n" +
                        "• Restart: Replays the animation from the beginning using your current exact inputs.\n" +
                        "• Reset: Completely clears all text fields and dropdowns so you can start completely fresh.\n" +
                        "• Export: Saves your final, finished algorithm results as either a PNG image or a PDF document so you can review them later.",
                "/images/step5_placeholder.png"
        ), "Step 5");

        add(stepsContainer, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        paginationPanel.setBackground(Color.WHITE);

        prevBtn = new JButton("◄ Previous");
        prevBtn.setEnabled(false);
        prevBtn.setFocusPainted(false);

        nextBtn = new JButton("Next ►");
        nextBtn.setFocusPainted(false);

        paginationPanel.add(prevBtn);
        paginationPanel.add(nextBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(Color.WHITE);

        backBtn = new JButton("Back to Dashboard");
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightPanel.add(backBtn);

        bottomPanel.add(paginationPanel, BorderLayout.CENTER);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * For readable panel for each instructional step using JTextArea
     */
    private JPanel createStepPanel(String title, String description, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);

        JPanel textPanel = new JPanel(new BorderLayout(0, 15));
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descArea.setForeground(new Color(50, 50, 50));
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
        } catch (Exception e) {
            System.out.println("Notice: Could not load placeholder image: " + imagePath);
        }

        panel.add(textPanel, BorderLayout.NORTH);
        panel.add(imageLabel, BorderLayout.CENTER);

        return panel;
    }

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

    public JButton getBackBtn() {
        return backBtn;
    }
}