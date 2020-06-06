// Richard FOUQUOIRE et Jérémy LAVEILLE - 30/05/2020
// ESIEE E2 groupe 10

import java.io.*;
import java.util.stream.IntStream;
import javax.imageio.*;
import java.awt.Color;
import java.awt.image.*;
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
    public static BufferedImage aOriginalImage = null;
    public static BufferedImage aEnergyImageVertical = null;
    public static BufferedImage aEnergyImageHorizontal = null;
    public static BufferedImage aResizedImage = null;
    public static int aPourcentageHorizontal;
    public static int aPourcentageVertical;
    public static String aNomImage;
    public static int aLargeurImage;
    public static int aHauteurImage;
    public static int aNewLargeurImage;
    public static int aNewHauteurImage;
    public static int[][] aGrille;
    public static int[][] aEnergyGrille;
    public static int[][] aCostTableVertical;
    public static int[][] aCostTableHorizontal;
    public static int aXmaxVertical;
    public static int aYmaxVertical;
    public static int aXmaxHorizontal;
    public static int aYmaxHorizontal;
    public static int aPlusFaibleCoutVertical;
    public static int[][] aVerticalSeamTab;
    public static int[][] aHorizontalSeamTab;
    public static int aOptimisation;
    public static boolean aHauteurPlusGrand = false;
    public static boolean aLargeurPlusGrand = false;
    public static float aPourcentageAvancement = 0;
    public static int aNbSeams;
    public static int[][][] aAllSeams;
    public static int aCompteurPourcentage=0;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
        int vCompteurWhile=0;
        boolean vResizeVertical;
        boolean vResizeHorizontal;
        Initialisation(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        System.out.println("\nNouvelle hauteur = "+aNewHauteurImage+"px, nouvelle largeur = "+aNewLargeurImage+"px\n");
        
        //aNewLargeurImage = aLargeurImage+1;
        //aNewHauteurImage = aHauteurImage+1;

        //aGrille = new int[aHauteurImage][aLargeurImage];
        
        while( (aLargeurImage > aNewLargeurImage) || (aHauteurImage > aNewHauteurImage) ) {
            aPourcentageAvancement = (aCompteurPourcentage*100)/aNbSeams;
            vResizeVertical=false;
            vResizeHorizontal=false;
            aGrille = getColorTab(aImage);
            if(aHauteurPlusGrand){
                if(aHauteurImage > aNewHauteurImage){
                    appliquerFiltreHorizontal(aImage);
                    calculCostTableHorizontal(aEnergyImageHorizontal,aLargeurImage,aHauteurImage);
                    calculSeamHorizontal();
                    aHauteurImage-=1;
                    vResizeHorizontal=true;
                    aCompteurPourcentage+=1;
                    aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
                }
                if((aLargeurImage > aNewLargeurImage)&&(vCompteurWhile%aOptimisation==0)){
                    appliquerFiltreVertical(aImage);
                    calculCostTableVertical(aEnergyImageVertical,aLargeurImage,aHauteurImage);
                    calculSeamVertical();
                    aLargeurImage-=1;
                    vResizeVertical=true;
                    aCompteurPourcentage+=1;
                    aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
                }
            }else if(aLargeurPlusGrand){
                if(aLargeurImage > aNewLargeurImage){
                    appliquerFiltreVertical(aImage);
                    calculCostTableVertical(aEnergyImageVertical,aLargeurImage,aHauteurImage);
                    calculSeamVertical();
                    aLargeurImage-=1;
                    vResizeVertical=true;
                    aCompteurPourcentage+=1;
                    aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
                }
                if((aHauteurImage > aNewHauteurImage)&&(vCompteurWhile%aOptimisation==0)){
                    appliquerFiltreHorizontal(aImage);
                    calculCostTableHorizontal(aEnergyImageHorizontal,aLargeurImage,aHauteurImage);
                    calculSeamHorizontal();
                    aHauteurImage-=1;
                    vResizeHorizontal=true;
                    aCompteurPourcentage+=1;
                    aImage = creerImage(resizeGrille(aGrille,vResizeVertical,vResizeHorizontal));
                }
            }

            /*System.out.println("\nCostTableHorizontal avant derniere ligne :");
            printTab(null , aCostTableHorizontal[aHauteurImage-1]);
            System.out.println("\nCostTableHorizontal derniere ligne :");
            printTab(null , aCostTableHorizontal[aHauteurImage]);*/
            /*System.out.println("\nCostTableVertical :");
            printTab(aCostTableVertical , null);
            System.out.println("\nCostTableHorizontal :");
            printTab(aCostTableHorizontal , null);*/
            System.out.println("Largeur actuelle : " + aLargeurImage + "px");
            System.out.println("Hauteur actuelle : " + aHauteurImage + "px");
            //System.out.println("vResizeVertical: "+vResizeVertical+" vResizeHorizontal: "+vResizeHorizontal);
            if(aLargeurImage>aNewLargeurImage || aHauteurImage>aNewHauteurImage){
                System.out.println(" => Pourcentage avancement :"+ aPourcentageAvancement+"%");
            }
            System.out.println("");
            vCompteurWhile+=1;
            /*aNomImage = "energie_verticale.png";
            creerFichier(aEnergyImageVertical);
            aNomImage = "energie_horizontale.png";
            creerFichier(aEnergyImageHorizontal);
            aNomImage = "chaton1.png";*/
        }
        System.out.println(" => Pourcentage avancement : 100%\n => Terminé !");
        creerFichier(creerImage(aGrille),"r");
        System.out.println();
        System.out.println("Traçage des seams en cours...");
        traceSeam();
        System.out.println("Traçage des seams terminé !");
    }

    //###ALGORITHMES DES IMAGES / TABLEAUX###//

    public static void traceSeam(){
        int[][] vGrilleOriginale = getColorTab(aOriginalImage);
        for(int i=0;i<aAllSeams.length;i++){
            for(int j=0;j<aAllSeams[i].length;j++){
                vGrilleOriginale[aAllSeams[i][j][0]][aAllSeams[i][j][1]]=-10485760;
            }
        }
        creerFichier(creerImage(vGrilleOriginale),"s"); 
    }

    public static int[][] resizeGrille(int[][] pGrille, boolean pResizeVertical, boolean pResizeHorizontal) {
        boolean vPixelAfficheV = true;
        boolean vPixelAfficheH = true;
        int doubleResize = 0;
        int[][] vGrille = new int[pGrille.length][pGrille[0].length];
        if(pResizeVertical && pResizeHorizontal) {
            vGrille = new int[pGrille.length-1][pGrille[0].length-1];
            doubleResize = -2;
        }else if(pResizeVertical) {
            vGrille = new int[pGrille.length][pGrille[0].length-1];
            doubleResize = 0;
        }else if(pResizeHorizontal) {
            vGrille = new int[pGrille.length-1][pGrille[0].length];
            doubleResize = 0;
        }
        int indiceV = -1;
        int indiceH = -1;
        //System.out.println("doubleResize = "+doubleResize);
        if(pResizeVertical) {
            //vGrille = new int[pGrille.length][pGrille[0].length-1];
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
                        //System.out.print("PIXEL - vertical |");
                    }
                    else {
                        //System.out.println("PIXEL ROUGE - vertical ["+i+","+j+"]");
                        //vGrille[i][j] = -1;
                        vGrille[i][j] = pGrille[i][j+1];
                        /*try{
                            vGrille[i][j] = pGrille[i][j+1];
                        }catch(java.lang.ArrayIndexOutOfBoundsException e ){
                            System.out.println("i:"+i+" j:"+j+" Taille vGrille:"+vGrille.length+","+vGrille[0].length+" Taille pGrille:"+pGrille.length+","+pGrille[0].length);
                        }*/
                    }
                }
                vPixelAfficheV=true;
            }
        }

        if(pResizeHorizontal) {
            //vGrille = new int[pGrille.length-1][pGrille[0].length];
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
                        //System.out.println("PIXEL ROUGE - horizontal");
                        //vGrille[i][j] = -10485760;
                        vGrille[i][j] = pGrille[i+1][j];
                    }
                }
                vPixelAfficheH=true;
            }
        }
        return vGrille;
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

    public static int e(int pL, int pC, int[][] pGrille){
        return pGrille[pL][pC+1];
    }

    public static int se(int pL, int pC, int[][] pGrille){
        if (pC+1>=aLargeurImage || pL-1<0){
            return aInfini;
        }else{
            return pGrille[pL-1][pC+1];
        }
    }

    //###CALCUL VERTICAL##//
    public static void calculCostTableVertical(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableVertical = new int[pHauteurImage][pLargeurImage];
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
            }
        }
    }

    static void calculSeamVertical(){
        aVerticalSeamTab = new int[aHauteurImage][2];
        plusFaibleCoutVertical();
        seamFinderVertical(aYmaxVertical,aXmaxVertical,-1);
        aAllSeams[aCompteurPourcentage]=aVerticalSeamTab;
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

    static void plusFaibleCoutVertical(){
       aXmaxVertical=0;
        aYmaxVertical=aHauteurImage-1;
        for (int i=0;i<=aLargeurImage-1;i++){
            if(aCostTableVertical[aYmaxVertical][i]<aCostTableVertical[aYmaxVertical][aXmaxVertical]){
                aXmaxVertical=i;
            }
        }
    }

    //###CALCUL HORIZONTAL###
    public static void calculCostTableHorizontal(BufferedImage pImage, int pLargeurImage, int pHauteurImage){
        aCostTableHorizontal = new int[pHauteurImage][pLargeurImage];
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

                if (c-1<0){
                    Me=aInfini;
                }else{
                    Me=aCostTableHorizontal[l][c-1]+e(l,c-1,aEnergyGrille);
                }

                if((l+1 >= pHauteurImage)||(c-1<0)) {
                    Mse = aInfini;
                }
                else {
                    Mse=aCostTableHorizontal[l+1][c-1]+se(l+1,c-1,aEnergyGrille);
                }

                if((l-1 < 0)||(c-1<0)) {   // (Avant chagement): if((c-1 < 0)||(l-1<0)) {
                    Mne = aInfini;
                }
                else {
                    Mne=aCostTableHorizontal[l-1][c-1]+ne(l-1,c-1,aEnergyGrille);
                }

                aCostTableHorizontal[l][c]= Math.min(Me, Math.min(Mse,Mne));
            }
        }
    }

    static void calculSeamHorizontal(){
        aHorizontalSeamTab = new int[aLargeurImage][2];
        plusFaibleCoutHorizontal();
        seamFinderHorizontal(aYmaxHorizontal,aXmaxHorizontal,-1);
        aAllSeams[aCompteurPourcentage]=aHorizontalSeamTab;
    }

    static void seamFinderHorizontal(int pL, int pC, int pCompteur){
        pCompteur+=1;
        
        int Mse=aInfini;
        int Me=aInfini;
        int Mne=aInfini;

        if((pC-1>=0)){  // || (pL-1<0)
            Me=aCostTableHorizontal[pL][pC-1]+e(pL,pC-1,aEnergyGrille);
        }
        if((pL-1>=0) && (pC-1>=0)){
            Mne=aCostTableHorizontal[pL-1][pC-1]+ne(pL-1,pC-1,aEnergyGrille);
        }
        if((pL+1<aHauteurImage) && (pC-1>=0)){
            Mse=aCostTableHorizontal[pL+1][pC-1]+se(pL+1,pC-1,aEnergyGrille);
        }

        if(aCostTableHorizontal[pL][pC] == Me){
            seamFinderHorizontal(pL,pC-1,pCompteur);
        } 
        else if (aCostTableHorizontal[pL][pC]==Mne){
            seamFinderHorizontal(pL-1,pC-1,pCompteur);
        }
        else if (aCostTableHorizontal[pL][pC]==Mse){
            seamFinderHorizontal(pL+1,pC-1,pCompteur);
        }
        /*System.out.print("pL:"+pL + "," + " pC:"+pC);
        System.out.println(" => "+aCostTableHorizontal[pL][pC] + " , Me:"+Me + " , Mne:"+Mne + " , Mse:"+Mse);
        if(pL==0 && pC==997) {
            System.out.println(" => "+aCostTableHorizontal[pL][pC] + " , Me:"+Me + " , Mne:"+Mne + " , Mse:"+Mse);
        }else {
            System.out.println("");
        }*/
        aHorizontalSeamTab[pCompteur] = new int[] {pL,pC};
    }

    static void plusFaibleCoutHorizontal(){
        aXmaxHorizontal=aLargeurImage-1;
        aYmaxHorizontal=0;
        for (int i=0;i<=aHauteurImage-1;i++){
            if(aCostTableHorizontal[i][aXmaxHorizontal]<aCostTableHorizontal[aYmaxHorizontal][aXmaxHorizontal]){
                aYmaxHorizontal=i;
            }
        }
        //System.out.println(aCostTableHorizontal[aYmaxHorizontal][aXmaxHorizontal]);
    }

     //###GESTION DES IMAGES / FICHIERS ###//

     public static void Initialisation (final String pNom, final int pPourcentageHorizontal, final int pPourcentageVertical){
        try{
            aNomImage=pNom;
            aPourcentageHorizontal = pPourcentageHorizontal;
            aPourcentageVertical = pPourcentageVertical;
            chargerImage("images/"+aNomImage);
            aOriginalImage=aImage;
            aNewHauteurImage = (int)(aHauteurImage-(aHauteurImage*(aPourcentageHorizontal)/100));
            aNewLargeurImage = (int)(aLargeurImage-(aLargeurImage*(aPourcentageVertical)/100));
            System.out.println("L'image doit être réduite de "+aPourcentageHorizontal+"% en hauteur et "+aPourcentageVertical+"% en largeur");
            if((aHauteurImage-aNewHauteurImage)>(aLargeurImage-aNewLargeurImage)){
                aHauteurPlusGrand = true;
                aOptimisation = (int)((aHauteurImage-aNewHauteurImage)/(aLargeurImage-aNewLargeurImage));
            } else if ((aHauteurImage-aNewHauteurImage)<=(aLargeurImage-aNewLargeurImage)){
                aLargeurPlusGrand = true;
                aOptimisation = (int)((aLargeurImage-aNewLargeurImage)/(aHauteurImage-aNewHauteurImage));
            }
            aNbSeams=(aLargeurImage-aNewLargeurImage)+(aHauteurImage-aNewHauteurImage);
            if(aLargeurPlusGrand){
                aAllSeams=new int[aNbSeams][aLargeurImage][2];
            }else{
                aAllSeams=new int[aNbSeams][aHauteurImage][2];
            }
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
            if(pOption=="r"){ 
                ImageIO.write(pImage, "PNG", new File("resized_images/resized_"+vNomFichier+"_"+aPourcentageHorizontal+"_"+aPourcentageVertical+vExtension));
            }else if(pOption=="s"){
                ImageIO.write(pImage, "PNG", new File("resized_images/seamed_"+vNomFichier+"_"+aPourcentageHorizontal+"_"+aPourcentageVertical+vExtension));
            }
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

    public static void printTab(int[][] pTab2D , int[] pTab){
        if(pTab2D != null) {
            for (int i=0;i<pTab2D.length-1;i++){
                for (int j=0;j<pTab2D[0].length;j++){
                    System.out.print(pTab2D[i][j]+" ");
                }
                System.out.println(" ");
            }
        }
        if(pTab != null) {
            for (int k=0;k<pTab.length;k++){
                System.out.print("["+k+"]"+pTab[k]+" ");
            }
            System.out.println(" ");
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

    public static void appliquerFiltreVertical(BufferedImage pImage) {
        /* Définition de la convolution du filtre vertical */

        //Vertical : 
        Kernel kernel = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
        ConvolveOp convolution = new ConvolveOp(kernel);
        aEnergyImageVertical = convolution.filter(pImage, null);

    }

    public static void appliquerFiltreHorizontal(BufferedImage pImage) {
        /* Définition de la convolution du filtre horizontal */

        //Horizontal : 
        Kernel kernel = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
        ConvolveOp convolution = new ConvolveOp(kernel);
        aEnergyImageHorizontal = convolution.filter(pImage, null);

        //FILTRE LAPLACIEN
        /*Kernel kernel = new Kernel(3, 3, new float[]{0f,1f,0f, 1f,-4f,1f, 0f,1f,0f});
        ConvolveOp convolution = new ConvolveOp(kernel);*/
    }
}