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
    
    /** Gives a reward only once per key (stops duplicate rewards). */
    public boolean claim(String key) {
        if (has("reward:" + key)) return false;
        set("reward:" + key);
        return true;
    }
    
    // ---------- meters ----------
    public void changeSanity(int d) { sanity = Math.max(0, Math.min(100, sanity + d)); }
    public void changeDebt(int d) { techDebt = Math.max(0, Math.min(100, techDebt + d)); }
    public void changeHeap(double d) { heap = Math.max(0, Math.min(100, heap + d)); }

    public void wrongAnswer() { mistakes++; changeSanity(-8); changeDebt(6); changeHeap(2); }
    public void usedHint()    { hints++; changeDebt(4); }
    public void timeout()     { timeouts++; changeSanity(-15); changeDebt(10); changeHeap(5); }
    public void cleanStep()   { changeSanity(2); }

    /** Manual garbage collection: -4% heap, 4 second cooldown, never below 85%. Returns a message. */
    public String gc() {
        if (gcCooldown > 0) return "GC is cooling down (" + (int) Math.ceil(gcCooldown) + "s)";
        if (heap <= 85) return "Heap is already as clean as it can get";
        heap = Math.max(85, heap - 4);
        gcCooldown = 4;
        return "Manual GC: heap -4%";
    }

    public boolean isGameOver() { return heap >= 100 || sanity <= 0; }

    /** The ending rules from the script. 10 = A (Rank S), 11 = B (Rank B), 12 = Game Over. */
    public int decideEnding() {
        if (isGameOver()) return 12;
        if (techDebt < 20 && sanity > 60) return 10;
        return 11;
    }

    public void reset() {
        sanity = 100; techDebt = 0; heap = 22; gcCooldown = 0; alarm = false;
        mistakes = 0; hints = 0; timeouts = 0; playSeconds = 0;
        flags.clear();
    }
    
}
