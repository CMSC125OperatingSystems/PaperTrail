package cmsc125.project3.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class DashboardView extends JPanel {
    private JButton helpBtn, aboutBtn, settingsBtn, startBtn, exitBtn;

    public DashboardView() {
        setLayout(new BorderLayout());

        // TOP PANEL (Help, About, Settings)
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

        // CENTER PANEL (Custom PaperTrail Button)
        startBtn = new PaperTrailButton();

        // To not stretch filling up entire screen
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.add(startBtn);

        add(centerWrapper, BorderLayout.CENTER);

        // BOTTOM PANEL (Exit Button)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 0));
        exitBtn = createImageButton("/images/exit.png", "Exit");
        bottomPanel.add(exitBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Load images
    private JButton createImageButton(String imagePath, String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                btn.setIcon(new ImageIcon(imgURL));
                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
            } else {
                System.out.println("Could not find image: " + imagePath);
            }
        } catch (Exception e) {
            System.out.println("Error loading image: " + imagePath);
        }

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class PaperTrailButton extends JButton {
        private Color topColor = new Color(0xFF8405), shadowColor = new Color(0x4343BF), playFillColor = new Color(0x4343BF), playStrokeColor = new Color(0xFF8405);

        public PaperTrailButton() {
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Set strict size so GridBagLayout knows exactly how big the clickable area is
            setPreferredSize(new Dimension(850, 250));

            // Swap colors on hover then redraw button
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    topColor = new Color(0x4343BF);
                    shadowColor = new Color(0xFF8405);
                    playFillColor = new Color(0xFF8405);
                    playStrokeColor = new Color(0x4343BF);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    topColor = new Color(0xFF8405);
                    shadowColor = new Color(0x4343BF);
                    playFillColor = new Color(0x4343BF);
                    playStrokeColor = new Color(0xFF8405);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String text = "PaperTrail";
            g2d.setFont(new Font("Arial", Font.BOLD, 150));
            FontMetrics metrics = g2d.getFontMetrics();

            // Calculate center positions
            int textX = (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = getHeight() / 2 + 50;

            // Draw 5px Shadow
            g2d.setColor(shadowColor);
            int thickness = 5;
            for (int i = -thickness; i <= thickness; i++) {
                for (int j = -thickness; j <= thickness; j++) {
                    g2d.drawString(text, textX + i, textY + j);
                }
            }

            // Draw Top Text
            g2d.setColor(topColor);
            g2d.drawString(text, textX, textY);

            // Draw Outlined Play Icon using TextLayout
            String playIcon = "▶︎";
            Font playFont = new Font("Arial", Font.BOLD, 200);
            TextLayout layout = new TextLayout(playIcon, playFont, g2d.getFontRenderContext()); // Create TextLayout to extract vector shape of play symbol
            int playX = (getWidth() - (int) layout.getAdvance()) / 2;
            AffineTransform originalTransform = g2d.getTransform(); // Save original graphics state before moving it
            g2d.translate(playX, textY);     // Move graphics cursor to play icon location
            Shape playShape = layout.getOutline(null);  // Get outline shape

            // Fill inside of play icon
            g2d.setColor(playFillColor);
            g2d.fill(playShape);

            // Draw outside stroke (outline) of play icon
            g2d.setColor(playStrokeColor);
            g2d.setStroke(new BasicStroke(5));
            g2d.draw(playShape);

            // Reset graphics state back to normal
            g2d.setTransform(originalTransform);
        }
    }

    public JButton getHelpBtn() { return helpBtn; }
    public JButton getAboutBtn() { return aboutBtn; }
    public JButton getSettingsBtn() { return settingsBtn; }
    public JButton getStartBtn() { return startBtn; }
    public JButton getExitBtn() { return exitBtn; }
}