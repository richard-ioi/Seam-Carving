// Richard FOUQUOIRE et Jérémy LAVEILLE - 29/05/2020
// ESIEE E2 groupe 10


import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;

public class Resizing extends Applet{
/*
Cet algorithme correspond à l'exercice n°2 du projet de l'unité d'algorithmique du 2e semestre.
Il permet, 
*/
	//###ATTRIBUTS###//
	public static BufferedImage aImage = null;

    //###FONCTION PRINCIPALE###//
    public static void main(String[] args){
    	chargerImage("Images/chaton.jpg");
    }

	//###AUTRES FONCTIONS##// 
	
	/*

	*/
    public void chargerImage(String pFileName){
		try{
			URL url = new URL(getCodeBase(), "chaton.jpg");
			aImage = ImageIO.read(new File(url));
			//aImage = ImageIO.read(new File("chaton.jpg"));
			//System.out.println(aImage);
			//System.out.println("L'image est bien chargée");
		}
		catch (IOException e){
			System.out.println("L'image ne s'est pas chargée");
			//e.printStackTrace();
		}
    }
}