package cmsc125.project3.theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public enum Theme { LIGHT, DARK }
    public enum FontSize { SMALL, MEDIUM, LARGE }

    private static Theme currentTheme = Theme.DARK;
    private static FontSize currentSize = FontSize.MEDIUM;

    public interface ThemeObserver { void onThemeChanged(); }
    private static final List<ThemeObserver> observers = new ArrayList<>();

    public static void addObserver(ThemeObserver obs) { observers.add(obs); }

    public static void broadcastThemeChange() {
        applyGlobalUIManager();
        for (ThemeObserver obs : observers) {
            obs.onThemeChanged();
        }
    }

    public static void setTheme(Theme theme) { currentTheme = theme; }
    public static Theme getTheme() { return currentTheme; }

    public static void setFontSize(FontSize size) { currentSize = size; }
    public static FontSize getFontSize() { return currentSize; }

    // --- FONT SIZES (Drastically Increased) ---
    public static int getPrimaryFontSize() {
        if (currentSize == FontSize.LARGE) return 36;
        if (currentSize == FontSize.MEDIUM) return 24;
        return 16;
    }

    public static int getSecondaryFontSize() {
        if (currentSize == FontSize.LARGE) return 28;
        if (currentSize == FontSize.MEDIUM) return 18;
        return 14;
    }

    public static int getTertiaryFontSize() {
        if (currentSize == FontSize.LARGE) return 20;
        if (currentSize == FontSize.MEDIUM) return 14;
        return 12;
    }

    // --- COLORS ---
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
        return currentTheme == Theme.LIGHT ? new Color(0x4343BF) : new Color(0x6A6AFF);
    }
    public static Color getAccentOrange() {
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
    public static String colorToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static void applyGlobalUIManager() {
        UIManager.put("Panel.background", getBackgroundColor());
        UIManager.put("OptionPane.background", getPanelColor());
        UIManager.put("OptionPane.messageForeground", getTextColor());
        UIManager.put("Button.background", getPanelColor());
        UIManager.put("Button.foreground", getTextColor());
        UIManager.put("Label.foreground", getTextColor());
        UIManager.put("RadioButton.background", getPanelColor());
        UIManager.put("RadioButton.foreground", getTextColor());
        UIManager.put("ComboBox.background", getPanelColor());
        UIManager.put("ComboBox.foreground", getTextColor());
        UIManager.put("ComboBox.selectionBackground", getAccentBlue());
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("TextField.background", getPanelColor());
        UIManager.put("TextField.foreground", getTextColor());
        UIManager.put("TextField.caretForeground", getTextColor());
        UIManager.put("Spinner.background", getPanelColor());
        UIManager.put("Spinner.foreground", getTextColor());
    }
}