package cmsc125.project3.theme;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {
    public enum Theme { LIGHT, DARK }

    // Toggle this to Theme.LIGHT or Theme.DARK to change the app's look!
    private static Theme currentTheme = Theme.DARK;

    public static void setTheme(Theme theme) {
        currentTheme = theme;
        applyGlobalUIManager();
    }

    public static Theme getTheme() { return currentTheme; }

    // -- Color Definitions --
    public static Color getBackgroundColor() {
        return currentTheme == Theme.LIGHT ? Color.WHITE : new Color(0x121212);
    }

    public static Color getPanelColor() {
        return currentTheme == Theme.LIGHT ? Color.WHITE : new Color(0x1E1E1E);
    }

    public static Color getTextColor() {
        return currentTheme == Theme.LIGHT ? Color.BLACK : new Color(0xE0E0E0);
    }

    public static Color getSecondaryTextColor() {
        return currentTheme == Theme.LIGHT ? Color.DARK_GRAY : new Color(0xAAAAAA);
    }

    public static Color getAccentBlue() {
        // Brighten the blue slightly in dark mode so it pops
        return currentTheme == Theme.LIGHT ? new Color(0x4343BF) : new Color(0x6A6AFF);
    }

    public static Color getAccentOrange() {
        // Brighten the orange slightly in dark mode
        return currentTheme == Theme.LIGHT ? new Color(0xFF8405) : new Color(0xFF9A33);
    }

    public static Color getErrorRed() {
        return currentTheme == Theme.LIGHT ? new Color(0xDE3121) : new Color(0xFF5252);
    }

    public static Color getBorderColor() {
        return currentTheme == Theme.LIGHT ? Color.BLACK : new Color(0x444444);
    }

    public static Color getPlaceholderColor() {
        return currentTheme == Theme.LIGHT ? Color.GRAY : new Color(0x888888);
    }

    // Helper for HTML injection (Labels)
    public static String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // Applies defaults globally to standard Swing components
    public static void applyGlobalUIManager() {
        UIManager.put("Panel.background", getBackgroundColor());
        UIManager.put("OptionPane.background", getPanelColor());
        UIManager.put("OptionPane.messageForeground", getTextColor());
        UIManager.put("Button.background", getPanelColor());
        UIManager.put("Button.foreground", getTextColor());
        UIManager.put("Label.foreground", getTextColor());
        UIManager.put("ComboBox.background", getPanelColor());
        UIManager.put("ComboBox.foreground", getTextColor());
        UIManager.put("ComboBox.selectionBackground", getAccentBlue());
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("TextField.background", getPanelColor());
        UIManager.put("TextField.foreground", getTextColor());
        UIManager.put("TextField.caretForeground", getTextColor());
        UIManager.put("Spinner.background", getPanelColor());
        UIManager.put("Spinner.foreground", getTextColor());
        UIManager.put("ScrollPane.background", getBackgroundColor());
        UIManager.put("Viewport.background", getBackgroundColor());
    }
}