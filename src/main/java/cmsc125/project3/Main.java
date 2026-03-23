package cmsc125.project3;

import cmsc125.project3.views.SplashScreenView;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PaperTrail Initialization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        SplashScreenView splashPanel = new SplashScreenView();

        frame.add(splashPanel);
        frame.setVisible(true);
    }
}