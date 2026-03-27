package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class AboutView extends JPanel implements ThemeManager.ThemeObserver {
    private JButton backBtn, githubBtn;
    private JLabel titleLabel, descLabel, teamTitle, copyrightLabel;
    private JPanel centerPanel, devsPanel, footerPanel;

    public AboutView() {
        initComponents();
        applyTheme();
        ThemeManager.addObserver(this);
    }

    @Override
    public void onThemeChanged() {
        applyTheme();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        titleLabel = new JLabel("About PaperTrail", SwingConstants.CENTER);
        String descText = "<html><div style='text-align: center;'>"
            + "PaperTrail is an interactive Page Replacement Algorithm Simulator.<br>"
            + "Built for CMSC 125 to help visualize and compare operating system memory management strategies."
            + "</div></html>";
        descLabel = new JLabel(descText, SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(descLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout(0, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        teamTitle = new JLabel("Meet the Developers", SwingConstants.CENTER);
        centerPanel.add(teamTitle, BorderLayout.NORTH);

        devsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        devsPanel.add(createDeveloperPanel("ali1x3", "Member", "/images/dev1_placeholder.png"));
        devsPanel.add(createDeveloperPanel("Schneidelstrom", "Group Leader", "/images/dev2_placeholder.png"));
        devsPanel.add(createDeveloperPanel("ddrhckrzz", "Member", "/images/dev3_placeholder.png"));
        centerPanel.add(devsPanel, BorderLayout.CENTER);

        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        githubBtn = new JButton();
        githubBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubBtn.setContentAreaFilled(false);
        githubBtn.setBorderPainted(false);
        githubBtn.setFocusPainted(false);
        githubBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        copyrightLabel = new JLabel("\u00A9 " + currentYear + " PaperTrail Developers. All Rights Reserved.");
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(githubBtn);
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(copyrightLabel);
        centerPanel.add(footerPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void applyTheme() {
        updatePanelBackgrounds(this, ThemeManager.getBackgroundColor());

        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(ThemeManager.getAccentBlue());

        descLabel.setFont(new Font("Arial", Font.PLAIN, ThemeManager.getPrimaryFontSize()));
        descLabel.setForeground(ThemeManager.getSecondaryTextColor());

        teamTitle.setFont(new Font("Arial", Font.BOLD, 24));
        teamTitle.setForeground(ThemeManager.getAccentOrange());

        String githubHex = ThemeManager.colorToHex(ThemeManager.getAccentBlue());
        githubBtn.setText("<html><u style='color:" + githubHex + ";'>View source code on GitHub</u></html>");
        githubBtn.setFont(new Font("Arial", Font.PLAIN, ThemeManager.getSecondaryFontSize()));

        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, ThemeManager.getTertiaryFontSize()));
        copyrightLabel.setForeground(ThemeManager.getSecondaryTextColor());

        backBtn.setBackground(ThemeManager.getPanelColor());
        backBtn.setForeground(ThemeManager.getTextColor());
    }

    private void updatePanelBackgrounds(Container container, Color bg) {
        container.setBackground(bg);
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                updatePanelBackgrounds((JPanel)c, bg);
            }
        }
    }

    private JPanel createDeveloperPanel(String name, String role, String imagePath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.getPanelColor()); // Use panel color for contrast

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setMaximumSize(new Dimension(120, 120));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No Image");
            }
        } catch (Exception e) {
            imageLabel.setText("Error");
        }

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(ThemeManager.getTextColor());

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setForeground(ThemeManager.getSecondaryTextColor());

        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(roleLabel);
        return panel;
    }

    public JButton getBackBtn() { return backBtn; }
    public JButton getGithubBtn() { return githubBtn; }
}