// Richard FOUQUOIRE et Jérémy LAVEILLE - 30/05/2020
// ESIEE E2 groupe 10

import java.awt.image.*;
import java.io.*;
import java.util.stream.IntStream;

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
    public static final int aInfini = Integer.MAX_VALUE; // Constante l'infini qui nous sert plus tard, pour que la coccinelle ne sorte pas de la grille
    public static BufferedImage aImage = null;
    public static BufferedImage aEnergyImage = null;
    public static BufferedImage aResizedImage = null;
    public static float aPourcentageHorizontal;
    public static float aPourcentageVertical;
    public static String aNomImage;
    public static int aLargeurImage;
    public static int aHauteurImage;
    public static int aNewLargeurImage;
    public static int aNewHauteurImage;
    public static int[][] aGrille;
    public static int[][] aCostTable;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        System.out.println("Nouvelle hauteur = "+aNewHauteurImage+"px, nouvelle largeur = "+aNewLargeurImage+"px");
        appliquerFiltre(aImage);
        calculCostTable(aEnergyImage);
        //printTab(aGrille);
        System.out.println("");
        //printTab(aCostTable);
        //creerImage(aCostTable);
        creerFichier();
    }

    //###AUTRES FONCTIONS###//

    public static void Initialisation (final String pNom, final int pPourcentageVertical, final int pPourcentageHorizontal){
        try{
            aNomImage=pNom;
            aPourcentageHorizontal = pPourcentageHorizontal;
            aPourcentageVertical = pPourcentageVertical;
            chargerImage("images/"+aNomImage);
            aNewHauteurImage = (int)(aHauteurImage-(aHauteurImage*(aPourcentageVertical)/100));
            aNewLargeurImage = (int)(aLargeurImage-(aLargeurImage*(aPourcentageHorizontal)/100));
            System.out.println("L'image doit être réduite de "+aPourcentageVertical+"% en hauteur et "+aPourcentageHorizontal+"% en largeur");
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
            aHauteurImage = aImage.getHeight();
            aLargeurImage = aImage.getWidth();
            System.out.println("L'image s'est bien chargée");
            System.out.println("Le nom de l'image est : "+aNomImage);
            System.out.println("Hauteur de l'image : "+aHauteurImage+"px, Largeur de l'image : "+aLargeurImage+"px");
        }
        catch (IOException e){
            System.out.println("L'image ne s'est pas chargée, vérifiez le nom.");
        }
    }

    public static int[][] getColorTab(BufferedImage pImage){
        int pLargeurImage = pImage.getWidth();
        int pHauteurImage = pImage.getHeight();
        int[][] COLORTab = new int[pHauteurImage][pLargeurImage];
        for (int i=0;i<pHauteurImage;i++){
            for (int j=0;j<pLargeurImage;j++){
                COLORTab[i][j]=pImage.getRGB(j,i);
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
        int[][]vRGBTab=new int[pHauteurImage][pLargeurImage];
        for (int i=0 ; i<pHauteurImage ; i++){
            for (int j=0 ; j<pLargeurImage ; j++){
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

        aEnergyImage = convolution2.filter(resultatIntermediaire, null);
    }

    public static int[][] resizeGrille(int[][] pGrille) {
        int[][] vGrille = new int[pGrille[0].length-1][pGrille.length-1];
        for (int i=0;i<pGrille.length-1;i++){
            for (int j=0;j<pGrille[0].length;j++){
                if( !ArrayUtils.indexOf(verticalSeam,new int[] {i,j}) && !ArrayUtils.indexOf(horizontalSeam,new int[] {i,j}) ) {
                    vGrille[i][j] = pGrille[i][j];
                }
            }
        }
        return vGrille;
    }

    public static void creerImage(int[][] pGrille){
        aResizedImage = new BufferedImage(pGrille[0].length, pGrille.length, BufferedImage.TYPE_INT_RGB);
        for (int i=0;i<pGrille.length-1;i++){
            for (int j=0;j<pGrille[0].length;j++){
                aResizedImage.setRGB(j,i,pGrille[i][j]);
            }
        }
    }

    public static void creerFichier(){
        try{
            ImageIO.write(aResizedImage, "JPG", new File("resized_images/resized_"+aNomImage));
        }
        catch (IOException e){
        }
    }
    
    /*
     Fonction qui calcule le coût des déplacements Nord-Ouest
    */
    public static int no(int pL, int pC, int[][] pGrille){
        if (pC-1<0){
            return aInfini;
        }else{
            return pGrille[pL+1][pC-1];
        }
    }

    /*
    Fonction qui calcule le coût des déplacements Nord
    */
    public static int n(int pL, int pC, int[][] pGrille){
        return pGrille[pL+1][pC];
    }

    /*
    Fonction qui calcule le coût des déplacements Nord-Est
    */
    public static int ne(int pL, int pC, int[][] pGrille){
        if (pC+1>=pGrille[0].length){
            return aInfini;
        }else{
            return pGrille[pL+1][pC+1];
        }
    }

    /*
    
    */
    public static void calculCostTable(BufferedImage pImage){
        int pHauteurImage = pImage.getHeight();
        int pLargeurImage = pImage.getWidth();
        aCostTable = new int[pHauteurImage][pLargeurImage];
        aGrille = getColorTab(aImage);

        for (int i=0; i<pLargeurImage ; i++){
            aCostTable[0][i]=aGrille[0][i];
        }

        int Mno=0;
        int Mn=0;
        int Mne=0;

        for (int l=1 ; l<pHauteurImage ; l++){
            for (int c=0 ; c<pLargeurImage ; c++){

                Mn=aCostTable[l-1][c]+n(l-1,c,aGrille);

                if(c+1>=pLargeurImage) {
                    Mno = aInfini;
                }
                else {
                    Mno=aCostTable[l-1][c+1]+no(l-1,c+1,aGrille);
                }

                if(c-1 < 0) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTable[l-1][c-1]+ne(l-1,c-1,aGrille);
                }

                aCostTable[l][c]= (int)Math.min(Mn, (int)Math.min(Mno,Mne));
            }
        }
    }

}