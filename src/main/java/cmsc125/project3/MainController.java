package cmsc125.project3;

import cmsc125.project3.services.*;
import cmsc125.project3.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.StringJoiner;

public class MainController {
    // ... (all existing private final fields remain the same) ...
    private final SplashScreenView splashView;
    private final DashboardView dashboardView;
    private final SimulateView simulateView;
    private final HelpView helpView;
    private final AboutView aboutView;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private Timer splashTimer;
    private int currentTick = 0;
    private Timer simTimer;
    private FIFO currentSimulation;
    private int faults = 0;
    private int simSteps = 0;

    public MainController(SplashScreenView splashView, DashboardView dashboardView, SimulateView simulateView, HelpView helpView, AboutView aboutView, JPanel cardPanel, CardLayout cardLayout) {
        this.splashView = splashView;
        this.dashboardView = dashboardView;
        this.simulateView = simulateView;
        this.helpView = helpView;
        this.cardPanel = cardPanel;
        this.aboutView = aboutView;
        this.cardLayout = cardLayout;

        initController();
    }

    private void initController() {
        // ... (splash timer logic remains the same) ...
        splashTimer = new Timer(100, e -> {
            if (currentTick < 10) {
                currentTick++;
                splashView.setProgressTick(currentTick);
            } else {
                splashTimer.stop();
                cardLayout.show(cardPanel, "Dashboard");
            }
        });
        splashTimer.start();

        dashboardView.getStartBtn().addActionListener(e -> cardLayout.show(cardPanel, "Simulate"));

        // *** NEW: Open Settings Dialog ***
        dashboardView.getSettingsBtn().addActionListener(e -> {
            // Find the top-level JFrame to act as the owner of the dialog
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(dashboardView);
            SettingsDialog dialog = new SettingsDialog(topFrame);
            dialog.setVisible(true);
        });

        // ... (all other listeners in initController remain the same) ...
        simulateView.getDataGenDropdown().addActionListener(e -> {
            String selectedOption = (String) simulateView.getDataGenDropdown().getSelectedItem();
            if (selectedOption != null && selectedOption.contains(".txt file")) {
                simulateView.getPageRefField().setEnabled(false);
                simulateView.getActionBtn().setText("Browse");
            } else if (selectedOption != null && selectedOption.equals("Random")) {
                simulateView.getPageRefField().setEnabled(true);
                simulateView.getActionBtn().setText("▶");
                simulateView.getPageRefField().setText(generateRandomString(15, 9));
            } else {
                simulateView.getPageRefField().setEnabled(true);
                simulateView.getActionBtn().setText("▶");
            }
        });

        simulateView.getActionBtn().addActionListener(e -> {
            if ("Browse".equals(simulateView.getActionBtn().getText())) {
                System.out.println("Opening File Chooser...");
            } else {
                startSimulation();
            }
        });

        simulateView.getSpeedDropdown().addActionListener(e -> {
            if (simTimer != null && simTimer.isRunning()) {
                simTimer.setDelay(getSpeedDelay());
            }
        });

        simulateView.getRedoBtn().addActionListener(e -> startSimulation());

        simulateView.getOutputBtn().addActionListener(e -> {
            Object[] options = {"PNG", "PDF", "Cancel"};
            int choice = JOptionPane.showOptionDialog(simulateView, "How would you like to export the results?", "Output Algorithm", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice == 0) System.out.println("Exporting to PNG...");
            else if (choice == 1) System.out.println("Exporting to PDF...");
        });

        simulateView.getResetBtn().addActionListener(e -> {
            if (simTimer != null) simTimer.stop();
            simulateView.getPageRefField().setText("");
            simulateView.getDataGenDropdown().setSelectedIndex(0);
            simulateView.getSimulationBoard().clear();
            faults = 0;
            simSteps = 0;
            simulateView.updateFaults(faults);
            simulateView.updateSteps(simSteps);
        });

        simulateView.getBackBtn().addActionListener(e -> {
            if (simTimer != null) simTimer.stop();
            cardLayout.show(cardPanel, "Dashboard");
        });

        dashboardView.getExitBtn().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit PaperTrail", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        dashboardView.getHelpBtn().addActionListener(e -> cardLayout.show(cardPanel, "Help"));
        dashboardView.getAboutBtn().addActionListener(e -> cardLayout.show(cardPanel, "About"));
        helpView.getBackBtn().addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        aboutView.getBackBtn().addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));

        aboutView.getGithubBtn().addActionListener(e -> {
            try { Desktop.getDesktop().browse(new java.net.URI("https://github.com/YourUsername/YourRepository")); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
    }

    private void startSimulation() {
        if (simTimer != null) simTimer.stop();

        String input = simulateView.getPageRefField().getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(simulateView, "Please enter a page reference string.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] sequenceStr = input.split(",");
        int[] sequence = new int[sequenceStr.length];
        try {
            for (int i = 0; i < sequenceStr.length; i++) {
                sequence[i] = Integer.parseInt(sequenceStr[i].trim());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(simulateView, "Sequence contains invalid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int frameSize = (int) simulateView.getFrameSizeSpinner().getValue();
        String algoChoice = (String) simulateView.getAlgoDropdown().getSelectedItem();

        if (algoChoice.contains("FIFO")) currentSimulation = new FIFO(sequence, frameSize);
        else if (algoChoice.contains("LRU")) currentSimulation = new LRU(sequence, frameSize);
        else if (algoChoice.contains("OPT")) currentSimulation = new OPT(sequence, frameSize);
        else if (algoChoice.contains("SC:")) currentSimulation = new SC(sequence, frameSize);
        else if (algoChoice.contains("ESC:")) currentSimulation = new ESC(sequence, frameSize);
        else if (algoChoice.contains("LFU")) currentSimulation = new LFU(sequence, frameSize);
        else if (algoChoice.contains("MFU")) currentSimulation = new MFU(sequence, frameSize);
        else currentSimulation = new FIFO(sequence, frameSize);

        simulateView.getSimulationBoard().clear();
        simulateView.getSimulationBoard().setFrameSize(frameSize);
        faults = 0;
        simSteps = 0;
        simulateView.updateFaults(faults);
        simulateView.updateSteps(simSteps);

        simTimer = new Timer(getSpeedDelay(), ev -> {
            simSteps++;
            simulateView.updateSteps(simSteps); // Changed from updateTimer

            boolean hasNext = currentSimulation.simulateStep();
            if (!hasNext) {
                simTimer.stop();
                return;
            }

            int page = currentSimulation.getReference()[currentSimulation.getReferencePointer() - 1];
            boolean isHit = currentSimulation.getPageStatus();
            int[] frames = currentSimulation.getFrame();

            if (!isHit) {
                faults++;
                simulateView.updateFaults(faults);
            }

            simulateView.getSimulationBoard().addStep(page, frames, isHit);

            JScrollBar horizontalScrollBar = simulateView.getScrollPane().getHorizontalScrollBar();
            horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
        });
        simTimer.start();
    }

    private int getSpeedDelay() {
        String speedStr = (String) simulateView.getSpeedDropdown().getSelectedItem();
        double speedMultiplier = Double.parseDouble(speedStr.replace("x", ""));
        return (int) (1000 / speedMultiplier);
    }

    private String generateRandomString(int count, int maxNumber) {
        Random rand = new Random();
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < count; i++) {
            joiner.add(String.valueOf(rand.nextInt(maxNumber + 1)));
        }
        return joiner.toString();
    }
}