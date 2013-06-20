package edu.ipn.codigoQR;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import java.io.File;
import java.io.FileOutputStream;

public class CodigoBidimensional {
	  private static final Integer WIDTH_IMAGE = Integer.valueOf(102);
	  private static final Integer HEIGHT_IMAGE = Integer.valueOf(102);
	  private static final String imageFormat = "png";

	  public void generaCodigoBidimensional(CodigoBidimensionalVO data)
	  {
	    String imageName = ImageMaker.guardaImagen(ImageMaker.createImage(), data.getRutaFinalImagen());

	    if (imageName != null)
	      try
	      {
	        BitMatrix bitMatrix = new QRCodeWriter().encode(data.getInformacion(), BarcodeFormat.QR_CODE, WIDTH_IMAGE.intValue(), HEIGHT_IMAGE.intValue());

	        MatrixToImageWriter.writeToStream(bitMatrix, "png", new FileOutputStream(new File(imageName)));
	      }
	      catch (WriterException e) {
	        e.printStackTrace();
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	  }

	  public static void main(String[] args)
	  {
	    CodigoBidimensional cb = new CodigoBidimensional();
	    CodigoBidimensionalVO data = new CodigoBidimensionalVO();
	    //data.setRutaFinalImagen("D:\\codigoQR.png");
	    data.setInformacion("?re=informacion&mas=informacion");
	    cb.generaCodigoBidimensional(data);
	  }

}
