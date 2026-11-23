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

    public void load() {
        try {
            Properties p = new Properties();
            FileInputStream in = new FileInputStream(FILE);
            p.load(in);
            in.close();
            animations = p.getProperty("animations", "true").equals("true");
            fullscreen = p.getProperty("fullscreen", "false").equals("true");
            windowW = Integer.parseInt(p.getProperty("windowW", "960"));
            windowH = Integer.parseInt(p.getProperty("windowH", "540"));
        } catch (Exception e) {
            // no settings file yet (or it is broken): keep the defaults
        }
    }
    
}
