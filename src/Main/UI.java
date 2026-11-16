package src.Main;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import java.awt.Image;

public class UI {
    
    GameManager gm;

    JFrame window;
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
        messageText.setBounds(50, 400, 700, 150);
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

        ImageIcon bgIcon = new ImageIcon("src\\res\\office.png");
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
