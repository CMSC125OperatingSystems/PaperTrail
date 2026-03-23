package cmsc125.project3;

import cmsc125.project3.views.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {
    private final SplashScreenView splashView;
    private final DashboardView dashboardView;
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private Timer timer;
    private int currentTick = 0;

    public MainController(SplashScreenView splashView, DashboardView dashboardView, JPanel cardPanel, CardLayout cardLayout) {
        this.splashView = splashView;
        this.dashboardView = dashboardView;
        this.cardPanel = cardPanel;
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
            System.out.println("PaperTrail start button clicked!");
            // Future logic goes here
        });

        dashboardView.getHelpBtn().addActionListener(e -> {
            System.out.println("PaperTrail help button clicked!");
            // Future logic goes here
        });

        dashboardView.getAboutBtn().addActionListener(e -> {
            System.out.println("PaperTrail about button clicked!");
            // Future logic goes here
        });

        dashboardView.getSettingsBtn().addActionListener(e -> {
            System.out.println("PaperTrail settings button clicked!");
            // Future logic goes here
        });

        dashboardView.getExitBtn().addActionListener(e -> {
            System.out.println("PaperTrail exit button clicked!");
            // System.exit(0); // Close application
            // Add confirmation JOption pane here
        });
    }
}