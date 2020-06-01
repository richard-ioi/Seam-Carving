// Richard FOUQUOIRE et Jérémy LAVEILLE - 30/05/2020
// ESIEE E2 groupe 10

import java.io.*;
import java.util.stream.IntStream;
import javax.imageio.*;
import java.awt.Color;
import java.awt.image.*;
//import java.awt.image.ConvolveOp;
//import java.awt.image.Kernel;
import java.util.Arrays;

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
    public static int[][] aCostTableVertical;
    public static int[][] aCostTableHorizontal;
    public static int aXmaxVertical;
    public static int aYmaxVertical;
    public static int aXmaxHorizontal;
    public static int aYmaxHorizontal;
    public static int aPlusFaibleCoutVertical;
    public static int[][] aVerticalSeamTab;
    public static int[][] aHorizontalSeamTab;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        System.out.println("Nouvelle hauteur = "+aNewHauteurImage+"px, nouvelle largeur = "+aNewLargeurImage+"px");
        while((aLargeurImage > aNewLargeurImage) || (aHauteurImage>aNewHauteurImage)) {
            appliquerFiltre(aImage);
            if(aLargeurImage>aNewLargeurImage){
                calculCostTableVertical(aEnergyImage,aLargeurImage,aHauteurImage);
                calculSeamVertical();
                aLargeurImage = aLargeurImage-1;
            }
            if(aHauteurImage>aNewHauteurImage){
                calculCostTableHorizontal(aEnergyImage,aLargeurImage,aHauteurImage);
                calculSeamHorizontal();
                aHauteurImage = aHauteurImage-1;
            }
            System.out.println("LargeurIMAGE : " + aLargeurImage);
            System.out.println("HauteurIMAGE : " + aHauteurImage);
            creerImage(resizeGrille(aGrille));
            aImage = aResizedImage;
        }
        creerImage(aGrille);
        creerFichier();
    }

    //###FONCTIONS ESSENTIELLES###//

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

    //###GESTION DES IMAGES / FICHIERS ###//

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
            ImageIO.write(aResizedImage, "PNG", new File("resized_images/resized_"+aNomImage));
        }
        catch (IOException e){
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

    public static void printTab(int[][] pTab){
        for (int i=0;i<pTab.length-1;i++){
            for (int j=0;j<pTab[0].length;j++){
                System.out.print(pTab[i][j]+" ");
            }
            System.out.println(" ");
        }
    }

    //###ANALYSE DE L'IMAGE###//
    
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
        boolean vPixelAffiche = true;
        int[][] vGrille = new int[pGrille.length][pGrille[0].length-1];
        int indice = -1;
        for (int i=0 ; i<pGrille.length ; i++){
            for (int j=0 ; j<pGrille[0].length-1 ; j++){
                for (int k=0 ; k<aVerticalSeamTab.length ; k++) {
                    if(aVerticalSeamTab[k][0] == i && aVerticalSeamTab[k][1] == j) {
                        indice = k;
                        break;
                    }
                    else {
                        indice = -1;
                    }
                }
                for (int l=0 ; l<aHorizontalSeamTab.length ; l++) {
                    if(aHorizontalSeamTab[l][0] == i && aHorizontalSeamTab[l][1] == j) {
                        indice = l;
                        break;
                    }
                    else {
                        indice = -1;
                    }
                }
                //System.out.print(indice+" ");
                if( indice == -1 ) {
                    if (vPixelAffiche==true){
                        vGrille[i][j] = pGrille[i][j];
                    }else{
                        vGrille[i][j-1] = pGrille[i][j];
                    }
                
                }
                else {
                    vPixelAffiche=false;
                }
                /*else {
                    //System.out.println("PIXEL ROUGE");
                    vGrille[i][j] = -10485760;
                }*/
            }
            vPixelAffiche=true;
        }
        return vGrille;
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

    public static int e(int pL, int pC, int[][] pGrille){
        return pGrille[pL][pC+1];
    }

    public static int se(int pL, int pC, int[][] pGrille){
        if (pC+1>=pGrille[0].length){
            return aInfini;
        }else{
            return pGrille[pL-1][pC+1];
        }
    }

    //###CALCUL VERTICAL##//
    public static void calculCostTableVertical(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableVertical = new int[pHauteurImage][pLargeurImage];
        aGrille = getColorTab(aImage);

        for (int i=0; i<pLargeurImage ; i++){
            aCostTableVertical[0][i]=aGrille[0][i];
        }

        int Mno=0;
        int Mn=0;
        int Mne=0;

        for (int l=1 ; l<pHauteurImage ; l++){
            for (int c=0 ; c<pLargeurImage ; c++){

                Mn=aCostTableVertical[l-1][c]+n(l-1,c,aGrille);

                if(c+1>=pLargeurImage) {
                    Mno = aInfini;
                }
                else {
                    Mno=aCostTableVertical[l-1][c+1]+no(l-1,c+1,aGrille);
                }

                if(c-1 < 0) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTableVertical[l-1][c-1]+ne(l-1,c-1,aGrille);
                }

                aCostTableVertical[l][c]= (int)Math.min(Mn, (int)Math.min(Mno,Mne));
            }
        }
    }

    static void calculSeamVertical(){
        aVerticalSeamTab = new int[aHauteurImage][2];
        plusFaibleCoutVertical();
        seamFinderVertical(aYmaxVertical,aXmaxVertical,-1);
        /*for (int i=0;i<aHauteurImage;i++){
            System.out.println("["+aVerticalSeamTab[i][0]+","+aVerticalSeamTab[i][1]+"]");
        }*/
    }

    static void seamFinderVertical(int pL, int pC, int pCompteur){
        pCompteur+=1;
        
        int Mno=aInfini;
        int Mn=aInfini;
        int Mne=aInfini;

        if((pL-1>=0)){
            Mn=aCostTableVertical[pL-1][pC]+n(pL-1,pC,aGrille);
        }
        if((pL-1>=0) && (pC-1>=0)){
            Mne=aCostTableVertical[pL-1][pC-1]+ne(pL-1,pC-1,aGrille);
        }
        if((pL-1>=0) && (pC+1<aLargeurImage)){
            Mno=aCostTableVertical[pL-1][pC+1]+no(pL-1,pC+1,aGrille);
        }

        if(aCostTableVertical[pL][pC] == Mn){
            seamFinderVertical(pL-1,pC,pCompteur);
        } 
        else if (aCostTableVertical[pL][pC]==Mne){
            seamFinderVertical(pL-1,pC-1,pCompteur);
        }
        else if (aCostTableVertical[pL][pC]==Mno){
            seamFinderVertical(pL-1,pC+1,pCompteur);
        }
        aVerticalSeamTab[pCompteur] = new int[] {pL,pC};

    }

    static void plusFaibleCoutVertical(){
        int valeurMinV = IntStream.of(aCostTableVertical[aCostTableVertical.length-1]).min().getAsInt();
        int[] vMax = new int[] {aCostTableVertical.length-1 , Arrays.asList(aCostTableVertical[aCostTableVertical.length-1]).indexOf(valeurMinV)};
        aYmaxVertical = vMax[0];
        aXmaxVertical = vMax[1]+1;
    }

    //###CALCUL HORIZONTAL###
    public static void calculCostTableHorizontal(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableHorizontal = new int[pHauteurImage][pLargeurImage];
        aGrille = getColorTab(aImage);

        for (int i=0; i<pHauteurImage ; i++){
            aCostTableHorizontal[i][0]=aGrille[i][0];
        }

        int Mne=0;
        int Me=0;
        int Mse=0;

        for (int c=1 ; c<pLargeurImage-1 ; c++){
            for (int l=1 ; l<pHauteurImage-1 ; l++){

                Me=aCostTableHorizontal[l][c-1]+e(l,c-1,aGrille);

                if((c-1<0)||(l+1>=pHauteurImage)) {
                    Mse = aInfini;
                }
                else {
                    Mse=aCostTableHorizontal[l+1][c-1]+se(l+1,c-1,aGrille);
                }

                if(c-1 < 0) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTableHorizontal[l-1][c-1]+ne(l-1,c-1,aGrille);
                }

                aCostTableHorizontal[l][c]= (int)Math.min(Me, (int)Math.min(Mse,Mne));
            }
        }
    }

    static void calculSeamHorizontal(){
        aHorizontalSeamTab = new int[aLargeurImage][2];
        plusFaibleCoutHorizontal();
        seamFinderHorizontal(aYmaxHorizontal,aXmaxHorizontal,-1);
        for (int i=0;i<aLargeurImage;i++){
            System.out.println("["+aHorizontalSeamTab[i][0]+","+aHorizontalSeamTab[i][1]+"]");
        }
    }

    static void seamFinderHorizontal(int pL, int pC, int pCompteur){
        pCompteur+=1;
        
        int Mse=aInfini;
        int Me=aInfini;
        int Mne=aInfini;

        if((pC-1>=0)){
            Me=aCostTableHorizontal[pL][pC-1]+e(pL,pC-1,aGrille);
        }
        if((pL-1>=0) && (pC-1>=0)){
            Mne=aCostTableVertical[pL-1][pC-1]+ne(pL-1,pC-1,aGrille);
        }
        if((pL+1<aHauteurImage) && (pC-1>=0)){
            Mse=aCostTableHorizontal[pL+1][pC-1]+se(pL+1,pC-1,aGrille);
        }

        if(aCostTableVertical[pL][pC] == Me){
            seamFinderHorizontal(pL,pC-1,pCompteur);
        } 
        else if (aCostTableVertical[pL][pC]==Mne){
            seamFinderHorizontal(pL-1,pC-1,pCompteur);
        }
        else if (aCostTableVertical[pL][pC]==Mse){
            seamFinderHorizontal(pL+1,pC-1,pCompteur);
        }
        aHorizontalSeamTab[pCompteur] = new int[] {pL,pC};

    }

    static void plusFaibleCoutHorizontal(){
        /*int valeurMinV = IntStream.of(aCostTableHorizontal[aCostTableHorizontal.length-1]).min().getAsInt();
        int[] vMax = new int[] {aCostTableHorizontal.length-1 , Arrays.asList(aCostTableHorizontal[aCostTableHorizontal.length-1]).indexOf(valeurMinV)};
        aYmaxHorizontal = vMax[0];
        aXmaxHorizontal = vMax[1]+1;;*/
        aXmaxHorizontal=aLargeurImage-1;
        aYmaxHorizontal=0;
        for (int i=0;i<aHauteurImage;i++){
            if(aCostTableHorizontal[i][aXmaxHorizontal]<aCostTableHorizontal[aYmaxHorizontal][aXmaxHorizontal]){
                aYmaxHorizontal=i;
            }
        }
    }

}
