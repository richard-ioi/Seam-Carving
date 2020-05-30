// Richard FOUQUOIRE et Jérémy LAVEILLE - 28/05/2020
// ESIEE E2 groupe 10

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.Color;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class SeamCarving{
/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet, de redimensionner une image par la méthode du Seam Carving.
Pour cela le programme utilise différentes focntions dan le but :
    -> De charger une image à partir d'un fichier (placé en argument),
    -> De lui appliquer un filtre par convolution pour obtenir "l'énergie" des différents pixels,
    -> De déterminer les "seams" correspondant au chemins des pixels à l'énergie la plus faible,
    -> De redimensionner l'image en retirant ces pixels moins importants et de créer un nouveau fichier png.
*/

    //###ATTRIBUTS###//
    public static BufferedImage aImage = null;
    public static BufferedImage aResizedImage = null;
    public static int aPourcentageHorizontal;
    public static int aPourcentageVertical;
    public static String aNomImage;
    public static int aLargeurImage;
    public static int aHauteurImage;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        System.out.println("R:"+getRGBPixel("r",getColorTab(aImage)[10][10]));
        System.out.println("G:"+getRGBPixel("g",getColorTab(aImage)[10][10]));
        System.out.println("B:"+getRGBPixel("b",getColorTab(aImage)[10][10]));
        appliquerFiltre(aImage);
        //printTab(getRGBTab("r",aImage));
        CreerImage();
        
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

    public static int[][] getColorTab(BufferedImage pImage){
        int pLargeurImage = pImage.getWidth();
        int pHauteurImage = pImage.getHeight();
        int[][] COLORTab = new int[pLargeurImage][pHauteurImage];
        for (int i=0;i<pLargeurImage;i++){
            for (int j=0;j<pHauteurImage;j++){
                COLORTab[i][j]=pImage.getRGB(i,j);
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

    public static int[][] getRGBTab(String pColor, BufferedImage pImage){
        int pLargeurImage = pImage.getWidth();
        int pHauteurImage = pImage.getHeight();
        int[][]vRGBTab=new int[pLargeurImage][pHauteurImage];
        for (int i=0; i< pLargeurImage; i++){
            for (int j=0; j< pHauteurImage; j++){
                vRGBTab[i][j]=getRGBPixel(pColor,getColorTab(pImage)[i][j]);
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

    public static void appliquerFiltre(BufferedImage pImage) {

        /* Définition de la première convolution */
        Kernel kernel1 = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
        ConvolveOp convolution1 = new ConvolveOp(kernel1);
        BufferedImage resultatIntermediaire = convolution1.filter(pImage, null);

        Kernel kernel2 = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
        ConvolveOp convolution2 = new ConvolveOp(kernel2);

        /*
        Kernel kernel = new Kernel(3, 3, new float[]{0f,1f,0f, 1f,-4f,1f, 0f,1f,0f});
        ConvolveOp convolution = new ConvolveOp(kernel);*/

        aResizedImage = convolution2.filter(resultatIntermediaire, null);
    }

    public static void CreerImage(){
        try{
            ImageIO.write(aResizedImage, "JPG", new File("resized_images/resized_"+aNomImage));
        }
        catch (IOException e){
        }
    }

}
