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
    
    /* 
    hotspot marker (no file yet)
    HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>(); */

     
    //title
    JButton[] titleButton = new JButton[5];
    JLabel[] titlePortrait = new JLabel[3];

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
        





    






}
