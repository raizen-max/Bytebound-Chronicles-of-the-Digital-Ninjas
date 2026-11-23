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

}