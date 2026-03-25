package cmsc125.project3;

import cmsc125.project3.views.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PaperTrail Initialization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Initialize Views
        SplashScreenView splashPanel = new SplashScreenView();
        DashboardView dashboardPanel = new DashboardView();
        PlayView playPanel = new PlayView();
        HelpView helpPanel = new HelpView();
        AboutView aboutPanel = new AboutView();

        // Setup CardLayout
        CardLayout cardLayout = new CardLayout();
        JPanel mainCardPanel = new JPanel(cardLayout);

        // Add both views to "Deck", and give string name
        mainCardPanel.add(splashPanel, "Splash");
        mainCardPanel.add(dashboardPanel, "Dashboard");
        mainCardPanel.add(playPanel, "Play");
        mainCardPanel.add(helpPanel, "Help");
        mainCardPanel.add(aboutPanel, "About");

        // Show splash screen first
        cardLayout.show(mainCardPanel, "Splash");

        // Initialize Controller, passing in views and CardLayout setup
        new MainController(splashPanel, dashboardPanel, playPanel, helpPanel, aboutPanel, mainCardPanel, cardLayout);

        // Add CardLayout panel to frame and display
        frame.add(mainCardPanel);
        frame.setVisible(true);
    }
}