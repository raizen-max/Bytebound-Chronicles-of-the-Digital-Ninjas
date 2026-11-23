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
    
}