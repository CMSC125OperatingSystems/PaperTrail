package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class SplashScreenView extends JPanel implements ThemeManager.ThemeObserver {
    private int progressTick = 0;

    public SplashScreenView() {
        ThemeManager.addObserver(this);
    }

    @Override
    public void onThemeChanged() {
        repaint(); // Just need to redraw with new theme colors
    }

    public void setProgressTick(int tick) {
        this.progressTick = tick;
        repaint();
    }

    public int getProgressTick() {
        return progressTick;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(ThemeManager.getBackgroundColor()); // Apply background
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = "PaperTrail";
        g2d.setFont(new Font("Arial", Font.BOLD, 150));

        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(text)) / 2;
        int textY = getHeight() / 2 - 60; // Shifted up slightly to fit subtitle

        g2d.setColor(ThemeManager.getAccentBlue());
        int thickness = 5;
        for (int i = -thickness; i <= thickness; i++) {
            for (int j = -thickness; j <= thickness; j++) {
                g2d.drawString(text, textX + i, textY + j);
            }
        }

        g2d.setColor(ThemeManager.getAccentOrange());
        g2d.drawString(text, textX, textY);

        // --- NEW: ADD SUBTITLE HERE ---
        String subtitle = "Page Replacement Algorithms Simulator";
        g2d.setFont(new Font("Arial", Font.PLAIN, 32)); // Clean, smaller font
        FontMetrics subMetrics = g2d.getFontMetrics();
        int subX = (getWidth() - subMetrics.stringWidth(subtitle)) / 2;
        int subY = textY + 60; // Position below the main title

        g2d.setColor(ThemeManager.getTextColor()); // Resolves nicely in Light/Dark theme
        g2d.drawString(subtitle, subX, subY);
        // ------------------------------

        int barWidth = 750, barHeight = 75, barX = (getWidth() - barWidth) / 2;
        int barY = subY + 60; // Push progress bar lower to account for subtitle
        int sectionWidth = barWidth / 10;

        for (int i = 0; i < 10; i++) {
            int currentSectionX = barX + (i * sectionWidth);
            if (i < progressTick) {
                g2d.setColor(ThemeManager.getAccentBlue());
                g2d.fillRect(currentSectionX, barY, sectionWidth, barHeight);
            }

            g2d.setColor(ThemeManager.getAccentOrange());
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(currentSectionX, barY, sectionWidth, barHeight);
            g2d.setStroke(new BasicStroke(1));
        }

        String percentageText = (progressTick * 10) + "%";
        g2d.setFont(new Font("Arial", Font.BOLD, 32));

        FontMetrics percentMetrics = g2d.getFontMetrics();
        int percentX = (getWidth() - percentMetrics.stringWidth(percentageText)) / 2;
        int percentY = barY + barHeight + 50;

        g2d.setColor(ThemeManager.getTextColor());
        g2d.drawString(percentageText, percentX, percentY);
    }
}