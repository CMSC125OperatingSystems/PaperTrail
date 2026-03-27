package cmsc125.project3;

import cmsc125.project3.controllers.MainController;
import cmsc125.project3.theme.ThemeManager;
import cmsc125.project3.views.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // 1. Force standard cross-platform look & feel to prevent OS Dark Mode clashing
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Initialize and Apply our Custom Theme
        ThemeManager.setTheme(ThemeManager.Theme.DARK); // Set to LIGHT or DARK here
        ThemeManager.applyGlobalUIManager();

        JFrame frame = new JFrame("PaperTrail Initialization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(ThemeManager.getBackgroundColor());

        // Initialize Views
        SplashScreenView splashPanel = new SplashScreenView();
        DashboardView dashboardPanel = new DashboardView();
        SimulateView simulatePanel = new SimulateView();
        HelpView helpPanel = new HelpView();
        AboutView aboutPanel = new AboutView();

        // Setup CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(ThemeManager.getBackgroundColor());

        // Add both views to "Deck", and give string name
        mainCardPanel.add(splashPanel, "Splash");
        mainCardPanel.add(dashboardPanel, "Dashboard");
        mainCardPanel.add(simulatePanel, "Simulate");
        mainCardPanel.add(helpPanel, "Help");
        mainCardPanel.add(aboutPanel, "About");

        // Show splash screen first
        cardLayout.show(mainCardPanel, "Splash");

        // Initialize Controller
        new MainController(splashPanel, dashboardPanel, simulatePanel, helpPanel, aboutPanel, mainCardPanel, cardLayout);

        // Add CardLayout panel to frame and display
        frame.add(mainCardPanel);
        frame.setVisible(true);
    }
}