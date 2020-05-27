public class Coccinelle {

    //###ATTRIBUTS###

    public static final int aInfiniNeg = -Integer.MAX_VALUE; /* constante -infinie */
    public static final int aL=7+1;
    public static final int aC=4+1;
    public static int[][] M = new int[aL][aC];
    
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
            
        System.out.println("\nGrille representant les pucerons :");
        afficheTab(aGrille);
        calculerM(aGrille);
        System.out.println("\nTableau chemin");
        afficheTab(M);
        System.out.println();
        System.out.println("Le chemin est le suivant:");
        accm(aL,aC,aGrille);
        System.out.println();
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
        if (pC-1<0){
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
        if((pL-1>=0) && (pC+1<=aC)){
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
    }

}