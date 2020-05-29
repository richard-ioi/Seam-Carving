/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet,
*/
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class SeamCarving{

    //###ATTRIBUTS###//
    public static BufferedImage aImage = null;
    public static int aPourcentageHorizontal;
    public static int aPourcentageVertical;
    public static String aNomImage;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        try{
        aNomImage=args[0];
        aPourcentageHorizontal = Integer.parseInt(args[1]);
        aPourcentageVertical = Integer.parseInt(args[2]);
        chargerImage("Images/"+aNomImage);
        } 
        catch (Exception e){
            System.out.println("ERREUR ! La commande a été mal introduite.");
            System.out.println("Vérifiez que vous avez bien tapé la commande comme suit :");
            System.out.println("java SeamCarving nomfichier.png %NouvelleLargeur %NouvelleHauteur");
        }
    }

    //###AUTRES FONCTIONS###//

    public static void chargerImage(String pFileName){
        try{
            aImage = ImageIO.read(new File(pFileName));
            System.out.println("L'image s'est bien chargée");
            //System.out.println("Informations de l'image:");
            //System.out.println(aImage);
            System.out.println("Le nom de l'image est : "+aNomImage);
            System.out.println("L'image doit être réduite de "+aPourcentageHorizontal+"% en largeur et "+aPourcentageVertical+"% en hauteur");
        }
        catch (IOException e){
            System.out.println("L'image ne s'est pas chargée, vérifiez le nom.");
        }
    }
}
