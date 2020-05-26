public class Coccinelle {

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

    public static void main(String[] args){
        int[][] aGrille = { {2,1,2,36,46,89,1,3},
                            {4,10,4,34,2,27,72,1},
                            {3,15,11,1,8,10,3,2},
                            {9,1,26,13,7,12,6,4},
                            {6,2,66,30,15,3,6,5} };

        afficheTab(aGrille);
    }
}