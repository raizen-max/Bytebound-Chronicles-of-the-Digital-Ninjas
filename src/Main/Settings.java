package src.Main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

 /** Player options. Saved to settings.properties next to the game, loaded on start. */
public class Settings {

    public static final String FILE = "settings.properties";
    boolean animations = true;  // idle + talking animations
    boolean fullscreen = false; // maximized window
    int windowW = 960, windowH = 540;
    
}
