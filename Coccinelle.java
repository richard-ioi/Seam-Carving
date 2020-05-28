//Richard FOUQUOIRE et Jérémy LAVEILLE - 28/05/2020
// ESIEE E2 groupe 10

public class Coccinelle {
/*
Cet algorithme correspond à l'exercice n°1 du projet de l'unité d'algorithmique du 2e semestre.
Il permet, à partir d'une grille de valeurs entières, de déterminer le meilleur chemin à emprumter pour atteindre la plus haute valeur.
Dans notre cas cela correspond à une coccinelle qui doit manger un nombre maximum de pucerons, répartis sur la grille, en suivant ce chemin.
Le programme utilise différentes fonctions dans le but :
    -> D'afficher la grille de départ,
    -> De calculer puis d'afficher une nouvelle grille correspondant, sur chaque case, au nb maximal de pucerons mangés jusqu'ici,
    -> D'indiquer le nombre maximal de pucerons qu'a mangé la coccinelle à la fin de son parcourt,
    -> Et enfin d'afficher le chemin qu'a suivit la coccinelle pour manger ce nb maximal de pucerons, ainsi que les cases de départ et d'arrivée.
*/

    //###ATTRIBUTS###

    public static final int aInfiniNeg = -Integer.MAX_VALUE; // Constante -l'infini qui nous sert plus tard, pour que la coccinelle ne sorte pas de la grille
    public static final int aL=7+1; // Nombre de lignes de la grille
    public static final int aC=4+1; // Nombre de colonnes de la grille
    public static int[][] M = new int[aL][aC]; // Tableau de terme général M[l][c] = m(l,c), nombre maximum de pucerons.
    public static int aLplusGrand=0; // Coordonnée L du tableau correspondant au terme m(l,c).
    public static int aCplusGrand=0; // Coordonnée C du tableau correspondant au terme m(l,c).
    public static int aPlusGrandNombre=0; // Nombre maximum de  pucerons mangés
    public static int aLAterrissage=0; // Coordonnée L de la case atterrissage
    public static int aCAterrissage=0; // Coordonnée C de la case atterissage
    public static int aLInterview=0; // Coordonnée L de la case interview
    public static int aCInterview=0; // Coordonnée C de la case interview
    
    // ###FONCTION PRINCIPALE###
    public static void main(String[] args){
        int[][] aGrille = { {2,4,3,9,6}, // Grille de pucerons
                            {1,10,15,1,2},
                            {2,4,11,26,66},
                            {36,34,1,13,30},
                            {46,2,8,7,15},
                            {89,27,10,12,3},
                            {1,72,3,6,6},
                            {3,1,2,4,5} };
            
        
        System.out.println("\nGrille représentant les pucerons au départ :");
        afficheTab(aGrille); // Affiche la grille dans le bon ordre
        calculerM(aGrille); // Calcule le tableau M de terme général m(l,c), nombre maximum de pucerons de aGrille.
        System.out.println("\nTableau M[L][C] de terme général M[l][c] = m(l,c) représentant le nb max de pucerons mangés :");
        afficheTab(M); // Affiche le tableau M dans le bon ordre
        plusGrandNombrePuceron(); // Détermine la case sur laquelle on atteint le plus grand nombre de pucerons dévorés.
        System.out.println("\nLa coccinelle a mangé "+aPlusGrandNombre+" pucerons");
        System.out.println("Le chemin suivit par la coccinelle est le suivant :");
        accm(aLplusGrand,aCplusGrand,aGrille); // Détermine le chemin que la coccinelle emprunte pour manger le plus grand nombre de pucerons
                                               // En mettant en paramètre les coordonnées de la case sur laquelle on atteint le plus grand nombre de pucerons dévorés
        System.out.println("\nCase d'atterrissage = ("+aLAterrissage+","+aCAterrissage+").");
        System.out.println("Case de l'interview = ("+aLInterview+","+aCInterview+").");
        System.out.println("");
    }

    //###AUTRES FONCTIONS###
    //

    /*
     Fonction qui affiche le tableau dans le bon ordre (Les lignes montent de bas en haut au lieu de descendre du haut vers le bas)
    */
    public static void afficheTab(int[][] tab) {
        for(int i=tab.length-1 ; i>=0 ; i--) {
            for(int j=0 ; j<tab[0].length ; j++) {
                if(tab[i][j] < 100)
                    System.out.print(" ");
                if(tab[i][j] < 10)
                    System.out.print(" ");
                System.out.print(" "+tab[i][j]);
            }
            System.out.println("");
        }
    }

    /*
     Fonction qui calcule le coût des déplacements Nord-Ouest
    */
    public static int no(int pL, int pC, int[][] pGrille){
        if (pC-1<0){
            return aInfiniNeg;
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
        if (pC+1>=aC){
            return aInfiniNeg;
        }else{
            return pGrille[pL+1][pC+1];
        }
    }

    /*
    Fonction qui calcule le nombre maximum de pucerons atteints pour chaque case
    */
    public static void calculerM(int[][] pGrille){
        for (int i=0; i<aC;i++){
            M[0][i]=pGrille[0][i];
        }

        int Mno=0;
        int Mn=0;
        int Mne=0;

        for (int l=1;l<aL;l++){
            for (int c=0;c<aC;c++){

                if(c+1>=aC) {
                    Mno = aInfiniNeg;
                }
                else {
                    Mno=M[l-1][c+1]+no(l-1,c+1,pGrille);
                }

                Mn=M[l-1][c]+n(l-1,c,pGrille);

                if(c-1 < 0) {
                    Mne = aInfiniNeg;
                }
                else {
                    Mne=M[l-1][c-1]+ne(l-1,c-1,pGrille);
                }

                M[l][c]= (int)Math.max(Mno, (int)Math.max(Mn,Mne));
            }
        }
    }

    /*
    Fonction qui détermine le plus grand nombre de pucerons consommés possible et 
    les coordonnées de la case correspondante
    */
    static void plusGrandNombrePuceron(){
        for (int l=0; l<aL;l++){
            for (int c=0; c<aC;c++){
                if (M[l][c]>aPlusGrandNombre){
                    aPlusGrandNombre=M[l][c];
                    aLplusGrand=l;
                    aCplusGrand=c;
                }
            }
        }
    }

    /*
    Fonction qui détermine le chemin parcouru par la coccinelle pour atteindre le plus grand
    nombre de pucerons dévorés en mettant en paramètre les coordonnées de la case correspondant
    au plus grand nombre de puces dévorées possible (grâce à la fonction plusGrandNombrePuceron()).
    */
    static void accm(int pL, int pC, int[][]pGrille){
        if ((pL==0) && (pC==0)){
            System.out.print("(0,0)");
            return;
        }
        int Mno=aInfiniNeg;
        int Mn=aInfiniNeg;
        int Mne=aInfiniNeg;

        if((pL-1>=0)){
            Mn=M[pL-1][pC]+n(pL-1,pC,pGrille);
        }
        if((pL-1>=0) && (pC-1>=0)){
            Mne=M[pL-1][pC-1]+ne(pL-1,pC-1,pGrille);
        }
        if((pL-1>=0) && (pC+1<aC)){
            Mno=M[pL-1][pC+1]+no(pL-1,pC+1,pGrille);
        }

        if(M[pL][pC] == Mn){
            accm(pL-1,pC,pGrille);
        } 
        else if (M[pL][pC]==Mne){
            accm(pL-1,pC-1,pGrille);
        }
        else if (M[pL][pC]==Mno){
            accm(pL-1,pC+1,pGrille);
        }
        System.out.printf("(%d,%d)",pL,pC);

        if (pL==aL-1){
            aLInterview=pL;
            aCInterview=pC;
        } else if (pL==0){
            aLAterrissage=pL;
            aCAterrissage=pC;
        }
    }
}