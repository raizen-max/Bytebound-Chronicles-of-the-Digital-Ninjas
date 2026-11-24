package src.Main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class UI {
     
    //palette
    static final Color NAVY = new Color(0x0e1633), NAVY2 = new Color(0x1d2b64), RED = new Color(0xd62839);
    static final Color WHITE = new Color(0xf4f4f8), YELLOW = new Color(0xffd479), PANEL = new Color(0x0a1128);
    static final int W = 960, H = 540;
    static int charsPerTick = 2;

    GameManager gm;
    JFrame window;
    Container content;

    //scaling
    double k = 1;
    int offset_x = 0, offset_y = 0;
    class Placed { JComponent c; int x, y, w, h, font; boolean top; }
    ArrayList<Placed> placed = new ArrayList<Placed>();
    HashMap<JComponent, Placed> placedOf = new HashMap<JComponent, Placed>();
    HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
    Timer iconTimer;

    //scenes
    JPanel[] scenePanel = new JPanel[13];
    JLabel[] bgLabel = new JLabel[13];
    ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
    ArrayList<Actor> actors = new ArrayList<Actor>();
    int frame = 0;
    
    // ---- scenes
    JPanel[] scenePanel = new JPanel[13];
    JLabel[] bgLabel = new JLabel[13];
    ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
    ArrayList<Actor> actors = new ArrayList<Actor>();
    int frame = 0;
    // hotspot marker (animated gem): frames assets/ui/hotspot_0..3.png
    HashMap<Hotspot, int[]> markerBase = new HashMap<Hotspot, int[]>();   
    int markerTick = 0, markerFrame = 0;
    static final int[] BOB = {0, 2, 4, 5, 6, 5, 4, 2};                   
    static final int MARKER = 56;                                         // marker size 

    // ---- title screen
    static final String VERSION = "v.0.2 Prototype", COPYRIGHT = "\u00A9 2026 Bytebound Team";
    static final Color CARD = new Color(0xeaf7ff), CARD_EDGE = new Color(0x8399b3);
    static final Color BTN = new Color(0x01274c), BTN_SEL = new Color(0xb51316);
    MenuButton[] titleButton = new MenuButton[5];
    JLabel titleBg, logoLabel, subtitleLabel;
    Pointer titlePointer;
    int titleSel = 0;                                   // which title button is selected (arrow keys / mouse)
    int[] titleY = new int[5];

    //HUD
    JPanel hudPanel;
    JProgressBar sanityBar, debtBar, heapBar;
    JLabel[] hudLabel = new JLabel[3];
    JLabel hudRight;
    JButton menuBtn;

    //message box
    JPanel msgPanel;
    JTextArea messageText;
    JLabel nameLabel, portraitLabel, objectiveLabel, toastLabel, blackout;
    double toastTime = 0;
    String fullText = "";
    int typePos = 0;
    String portraitWho = "", portraitExpr = "";
    ArrayList<String> log = new ArrayList<String>(); 

    //popup
    JPanel popupPanel;
    JLabel popupTitle, popupPortrait, popupRole;
    String popupName = "";

    //talking animation
    HashMap<String, ArrayList<ImageIcon>> talkFrames = new HashMap<String, ArrayList<ImageIcon>>();
    String talkSpeaker = "", talkExpr = "";
    int talkTicks = 0, talkIndex = 0;

    //panel for puzzles
    JPanel puzzlePanel;
    JLabel puzTitle, puzInstr, puzTimer, puzStep;
    JTextArea puzCode, puzFeedback;
    JProgressBar puzTimerBar;
    JButton[] puzOption = new JButton[4];
    JButton puzHint, puzGc;

    // other overlays
    JPanel deployPanel, cardPanel;
    JLabel cardTitle, cardSub;
    double creditsTime = 0;

    static class LayerPanel extends JPanel {
        LayerPanel() { super(null); }
        public boolean isOptimizedDrawingEnabled() { return false; }
    }

    public UI(GameManager gm) {
        this.gm = gm;
        createMainField();

        window.setVisible(true);

        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        resizeComponents();

        window.setVisible(true);
    }

    public void createMainField() {

        window = new JFrame();

        window.setTitle("Bytebound");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.getContentPane().setBackground(Color.black);

        window.setLayout(null);

        messageText = new JTextArea("Test Message");
        messageText.setBounds(10, 100, 300, 10);
        messageText.setBackground(Color.black);
        messageText.setForeground(Color.white);
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setFont(new Font("Press Start 2P", Font.PLAIN, 26));

        window.add(messageText);
    } 


    //===================================================
    // ------------------- Scaling  ---------------------
    //===================================================
    static Font font(int size) { return new Font(Font.MONOSPACED, Font.BOLD, size); }

    // Adds a component using LOGICAL (960x540) coordinates and remembers them so it can be re-scaled. 
    void put(Container parent, JComponent c, int x, int y, int w, int h) {
        parent.add(c);
        Placed p = new Placed();
        p.c = c; p.x = x; p.y = y; p.w = w; p.h = h;
        p.top = (parent == content);
        // p.font = (c instanceof JPanel || c instanceof MenuButton || c instanceof Pointer || c instanceof Divider) ? 0 : c.getFont().getSize();
        placed.add(p);
        placedOf.put(c, p);
        apply(p);
    } 

    void apply(Placed p) {
        int x = (int) Math.round(p.x * k) + (p.top ? offset_x : 0);
        int y = (int) Math.round(p.y * k) + (p.top ? offset_y : 0);
        p.c.setBounds(x, y, (int) Math.round(p.w * k), (int) Math.round(p.h * k));
        if (p.font > 0) p.c.setFont(font(Math.max(8, (int) Math.round(p.font * k))));
    }

    /** Change where a component is (logical coordinates). */
    void move(JComponent c, int x, int y, int w, int h) {
        Placed p = placedOf.get(c);
        p.x = x; p.y = y; p.w = w; p.h = h;
        apply(p);
    }

    void setFontSize(JComponent c, int size) {
        Placed p = placedOf.get(c);
        p.font = size;
        apply(p);
    }

    public void resizeComponents() {
        int w = content.getWidth(), h = content.getHeight();
        if (w <= 0 || h <= 0) return;
        double nk = Math.min(w / (double) W, h / (double) H);
        if (Math.abs(nk - k) > 0.0001) iconCache.clear();
        k = nk;
        offset_x = (int) ((w - W * k) / 2);
        offset_y = (int) ((h - H * k) / 2);
        for (int i = 0; i < placed.size(); i++) apply(placed.get(i));
        content.repaint();
    }

    // Finds an image inside assets/<category>/ 
    String findImage(String category, String name) {
        String path = "assets/" + category + "/" + name + ".png";
    return new File(path).exists() ? path : null;
    }

    //Image scaling and caching
    ImageIcon icon(String category, String name, int w, int h) {
        int pw = Math.max(1, (int) Math.round(w * k)), ph = Math.max(1, (int) Math.round(h * k));
        String key = category + "/" + name + "@" + pw + "x" + ph;
        if (iconCache.containsKey(key)) return iconCache.get(key);
        ImageIcon icon = null;
        String path = findImage(category, name);
        if (path != null) {
            try {
                BufferedImage src = ImageIO.read(new File(path));
                BufferedImage big = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = big.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(src, 0, 0, pw, ph, null);
                g.dispose();
                icon = new ImageIcon(big);
            } catch (Exception e) {
                System.out.println("Could not load " + path);
            }
        } else {
            System.out.println("Missing image: " + category + "/" + name);
        }
        iconCache.put(key, icon);
        return icon;
    }
    
    // ADD refreshIcons method

    JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(size));
        l.setForeground(color);
        return l;
    }

    JButton button(String text, int size, Color bg) {
        JButton b = new JButton(text);
        b.setFont(font(size));
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setFocusable(false);          // so the Space key won't press a button or smthn
        b.setOpaque(true);
        b.setBorder(new LineBorder(WHITE, 2));
        return b;
    }

    // =====================================================================================
    // building the screens
    // =====================================================================================
    void createToast() {
        toastLabel = label("", 16, WHITE);
        toastLabel.setOpaque(true);
        toastLabel.setBackground(PANEL);
        toastLabel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(YELLOW, 2), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        toastLabel.setVisible(false);
        put(content, toastLabel, 20, 46, 640, 34);
    }

    void createObjective() {
        objectiveLabel = label("", 15, WHITE);
        objectiveLabel.setOpaque(true);
        objectiveLabel.setBackground(PANEL);
        objectiveLabel.setBorder(new LineBorder(RED, 2));
        objectiveLabel.setVisible(false);
        put(content, objectiveLabel, 20, 490, 920, 36);
    }

    void createMessageBox() {
        nameLabel = label("", 15, WHITE);
        nameLabel.setOpaque(true);
        nameLabel.setBackground(RED);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        nameLabel.setVisible(false);
        put(content, nameLabel, 176, 378, 300, 26);

        portraitLabel = new JLabel();
        portraitLabel.setOpaque(true);
        portraitLabel.setBackground(NAVY);
        portraitLabel.setBorder(new LineBorder(RED, 3));
        portraitLabel.setVisible(false);
        put(content, portraitLabel, 28, 368, 128, 128);

        msgPanel = new JPanel(null);
        msgPanel.setBackground(PANEL);
        msgPanel.setBorder(new LineBorder(RED, 4));
        msgPanel.setVisible(false);

        messageText = new JTextArea();
        messageText.setBackground(PANEL);
        messageText.setForeground(WHITE);
        messageText.setEditable(false);
        messageText.setFocusable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setFont(font(19));
        put(content, msgPanel, 20, 392, 920, 128);
        put(msgPanel, messageText, 24, 14, 870, 100);

        /* MouseAdapter advance = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { gm.ev.next(); }
        }; 
        msgPanel.addMouseListener(advance);
        messageText.addMouseListener(advance);
        portraitLabel.addMouseListener(advance);
        nameLabel.addMouseListener(advance); */
    }

    void createHud() {
        hudPanel = new JPanel(null);
        hudPanel.setBackground(NAVY);
        hudPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, RED));
        put(content, hudPanel, 0, 0, 960, 37);

        String[] names = {"SANITY", "TECH DEBT", "JVM HEAP"};
        int[] xs = {10, 246, 482};
        JProgressBar[] bars = new JProgressBar[3];
        for (int i = 0; i < 3; i++) {
            hudLabel[i] = label(names[i], 12, WHITE);
            put(hudPanel, hudLabel[i], xs[i], 8, 90, 18);
            bars[i] = new JProgressBar(0, 100);
            bars[i].setStringPainted(true);
            bars[i].setBackground(NAVY2);
            put(hudPanel, bars[i], xs[i] + 84, 9, 110, 16);
        }
        sanityBar = bars[0]; debtBar = bars[1]; heapBar = bars[2];

        hudRight = label("", 12, WHITE);
        hudRight.setHorizontalAlignment(SwingConstants.RIGHT);
        put(hudPanel, hudRight, 684, 8, 190, 18);

        menuBtn = button("MENU", 13, NAVY2);
        /* menuBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { menu.open("pause", ""); }
        }); */
        put(hudPanel, menuBtn, 880, 5, 72, 26);
    }

    void createPopup() {
        popupPanel = new JPanel(null);
        popupPanel.setBackground(PANEL);
        popupPanel.setBorder(new LineBorder(RED, 3));
        popupTitle = label("", 13, WHITE);
        popupTitle.setOpaque(true);
        popupTitle.setBackground(RED);
        popupTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        popupPortrait = new JLabel();
        popupRole = label("", 12, new Color(0xc9d1f0));
        popupPanel.setVisible(false);
        put(content, popupPanel, 668, 44, 270, 176);
        put(popupPanel, popupTitle, 0, 0, 270, 22);
        put(popupPanel, popupPortrait, 10, 34, 128, 128);
        put(popupPanel, popupRole, 146, 60, 120, 60);
    }

    void createBlackout() {
        blackout = new JLabel();
        blackout.setOpaque(true);
        blackout.setBackground(Color.black);
        blackout.setVisible(false);
        put(content, blackout, 0, 0, 960, 540);
    }

    void createScenes() {
        for (int n = 1; n <= 12; n++) {
            scenePanel[n] = new LayerPanel();
            scenePanel[n].setBackground(Color.black);
            scenePanel[n].setVisible(false);
            put(content, scenePanel[n], 0, 0, 960, 540);

            // 1. hotspots (markers first = on top)
            String[][] rows = SceneData.hotspots(n);
            ArrayList<Hotspot> mine = new ArrayList<Hotspot>();
            for (int i = 0; i < rows.length; i++) {
                String[] r = rows[i];
                Hotspot h = new Hotspot();
                h.scene = n; h.id = r[0]; h.label = r[1];
                int x = Integer.parseInt(r[2]) * 3, y = Integer.parseInt(r[3]) * 3;
                int w = Integer.parseInt(r[4]) * 3, hh = Integer.parseInt(r[5]) * 3;
                h.actor = r[6]; h.requires = r[7]; h.locked = r[8];
                h.once = r[9].equals("true"); h.action = r[10];

                h.area = new JLabel();
                h.area.setToolTipText(h.label);
                h.marker = new JLabel();
                HotspotClick click = new HotspotClick(h);
                h.area.addMouseListener(click);
                h.marker.addMouseListener(click);
                hotspots.add(h);
                mine.add(h);
            }
            for (int i = 0; i < mine.size(); i++) {
                Hotspot h = mine.get(i);
                int y = Integer.parseInt(rows[i][3]) * 3, x = Integer.parseInt(rows[i][2]) * 3;
                int w = Integer.parseInt(rows[i][4]) * 3, hh = Integer.parseInt(rows[i][5]) * 3;
                // characters get the gem above their head, objects get it in the middle
                int my = h.actor.equals("") ? y + hh / 2 - MARKER / 2 : y - MARKER + 12;
                markerBase.put(h, new int[]{x + w / 2 - MARKER / 2, my});
                put(scenePanel[n], h.marker, x + w / 2 - MARKER / 2, my, MARKER, MARKER);
            }
            for (int i = 0; i < mine.size(); i++) {
                Hotspot h = mine.get(i);
                put(scenePanel[n], h.area, Integer.parseInt(rows[i][2]) * 3, Integer.parseInt(rows[i][3]) * 3,
                        Integer.parseInt(rows[i][4]) * 3, Integer.parseInt(rows[i][5]) * 3);
            }

            // 2. characters
            String[][] arows = SceneData.actors(n);
            for (int i = 0; i < arows.length; i++) {
                Actor a = new Actor();
                a.scene = n; a.name = arows[i][0]; a.sprite = arows[i][1];
                a.startsVisible = arows[i][4].equals("true");
                if (!a.sprite.equals("popup")) {
                    a.label = new JLabel();
                    put(scenePanel[n], a.label, Integer.parseInt(arows[i][2]) * 3, Integer.parseInt(arows[i][3]) * 3, 96, 144);
                }
                actors.add(a);
            }

            // 3. background (last = at the bottom)
            bgLabel[n] = new JLabel();
            put(scenePanel[n], bgLabel[n], 0, 0, 960, 540);
        }
    }

    void createTitle() {
        JPanel t = new LayerPanel();
        t.setBackground(NAVY);
        put(content, t, 0, 0, 960, 540);
        scenePanel[0] = t;

        // footer (drawn first = on top): key hints on the left, version on the right
        String[] keys = {"\u2191", "\u2193", "Enter"};
        int[] kx = {268, 296, 370}, kw = {24, 24, 52};
        for (int i = 0; i < 3; i++) {
            JLabel key = label(keys[i], 12, BTN);
            key.setHorizontalAlignment(SwingConstants.CENTER);
            key.setOpaque(true);
            key.setBackground(CARD);
            key.setBorder(new LineBorder(CARD_EDGE, 2));
            put(t, key, kx[i], 478, kw[i], 22);
        }
        JLabel select = label("Select", 11, new Color(0x5d6b84));
        put(t, select, 324, 480, 44, 18);
        JLabel confirm = label("Confirm", 11, new Color(0x5d6b84));
        put(t, confirm, 426, 480, 60, 18);
        JLabel version = label("<html><div style='text-align:right'>" + VERSION + "<br>" + COPYRIGHT + "</div></html>", 11, new Color(0x5d6b84));
        version.setHorizontalAlignment(SwingConstants.RIGHT);
        put(t, version, 462, 472, 230, 34);

        // buttons
        String[] names = {"New Game", "Continue", "Load Game", "Settings", "Exit"};
        for (int i = 0; i < 5; i++) {
            final int id = i;
            titleY[i] = 190 + i * 56;
            titleButton[i] = new MenuButton(names[i], 280, 44, 20);
            titleButton[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { titleSel = id; titleClick(id); }
            });
            titleButton[i].addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (titleButton[id].isEnabled()) setTitleSel(id); }
            });
            put(t, titleButton[i], 340, titleY[i], 280, 44);
        }
        titlePointer = new Pointer();
        put(t, titlePointer, 308, titleY[0] + 12, 22, 20);

        // divider, subtitle, logo, card, background
        put(t, new Divider(), 340, 156, 280, 14);
        subtitleLabel = new JLabel();
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        put(t, subtitleLabel, 280, 132, 400, 16);
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        put(t, logoLabel, 280, 62, 400, 54);
        put(t, new TitleCard(), 250, 30, 460, 480);
        titleBg = new JLabel();
        put(t, titleBg, 0, 0, 960, 540);
        setTitleSel(0);
    }

    /** Re-applies the title pictures (called at start and after every resize). */
    void refreshTitleArt() {
        titleBg.setIcon(icon("backgrounds", "title_" + frame, 960, 540));
        logoLabel.setIcon(icon("ui", "logo", 111 * 3, 17 * 3));
        subtitleLabel.setIcon(icon("ui", "subtitle", 167 * 2, 7 * 2));
    }

    /** Select a title button (mouse hover or arrow keys): it turns red and the pointer moves next to it. */
    void setTitleSel(int i) {
        titleSel = i;
        for (int b = 0; b < 5; b++) { titleButton[b].selected = (b == i); titleButton[b].repaint(); }
        move(titlePointer, 308, titleY[i] + 12, 22, 20);
    }

    void moveTitleSel(int dir) {
        if (gm.sc.current != 0 || menu.isOpen()) return;
        int i = titleSel;
        for (int n = 0; n < 5; n++) {
            i = (i + dir + 5) % 5;
            if (titleButton[i].isEnabled()) { setTitleSel(i); return; }     // skip CONTINUE when there is no autosave
        }
    }

    /** Space / Enter: confirm the selected title button, otherwise advance the dialogue. */
    void confirmKey() {
        if (gm.sc.current == 0 && !menu.isOpen()) { if (titleButton[titleSel].isEnabled()) titleClick(titleSel); }
        else gm.ev.next();
    }

    // ---- small custom-painted pieces of the title screen
    static void pixelRect(Graphics2D g, int x, int y, int w, int h) {      // rectangle with notched (pixel) corners
        g.fillRect(x + 2, y, w - 4, h);
        g.fillRect(x, y + 2, w, h - 4);
        g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }
    
    /** Navy pixel button with a drop shadow. Turns red when selected or hovered. Used on the title and menu screens. */
    static class MenuButton extends JButton {
        int lw, lh, lfont;
        boolean selected = false, hot = false;
        MenuButton(String text, int lw, int lh, int lfont) {
            super(text);
            this.lw = lw; this.lh = lh; this.lfont = lfont;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false); setFocusable(false); setOpaque(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hot = true; repaint(); }
                public void mouseExited(MouseEvent e) { hot = false; repaint(); }
            });
        }
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.scale(getWidth() / (double) lw, getHeight() / (double) lh);
            boolean on = isEnabled(), red = on && (selected || hot);
            // Color body = !on ? new Color(0x6d7b92) : red ? BTN_SEL : BTN;
            Color light = !on ? new Color(0x8593aa) : red ? new Color(0xe0383b) : new Color(0x1d4a85);
            int bw = lw - 5, bh = lh - 5;
            g.setColor(new Color(0x8a96b0));
            pixelRect(g, 5, 5, bw, bh);                       // drop shadow
            // g.setColor(body);
            pixelRect(g, 0, 0, bw, bh);
            g.setColor(light);
            g.fillRect(3, 2, bw - 6, 2);                      // top highlight
            g.setFont(font(lfont));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(on ? Color.WHITE : new Color(0xc2cbdb));
            g.drawString(getText(), (bw - fm.stringWidth(getText())) / 2, (bh + fm.getAscent() - fm.getDescent()) / 2);
            g.dispose();
        }
    }

    /** The red arrow that points at the selected title button. */
    static class Pointer extends JComponent {
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.scale(getWidth() / 22.0, getHeight() / 20.0);
            g.setColor(BTN_SEL);
            g.fillPolygon(new int[]{2, 18, 2}, new int[]{1, 10, 19}, 3);
            g.setColor(new Color(0xe0383b));
            g.fillPolygon(new int[]{4, 12, 4}, new int[]{5, 10, 6}, 3);
            g.dispose();
        }
    }

    /** Thin line with a red square in the middle, under the logo. */
    static class Divider extends JComponent {
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.scale(getWidth() / 280.0, getHeight() / 14.0);
            g.setColor(new Color(0xafbdc9));
            g.fillRect(0, 6, 120, 2);
            g.fillRect(160, 6, 120, 2);
            g.setColor(BTN_SEL);
            g.fillRect(133, 3, 14, 8);
            g.dispose();
        }
    }

    /** The light framed panel behind the menu, with notched corners and a soft double border. */
    static class TitleCard extends JComponent {
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.scale(getWidth() / 460.0, getHeight() / 480.0);
            g.setColor(new Color(0x5d6b84)); pixelRect(g, 4, 4, 456, 476);        // shadow
            g.setColor(CARD_EDGE);           pixelRect(g, 0, 0, 456, 476);        // border
            g.setColor(new Color(0xc9d6e6)); pixelRect(g, 3, 3, 450, 470);        // inner line
            g.setColor(CARD);                pixelRect(g, 5, 5, 446, 466);        // paper
            g.dispose();
        }
    }

    void titleClick(int i) {
        if (menu.isOpen()) return;
        if (i == 0) gm.ev.startGame();
        else if (i == 1) { if (!gm.saves.load(0)) toast("No autosave yet. Start a new game!", 2.5); }
        else if (i == 2) menu.open("load", "");
        else if (i == 3) menu.open("settings", "");
        else gm.quit();
    }

    /** CONTINUE is only available when an autosave exists. */
    public void refreshTitleButtons() {
        titleButton[1].setEnabled(gm.saves.exists(0));
    }

    void createCardPanel() {
        cardPanel = new JPanel(null);
        cardPanel.setBackground(Color.black);
        cardTitle = label("", 72, WHITE);
        cardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardSub = label("", 32, WHITE);
        cardSub.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel hint = label("click to continue", 16, new Color(0x8a96c4));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.setVisible(false);
        put(content, cardPanel, 0, 0, 960, 540);
        put(cardPanel, cardTitle, 0, 170, 960, 100);
        put(cardPanel, cardSub, 0, 285, 960, 40);
        put(cardPanel, hint, 0, 470, 960, 20);
        cardPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { gm.ev.cardClicked(); }
        });
    }

    void createDeployPanel() {
        deployPanel = new JPanel(null);
        deployPanel.setBackground(new Color(0x0b1230));
        deployPanel.setBorder(new LineBorder(RED, 4));
        JLabel title = label(" IntelliJ IDEA", 14, WHITE);
        title.setOpaque(true);
        title.setBackground(NAVY2);
        JLabel q = label("DEPLOY POLYMORPHIC BUILD TO PRODUCTION?", 17, WHITE);
        q.setHorizontalAlignment(SwingConstants.CENTER);
        JButton yes = button("Y - DEPLOY", 16, new Color(0x1f6b3a));
        JButton no = button("N - WAIT", 16, new Color(0x6b1f2a));
        yes.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { gm.ev.deploy(true); } });
        no.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { gm.ev.deploy(false); } });
        deployPanel.setVisible(false);
        put(content, deployPanel, 250, 190, 460, 180);
        put(deployPanel, title, 0, 0, 460, 26);
        put(deployPanel, q, 0, 50, 460, 30);
        put(deployPanel, yes, 60, 110, 150, 46);
        put(deployPanel, no, 250, 110, 150, 46);
    }

    void createPuzzlePanel() {
        puzzlePanel = new JPanel(null);
        puzzlePanel.setBackground(new Color(0x0b1230));
        puzzlePanel.setBorder(new LineBorder(RED, 3));
        puzzlePanel.setVisible(false);
        put(content, puzzlePanel, 30, 44, 900, 476);

        puzTitle = label("", 15, YELLOW);
        puzInstr = label("", 16, WHITE);
        puzCode = new JTextArea();
        puzCode.setBackground(new Color(0x050a1e));
        puzCode.setForeground(new Color(0xaecbff));
        puzCode.setFont(font(16));
        puzCode.setEditable(false);
        puzCode.setFocusable(false);
        puzTimer = label("", 24, WHITE);
        puzTimerBar = new JProgressBar(0, 100);
        puzStep = label("", 13, new Color(0xaecbff));
        puzHint = button("HINT (+4 debt)", 12, new Color(0x6b5a1f));
        puzGc = button("RUN GC", 14, new Color(0x1f6b3a));
        puzHint.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { gm.puzzle.hint(); } });
        puzGc.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { gm.puzzle.gc(); } });
        puzFeedback = new JTextArea();
        puzFeedback.setBackground(new Color(0x050a1e));
        puzFeedback.setForeground(WHITE);
        puzFeedback.setFont(font(15));
        puzFeedback.setEditable(false);
        puzFeedback.setFocusable(false);
        puzFeedback.setLineWrap(true);
        puzFeedback.setWrapStyleWord(true);

        put(puzzlePanel, puzTitle, 20, 10, 860, 24);
        put(puzzlePanel, puzInstr, 20, 38, 860, 26);
        put(puzzlePanel, puzCode, 20, 76, 580, 170);
        put(puzzlePanel, puzTimer, 630, 76, 260, 34);
        put(puzzlePanel, puzTimerBar, 630, 114, 250, 14);
        put(puzzlePanel, puzStep, 630, 134, 250, 18);
        put(puzzlePanel, puzHint, 630, 166, 130, 38);
        put(puzzlePanel, puzGc, 770, 166, 110, 38);
        for (int i = 0; i < 4; i++) {
            final int choice = i;
            puzOption[i] = button("", 14, NAVY2);
            puzOption[i].setHorizontalAlignment(SwingConstants.LEFT);
            puzOption[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { gm.puzzle.choose(choice); }
            });
            put(puzzlePanel, puzOption[i], 20 + (i % 2) * 440, 264 + (i / 2) * 56, 420, 46);
        }
        put(puzzlePanel, puzFeedback, 20, 384, 860, 70);
    }

    void createCreditsPanel() {
        creditsPanel = new UmlPanel();
        creditsPanel.setVisible(false);
        put(content, creditsPanel, 0, 0, 960, 540);
        creditsPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { gm.ev.creditsClicked(); }
        });
    }

    /** Keyboard shortcuts. */
    void createKeys() {
        InputMap im = window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = window.getRootPane().getActionMap();
        bind(im, am, "ESCAPE", new Runnable() { public void run() { menu.toggle(); } });
        bind(im, am, "SPACE", new Runnable() { public void run() { confirmKey(); } });
        bind(im, am, "ENTER", new Runnable() { public void run() { confirmKey(); } });
        bind(im, am, "UP", new Runnable() { public void run() { moveTitleSel(-1); } });
        bind(im, am, "DOWN", new Runnable() { public void run() { moveTitleSel(1); } });
        bind(im, am, "F5", new Runnable() { public void run() { gm.quickSave(); } });
        bind(im, am, "F9", new Runnable() { public void run() { gm.quickLoad(); } });
        bind(im, am, "F11", new Runnable() { public void run() { toggleFullscreen(); } });
        bind(im, am, "G", new Runnable() { public void run() { keyGC(); } });
    }

    void bind(InputMap im, ActionMap am, String key, final Runnable action) {
        im.put(KeyStroke.getKeyStroke(key), key);
        am.put(key, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    void keyGC() {
        if (menu.isOpen()) return;
        if (gm.puzzle.active) gm.puzzle.gc();
        else if (gm.sc.current == 7 && !gm.ev.talking) toast(gm.player.gc(), 2.0);
    }

    void toggleFullscreen() {
        boolean on = (window.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0;
        window.setExtendedState(on ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
        gm.settings.fullscreen = on;
    }

    

    
    







    






}
