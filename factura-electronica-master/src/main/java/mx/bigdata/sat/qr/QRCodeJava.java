package mx.bigdata.sat.qr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
 
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class QRCodeJava {
    
    public static ByteArrayOutputStream getQrImage(String texto, int ancho, int alto){
        return QRCode.from(texto).to(ImageType.PNG).withSize(ancho, alto) .stream();
    }
    
    public static void main(String[] args) {
        ByteArrayOutputStream out = QRCode.from("OSCAR AARON REVILLA ESCALONA QR TEST").to(ImageType.PNG).withSize(500, 500) .stream();
        
        /*// override the image type to be JPG
        QRCode.from("Hello World").to(ImageType.JPG).file();
        QRCode.from("Hello World").to(ImageType.JPG).stream();
          
        // override image size to be 250x250
        QRCode.from("Hello World").withSize(250, 250).file();
        QRCode.from("Hello World").withSize(250, 250).stream();
          
        // override size and image type
        QRCode.from("Hello World").to(ImageType.GIF).withSize(250, 250).file();
        QRCode.from("Hello World").to(ImageType.GIF).withSize(250, 250).stream();*/
 
        try {
            FileOutputStream fout = new FileOutputStream(new File("C:\\OARE.PNG"));
 
            fout.write(out.toByteArray());
 
            fout.flush();
            fout.close();
 
        } catch (FileNotFoundException e) {
            // Do Logging
        	e.printStackTrace();
        } catch (IOException e) {
            // Do Logging
        	e.printStackTrace();
        }
    }
}
