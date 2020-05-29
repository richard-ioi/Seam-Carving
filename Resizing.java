// Richard FOUQUOIRE et Jérémy LAVEILLE - 29/05/2020
// ESIEE E2 groupe 10


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Resizing{
/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet, 
*/
	//###ATTRIBUTS###//
	public BufferedImage aImage;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
    	chargerImage("chaton.jpg");
    }

	//###AUTRES FONCTIONS##// 
	
	/*

	*/
    public static void chargerImage(String pFileName){
		try{
			BufferedImage aImage = ImageIO.read(new File(pFileName));
			//System.out.println(aImage);
			System.out.println("L'image est bien chargée");
		}
		catch (IOException e){
			System.out.println("L'image ne s'est pas chargée");
			//e.printStackTrace();
		}
    }
}