package cmsc125.project3;

import cmsc125.project3.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {
    private final SplashScreenView splashView;
    private final DashboardView dashboardView;
    private final PlayView playView;
    private final HelpView helpView;
    private final AboutView aboutView;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private Timer timer;
    private int currentTick = 0;

    public MainController(SplashScreenView splashView, DashboardView dashboardView, PlayView playView, HelpView helpView, AboutView aboutView, JPanel cardPanel, CardLayout cardLayout) {
        this.splashView = splashView;
        this.dashboardView = dashboardView;
        this.playView = playView;
        this.helpView = helpView;
        this.cardPanel = cardPanel;
        this.aboutView = aboutView;
        this.cardLayout = cardLayout;

        initController();
    }

    private void initController() {
        // Setup Timer
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentTick < 10) {
                    currentTick++;
                    splashView.setProgressTick(currentTick); // Update visual view
                } else {
                    timer.stop();
                    // Swap screen from "Splash" to "Dashboard"
                    cardLayout.show(cardPanel, "Dashboard");
                }
            }
        });
        timer.start();

        dashboardView.getStartBtn().addActionListener(e -> {
            cardLayout.show(cardPanel, "Play");
        });

        setupAlgorithmMenuLogic(); // Handle logic of "All" checkbox interacting with individual algorithms

        // Data Generation Dropdown Logic (Toggle TextField and Run/Browse Button)
        playView.getDataGenDropdown().addActionListener(e -> {
            String selectedOption = (String) playView.getDataGenDropdown().getSelectedItem();
            if ("User-defined input from a .txt file".equals(selectedOption)) {
                playView.getPageRefField().setEnabled(false);
                playView.getActionBtn().setText("Browse");
            } else {
                playView.getPageRefField().setEnabled(true);
                playView.getActionBtn().setText("Run");
            }
        });

        // Run or Browse Button Logic (Validation happens here)
        playView.getActionBtn().addActionListener(e -> {
            String actionType = playView.getActionBtn().getText();

            if ("Browse".equals(actionType)) System.out.println("Opening File Chooser...");
            else if ("Run".equals(actionType)) {
                String input = playView.getPageRefField().getText().trim();

                if (input.isEmpty()) {
                    JOptionPane.showMessageDialog(playView, "Please enter a page reference string.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] sequence = input.split(",");
                int length = sequence.length;

                // Minimum validation
                if (length < 10) JOptionPane.showMessageDialog(playView, "The page reference string must contain at least 10 numbers.\nYou currently have: " + length, "Invalid Length", JOptionPane.WARNING_MESSAGE);
                else {
                    System.out.println("Validation passed! Running algorithm simulation...");
                    // Pass the clean sequence to your algorithm engine here
                }
            }
        });

        // Restart Button Logic
        playView.getRestartBtn().addActionListener(e -> {
            System.out.println("Restarting simulation with current inputs...");
            // Restart logic goes here
        });

        // Export Button Logic
        playView.getExportBtn().addActionListener(e -> {
            Object[] options = {"PNG", "PDF", "Cancel"};
            int choice = JOptionPane.showOptionDialog(playView, "How would you like to export the results?", "Export Results", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (choice == 0) System.out.println("Exporting to PNG...");
            else if (choice == 1) System.out.println("Exporting to PDF...");

            // Export logic goes here
        });

        // Reset Button Logic
        playView.getResetBtn().addActionListener(e -> {
            playView.getPageRefField().setText("");
            playView.getDataGenDropdown().setSelectedIndex(0);
        });

        playView.getBackBtn().addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));   // Back to Dashboard View button

        dashboardView.getExitBtn().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit PaperTrail", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        dashboardView.getHelpBtn().addActionListener(e -> {
            cardLayout.show(cardPanel, "Help");
        });

        dashboardView.getAboutBtn().addActionListener(e -> {
            cardLayout.show(cardPanel, "About");
        });

        dashboardView.getSettingsBtn().addActionListener(e -> {
            System.out.println("PaperTrail settings button clicked!");
            // Future logic goes here
        });

        helpView.getBackBtn().addActionListener(e -> {
            cardLayout.show(cardPanel, "Dashboard");
        });

        aboutView.getBackBtn().addActionListener(e -> {
            cardLayout.show(cardPanel, "Dashboard");
        });

        aboutView.getGithubBtn().addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://github.com/YourUsername/YourRepository"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        dashboardView.getExitBtn().addActionListener(e -> {
            System.out.println("PaperTrail exit button clicked!");
            // System.exit(0); // Close application
            // Add confirmation JOption pane here
        });
    }

    /**
     * Handles the complex logic of the "All" checkbox interacting with the individual algorithms.
     */
    private void setupAlgorithmMenuLogic() {
        JCheckBoxMenuItem[] boxes = playView.getAlgoCheckboxes();
        JCheckBoxMenuItem allBox = boxes[0];

        // If "All" is clicked, check or uncheck everything else
        allBox.addActionListener(e -> {
            boolean isChecked = allBox.isSelected();

            // Start loop at 1 to skip "All" box itself
            for (int i = 1; i < boxes.length; i++) {
                boxes[i].setSelected(isChecked);
            }
        });

        // If individual item is clicked, check if we need to update "All" box
        for (int i = 1; i < boxes.length; i++) {
            boxes[i].addActionListener(e -> {
                JCheckBoxMenuItem clickedBox = (JCheckBoxMenuItem) e.getSource();

                if (!clickedBox.isSelected()) allBox.setSelected(false);    // If any single item is unchecked, "All" should definitely be unchecked
                else {
                    boolean allAreSelected = true;  // If item is checked, verify if everything is checked

                    // Start loop at 1 to check only individual algorithms
                    for (int j = 1; j < boxes.length; j++) {
                        if (!boxes[j].isSelected()) {
                            allAreSelected = false;
                            break;
                        }
                    }

                    // Update "All" box based on check
                    allBox.setSelected(allAreSelected);
                }
            });
        }
    }
}