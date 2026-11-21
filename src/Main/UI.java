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
    
    /* hotspot marker (no file yet)
    HashMap<String, ImageIcon> iconCache = new HashMap<String, ImageIcon>();
    */

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
    //JTextArea messageText;
    JLabel nameLabel, portraitLabel, objectiveLabel, toastLabel, blackout;
    double toastTime = 0; 

    //popup
    JPanel popupPanel;
    JLabel popupTitle;
    String popupName = "";

    //talking animation
    HashMap<String, ArrayList<ImageIcon>> talkFrames = new HashMap<String, ArrayList<ImageIcon>>();
    String talkSpeaker = "", talkExpr = "";
    int talkTicks = 0, talkIndex = 0;

    public JTextArea messageText;
    public JPanel bgPanel[] = new JPanel[10];
    public JLabel bgLabel[] = new JLabel[10];

    Image originalBg;

    public UI(GameManager gm) {

        this.gm = gm;

        createMainField();

        createBackground();

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

    public void createBackground() {

        bgPanel[1] = new JPanel();
        bgPanel[1].setBounds(50, 50, 700, 350); 
        bgPanel[1].setBackground(Color.blue);
        bgPanel[1].setLayout(null);
        window.add(bgPanel[1]);

        bgLabel[1] = new JLabel();  
        bgLabel[1].setBounds(0, 0, 700, 350);

        ImageIcon bgIcon = new ImageIcon("src\\res\\officee.png");
        originalBg = bgIcon.getImage();

        bgPanel[1].add(bgLabel[1]);
    }

    public void resizeComponents() {

        int width = window.getContentPane().getWidth();
        int height = window.getContentPane().getHeight();

        int bgHeight = (int)(height * 0.7);
        bgPanel[1].setBounds(0, 0, width, bgHeight);

        bgLabel[1].setBounds(0, 0, width, bgHeight);

        bgLabel[1].setIcon(new ImageIcon(
            originalBg.getScaledInstance(width, bgHeight, Image.SCALE_SMOOTH)
        ));

        messageText.setBounds(
            (int)(width * 0.05),
            (int)(height * 0.7),
            (int)(width * 0.9),
            (int)(height * 0.25)
        );
    }
}
