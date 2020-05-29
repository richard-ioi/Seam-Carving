// Richard FOUQUOIRE et Jérémy LAVEILLE - 29/05/2020
// ESIEE E2 groupe 10

import java.awt.*;
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

    static String aImagePath = "Images/chaton1.png";
    static BufferedImage aMonImage = ImageIO.read(new File(aImagePath));
    static JLabel aPicLabel = new JLabel(new ImageIcon(aMonImage));
    static JPanel aJPanel = new JPanel();
    static JFrame aF = new JFrame();
    
    // ###FONCTION PRINCIPALE###
    public static void main(String[] args) {

        jPanel.add(aPicLabel);

        aF.setSize(new Dimension(aMonImage.getWidth(), aMonImage.getHeight()));
        aF.add(aJPanel);
        aF.setVisible(true);
    }

    //###AUTRES FONCTIONS###
    


}