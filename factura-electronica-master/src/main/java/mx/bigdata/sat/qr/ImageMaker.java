package edu.ipn.codigoQR;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class ImageMaker {
	  
	public static BufferedImage createImage()
	  {
	    BufferedImage img = new BufferedImage(102, 102, 1);
	    img.createGraphics();
	    Graphics2D g = (Graphics2D)img.getGraphics();
	    g.fillRect(0, 0, 102, 102);

	    for (int i = 1; i < 49; i++) {
	      g.setColor(new Color(5 * i, 5 * i, 6 + i * 3));
	      g.fillRect(2 * i, 2 * i, 3 * i, 3);
	    }
	    return img;
	  }

	  public static String guardaImagen(BufferedImage img, String ruta)
	  {
	    try {
	      //BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ruta));
	      	      
          ImageIO.write(img, "jpg",new File("C:\\out.jpg"));
          ImageIO.write(img, "gif",new File("C:\\out.gif"));
          ImageIO.write(img, "png",new File("C:\\out.png"));

          
	      /*JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
	      JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
	      int quality = 5;
	      quality = Math.max(0, Math.min(quality, 100));
	      param.setQuality(quality / 100.0F, false);
	      encoder.setJPEGEncodeParam(param);
	      encoder.encode(img);*/
	      //out.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	    return ruta;
	  }
}
