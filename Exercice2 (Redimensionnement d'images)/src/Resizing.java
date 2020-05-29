/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet,
*/
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Resizing{
    //###ATTRIBUTS###//
    public static BufferedImage aImage = null;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        chargerImage("images/chaton.jpg");
    }

    //###AUTRES FONCTIONS###//

    public static void chargerImage(String pFileName){
        try{
            aImage = ImageIO.read(new File(pFileName));
            System.out.println("L'image s'est bien chargée");
            System.out.println("Informations de l'image:");
            System.out.println(aImage);
        }
        catch (IOException e){
            System.out.println("L'image ne s'est pas chargée");
        }
    }
}
