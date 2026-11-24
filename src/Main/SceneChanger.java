package src.Main;

public class SceneChanger {

    GameManager gm;
    int current = 0;       
    boolean fading = false; /

    public SceneChanger(GameManager gm) { this.gm = gm; }

    public void showTitle() {
        current = 0;
        gm.ui.hideOverlays();
        gm.ui.toastLabel.setVisible(false);
        gm.ui.toastTime = 0;
        gm.ui.showOnly(0);
        gm.ui.hud(false, "");
        gm.ui.hidePopup();
        gm.ui.refreshTitleButtons();
    }

    public boolean canEnter(int n) {
        if (n == 12) return true;
        if (n == 10 || n == 11) return current == 9;
        if (n == 1) return current == 0 || current >= 10;
        return n == current + 1;
    }

    public boolean showScene(int n) {
        if (!canEnter(n)) {
            System.out.println("Blocked scene jump " + current + " -> " + n);
            return false;
        }
        current = n;
        String[] info = SceneData.info(n);        // {act, title, time, bg, enterDialogue}
        gm.ui.showOnly(n);
        gm.ui.setBackground(n);
        gm.ui.resetActors(n);
        gm.ui.hud(n < 10, info[0] + " | " + info[2]);
        gm.ui.hidePopup();
        gm.ui.toast(info[1], 3.0);
        gm.ev.talking = false;
        gm.ev.paused = false;
        if (n >= 1 && n <= 9) gm.saves.save(0, true);   // AUTOSAVE at the start of every scene
        gm.ev.refresh();
        if (!info[4].equals("")) gm.ev.startTalk(info[4]);
        return true;
    }

    public void restartScene(int n) {
        current = (n == 1) ? 0 : n - 1;
        showScene(n);
    }

    public void restoreScene(int n, String actors) {
        current = n;
        String[] info = SceneData.info(n);
        gm.ui.showOnly(n);
        gm.ui.setBackground(n);
        gm.ui.resetActors(n);
        gm.ui.hidePopup();
        String[] list = actors.split("\\|");
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals("")) continue;
            String[] part = list[i].split(":");
            gm.ui.setActor(part[0], part[1].equals("1"));
        }
        gm.ui.hud(n < 10, info[0] + " | " + info[2]);
        gm.ev.talking = false;
        gm.ev.paused = false;
        gm.ev.refresh();
    }
}
