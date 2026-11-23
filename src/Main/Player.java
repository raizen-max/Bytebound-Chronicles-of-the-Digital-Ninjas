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
    
}
