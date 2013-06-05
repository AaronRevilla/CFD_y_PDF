package mx.bigdata.sat.Pdf;

import java.io.ByteArrayOutputStream;

public interface PdfBuilder {
    public ByteArrayOutputStream createPdf(Object object) throws Exception;
}
