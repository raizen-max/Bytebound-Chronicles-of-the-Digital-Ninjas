package src.Main;

public class GameManager {

    Settings settings = new Settings();
    Player player = new Player();
    SceneChanger sc = new SceneChanger(this);
    EventHandler ev = new EventHandler(this);
    PuzzleHandler puzzle = new PuzzleHandler(this);
    SaveManager saves = new SaveManager(this);
    UI ui;
    MenuUI menu;      // pause / settings / save / load screens (created inside UI)

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() { new GameManager(); }
        });
    }

    public GameManager() {
        settings.load();
        ui = new UI(this);
        ev.loadScript("script/dialogue.txt");
        ev.startTimer();
        sc.showTitle();
    }

    /** Manual saving is only allowed while exploring a scene (not during dialogue, puzzles or cards). */
    public boolean canSave() {
        int s = sc.current;
        return s >= 1 && s < 10 && !ev.talking && !puzzle.active && !ui.overlayShowing() && !sc.fading;
    }

    public void quickSave() {
        if (menu.isOpen()) return;
        if (!canSave()) { ui.toast("You can only save while exploring a scene.", 2.0); return; }
        gm_toast(saves.save(1, false) ? "Quick saved to slot 1" : "Save failed");
    }

    public void quickLoad() {
        if (menu.isOpen()) return;
        gm_toast(saves.load(1) ? "Loaded slot 1" : "Slot 1 is empty");
    }

    private void gm_toast(String text) { ui.toast(text, 2.0); }

    public void toTitle() {
        player.reset();
        ui.resetEverything();
        sc.showTitle();
    }

    public void quit() {
        settings.save();
        System.exit(0);
    }
}
