/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet,
*/
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.Color;

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
        System.out.println("R:"+getRGBPixel("r",getColorTab()[40][40]));
        System.out.println("G:"+getRGBPixel("g",getColorTab()[40][40]));
        System.out.println("B:"+getRGBPixel("b",getColorTab()[40][40]));
        printTab(getRGBTab("g"));
        
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
            System.out.println("Le nom de l'image est : "+aNomImage);
            System.out.println("Largeur de l'image : "+aLargeurImage+"px, Hauteur de l'image : "+aHauteurImage+"px");
            System.out.println("L'image doit être réduite de "+aPourcentageHorizontal+"% en largeur et "+aPourcentageVertical+"% en hauteur");
        }
        catch (IOException e){
            System.out.println("L'image ne s'est pas chargée, vérifiez le nom.");
        }
    }

    public static int[][] getColorTab(){
        int[][] COLORTab = new int[aLargeurImage][aHauteurImage];
        for (int i=0;i<aLargeurImage;i++){
            for (int j=0;j<aHauteurImage;j++){
                COLORTab[i][j]=aImage.getRGB(i,j);
            }
        }
        return COLORTab;
    }

    public static int getRGBPixel(String pColor, int pPixel){
        Color vColor = new Color(pPixel);
        if (pColor=="r"){
            return vColor.getRed();
        } else if (pColor=="g"){
            return vColor.getGreen();
        } else if (pColor=="b"){
            return vColor.getBlue();
        }
        return 0;
    }

    public static int[][] getRGBTab(String pColor){
        int[][]vRGBTab=new int[aLargeurImage][aHauteurImage];
        for (int i=0; i< aLargeurImage; i++){
            for (int j=0; j< aHauteurImage; j++){
                vRGBTab[i][j]=getRGBPixel(pColor,getColorTab()[i][j]);
            }
        }
        return vRGBTab;
    }

    public static void printTab(int[][] pTab){
        for (int i=0;i<pTab.length-1;i++){
            for (int j=0;j<pTab[0].length;j++){
                System.out.print(pTab[i][j]+" ");
            }
            System.out.println(" ");
        }
    }
}
