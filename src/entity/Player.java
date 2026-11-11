package src.entity;

import src.Main.GamePanel;
import src.Main.KeyHandler;


public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        
        this.gp = gp;
        this.keyH = keyH;
    }
}
