public class Coccinelle {


    public void afficheTab() {
        for(int j=this.aTailleColonne-1 ; j>=0 ; j--) {
            for(int i=0 ; i<this.aTailleLigne ; i++) {
                System.out.print(" "+this.aTab[i][j]);
            }
            System.out.println("");
        }
    }

}