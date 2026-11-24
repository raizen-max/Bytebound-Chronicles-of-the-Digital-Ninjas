package src.Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Timer;

/*
    1. plays the dialogue script (script/dialogue.txt) and runs its @commands
 */

public class EventHandler {

    GameManager gm;
    HashMap<String, ArrayList<String>> script = new HashMap<String, ArrayList<String>>();
    ArrayList<String> lines = new ArrayList<String>();
    int index = 0;
    boolean talking = false;   // a dialogue is on screen (or paused waiting for a puzzle/card)
    boolean paused = false;
    int tickCount = 0;

    public EventHandler(GameManager gm) { this.gm = gm; }

    // =========================================================== loading
    public void loadScript(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line, name = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    name = line.substring(1, line.length() - 1);
                    script.put(name, new ArrayList<String>());
                } else if (name != null && !line.equals("") && !line.startsWith("#")) {
                    script.get(name).add(line);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Could not read " + path + ": " + e.getMessage());
        }
    }

    // =========================================================== dialogue
    public void startTalk(String name) {
        if (!script.containsKey(name)) { System.out.println("Missing dialogue: " + name); return; }
        lines = script.get(name);
        index = 0;
        talking = true;
        paused = false;
        gm.ui.refreshHotspots();       // markers disappear while someone is talking
        advance();
    }

    public void next() {
        if (!talking || paused || gm.menu.isOpen()) return;
        if (gm.ui.isTyping()) gm.ui.finishTyping();     // 1st click: show the whole line
        else advance();                                 // 2nd click: next line
    }

    private void advance() {
        while (true) {
            if (index >= lines.size()) {            // dialogue finished
                talking = false;
                gm.ui.hideMessage();
                refresh();
                return;
            }
            String line = lines.get(index);
            index++;
            if (line.startsWith("@")) {
                int space = line.indexOf(' ');
                String cmd = (space < 0) ? line.substring(1) : line.substring(1, space);
                String arg = (space < 0) ? "" : line.substring(space + 1).trim();
                if (runCommand(cmd.toUpperCase(), arg)) return;   // true = stop reading this dialogue
                continue;
            }
            String[] part = line.split("\\|", 3);   // SPEAKER | expression | text
            if (part.length < 3) continue;
            gm.ui.showLine(part[0].trim().toUpperCase(), part[1].trim().toLowerCase(), part[2].trim());
            return;
        }
    }

    private boolean runCommand(String cmd, String arg) {
        Player p = gm.player;
        if (cmd.equals("FLAG")) p.set(arg);
        else if (cmd.equals("SANITY")) p.changeSanity(Integer.parseInt(arg));
        else if (cmd.equals("DEBT")) p.changeDebt(Integer.parseInt(arg));
        else if (cmd.equals("HEAP")) p.changeHeap(Double.parseDouble(arg));
        else if (cmd.equals("HEAPSET")) p.heap = Double.parseDouble(arg);
        else if (cmd.equals("SHOW")) { gm.ui.setActor(arg, true); refresh(); }
        else if (cmd.equals("HIDE")) { gm.ui.setActor(arg, false); refresh(); }
        else if (cmd.equals("ALARM")) { p.alarm = arg.equalsIgnoreCase("on"); gm.ui.setBackground(gm.sc.current); }
        else if (cmd.equals("BLACKOUT")) gm.ui.blackout.setVisible(arg.equalsIgnoreCase("on"));
        else if (cmd.equals("GOTO")) return gm.sc.showScene(Integer.parseInt(arg));
        else if (cmd.equals("ENDING")) return gm.sc.showScene(p.decideEnding());
        else if (cmd.equals("PUZZLE")) {
            if (!p.has("puzzle" + arg + "_done")) { paused = true; gm.puzzle.start(Integer.parseInt(arg)); return true; }
        }
        else if (cmd.equals("CARD")) {
            String[] t = arg.split("\\|");
            gm.ui.showCard(t[0], t.length > 1 ? t[1] : "");
            paused = true;
            return true;
        }
        else if (cmd.equals("CREDITS")) { gm.ui.showCredits(); paused = true; return true; }
        else if (cmd.equals("DEPLOY")) { gm.ui.deployPanel.setVisible(true); paused = true; return true; }
        else System.out.println("Unknown command: " + cmd);
        return false;
    }

    // =========================================================== events from buttons / panels
    public void cardClicked() {
        gm.ui.cardPanel.setVisible(false);
        paused = false;
        advance();
    }

    public void creditsClicked() {
        if (gm.ui.creditsTime < 4) return;
        gm.ui.creditsPanel.setVisible(false);
        gm.toTitle();
    }

    public void deploy(boolean yes) {
        gm.ui.deployPanel.setVisible(false);
        startTalk(yes ? "s8_deploy_yes" : "s8_deploy_no");
    }

    public void puzzleDone(int id, boolean timedOut) {
        gm.player.set("puzzle" + id + "_done");
        if (timedOut) gm.player.set("puzzle" + id + "_hacked");
        Puzzles p = Puzzles.get(id);
        startTalk(p.doneDialogue);
    }

    public void startGame() {
        gm.player.reset();
        gm.ui.resetEverything();
        gm.sc.showScene(1);
    }

    public boolean isShown(Hotspot h) {
        if (h.scene != gm.sc.current || h.done) return false;
        if (!h.actor.equals("") && !gm.ui.isActorVisible(h.actor)) return false;
        return true;
    }

    public boolean isUnlocked(Hotspot h) { return gm.player.hasAll(h.requires); }

    public void clickHotspot(Hotspot h) {
        if (talking || gm.puzzle.active || gm.menu.isOpen()) return;   // ignore clicks while busy
        if (!isShown(h)) return;
        if (!isUnlocked(h)) {                              // prerequisites missing
            if (!h.locked.equals("")) gm.ui.toast(h.locked, 2.5);
            return;
        }
        if (h.once) h.done = true;                         // no repeats
        gm.player.set(h.id);                               // the hotspot's id becomes a flag
        if (h.action.equals("GC")) gm.ui.toast(gm.player.gc(), 2.0);
        else if (h.action.startsWith("DIALOGUE:")) startTalk(h.action.substring(9));
        refresh();
    }

    public void refresh() {
        gm.ui.refreshHotspots();
        gm.ui.refreshObjective();
    }

    // =========================================================== timer
    public void startTimer() {
        Timer t = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) { tick(); }
        });
        t.start();
    }

    private void tick() {
        tickCount++;
        Player p = gm.player;
        if (gm.menu.isOpen()) { gm.ui.tick(0.1); return; }     // the game is frozen while the menu is open
        if (gm.sc.current >= 1 && gm.sc.current < 10) p.playSeconds += 0.1;
        if (p.gcCooldown > 0) p.gcCooldown = Math.max(0, p.gcCooldown - 0.1);
        if (gm.puzzle.active) gm.puzzle.tick(0.1);
        gm.ui.tick(0.1);
        if (tickCount % 6 == 0) gm.ui.flipActors();           // idle animation
        int s = gm.sc.current;
        if (s >= 1 && s < 10 && p.isGameOver()) {             // Fail State: heap 100% or sanity 0%
            gm.puzzle.cancel();
            gm.ui.hideOverlays();
            gm.sc.showScene(12);
        }
        gm.ui.updateHud();
    }
}
