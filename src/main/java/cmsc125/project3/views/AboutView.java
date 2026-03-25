package cmsc125.project3.views;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class AboutView extends JPanel {
    private JButton backBtn, githubBtn;
    private final Color THEME_BLUE = new Color(0x4343BF), THEME_ORANGE = new Color(0xFF8405);

    public AboutView() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        initComponents();
    }

    private void initComponents() {
        // Top Panel (Title & Description)
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));

        JLabel titleLabel = new JLabel("About PaperTrail", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(THEME_BLUE);

        String descText = "<html><div style='text-align: center;'>"
                + "PaperTrail is an interactive Page Replacement Algorithm Simulator.<br>"
                + "Built for CMSC 125 to help visualize and compare operating system memory management strategies."
                + "</div></html>";
        JLabel descLabel = new JLabel(descText, SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descLabel.setForeground(Color.DARK_GRAY);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(descLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel (Developers)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 30));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel teamTitle = new JLabel("Meet the Developers", SwingConstants.CENTER);
        teamTitle.setFont(new Font("Arial", Font.BOLD, 24));
        teamTitle.setForeground(THEME_ORANGE);
        centerPanel.add(teamTitle, BorderLayout.NORTH);

        JPanel devsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        devsPanel.setBackground(Color.WHITE);

        devsPanel.add(createDeveloperPanel("ali1x3", "Member", "/images/dev1_placeholder.png"));
        devsPanel.add(createDeveloperPanel("Schneidelstrom", "Group Leader", "/images/dev2_placeholder.png"));
        devsPanel.add(createDeveloperPanel("ddrhckrzz", "Member", "/images/dev3_placeholder.png"));

        centerPanel.add(devsPanel, BorderLayout.CENTER);

        // GitHub & Copyright Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(Color.WHITE);

        githubBtn = new JButton("<html><u>View source code on GitHub</u></html>");
        githubBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        githubBtn.setForeground(THEME_BLUE);
        githubBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubBtn.setContentAreaFilled(false);
        githubBtn.setBorderPainted(false);
        githubBtn.setFocusPainted(false);
        githubBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        JLabel copyrightLabel = new JLabel("\u00A9 " + currentYear + " PaperTrail Developers. All Rights Reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.GRAY);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(githubBtn);
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(copyrightLabel);

        centerPanel.add(footerPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel (Back Button)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Create formatted profile panel for developer
     */
    private JPanel createDeveloperPanel(String name, String role, String imagePath) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imageLabel.setPreferredSize(new Dimension(120, 120));
        imageLabel.setMaximumSize(new Dimension(120, 120));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                // Scale image to fit box
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setText("No Image");
                imageLabel.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            imageLabel.setText("Error");
        }

        // Name Label
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role Label
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setForeground(Color.DARK_GRAY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with vertical spacing
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