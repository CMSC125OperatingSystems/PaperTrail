package cmsc125.project3.views;

import javax.swing.*;
import java.awt.*;

public class SplashScreenView extends JPanel {
    private int progressTick = 0;

    public SplashScreenView() {
        // Constructor is empty, exists to initialize panel
    }

    // Controller will call method every 500ms to update bar
    public void setProgressTick(int tick) {
        this.progressTick = tick;
        repaint(); // Redraw graphics with new tick
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String text = "PaperTrail";
        g2d.setFont(new Font("Arial", Font.BOLD, 150));

        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(text)) / 2, textY = getHeight() / 2 - 20;

        g2d.setColor(new Color(0x4343BF));
        int thickness = 5;
        for (int i = -thickness; i <= thickness; i++) {
            for (int j = -thickness; j <= thickness; j++) {
                g2d.drawString(text, textX + i, textY + j);
            }
        }

        g2d.setColor(new Color(0xFF8405));
        g2d.drawString(text, textX, textY);

        int barWidth = 750, barHeight = 75, barX = (getWidth() - barWidth) / 2, barY = textY + 50, sectionWidth = barWidth / 10;
        for (int i = 0; i < 10; i++) {
            int currentSectionX = barX + (i * sectionWidth);
            if (i < progressTick) {
                g2d.setColor(new Color(0x4343BF));
                g2d.fillRect(currentSectionX, barY, sectionWidth, barHeight);
            }

            g2d.setColor(new Color(0xFF8405));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(currentSectionX, barY, sectionWidth, barHeight);
            g2d.setStroke(new BasicStroke(1));
        }

        String percentageText = (progressTick * 10) + "%";
        g2d.setFont(new Font("Arial", Font.BOLD, 32));

        FontMetrics percentMetrics = g2d.getFontMetrics();
        int percentX = (getWidth() - percentMetrics.stringWidth(percentageText)) / 2, percentY = barY + barHeight + 50;
        g2d.setColor(Color.BLACK);
        g2d.drawString(percentageText, percentX, percentY);
    }
}