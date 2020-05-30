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
    public static int aLargeurImage;
    public static int aHauteurImage;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        //System.out.println("Valeur RGB Du tableau en 0, 0:"+getRGBTab(aImage)[0][0]);
        printRGBTab(getRGBTab(aImage));
    }

    //###AUTRES FONCTIONS###//

    public static void Initialisation (final String pNom, final int pPourcentageHorizontal, final int pPourcentageVertical){
        try{
            aNomImage=pNom;
            aPourcentageHorizontal = pPourcentageHorizontal;
            aPourcentageVertical = pPourcentageVertical;
            chargerImage("Images/"+aNomImage);
        } 
        catch (Exception e){
            System.out.println("ERREUR ! La commande a été mal introduite.");
            System.out.println("Vérifiez que vous avez bien tapé la commande comme suit :");
            System.out.println("java SeamCarving nomfichier.png %NouvelleLargeur %NouvelleHauteur");
        }

    }

    public static void chargerImage(String pFileName){
        try{
            aImage = ImageIO.read(new File(pFileName));
            aLargeurImage = aImage.getWidth();
            aHauteurImage = aImage.getHeight();
            System.out.println("L'image s'est bien chargée");
            //System.out.println("Informations de l'image:");
            //System.out.println(aImage);
            System.out.println("Le nom de l'image est : "+aNomImage);
            System.out.println("Largeur de l'image : "+aLargeurImage+"px, Hauteur de l'image : "+aHauteurImage+"px");
            System.out.println("L'image doit être réduite de "+aPourcentageHorizontal+"% en largeur et "+aPourcentageVertical+"% en hauteur");
        }
        catch (IOException e){
            System.out.println("L'image ne s'est pas chargée, vérifiez le nom.");
        }
    }

    public static int[][] getRGBTab(BufferedImage pImage){
        int pLargeur=pImage.getWidth();
        int pHauteur=pImage.getHeight();
        int[][] RGBTab = new int[pLargeur][pHauteur];
        for (int i=0;i<pLargeur;i++){
            for (int j=0;j<pHauteur;j++){
                RGBTab[i][j]=pImage.getRGB(i,j)&0xFFFFFF;
            }
        }
        return RGBTab;
    }

    public static void printRGBTab(int[][] pTab){
        for (int i=0;i<aLargeurImage;i++){
            for (int j=0;j<aHauteurImage;j++){
                System.out.print(pTab[i][j]+" ");
            }
            System.out.println(" ");
        }
    }
}
