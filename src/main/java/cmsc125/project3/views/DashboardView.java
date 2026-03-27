package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView extends JPanel implements ThemeManager.ThemeObserver {
    private JButton helpBtn, aboutBtn, settingsBtn, startBtn, exitBtn;

    public DashboardView() {
        initComponents();
        applyTheme();
        ThemeManager.addObserver(this);
    }

    @Override
    public void onThemeChanged() {
        applyTheme();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 0, 100));

        helpBtn = createImageButton("/images/help.png", "Help");
        aboutBtn = createImageButton("/images/about.png", "About");
        settingsBtn = createImageButton("/images/settings.png", "Settings");

        JPanel centerTopPanel = new JPanel();
        centerTopPanel.add(aboutBtn);

        topPanel.add(helpBtn, BorderLayout.WEST);
        topPanel.add(centerTopPanel, BorderLayout.CENTER);
        topPanel.add(settingsBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        startBtn = new PaperTrailButton();
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(startBtn);
        add(centerWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        exitBtn = createImageButton("/images/exit.png", "Exit");
        bottomPanel.add(exitBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyTheme() {
        updatePanelBackgrounds(this, ThemeManager.getBackgroundColor());
        helpBtn.setForeground(ThemeManager.getTextColor());
        aboutBtn.setForeground(ThemeManager.getTextColor());
        settingsBtn.setForeground(ThemeManager.getTextColor());
        exitBtn.setForeground(ThemeManager.getTextColor());
        startBtn.repaint();
    }

    private void updatePanelBackgrounds(Container container, Color bg) {
        container.setBackground(bg);
        for (Component c : container.getComponents()) {
            if (c instanceof Container) {
                updatePanelBackgrounds((Container) c, bg);
            }
        }
    }

    private JButton createImageButton(String imagePath, String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                btn.setIcon(new ImageIcon(imgURL));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
            }
        } catch (Exception e) {}
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class PaperTrailButton extends JButton {
        private Color topColor, shadowColor, playFillColor, playStrokeColor;

        public PaperTrailButton() {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(850, 250));
            resetDefaultColors();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    topColor = ThemeManager.getAccentBlue();
                    shadowColor = ThemeManager.getAccentOrange();
                    playFillColor = ThemeManager.getAccentOrange();
                    playStrokeColor = ThemeManager.getAccentBlue();
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    resetDefaultColors();
                    repaint();
                }
            });
        }

        private void resetDefaultColors() {
            topColor = ThemeManager.getAccentOrange();
            shadowColor = ThemeManager.getAccentBlue();
            playFillColor = ThemeManager.getAccentBlue();
            playStrokeColor = ThemeManager.getAccentOrange();
        }

        @Override
        public void repaint() {
            resetDefaultColors(); // Ensure colors are correct before repainting
            super.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String text = "PaperTrail";
            g2d.setFont(new Font("Arial", Font.BOLD, 150));
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = getHeight() / 2 + 50;

            // Draw Shadow
            g2d.setColor(shadowColor);
            int thickness = 5;
            for (int i = -thickness; i <= thickness; i++) {
                for (int j = -thickness; j <= thickness; j++) {
                    g2d.drawString(text, textX + i, textY + j);
                }
            }
            // Draw Main Text
            g2d.setColor(topColor);
            g2d.drawString(text, textX, textY);

            // Draw Custom Polygon Play Icon (Fixes the missing glyph box issue completely)
            int size = 160;
            int px = (getWidth() - size) / 2 + 10;
            int py = (getHeight() - size) / 2;

            Polygon playShape = new Polygon();
            playShape.addPoint(px, py); // Top left
            playShape.addPoint(px + size, py + size / 2); // Center Right
            playShape.addPoint(px, py + size); // Bottom left

            g2d.setColor(playFillColor);
            g2d.fillPolygon(playShape);

            g2d.setColor(playStrokeColor);
            g2d.setStroke(new BasicStroke(6));
            g2d.drawPolygon(playShape);
        }
    }

    public JButton getHelpBtn() { return helpBtn; }
    public JButton getAboutBtn() { return aboutBtn; }
    public JButton getSettingsBtn() { return settingsBtn; }
    public JButton getStartBtn() { return startBtn; }
    public JButton getExitBtn() { return exitBtn; }
}