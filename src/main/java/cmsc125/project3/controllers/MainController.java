package cmsc125.project3.controllers;

import cmsc125.project3.services.*;
import cmsc125.project3.theme.ThemeManager;
import cmsc125.project3.views.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;
import java.awt.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class MainController {
    private final SimulateView simulateView;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;

    private Timer simTimer;
    private List<FIFO> runningSimulations;
    private List<String> runningAlgoNames;
    private int totalSteps;

    public MainController(SplashScreenView splashView, DashboardView dashboardView, SimulateView simulateView, HelpView helpView, AboutView aboutView, JPanel cardPanel, CardLayout cardLayout) {
        this.simulateView = simulateView;
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        this.runningSimulations = new ArrayList<>();
        this.runningAlgoNames = new ArrayList<>();

        initGeneralListeners(splashView, dashboardView, helpView, aboutView);
        initSimulationListeners();
        setupAlgorithmMenuLogic();
    }

    private void initGeneralListeners(SplashScreenView splash, DashboardView dash, HelpView help, AboutView about) {
        new Timer(100, e -> {
            splash.setProgressTick(splash.getProgressTick() + 1);
            if (splash.getProgressTick() >= 10) {
                ((Timer)e.getSource()).stop();
                cardLayout.show(cardPanel, "Dashboard");
            }
        }).start();

        dash.getStartBtn().addActionListener(e -> cardLayout.show(cardPanel, "Simulate"));
        dash.getSettingsBtn().addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(dash);
            new SettingsDialog(topFrame).setVisible(true);
        });
        dash.getHelpBtn().addActionListener(e -> cardLayout.show(cardPanel, "Help"));
        dash.getAboutBtn().addActionListener(e -> cardLayout.show(cardPanel, "About"));
        dash.getExitBtn().addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit PaperTrail", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        simulateView.getBackBtn().addActionListener(e -> { if (simTimer != null) simTimer.stop(); cardLayout.show(cardPanel, "Dashboard"); });
        help.getBackBtn().addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        about.getBackBtn().addActionListener(e -> cardLayout.show(cardPanel, "Dashboard"));
        about.getGithubBtn().addActionListener(e -> { try { Desktop.getDesktop().browse(new java.net.URI("https://github.com/CMSC125OperatingSystems/PaperTrail.git")); } catch (Exception ex) { ex.printStackTrace(); } });
    }

    private void initSimulationListeners() {
        simulateView.getDataGenDropdown().addActionListener(e -> {
            boolean isFile = "User-defined input from a .txt file".equals(simulateView.getDataGenDropdown().getSelectedItem());
            simulateView.getRandomBtn().setEnabled(!isFile);
            simulateView.getBrowseBtn().setEnabled(isFile);
            simulateView.getPageRefField().setEnabled(!isFile);
        });

        simulateView.getRandomBtn().addActionListener(e -> {
            String lengthStr = JOptionPane.showInputDialog(simulateView, "Enter reference string size (10-40):", "20");
            if (lengthStr == null) return; // User canceled
            try {
                int length = Integer.parseInt(lengthStr);
                if (length < 10 || length > 40) throw new NumberFormatException();
                simulateView.getPageRefField().setText(generateRandomString(length, 9));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(simulateView, "Invalid size. Please enter a number between 10 and 40.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        simulateView.getBrowseBtn().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            if (fileChooser.showOpenDialog(simulateView) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (Scanner scanner = new Scanner(file)) {
                    if (scanner.hasNextLine()) {
                        simulateView.getPageRefField().setText(scanner.nextLine().trim());
                    }
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(simulateView, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- LIVE SPEED UPDATES ---
        simulateView.getSpeedDropdown().addActionListener(e -> {
            if (simTimer != null) {
                simTimer.setDelay(getSpeedDelay());
            }
        });

        simulateView.getRunBtn().addActionListener(e -> startSimulation());

        simulateView.getResetBtn().addActionListener(e -> {
            if (simTimer != null) simTimer.stop();
            simulateView.getPageRefField().setText("");
            simulateView.clearResults();
            simulateView.setInfo(" ", 0);
        });

        simulateView.getOutputBtn().addActionListener(e -> {
            exportResults();
        });
    }

    private void startSimulation() {
        if (simTimer != null) simTimer.stop();

        String input = simulateView.getPageRefField().getText().trim();
        if (input.isEmpty()) { JOptionPane.showMessageDialog(simulateView, "Page reference string is empty.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }

        String[] sequenceStr = input.split(",");
        int[] sequence = new int[sequenceStr.length];
        try {
            for (int i = 0; i < sequenceStr.length; i++) sequence[i] = Integer.parseInt(sequenceStr[i].trim());
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(simulateView, "Invalid characters in reference string.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }

        List<String> selectedAlgos = getSelectedAlgorithms();
        if (selectedAlgos.isEmpty()) { JOptionPane.showMessageDialog(simulateView, "Please select at least one algorithm.", "Input Error", JOptionPane.WARNING_MESSAGE); return; }

        int frameSize = (int) simulateView.getFrameSizeSpinner().getValue();
        totalSteps = sequence.length;

        simulateView.clearResults();
        simulateView.setInfo(input, frameSize);
        runningSimulations.clear();
        runningAlgoNames.clear();

        for (String algoName : selectedAlgos) {
            FIFO sim = createAlgorithmInstance(algoName, sequence, frameSize);
            runningSimulations.add(sim);
            runningAlgoNames.add(algoName);
            simulateView.addAlgorithmPanel(algoName, frameSize);
        }

        simTimer = new Timer(getSpeedDelay(), e -> simulationStep());
        simTimer.start();
    }

    private void simulationStep() {
        if (runningSimulations.isEmpty() || runningSimulations.get(0).getReferencePointer() >= totalSteps) {
            finishSimulation();
            return;
        }

        for (int i = 0; i < runningSimulations.size(); i++) {
            FIFO sim = runningSimulations.get(i);
            sim.simulateStep();

            int page = sim.getReference()[sim.getReferencePointer() - 1];
            boolean isHit = sim.getPageStatus();
            int[] frames = sim.getFrame();

            simulateView.addStepToAlgorithm(i, page, frames, isHit);
        }
    }

    private void finishSimulation() {
        simTimer.stop();
        for (int i = 0; i < runningSimulations.size(); i++) {
            int faults = 0;
            FIFO sim = createAlgorithmInstance(runningAlgoNames.get(i), runningSimulations.get(i).getReference(), runningSimulations.get(i).getFrame().length);
            while(sim.simulateStep()) {
                if (!sim.getPageStatus()) faults++;
            }
            int hits = totalSteps - faults;
            double faultRate = (double) faults / totalSteps;
            simulateView.addSummaryToAlgorithm(i, hits, faults, faultRate);
        }
    }

    /**
     * Captures the entire resultsContainer as an image and saves it as PNG or PDF.
     */
    private void exportResults() {
        JPanel results = simulateView.getResultsContainer();

        if (results.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(simulateView, "No simulation results to export.", "Export Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Let user choose format
        Object[] options = {"PNG Image", "PDF Document", "Cancel"};
        int choice = JOptionPane.showOptionDialog(simulateView,
                "Exporting all current algorithm results.\nSelect Export Format:",
                "Export Results",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return; // User cancelled

        boolean isPdf = (choice == 1);

        // 2. Setup File Chooser
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setDialogTitle("Save Export As");

        if (isPdf) {
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Document", "pdf"));
        } else {
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));
        }

        if (fileChooser.showSaveDialog(simulateView) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Ensure correct file extension
            String ext = isPdf ? ".pdf" : ".png";
            if (!file.getName().toLowerCase().endsWith(ext)) {
                file = new File(file.getAbsolutePath() + ext);
            }

            try {
                // 3. CAPTURE LOGIC
                int width = results.getWidth();
                int height = results.getHeight();

                // If the panel hasn't been fully painted or is 0, use preferred size
                if (width <= 0 || height <= 0) {
                    width = results.getPreferredSize().width;
                    height = results.getPreferredSize().height;
                }

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();

                // Set background for the image based on theme
                g2.setColor(ThemeManager.getBackgroundColor());
                g2.fillRect(0, 0, width, height);

                // Paint the results container onto the image
                results.paint(g2);
                g2.dispose();

                // 4. SAVE based on user choice
                if (isPdf) {
                    try (PDDocument document = new PDDocument()) {
                        // Create a custom PDF page size matching the image dimensions
                        PDPage page = new PDPage(new PDRectangle(width, height));
                        document.addPage(page);

                        // Embed the image into the PDF
                        PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
                        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                            contentStream.drawImage(pdImage, 0, 0, width, height);
                        }
                        document.save(file);
                    }
                } else {
                    ImageIO.write(image, "png", file);
                }

                JOptionPane.showMessageDialog(simulateView,
                        "Successfully exported results to:\n" + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(simulateView, "Failed to export: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private FIFO createAlgorithmInstance(String name, int[] ref, int fs) {
        switch(name) {
            case "FIFO": return new FIFO(ref, fs);
            case "LRU": return new LRU(ref, fs);
            case "OPT": return new OPT(ref, fs);
            case "SC": return new SC(ref, fs);
            case "ESC": return new ESC(ref, fs);
            case "LFU": return new LFU(ref, fs);
            case "MFU": return new MFU(ref, fs);
            default: return null;
        }
    }

    private List<String> getSelectedAlgorithms() {
        List<String> selected = new ArrayList<>();
        JCheckBoxMenuItem[] boxes = simulateView.getAlgoCheckboxes();
        for (int i = 1; i < boxes.length; i++) { // Start at 1 to skip "All"
            if (boxes[i].isSelected()) {
                selected.add(boxes[i].getText());
            }
        }
        return selected;
    }

    private void setupAlgorithmMenuLogic() {
        JCheckBoxMenuItem[] boxes = simulateView.getAlgoCheckboxes();
        JCheckBoxMenuItem allBox = boxes[0];

        allBox.addActionListener(e -> {
            for (int i = 1; i < boxes.length; i++) boxes[i].setSelected(allBox.isSelected());
        });

        for (int i = 1; i < boxes.length; i++) {
            boxes[i].addActionListener(e -> {
                boolean allSelected = true;
                for (int j = 1; j < boxes.length; j++) {
                    if (!boxes[j].isSelected()) {
                        allSelected = false;
                        break;
                    }
                }
                allBox.setSelected(allSelected);
            });
        }
    }

    private int getSpeedDelay() {
        String speedStr = (String) simulateView.getSpeedDropdown().getSelectedItem();
        double multiplier = Double.parseDouble(speedStr.replace("x", ""));
        return (int) (1000 / multiplier);
    }

    private String generateRandomString(int count, int max) {
        Random rand = new Random();
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < count; i++) joiner.add(String.valueOf(rand.nextInt(max + 1)));
        return joiner.toString();
    }
}