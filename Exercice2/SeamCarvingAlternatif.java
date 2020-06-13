// Richard FOUQUOIRE et Jérémy LAVEILLE - 30/05/2020
// ESIEE E2 groupe 10

import java.io.*;
import java.util.stream.IntStream;
import javax.imageio.*;
import java.awt.Color;
import java.awt.image.*;
import java.util.Arrays;

public class SeamCarvingAlternatif{
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
    public static BufferedImage aOriginalImage = null;
    public static BufferedImage aEnergyImageVertical = null;
    public static BufferedImage aEnergyImageHorizontal = null;
    public static BufferedImage aResizedImage = null;
    public static String aNomImage;
    public static String aNomResizedFinal;
    public static String aNomSeamedFinal;
    public static boolean aHauteurPlusGrand = false;
    public static boolean aLargeurPlusGrand = false;
    public static float aPourcentageAvancement = 0;
    public static int aPourcentageHorizontal;
    public static int aPourcentageVertical;
    public static int aLargeurImage;
    public static int aHauteurImage;
    public static int aNewLargeurImage;
    public static int aNewHauteurImage;
    public static int aXmaxVertical;
    public static int aYmaxVertical;
    public static int aXmaxHorizontal;
    public static int aYmaxHorizontal;
    public static int aPlusFaibleCoutVertical;
    public static int aOptimisation;
    public static int aNbSeams;
    public static int aCompteurPourcentage=0;
    public static int[][] aGrille;
    public static int[][] aEnergyGrille;
    public static int[][] aCostTableVertical;
    public static int[][] aCostTableHorizontal;
    public static int[][] aCostTableVertical2;
    public static int[][] aCostTableHorizontal2;
    public static int[][] aVerticalSeamTab;
    public static int[][] aHorizontalSeamTab;
    public static int[][][] aAllSeams;
    public static int[] aXmaxVerticalTab;
    public static int[] aYmaxHorizontalTab;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        int vCompteurWhile=0;
        boolean vResizeVertical=false;
        boolean vResizeHorizontal=false;
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        aGrille = getColorTab(aImage);
        appliquerFiltreHorizontal(aImage);
        appliquerFiltreVertical(aImage);
        calculCostTableHorizontal(aEnergyImageHorizontal,aLargeurImage,aHauteurImage);
        calculCostTableVertical(aEnergyImageVertical,aLargeurImage,aHauteurImage);
        
        while( (aLargeurImage > aNewLargeurImage)){//|| (aHauteurImage > aNewHauteurImage) ) {
            aPourcentageAvancement = (aCompteurPourcentage*100)/aNbSeams;
            vResizeVertical=false;
            vResizeHorizontal=false;
            /*if(aHauteurImage > aNewHauteurImage){
                calculSeamHorizontal();
                aHauteurImage-=1;
                vResizeHorizontal=true;
                aCompteurPourcentage+=1;
                //aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
            }*/
            if(aLargeurImage > aNewLargeurImage){
                calculSeamVertical();
                //retraitSeamCostTableVertical();
                aLargeurImage-=1;
                vResizeVertical=true;
                aCompteurPourcentage+=1;
                //aGrille=resizeGrille(aGrille,vResizeVertical,vResizeHorizontal);
                //aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
            }
            System.out.println("Largeur actuelle : " + aLargeurImage + "px");
            System.out.println("Hauteur actuelle : " + aHauteurImage + "px");
            if(aLargeurImage>aNewLargeurImage){// || aHauteurImage>aNewHauteurImage){
                System.out.println(" => Pourcentage avancement : "+ aPourcentageAvancement+"%");
            }
            System.out.println("");
            vCompteurWhile+=1;
        }
        System.out.println(" => Pourcentage avancement : 100%\n => TERMINE !");
        System.out.println();
        System.out.println("L'image est en cours de redimensionnement, patientez... Cela prend pas mal de temps.");
        for(int p=0;p<aXmaxVerticalTab.length;p++){
            System.out.println(aXmaxVerticalTab[p]);
        }
        //aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
        creerFichier(creerImage(aGrille),"r");
        traceSeam();
        System.out.println();
        System.out.println("Fichier redimensionne : "+aNomResizedFinal);
        System.out.println("Fichier comportant les seams : "+aNomSeamedFinal);
        System.out.println();
    }

    // ---- INITIALISATION - CHARGEMENT DE L'IMAGE ---- //

    public static void retraitSeamCostTableVertical(){
        int [][] vGrille = new int[aHauteurImage][aLargeurImage-1];
        for (int i=0;i<aHauteurImage;i++){
            for (int j=0; j<aLargeurImage-1;j++){
                if ((i==aVerticalSeamTab[i][0])&&(j==aVerticalSeamTab[i][1])){
                    //
                } else{
                    vGrille[i][j]=aCostTableVertical[i][j];
                }
            }
        }
        aCostTableVertical = vGrille;
        System.out.println(aCostTableVertical[0].length);
    }


    public static void Initialisation (final String pNom, final int pPourcentageHorizontal, final int pPourcentageVertical){
        try{
            aNomImage=pNom;
            aPourcentageHorizontal = pPourcentageHorizontal;
            aPourcentageVertical = pPourcentageVertical;
            chargerImage("images/"+aNomImage);
            aOriginalImage=aImage;
            aNewHauteurImage = (int)(aHauteurImage-(aHauteurImage*(aPourcentageHorizontal)/100));
            aNewLargeurImage = (int)(aLargeurImage-(aLargeurImage*(aPourcentageVertical)/100));
            aXmaxVerticalTab = new int[aLargeurImage-aNewLargeurImage];
            aYmaxHorizontalTab = new int[aHauteurImage-aNewHauteurImage];
            System.out.println("L'image doit être réduite de "+aPourcentageHorizontal+"% en hauteur et "+aPourcentageVertical+"% en largeur");
            System.out.println("\nNouvelle hauteur = "+aNewHauteurImage+"px, nouvelle largeur = "+aNewLargeurImage+"px\n");
            if((aHauteurImage-aNewHauteurImage)>(aLargeurImage-aNewLargeurImage)) {
                aHauteurPlusGrand = true;
                aOptimisation = (int)((aHauteurImage-aNewHauteurImage)/(aLargeurImage-aNewLargeurImage));
            }else if((aHauteurImage-aNewHauteurImage)<=(aLargeurImage-aNewLargeurImage)) {
                aLargeurPlusGrand = true;
                aOptimisation = (int)((aLargeurImage-aNewLargeurImage)/(aHauteurImage-aNewHauteurImage));
            }
            aNbSeams=(aLargeurImage-aNewLargeurImage)+(aHauteurImage-aNewHauteurImage);
            if(aLargeurPlusGrand) {
                aAllSeams=new int[aNbSeams][aLargeurImage][2];
            }else {
                aAllSeams=new int[aNbSeams][aHauteurImage][2];
            }
        }
        catch (Exception e){
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

    // ---- DETERMINATION DE LA VALEUR DES PIXELS / CALCUL DES SEAMS ---- //

    public static void appliquerFiltreVertical(BufferedImage pImage) {
        /* Définition de la convolution du filtre vertical */
        Kernel kernel = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
        ConvolveOp convolution = new ConvolveOp(kernel);
        aEnergyImageVertical = convolution.filter(pImage, null);

    }

    public static void appliquerFiltreHorizontal(BufferedImage pImage) {
        /* Définition de la convolution du filtre horizontal */
        Kernel kernel = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
        ConvolveOp convolution = new ConvolveOp(kernel);
        aEnergyImageHorizontal = convolution.filter(pImage, null);
    }
    //Autre possibilite : FILTRE LAPLACIEN
    /*Kernel kernel = new Kernel(3, 3, new float[]{0f,1f,0f, 1f,-4f,1f, 0f,1f,0f});
    ConvolveOp convolution = new ConvolveOp(kernel);*/

    static void calculSeamVertical(){
        aVerticalSeamTab = new int[aHauteurImage][2];
        plusFaibleCoutVertical();
        seamFinderVertical(aYmaxVertical,aXmaxVertical,-1);
        aAllSeams[aCompteurPourcentage]=aVerticalSeamTab;
    }

    static void calculSeamHorizontal(){
        aHorizontalSeamTab = new int[aLargeurImage][2];
        plusFaibleCoutHorizontal();
        seamFinderHorizontal(aYmaxHorizontal,aXmaxHorizontal,-1);
        aAllSeams[aCompteurPourcentage]=aHorizontalSeamTab;
    }

    static void plusFaibleCoutVertical(){
        boolean vExiste=false;
        aXmaxVertical=0;
        aYmaxVertical=aHauteurImage-1;
        for (int i=0;i<=aLargeurImage-1;i++){
            if(aCostTableVertical2[aYmaxVertical][i]<aCostTableVertical2[aYmaxVertical][aXmaxVertical]){
                for(int j=0;j<aXmaxVerticalTab.length;j++){
                    if(i==aXmaxVerticalTab[j]){
                        vExiste=true;
                        break;
                    }
                    if(j-1 >= 0) {
                        if(aXmaxVerticalTab[j-1]==0 && aXmaxVerticalTab[j]==0) {
                            break;
                        }
                    }
                }
                if(vExiste==false){
                    aXmaxVertical=i;
                }
            }
        }
        System.out.println(aCostTableVertical2[aYmaxVertical][aXmaxVertical]);
        aCostTableVertical2[aYmaxVertical][aXmaxVertical] = aInfini;
        aXmaxVerticalTab[aImage.getWidth()-aLargeurImage]=aXmaxVertical;
    }

    public static void calculCostTableVertical(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableVertical = new int[pHauteurImage][pLargeurImage];
        aCostTableVertical2 = new int[pHauteurImage][pLargeurImage];
        aEnergyGrille = getColorTab(pImage);
        noEmptyLines(aEnergyGrille);

        for (int i=0; i<pLargeurImage ; i++){
            aCostTableVertical[0][i]=aEnergyGrille[0][i];
        }
        int Mno=0;
        int Mn=0;
        int Mne=0;

        for (int l=1 ; l<pHauteurImage ; l++){
            for (int c=0 ; c<pLargeurImage ; c++){

                Mn=aCostTableVertical[l-1][c]+n(l-1,c,aEnergyGrille);

                if(c+1>=pLargeurImage) {
                    Mno = aInfini;
                }
                else {
                    Mno=aCostTableVertical[l-1][c+1]+no(l-1,c+1,aEnergyGrille);
                }

                if(c-1 < 0) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTableVertical[l-1][c-1]+ne(l-1,c-1,aEnergyGrille);
                }

                aCostTableVertical[l][c]= (int)Math.min(Mn, (int)Math.min(Mno,Mne));
                aCostTableVertical2[l][c] = aCostTableVertical[l][c];
            }
        }
    }

    static void seamFinderVertical(int pL, int pC, int pCompteur){
        pCompteur+=1;
        int Mno=aInfini;
        int Mn=aInfini;
        int Mne=aInfini;

        if((pL-1>=0)){
            Mn=aCostTableVertical[pL-1][pC]+n(pL-1,pC,aEnergyGrille);
        }
        if((pL-1>=0) && (pC-1>=0)){
            Mne=aCostTableVertical[pL-1][pC-1]+ne(pL-1,pC-1,aEnergyGrille);
        }
        if((pL-1>=0) && (pC+1<aLargeurImage)){
            Mno=aCostTableVertical[pL-1][pC+1]+no(pL-1,pC+1,aEnergyGrille);
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

    static void plusFaibleCoutHorizontal(){
        aXmaxHorizontal=aLargeurImage-1;
        aYmaxHorizontal=0;
        for (int i=0;i<=aHauteurImage-1;i++){
            if(aCostTableHorizontal[i][aXmaxHorizontal]<aCostTableHorizontal[aYmaxHorizontal][aXmaxHorizontal]){
                aYmaxHorizontal=i;
            }
        }
    }

    public static void calculCostTableHorizontal(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableHorizontal = new int[pHauteurImage][pLargeurImage];
        aCostTableHorizontal2 = new int[pHauteurImage][pLargeurImage];
        aEnergyGrille = getColorTab(pImage);
        noEmptyLines(aEnergyGrille);

        for (int i=0; i<pHauteurImage ; i++){
            aCostTableHorizontal[i][0]=aEnergyGrille[i][0];
        }
        int Mne=0;
        int Me=0;
        int Mse=0;

        for (int c=1 ; c<pLargeurImage ; c++){
            for (int l=0 ; l<pHauteurImage ; l++){

                if (c-1<0) {
                    Me=aInfini;
                }else {
                    Me=aCostTableHorizontal[l][c-1]+e(l,c-1,aEnergyGrille);
                }

                if((l+1 >= pHauteurImage)||(c-1<0)) {
                    Mse = aInfini;
                }
                else {
                    Mse=aCostTableHorizontal[l+1][c-1]+se(l+1,c-1,aEnergyGrille);
                }

                if((l-1 < 0)||(c-1<0)) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTableHorizontal[l-1][c-1]+ne(l-1,c-1,aEnergyGrille);
                }

                aCostTableHorizontal[l][c]= Math.min(Me, Math.min(Mse,Mne));
                aCostTableHorizontal2[l][c] = aCostTableHorizontal[l][c];
            }
        }
    }

    static void seamFinderHorizontal(int pL, int pC, int pCompteur){
        pCompteur+=1;
        
        int Mse=aInfini;
        int Me=aInfini;
        int Mne=aInfini;

        if((pC-1>=0)) {
            Me=aCostTableHorizontal[pL][pC-1]+e(pL,pC-1,aEnergyGrille);
        }
        if((pL-1>=0) && (pC-1>=0)) {
            Mne=aCostTableHorizontal[pL-1][pC-1]+ne(pL-1,pC-1,aEnergyGrille);
        }
        if((pL+1<aHauteurImage) && (pC-1>=0)) {
            Mse=aCostTableHorizontal[pL+1][pC-1]+se(pL+1,pC-1,aEnergyGrille);
        }

        if(aCostTableHorizontal[pL][pC] == Me) {
            seamFinderHorizontal(pL,pC-1,pCompteur);
        } 
        else if (aCostTableHorizontal[pL][pC]==Mne) {
            seamFinderHorizontal(pL-1,pC-1,pCompteur);
        }
        else if (aCostTableHorizontal[pL][pC]==Mse) {
            seamFinderHorizontal(pL+1,pC-1,pCompteur);
        }
        aHorizontalSeamTab[pCompteur] = new int[] {pL,pC};
    }

    /*
     Fonction qui calcule le coût des déplacements Nord-Ouest
    */
    public static int no(int pL, int pC, int[][] pGrille){
        if (pC-1<0 || pL+1>=aHauteurImage){
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
        if (pC+1>=aLargeurImage || pL+1>=aHauteurImage){
            return aInfini;
        }else{
            return pGrille[pL+1][pC+1];
        }
    }

     /*
    Fonction qui calcule le coût des déplacements Est
    */
    public static int e(int pL, int pC, int[][] pGrille){
        return pGrille[pL][pC+1];
    }

     /*
    Fonction qui calcule le coût des déplacements Sud-Est
    */
    public static int se(int pL, int pC, int[][] pGrille){
        if (pC+1>=aLargeurImage || pL-1<0){
            return aInfini;
        }else{
            return pGrille[pL-1][pC+1];
        }
    }

    public static void noEmptyLines(int[][] pGrille) {
        for(int l=1 ; l<pGrille.length-2 ; l++) {
            pGrille[l][0] = pGrille[l][1];
            pGrille[l][pGrille[0].length-1] = pGrille[l][pGrille[0].length-2];
        }
        for(int c=0 ; c<pGrille[0].length ; c++) {
            pGrille[0][c] = pGrille[1][c];
            pGrille[pGrille.length-2][c] = pGrille[pGrille.length-3][c];
            pGrille[pGrille.length-1][c] = pGrille[pGrille.length-3][c];
        }
    }

    // ---- RESTRUCTURATION DE L'IMAGE A PARTIR DES DONNEES DES SEAMS ---- //

    public static int[][] resizeGrille(int[][] pGrille, boolean pResizeVertical, boolean pResizeHorizontal) {
        int[][] vGrille = new int[pGrille.length][pGrille[0].length];
        for(int a=0; a<aAllSeams.length;a++){ 
            aVerticalSeamTab=aAllSeams[a];
            boolean vPixelAfficheV = true;
            boolean vPixelAfficheH = true;
            if(pResizeVertical && pResizeHorizontal) {
                vGrille = new int[pGrille.length-1][pGrille[0].length-1];
            }else if(pResizeVertical) {
                vGrille = new int[pGrille.length][pGrille[0].length-1];
            }else if(pResizeHorizontal) {
                vGrille = new int[pGrille.length-1][pGrille[0].length];
            }
            int indiceV = -1;
            int indiceH = -1;
            if(pResizeVertical) {
                for (int i=0 ; i<vGrille.length ; i++){
                    for (int j=0 ; j<vGrille[0].length ; j++){
                        for (int k=0 ; k<aVerticalSeamTab.length ; k++) {
                            if(aVerticalSeamTab[k][0] == i && aVerticalSeamTab[k][1] == j) {
                                indiceV = k;
                                vPixelAfficheV=false;
                                break;
                            }
                            else {
                                indiceV = -1;
                            }
                        }
                        if( indiceV == -1 && vPixelAfficheV==true) {
                            vGrille[i][j] = pGrille[i][j];
                        }
                        else {
                            vGrille[i][j] = pGrille[i][j+1];
                        }
                    }
                    vPixelAfficheV=true;
                }
            }

            if(pResizeHorizontal) {
                for (int j=0 ; j<vGrille[0].length ; j++){
                    for (int i=0 ; i<vGrille.length ;i++){
                        for (int l=0 ; l<aHorizontalSeamTab.length ; l++) {
                            if(aHorizontalSeamTab[l][0] == i && aHorizontalSeamTab[l][1] == j) {
                                indiceH = l;
                                vPixelAfficheH=false;
                                break;
                            }
                            else {
                                indiceH = -1;
                            }
                        }
                        if( indiceH == -1 && vPixelAfficheH==true) {
                            vGrille[i][j] = pGrille[i][j];
                        }
                        else {
                            vGrille[i][j] = pGrille[i+1][j];
                        }
                    }
                    vPixelAfficheH=true;
                }
            }
        }
        return vGrille;
    }

    public static void traceSeam(){
        int[][] vGrilleOriginale = getColorTab(aOriginalImage);
        for(int i=0;i<aAllSeams.length;i++){
            for(int j=0;j<aAllSeams[i].length;j++){
                vGrilleOriginale[aAllSeams[i][j][0]][aAllSeams[i][j][1]]=-10485760;
            }
        }
        creerFichier(creerImage(vGrilleOriginale),"s"); 
    }



    // ---- ETAPE FINALE : CREATION DE L'IMAGE ET DU FICHIER ---- //
    
    public static BufferedImage creerImage(int[][] pGrille){
        BufferedImage vResizedImage = new BufferedImage(pGrille[0].length, pGrille.length, BufferedImage.TYPE_INT_RGB);
        for (int i=0;i<pGrille.length-1;i++){
            for (int j=0;j<pGrille[0].length;j++){
                vResizedImage.setRGB(j,i,pGrille[i][j]);
            }
        }
        return vResizedImage;
    }

    public static void creerFichier(BufferedImage pImage,String pOption){
        try{
            String vNomFichier="";
            String vExtension="";
            for(int i=0;i<aNomImage.length()-4;i++){
                vNomFichier+=aNomImage.charAt(i);
            }
            for(int j=aNomImage.length()-4;j<aNomImage.length();j++){
                vExtension+=aNomImage.charAt(j);
            }
            aNomResizedFinal="resized_"+vNomFichier+"_"+aPourcentageHorizontal+"_"+aPourcentageVertical+vExtension;
            aNomSeamedFinal="seamed_"+vNomFichier+"_"+aPourcentageHorizontal+"_"+aPourcentageVertical+vExtension;
            if(pOption=="r"){ 
                ImageIO.write(pImage, "PNG", new File("resized_images/"+aNomResizedFinal));
            }else if(pOption=="s"){
                ImageIO.write(pImage, "PNG", new File("resized_images/"+aNomSeamedFinal));
            }
        }
        catch (IOException e){
        }
    }
}