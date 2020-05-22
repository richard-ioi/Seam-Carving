public class Coccinelle {

    //###Attributs##
    int aTailleColonne=0;
    int aTailleLigne=0;
    int[][] aTab=null;

    public static Tableau(final int pTailleColonne, final int pTailleLigne){
        aTailleColonne=pTailleColonne+1;
        aTailleLigne=pTailleLigne+1;
        int[][] aTab=new int[pTailleLigne][pTailleColonne];
    }

    public static void afficheTab() {
        for(int j=aTailleColonne-1 ; j>=0 ; j--) {
            for(int i=0 ; i<aTailleLigne ; i++) {
                System.out.print(" "+aTab[i][j]);
            }
            System.out.println("");
        }
    }

    Tableau aGrille = new Tableau(7,4);
        aGrille.aTab={{2,4,3,9,6},
                    {1,10,15,1,2},
                    {2,4,11,26,66},
                    {36,34,1,13,30},
                    {46,2,8,7,15},
                    {89,27,10,12,3},
                    {1,72,3,6,6},
                    {3,1,2,4,5}};

    public static void main(String[] args){
        aGrille.afficheTab();
    }
}