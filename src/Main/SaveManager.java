package src.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Save / load game.
 *  slot 0 = AUTOSAVE (made at the start of every scene; loading it replays that scene from its start)
 *  slot 1-3 = manual saves (only while exploring a scene; loading puts you back exactly there)
 * Files are plain text in the /saves folder, so you can open them in Notepad and see what is stored.
 */
public class SaveManager {

    GameManager gm;
    static final String DIR = "saves";

    public SaveManager(GameManager gm) { this.gm = gm; }

    private File file(int slot) { return new File(DIR, slot == 0 ? "autosave.sav" : "slot" + slot + ".sav"); }

    public boolean exists(int slot) { return file(slot).exists(); }

    private Properties read(int slot) {
        try {
            Properties p = new Properties();
            FileInputStream in = new FileInputStream(file(slot));
            p.load(in);
            in.close();
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    /** sceneStart = true for the autosave (the scene will be replayed from its beginning). */
    public boolean save(int slot, boolean sceneStart) {
        Player pl = gm.player;
        Properties p = new Properties();
        p.setProperty("version", "1");
        p.setProperty("time", "" + System.currentTimeMillis());
        p.setProperty("mode", sceneStart ? "start" : "explore");
        p.setProperty("scene", "" + gm.sc.current);
        p.setProperty("sanity", "" + pl.sanity);
        p.setProperty("techDebt", "" + pl.techDebt);
        p.setProperty("heap", "" + pl.heap);
        p.setProperty("alarm", "" + pl.alarm);
        p.setProperty("mistakes", "" + pl.mistakes);
        p.setProperty("hints", "" + pl.hints);
        p.setProperty("playSeconds", "" + pl.playSeconds);
        p.setProperty("flags", String.join("|", pl.flags));
        // hotspots already used up, and which characters are on stage
        String done = "", actors = "";
        for (int i = 0; i < gm.ui.hotspots.size(); i++) {
            Hotspot h = gm.ui.hotspots.get(i);
            if (h.done) done += h.scene + ":" + h.id + "|";
        }
        for (int i = 0; i < gm.ui.actors.size(); i++) {
            Actor a = gm.ui.actors.get(i);
            if (a.scene == gm.sc.current) actors += a.name + ":" + (a.visible ? "1" : "0") + "|";
        }
        p.setProperty("done", done);
        p.setProperty("actors", actors);
        try {
            new File(DIR).mkdirs();
            FileOutputStream out = new FileOutputStream(file(slot));
            p.store(out, "Bytebound save");
            out.close();
            return true;
        } catch (Exception e) {
            System.out.println("Could not save: " + e.getMessage());
            return false;
        }
    }

    public boolean load(int slot) {
        Properties p = read(slot);
        if (p == null) return false;
        try {
            // 1. read and check everything BEFORE touching the running game
            int sanity = Integer.parseInt(p.getProperty("sanity"));
            int techDebt = Integer.parseInt(p.getProperty("techDebt"));
            double heap = Double.parseDouble(p.getProperty("heap"));
            boolean alarm = p.getProperty("alarm").equals("true");
            int mistakes = Integer.parseInt(p.getProperty("mistakes", "0"));
            int hints = Integer.parseInt(p.getProperty("hints", "0"));
            double playSeconds = Double.parseDouble(p.getProperty("playSeconds", "0"));
            int scene = Integer.parseInt(p.getProperty("scene"));
            String mode = p.getProperty("mode");
            if (scene < 1 || scene > 9 || SceneData.info(scene) == null) return false;
            if (!mode.equals("start") && !mode.equals("explore")) return false;
            String[] flags = p.getProperty("flags", "").split("\\|");
            String[] done = p.getProperty("done", "").split("\\|");

            // 2. only now change the game
            Player pl = gm.player;
            gm.ui.resetEverything();
            pl.reset();
            pl.sanity = sanity; pl.techDebt = techDebt; pl.heap = heap; pl.alarm = alarm;
            pl.mistakes = mistakes; pl.hints = hints; pl.playSeconds = playSeconds;
            for (String f : flags) if (!f.equals("")) pl.set(f);

            if (mode.equals("start")) {
                gm.sc.restartScene(scene);                       // replay the scene from its beginning
            } else {
                for (String d : done) {
                    if (d.equals("")) continue;
                    String[] part = d.split(":");
                    for (int i = 0; i < gm.ui.hotspots.size(); i++) {
                        Hotspot h = gm.ui.hotspots.get(i);
                        if (h.scene == Integer.parseInt(part[0]) && h.id.equals(part[1])) h.done = true;
                    }
                }
                gm.sc.restoreScene(scene, p.getProperty("actors", ""));
            }
            return true;
        } catch (Exception e) {
            System.out.println("Broken save file: " + e);
            return false;
        }
    }

    public String describe(int slot) {
        Properties p = read(slot);
        if (p == null) return "- empty -";
        try {
            int scene = Integer.parseInt(p.getProperty("scene"));
            String when = new SimpleDateFormat("dd MMM HH:mm").format(new Date(Long.parseLong(p.getProperty("time"))));
            String kind = p.getProperty("mode").equals("start") ? "start of " : "";
            return kind + "Scene " + scene + "  " + when + "  S" + p.getProperty("sanity") + " D" + p.getProperty("techDebt");
        } catch (Exception e) {
            return "- broken file -";
        }
    }
}
