public class Coccinelle {

    //###ATTRIBUTS###

    public static final int aInfiniNeg = -Integer.MAX_VALUE; /* constante -infinie */
    public static final int aL=7+1;
    public static final int aC=4+1;
    public static int[][] M = new int[aL][aC];
    //public static int[][] aGrille = new int[aL][aC];
    
    // ###FONCTION PRINCIPALE###
    public static void main(String[] args){
        int[][] aGrille = { {2,4,3,9,6},
                            {1,10,15,1,2},
                            {2,4,11,26,66},
                            {36,34,1,13,30},
                            {46,2,8,7,15},
                            {89,27,10,12,3},
                            {1,72,3,6,6},
                            {3,1,2,4,5} };
        
        //aGrille=aGrille2;
            
        System.out.println("\nGrille representant les pucerons :");
        afficheTab(aGrille);
        calculerM(aGrille);
        afficheTab(M);
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

    //Coûts des déplacements Nord-Ouest
    public static int no(int pL, int pC, int[][] pGrille){
        if (pC-1<=0){
            return aInfiniNeg;
        }else{
            return pGrille[pL+1][pC-1];
        }
    }

    //Coûts des déplacements Nord
    public static int n(int pL, int pC, int[][] pGrille){
        return pGrille[pL+1][pC];
    }

    //Coût des déplacements Nord-Est
    public static int ne(int pL, int pC, int[][] pGrille){
        if (pC+1>=aC){
            return aInfiniNeg;
        }else{
            return pGrille[pL+1][pC+1];
        }
    }

    public static void calculerM(int[][] pGrille){
        for (int i=0; i<aC;i++){
            M[0][i]=pGrille[i][0];
        }

        for (int l=1;l<aL;l++){
            for (int c=0;c<aC;c++){
                int Mno=0;
                int Mn=0;
                int Mne=0;
                
                if(c+1>aC) {
                    Mno = aInfiniNeg;
                }
                else {
                    Mno=M[l-1][c+1]+no(l-1,c+1,pGrille);
                }

                Mn=M[l-1][c]+n(l-1,c,pGrille);
                }

                if(c-1 < 0) { Mne = aInfiniNeg; }
                else {
                    Mne=M[l-1][c-1]+ne(l-1,c-1,pGrille);
                }

                M[l][c]= (int)Math.max(Mno, (int)Math.max(Mn,Mne));
            }
        }
    }

}