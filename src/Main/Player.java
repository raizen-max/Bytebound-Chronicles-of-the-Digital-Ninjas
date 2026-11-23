package src.Main;

import java.util.ArrayList;

/**
 * Everything that changes while playing:
 *  - the GAMEPLAY meters from the script (sanity, tech debt, JVM heap)
 *  - the story flags (e.g. "met_granger") that unlock things
 */
public class Player {

    public int sanity = 100;
    public int techDebt = 0;
    public double heap = 22;
    public double gcCooldown = 0;
    public boolean alarm = false;

    // statistics (shown on the credits screen, kept in save files)
    public int mistakes = 0, hints = 0, timeouts = 0;
    public double playSeconds = 0;
    
        ArrayList<String> flags = new ArrayList<String>();

    // ---------- flags ----------
    public boolean has(String flag) { return flags.contains(flag); }

    public void set(String flag) { if (!flags.contains(flag)) flags.add(flag); }

    /** csv = "flagA,flagB". True if ALL of them are set (empty = true). */
    public boolean hasAll(String csv) {
        if (csv.trim().equals("")) return true;
        String[] list = csv.split(",");
        for (int i = 0; i < list.length; i++) {
            if (!has(list[i].trim())) return false;
        }
        return true;
    }
    
}
