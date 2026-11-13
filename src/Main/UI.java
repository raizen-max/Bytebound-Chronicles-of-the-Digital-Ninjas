package src.Main;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JTextArea;
import java.awt.Font;

public class UI {
    
    GameManager gm;

    JFrame window;
    public JTextArea messageText;

    public UI(GameManager gm) {

        this.gm = gm;

        createMainField();

        window.setVisible(true);
    }

    public void createMainField() {

        window = new JFrame();
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(Color.black);
        window.setLayout(null);

        messageText = new JTextArea("Test Message");
        messageText.setBounds(50, 400, 700, 500);
        messageText.setBackground(Color.blue);
        messageText.setForeground(Color.white);
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setFont(new Font("Times New Roman", Font.PLAIN, 26));
        window.add(messageText);
    } 

}
