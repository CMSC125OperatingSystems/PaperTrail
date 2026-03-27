package cmsc125.project3.views;

import cmsc125.project3.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {

    public SettingsDialog(Frame owner) {
        super(owner, "Settings", true);
        setLayout(new BorderLayout(20, 20));
        setSize(400, 250);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(ThemeManager.getPanelColor());
        getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- THEME SELECTION ---
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setBackground(ThemeManager.getPanelColor());
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        themeLabel.setForeground(ThemeManager.getTextColor());

        JRadioButton lightRadio = new JRadioButton("Light");
        lightRadio.setBackground(ThemeManager.getPanelColor());
        lightRadio.setForeground(ThemeManager.getTextColor());
        lightRadio.setSelected(ThemeManager.getTheme() == ThemeManager.Theme.LIGHT);

        JRadioButton darkRadio = new JRadioButton("Dark");
        darkRadio.setBackground(ThemeManager.getPanelColor());
        darkRadio.setForeground(ThemeManager.getTextColor());
        darkRadio.setSelected(ThemeManager.getTheme() == ThemeManager.Theme.DARK);

        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(lightRadio);
        themeGroup.add(darkRadio);

        themePanel.add(themeLabel);
        themePanel.add(lightRadio);
        themePanel.add(darkRadio);

        // --- FONT SIZE SELECTION ---
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fontPanel.setBackground(ThemeManager.getPanelColor());
        JLabel fontLabel = new JLabel("Font Size:");
        fontLabel.setFont(new Font("Arial", Font.BOLD, 16));
        fontLabel.setForeground(ThemeManager.getTextColor());

        JRadioButton smallRadio = new JRadioButton("Small");
        smallRadio.setBackground(ThemeManager.getPanelColor());
        smallRadio.setForeground(ThemeManager.getTextColor());
        smallRadio.setSelected(ThemeManager.getFontSize() == ThemeManager.FontSize.SMALL);

        JRadioButton mediumRadio = new JRadioButton("Medium");
        mediumRadio.setBackground(ThemeManager.getPanelColor());
        mediumRadio.setForeground(ThemeManager.getTextColor());
        mediumRadio.setSelected(ThemeManager.getFontSize() == ThemeManager.FontSize.MEDIUM);

        JRadioButton largeRadio = new JRadioButton("Large");
        largeRadio.setBackground(ThemeManager.getPanelColor());
        largeRadio.setForeground(ThemeManager.getTextColor());
        largeRadio.setSelected(ThemeManager.getFontSize() == ThemeManager.FontSize.LARGE);

        ButtonGroup fontGroup = new ButtonGroup();
        fontGroup.add(smallRadio);
        fontGroup.add(mediumRadio);
        fontGroup.add(largeRadio);

        fontPanel.add(fontLabel);
        fontPanel.add(smallRadio);
        fontPanel.add(mediumRadio);
        fontPanel.add(largeRadio);

        // --- BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeManager.getPanelColor());
        JButton saveButton = new JButton("Save & Close");
        saveButton.setBackground(ThemeManager.getAccentBlue());
        saveButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> {
            ThemeManager.Theme newTheme = darkRadio.isSelected() ? ThemeManager.Theme.DARK : ThemeManager.Theme.LIGHT;
            ThemeManager.setTheme(newTheme);

            ThemeManager.FontSize newSize = largeRadio.isSelected() ? ThemeManager.FontSize.LARGE : (mediumRadio.isSelected() ? ThemeManager.FontSize.MEDIUM : ThemeManager.FontSize.SMALL);
            ThemeManager.setFontSize(newSize);

            // Trigger the live refresh across the whole app!
            ThemeManager.broadcastThemeChange();
            SwingUtilities.updateComponentTreeUI(owner);
            dispose();
        });

        buttonPanel.add(saveButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(ThemeManager.getPanelColor());
        centerPanel.add(themePanel);
        centerPanel.add(fontPanel);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}