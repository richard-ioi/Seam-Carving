public class Coccinelle {

    //###ATTRIBUTS###

    static final int aInfiniNeg = -Integer.MAX_VALUE; /* constante -infinie */
    static final int aL=7;
    static final int aC=4;
    static int[][] M = new int[aL][aC];

    
    // ###FONCTION PRINCIPALE###
    public static void main(String[] args){
        int[][] aGrille = { {2,1,2,36,46,89,1,3},
                            {4,10,4,34,2,27,72,1},
                            {3,15,11,1,8,10,3,2},
                            {9,1,26,13,7,12,6,4},
                            {6,2,66,30,15,3,6,5} };
        System.out.println("\nGrille representant les pucerons :");
        afficheTab(aGrille);
        /*
        int[][] aGrilleChemin = calculChemin(aGrille);
        System.out.println("\nTableau M[L][C] de terme general M[l][c] = m(l,c) representant le chemin de la coccinelle :");
        afficheTab(aGrilleChemin);

        int aMaxPucerons = max(aGrilleChemin);
        System.out.println("La coccinelle a mange "+aMaxPucerons+" Pucerons");
        */
    }

    //###AUTRES FONCTIONS###

    public static void afficheTab(int[][] tab) {
        for(int j=tab[0].length-1 ; j>=0 ; j--) {
            for(int i=0 ; i<tab.length ; i++) {
                if(tab[i][j] < 100)
                    System.out.print(" ");
                if(tab[i][j] < 10)
                    System.out.print(" ");
                System.out.print(" "+tab[i][j]);
            }
            System.out.println("");
        }
    }

    //Coûts des déplacements Nord-Ouest
    public static int no(int pL, int pC){
        if (pC-1<0){
            return aInfiniNeg;
        }else{
            return aGrille[pL+1][pC-1];
        }
    }

    //Coûts des déplacements Nord
    public static int n(int pL, int pC){
        return aGrille[pL+1][pC];
    }

    //Coût des déplacements Nord-Est
    public static int ne(int pL, int pC){
        if (pC+1>aC){
            return aInfiniNeg;
        }else{
            return aGrille[pL+1][pC+1];
        }
    }

    static void calculerM(){
        for (int i=0; i<aC;i++){
            M[0][i]=aGrille[i][0];
        }

        for (int l=0;l<aL;l++){
            for (int c=0;c<aC;c++){
                int Mno=M[l-1][c+1]+no(l-1,c+1);
                int Mn=M[l-1][c]+n(l-1,c);
                int Mne=M[l-1][c-1]+ne(l-1,c-1);
                M[l][c]= (int)Math.max(Mno, (int)Math.max(Mn,Mne));
            }
        }

    }

    public static int[][] calculChemin(int[][] grille) {
        int[][] nouvelleGrille = new int[grille.length][grille[0].length];
        
        return nouvelleGrille;
    }
}