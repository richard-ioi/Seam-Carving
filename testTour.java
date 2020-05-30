import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Sobel {

 public static void main(String[] args) {
  BufferedImage source=null;
  try {
   source = ImageIO.read(new File("tour.png"));
  }
  catch (IOException e) {
   e.printStackTrace();
  }
  
  /* Définition de la première convolution */
  Kernel kernel1 = new Kernel(3, 3, new float[]{1f, 0f, -1f, 2f, 0f, -2f, 1f, 0f, -1f});
  ConvolveOp convolution1 = new ConvolveOp(kernel1);
  BufferedImage resultatIntermediaire = convolution1.filter(source, null);
  
  /* Définition de la deuxième convolution */
  Kernel kernel2 = new Kernel(3, 3, new float[]{1f, 2f, 1f, 0f, 0f, 0f, -1f, -2f, -1f});
  ConvolveOp convolution2 = new ConvolveOp(kernel2);
  BufferedImage resultat = convolution2.filter(resultatIntermediaire, null);
  
  /* Ecriture du résultat */
  try {
   ImageIO.write(resultat, "PNG", new File("resized_tour.png"));
  } 
  catch (IOException e) {
   e.printStackTrace();
  }
 }

}