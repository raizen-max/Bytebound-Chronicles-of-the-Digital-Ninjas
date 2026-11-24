package src.Main;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * One full-screen overlay that shows different "pages": pause, settings, save, load, log, quit.
 * A page is just a title + up to 7 buttons. click(i) decides what button i does on the current page.
 */
public class MenuUI {

    GameManager gm;
    UI ui;
    JPanel panel;
    JLabel title, info;
    JButton[] btn = new JButton[7];
    JTextArea logArea;
    String page = "", back = "";
    int shade = 225; 

    public MenuUI(GameManager gm, UI ui) {
        this.gm = gm;
        this.ui = ui;

        panel = new JPanel(null) {
            protected void paintComponent(Graphics g) {            // dark see-through background
                g.setColor(new Color(5, 8, 20, shade));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.addMouseListener(new MouseAdapter() { });             // swallow clicks so the game underneath ignores them
        panel.setVisible(false);
        ui.put(ui.content, panel, 0, 0, 960, 540);

        title = ui.label("", 36, UI.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        ui.put(panel, title, 0, 36, 960, 50);

        for (int i = 0; i < 7; i++) {
            final int id = i;
            btn[i] = new UI.MenuButton("", 580, 44, 16);
            btn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { click(id); }
            });
            ui.put(panel, btn[i], 190, 108 + i * 52, 580, 44);
        }

        logArea = new JTextArea();
        logArea.setBackground(UI.PANEL);
        logArea.setForeground(UI.WHITE);
        logArea.setFont(UI.font(14));
        logArea.setEditable(false);
        logArea.setFocusable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setVisible(false);
        ui.put(panel, logArea, 90, 100, 780, 310);

        info = ui.label("", 15, UI.YELLOW);
        info.setHorizontalAlignment(SwingConstants.CENTER);
        ui.put(panel, info, 0, 486, 960, 24);
    }

        public boolean isOpen() { return panel.isVisible(); }

    /** Esc key: closes the menu, or opens the pause menu while playing. */
    public void toggle() {
        if (isOpen()) close();
        else if (gm.sc.current >= 1 && gm.sc.current < 10) open("pause", "");
    }

    public void open(String p, String backTo) {
        shade = (gm.sc.current == 0) ? 248 : 225;
        panel.setVisible(true);
        show(p, backTo);
    }

    public void close() {
        panel.setVisible(false);
        gm.ev.refresh();
    }

    // ---------------------------------------------------------------- pages
    private void setButtons(String... text) {
        for (int i = 0; i < 7; i++) {
            btn[i].setVisible(i < text.length);
            if (i < text.length) btn[i].setText(text[i]);
        }
        logArea.setVisible(false);
    }

    void show(String p, String backTo) {
        page = p;
        back = backTo;
        info.setText("");
        if (p.equals("pause")) {
            title.setText("PAUSED");
            setButtons("RESUME", "SAVE GAME", "LOAD GAME", "SETTINGS", "DIALOGUE LOG", "QUIT TO TITLE");
        } else if (p.equals("settings")) {
            title.setText("SETTINGS");
            Settings s = gm.settings;
            setButtons("ANIMATIONS:  " + (s.animations ? "ON" : "OFF"),
                    "FULLSCREEN:  " + (s.fullscreen ? "ON" : "OFF"),
                    "BACK");
        } else if (p.equals("save")) {
            title.setText("SAVE GAME");
            setButtons("SLOT 1:  " + gm.saves.describe(1), "SLOT 2:  " + gm.saves.describe(2),
                    "SLOT 3:  " + gm.saves.describe(3), "BACK");
            info.setText(gm.canSave() ? "Pick a slot to save (overwrites it)." : "You can only save while exploring a scene.");
        } else if (p.equals("load")) {
            title.setText("LOAD GAME");
            setButtons("AUTOSAVE:  " + gm.saves.describe(0), "SLOT 1:  " + gm.saves.describe(1),
                    "SLOT 2:  " + gm.saves.describe(2), "SLOT 3:  " + gm.saves.describe(3), "BACK");
            info.setText("The autosave is made at the start of every scene.");
        } else if (p.equals("log")) {
            title.setText("DIALOGUE LOG");
            setButtons("", "", "", "", "", "", "BACK");
            for (int i = 0; i < 6; i++) btn[i].setVisible(false);
            String text = "";
            int from = Math.max(0, ui.log.size() - 9);
            for (int i = from; i < ui.log.size(); i++) text += ui.log.get(i) + "\n\n";
            logArea.setText(text.equals("") ? "(nothing said yet)" : text);
            logArea.setVisible(true);
        } else if (p.equals("quit")) {
            title.setText("QUIT TO TITLE?");
            setButtons("YES, QUIT TO TITLE", "CANCEL");
            info.setText("Progress since the last save is lost. Your autosave stays.");
        }
    }

    // ---------------------------------------------------------------- what each button does
    void click(int i) {
        Settings s = gm.settings;
        if (page.equals("pause")) {
            if (i == 0) close();
            else if (i == 1) show("save", "pause");
            else if (i == 2) show("load", "pause");
            else if (i == 3) show("settings", "pause");
            else if (i == 4) show("log", "pause");
            else if (i == 5) show("quit", "pause");
        } else if (page.equals("settings")) {
            if (i == 0) s.animations = !s.animations;
            else if (i == 1) {
                s.fullscreen = !s.fullscreen;
                ui.window.setExtendedState(s.fullscreen ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
            } else { s.save(); goBack(); return; }
            s.save();
            show("settings", back);
        } else if (page.equals("save")) {
            if (i == 3) { goBack(); return; }
            if (!gm.canSave()) { info.setText("You can only save while exploring a scene."); return; }
            boolean ok = gm.saves.save(i + 1, false);
            show("save", back);
            info.setText(ok ? "Saved to slot " + (i + 1) + "!" : "Could not write the save file.");
        } else if (page.equals("load")) {
            if (i == 4) { goBack(); return; }
            if (!gm.saves.exists(i)) { info.setText("That slot is empty."); return; }
            panel.setVisible(false);
            if (!gm.saves.load(i)) { panel.setVisible(true); info.setText("That save file is broken."); }
        } else if (page.equals("log")) {
            goBack();
        } else if (page.equals("quit")) {
            if (i == 0) { panel.setVisible(false); gm.toTitle(); }
            else goBack();
        }
    }

    void goBack() {
        if (back.equals("")) close();
        else show(back, "");
    }
}

