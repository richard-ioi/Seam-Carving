// Richard FOUQUOIRE et Jérémy LAVEILLE - 29/05/2020
// ESIEE E2 groupe 10

import java.awt;
import java.ImageIO;
import java.ImageIcon;
import java.File;
import java.JFrame;
import java.JLabel;
import java.jPanel;
import java.Dimension;

public class Resizing {
/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet, 
*/

    //###ATTRIBUTS###

    
    
    // ###FONCTION PRINCIPALE###
    public static void main(String[] args) {
        String imagePath = "Images/chaton1.png";
        BufferedImage myPicture = ImageIO.read(new File(imagePath));

        JLabel picLabel = new JLabel(new ImageIcon(myPicture));

        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);

        JFrame f = new JFrame();
        f.setSize(new Dimension(myPicture.getWidth(), myPicture.getHeight()));
        f.add(jPanel);
        f.setVisible(true);
    }

    //###AUTRES FONCTIONS###
    


}