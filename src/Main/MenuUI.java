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
