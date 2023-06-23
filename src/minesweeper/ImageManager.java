package minesweeper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageManager {
    public static Image[] NUMBERS;

    public static Image BOMB;
    public static Image BOMB_HIT;

    public static Image FLAG;
    public static Image BAD_FLAG;

    public static Image HIDDEN;
    public static Image HIDDEN_PRESSED;

    public static final String[] THEME_IDS = { "xp", "98", "gameboy", "mario", "icicle", "hd", "doodle", "shine", "crayon" };
	public static final String[] THEME_NAMES = { "Windows XP", "Windows 98", "Gameboy", "Mario", "Icicle", "HD", "Doodle", "Shine", "Crayon" };
    public static final int[] THEME_COLORS = { 0x808080, 0xC0C0C0, 0x4D533C, 0xD0B030, 0xB2B2FF, 0x282F3F, 0xFFFFFF, 0xC09161, 0x808080 };
	public static final int[] THEME_SIZES = { 16, 32, 64, 128 };
    
    private static String path = "images/";
    public static String theme = THEME_NAMES[0];
    public static int bgColor = THEME_COLORS[0];
    public static int size = THEME_SIZES[0];


    public static void load(String theme) { load(theme, size); }
    public static void load(int size) { load(theme, size); }
    public static void load(String theme, int size) {
        ImageManager.theme = theme;
        ImageManager.bgColor = getColor(theme);
        ImageManager.size = size;

        NUMBERS = new Image[9];
        for (int i = 0; i < NUMBERS.length; i++) {
            NUMBERS[i] = getIcon(i + ".png");
        }

        BOMB = getIcon("bomb.png");
        BOMB_HIT = getIcon("bomb_hit.png");

        FLAG = getIcon("flag.png");
        BAD_FLAG = getIcon("bad_flag.png");

        HIDDEN = getIcon("hidden.png");
        HIDDEN_PRESSED = getIcon("hidden_pressed.png");
    }

    private static Image getIcon(String file) {
        BufferedImage original;
        try {
            original = ImageIO.read(ImageManager.class.getResource(path + theme + "/" + file));
            return original.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        } catch (IOException err) {
            System.out.println("Failed to load image: " + file);
            return null;
            // e.printStackTrace();
        }
    }

    private static int getColor(String theme) {
        for (int i = 0; i < THEME_IDS.length; i++) {
            if (THEME_IDS[i].equals(theme)) return THEME_COLORS[i];
        }

        return 0;
    }
}
