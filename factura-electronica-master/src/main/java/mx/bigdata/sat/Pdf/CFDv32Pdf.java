package mx.bigdata.sat.Pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.xml.transform.stream.StreamSource;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Conceptos.Concepto;
import mx.bigdata.sat.cfdi.v32.schema.Comprobante.Impuestos.Traslados.Traslado;
import mx.bigdata.sat.qr.QRCodeJava;
import mx.bigdata.sat.utils.Numero_a_Letra;

public class CFDv32Pdf extends PdfPageEventHelper implements PdfBuilder {

	private static final Font[] font = new Font[16];//Letras
	private static float[] encabezadoDimension = {0.15f, 0.70f, 0.15f};//Encabezado
	private static float[] cuerpoDimension = {0.075f, 0.475f, 0.075f, 0.075f, 0.15f, 0.15f};//Cuerpo
	private static float[] pieDimension = {0.225f, 0.475f, 0.3f};//Pie
	private static float[] proporcion = {500f, 380f};
	private static float[] margenes = {15f, 15f};
	private static final int[] cellBorderType = {0, 1, 2, 4, 6};
	private static Comprobante datos;
        private static String cadenaOriginal;
	//private static Resources recursos;
        Properties recursos;  
	
	public CFDv32Pdf() throws IOException {
		//Se inicializa las fuentes a utilizar
        font[0] = new Font(FontFactory.getFont("BOLD","",15));
        font[1] = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        font[1].setColor(new BaseColor(0x9f, 0x00, 0x00));
        //font[2] = new Font(Font.STRIKETHRU, 11, Font.ITALIC);
        font[2] = new Font(FontFactory.getFont("ITALIC","",11));
        font[3] = new Font(FontFactory.getFont("BOLD","",11));
        font[4] = new Font(FontFactory.getFont("BOLD","",9));
        font[5] = new Font(FontFactory.getFont("BOLD","",8));
        font[6] = new Font(FontFactory.getFont("BOLD","",7));
        font[7] = new Font(FontFactory.getFont("BOLD","",5));
        font[8] = new Font(FontFactory.getFont("",5));
        font[9] = new Font(FontFactory.getFont("",6));
        font[10] = new Font(FontFactory.getFont("BOLD","",6));
        font[11] = new Font(FontFactory.getFont("",3));
        font[12] = new Font(FontFactory.getFont("",4));
        font[13] = new Font(FontFamily.TIMES_ROMAN, 13, Font.ITALIC);
        font[14] = new Font(FontFactory.getFont("BOLD","",16));
        font[15] = new Font(FontFamily.UNDEFINED, 8, Font.BOLD/* | Font.UNDERLINE*/);
	//recursos = new Resources();
        recursos = new Properties(); 
        recursos.load(new StreamSource(getClass().getResourceAsStream("/config.properties")).getInputStream());
	}
        
        public CFDv32Pdf(String cadenaOriginal) throws IOException {
        //Se inicializa las fuentes a utilizar
        font[0] = new Font(FontFactory.getFont("BOLD","",15));
        font[1] = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        font[1].setColor(new BaseColor(0x9f, 0x00, 0x00));
        //font[2] = new Font(Font.STRIKETHRU, 11, Font.ITALIC);
        font[2] = new Font(FontFactory.getFont("ITALIC","",11));
        font[3] = new Font(FontFactory.getFont("BOLD","",11));
        font[4] = new Font(FontFactory.getFont("BOLD","",9));
        font[5] = new Font(FontFactory.getFont("BOLD","",8));
        font[6] = new Font(FontFactory.getFont("BOLD","",7));
        font[7] = new Font(FontFactory.getFont("BOLD","",5));
        font[8] = new Font(FontFactory.getFont("",5));
        font[9] = new Font(FontFactory.getFont("",6));
        font[10] = new Font(FontFactory.getFont("BOLD","",6));
        font[11] = new Font(FontFactory.getFont("",3));
        font[12] = new Font(FontFactory.getFont("",4));
        font[13] = new Font(FontFamily.TIMES_ROMAN, 13, Font.ITALIC);
        font[14] = new Font(FontFactory.getFont("BOLD","",16));
        font[15] = new Font(FontFamily.UNDEFINED, 8, Font.BOLD/* | Font.UNDERLINE*/);
	//recursos = new Resources();
        recursos = new Properties(); 
        recursos.load(new StreamSource(getClass().getResourceAsStream("/config.properties")).getInputStream());
        this.cadenaOriginal = cadenaOriginal;
	}

 	public ByteArrayOutputStream createPdf(Object object) throws DocumentException, IOException {
		ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
		Document document = new Document(PageSize.LETTER, margenes[0], margenes[1], 5f, 25f);
		PdfWriter writer;
		
			datos = (Comprobante) object;
                        writer = PdfWriter.getInstance(document, pdfBytes);
			document.open();
			document.addTitle("COMPROBANTE FISCAL DIGITAL POR INTERNET");
			document.addCreator("EDUCAL");
			document.addAuthor("CONACULTA");
			document.add(this.cuerpo());
			document.add(this.pie());
			document.close();
			pdfBytes = this.setPageNumbers(pdfBytes, document.right() - 43f , document.bottom() - 7.5f); //Incrusta el numero de pagina
        
			writer.close();
			pdfBytes.close();
			document.close();
			return pdfBytes;
		
	}

	private PdfPTable encabezado() throws DocumentException, MalformedURLException, IOException {
		PdfPTable tablaN1, tablaN2, tablaN3, tablaN1a, tablaDatosCFD, tablaMaestra;
		PdfPCell celdaN1, celdaN2, celdaN3;
		Paragraph parrafo;
		Image img;
		float[] widths = {2.5f, 10.3f, 1.3f, 3.4f};
		float[] domicilios = {1.5f,0f,1f};
		RoundRectangle round = new RoundRectangle();
		
		tablaMaestra = new PdfPTable(1);
		tablaMaestra.setWidthPercentage(101);
		tablaMaestra.setSplitLate(false);
		tablaMaestra.getDefaultCell().setBorder(0);
		
		tablaN1 = new PdfPTable(3);
		tablaN1.setWidthPercentage(100);
		tablaN1.setSplitLate(false);
		tablaN1.setWidths(encabezadoDimension);
		tablaN1.getDefaultCell().setBorder(0);
		
		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		// PRIMERA CELDA DE LA TABLA MAESTRA
		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				// Nivel 1 - Celda Logotipo CONACULTA esquina superior izquierda
				celdaN1 = new PdfPCell();
				//celdaN1.setMinimumHeight(proporcion[0]);
				celdaN1.setBorder(0);
				celdaN1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				celdaN1.setVerticalAlignment(Element.ALIGN_TOP);
				
				// Se carga la imagen del logo
				img = Image.getInstance(recursos.getProperty("url.img.logo.educal"));
				//img.scalePercent(65);
				img.scaleToFit(100, 100);
				img.setGrayFill(1f);
				img.setAlignment(Image.LEFT);
				celdaN1.addElement(new Paragraph(" ", font[7]));
				//celdaN1.addElement(new Paragraph(" ", font[0]));
				//celdaN1.addElement(new Paragraph(" ", font[10]));
				celdaN1.addElement(img);
				tablaN1.addCell(celdaN1);
		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		// SEGUNDA CELDA DE LA TABLA MAESTRA
		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				// Nivel 1 - Celda Central Superio del Encabezado de la pagina
				celdaN1 = new PdfPCell();
				celdaN1.setBorder(cellBorderType[0]);
				celdaN1.setVerticalAlignment(Element.ALIGN_TOP);
				
				// Nivel 1 - Se agrega parrafo
				parrafo = new Paragraph("EDUCAL, S.A. DE C.V.", font[14]);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celdaN1.addElement(parrafo);       
				// Nivel 1 - Se agrega parrafo
				parrafo = new Paragraph("\"Librer\u00edas con arte\"", font[13]);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celdaN1.addElement(parrafo);
				// Nivel 1 - Se agraga parrafo
				parrafo = new Paragraph("RFC EDU820217-8I3, R\u00e9gimen Fiscal: Persona moral del r\u00e9gimen general", font[7]);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celdaN1.addElement(parrafo); 
				// 	Nivel 1 - Se agraga parrafo
				parrafo = new Paragraph("Av.Ceyl\u00e1n No.450, Col.Euzkadl C.P.02660 M\u00e9xico, D.F., Tels.5554-4000, Fax.53544000 Ext.4302",font[7]);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celdaN1.addElement(parrafo);
				// Nivel 1 - Se agraga parrafo
				parrafo = new Paragraph("e-mail:ventasinternet@educal.com.mx", font[7]);
				parrafo.setAlignment(Element.ALIGN_CENTER);
				celdaN1.addElement(parrafo); 
				tablaN1.addCell(celdaN1);
                    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    //TERCERA CELDA DE LA TABLA MAESTRA
                    //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				// Nivel 1 - Celda Logotipo EDUCAL esquina superior izquierda
				celdaN1 = new PdfPCell();
				celdaN1.setBorder(0);
				celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN1.setVerticalAlignment(Element.ALIGN_TOP);
				//celdaN1.setMinimumHeight(proporcion[0]);
				// 	Se carga la imagen del logo
				img = Image.getInstance(recursos.getProperty("url.img.logo.conaculta"));
				//img.scalePercent(70);
				img.scaleToFit(100, 100);
				img.setGrayFill(1f);
				img.setAlignment(Image.RIGHT);
				celdaN1.addElement(new Paragraph(" ", font[7]));
				celdaN1.addElement(img);
				tablaN1.addCell(celdaN1);
				
				tablaMaestra.addCell(tablaN1);
    		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // FIN TABLA HEADER 1
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    		// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // COMIENZO TABLA HEADER 2
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				tablaN2 = new PdfPTable(2);
				tablaN2.getDefaultCell().setBorder(0);
				tablaN2.setWidthPercentage(100);
				tablaN2.setSplitLate(false);
				float[] anchos = {0.65f ,0.35f};
				tablaN2.setWidths(anchos);
			// XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	        // TABLA DATOS SUCRSAL Y CLIENTE
	        // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				tablaN3 = new PdfPTable(1);
				tablaN3.setWidthPercentage(100);
				tablaN3.getDefaultCell().setBorder(0);
				//tablaN2.setWidths(widths);
                    
				tablaN1 = new PdfPTable(2);
				float[] porcentajesAuxiliares = { 30f , 70f};
				tablaN1.setWidths(porcentajesAuxiliares);
				tablaN1.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				
				celdaN1 = new PdfPCell(new Paragraph("DATOS DE LA SUCURSAL",font[15]));
				celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN1.setBorder(cellBorderType[2]);
				tablaN1.addCell(celdaN1);
				celdaN1 = new PdfPCell(new Paragraph("\t ",font[15]));
				celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN1.setBorder(cellBorderType[0]);
				tablaN1.addCell(celdaN1);
				tablaN3.addCell(tablaN1);
				
				/*celdaN2 = new PdfPCell(tablaN1);
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(2);
				//celdaN2.addElement(tablaN1);
				tablaN1a.addCell(celdaN2);
				
				celdaN2 = new PdfPCell(new Paragraph(" ",font[5]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(2);
				tablaN1a.addCell(celdaN2);*/
				
				tablaN1a = new PdfPTable(4);
				tablaN1a.setWidthPercentage(100);
				tablaN1a.setWidths(widths);
				           
				celdaN2 = new PdfPCell(new Paragraph("DOMICILIO: ", font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				tablaN1a.addCell(celdaN2);
                                // 	Nivel 2 - Celda5//////
				if (datos.getEmisor().getDomicilioFiscal().toString().length() > 0) {
					celdaN2 = new PdfPCell(new Paragraph(datos.getEmisor().getDomicilioFiscal().toString(), font[9]));
					celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
					celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
					celdaN2.setBorder(cellBorderType[0]);
					celdaN2.setColspan(3);
					tablaN1a.addCell(celdaN2);
				}
				
				// Nivel 2
				if (datos.getEmisor().getDomicilioFiscal().toString().length() != 0) {
					celdaN2 = new PdfPCell(new Paragraph("  ", font[7]));
					celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
					celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
					celdaN2.setBorder(cellBorderType[0]);
					tablaN1a.addCell(celdaN2);
				}
				// Nivel 2 - Celda6/////
				celdaN2 = new PdfPCell(new Paragraph(/*datos.getDomicilioRC()*/ " ", font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(3);
				tablaN1a.addCell(celdaN2);
				
				tablaN3.addCell(tablaN1a);
				
				tablaN1 = new PdfPTable(2);
				tablaN1.setWidths(porcentajesAuxiliares);
				tablaN1.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				
				celdaN1 = new PdfPCell(new Paragraph("DATOS DEL CLIENTE",font[15]));
				celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN1.setBorder(cellBorderType[2]);
				tablaN1.addCell(celdaN1);
				celdaN1 = new PdfPCell(new Paragraph("\t ",font[15]));
				celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN1.setBorder(cellBorderType[0]);
				tablaN1.addCell(celdaN1);
				tablaN3.addCell(tablaN1);
				
				
				// 	Nivel 2 - Celda1
				/*celdaN2 = new PdfPCell(new Paragraph("DATOS DEL CLIENTE",font[5]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[2]);
				celdaN2.setColspan(2);
				tablaN1a.addCell(celdaN2);
				
				celdaN2 = new PdfPCell(new Paragraph(" ",font[5]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(2);
				tablaN1a.addCell(celdaN2);*/
				
				tablaN1a = new PdfPTable(4);
				tablaN1a.setWidthPercentage(100);
				tablaN1a.setWidths(widths);
				
				// 	Nivel 2 - Celda1
				celdaN2 = new PdfPCell(new Paragraph("RAZ\u00d3N SOCIAL: ", font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				tablaN1a.addCell(celdaN2);				
				// Nivel 2 - Celda2
				celdaN2 = new PdfPCell(new Paragraph(datos.getReceptor().getNombre(), font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				tablaN1a.addCell(celdaN2);
				// Nivel 2 - Celda3
				celdaN2 = new PdfPCell(new Paragraph("R.F.C.: "+ datos.getReceptor().getRfc(), font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(2);
				tablaN1a.addCell(celdaN2);
				//	Nivel 2 - Celda4
				celdaN2 = new PdfPCell(new Paragraph("DOMICILIO: ", font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				tablaN1a.addCell(celdaN2);
				// Nivel 2 - Celda5
				if (datos.getReceptor().getDomicilio().toString().length() > 0) {
					celdaN2 = new PdfPCell(new Paragraph(datos.getReceptor().getDomicilio().toString(), font[9]));
					celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
					celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
					celdaN2.setBorder(cellBorderType[0]);
					celdaN2.setColspan(3);
					tablaN1a.addCell(celdaN2);
				}
				// Nivel 2
				if (datos.getReceptor().getDomicilio().toString().length() != 0) {
					celdaN2 = new PdfPCell(new Paragraph("", font[7]));
					celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
					celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
					celdaN2.setBorder(cellBorderType[0]);
					tablaN1a.addCell(celdaN2);
				}
				// Nivel 2 - Celda6/////
				celdaN2 = new PdfPCell(new Paragraph(datos.getReceptor().getDomicilio().toString(), font[9]));
				celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				if (datos.getReceptor().getDomicilio().toString().length() > 0) {
					celdaN2.setColspan(3);
				} else {
					celdaN2.setColspan(6);
				}
				tablaN1a.addCell(celdaN2);
				tablaN3.addCell(tablaN1a);
				tablaN2.addCell(tablaN3);
                                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                                // TABLA DATOS CFD
                                // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
				tablaDatosCFD = new PdfPTable(2);
				//tablaDatosCFD.getDefaultCell().setBorder(1);
				tablaDatosCFD.setWidthPercentage(100);
				float[] porcentajesAncho = { 0.5f, 0.5f };
				tablaDatosCFD.setWidths(porcentajesAncho);
            
				//Serie:
				celdaN2 = new PdfPCell(new Paragraph("No. Serie del certificado SAT: " + datos.getSerieFolioFiscalOrig(), font[5]));
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				//celdaN2.setColspan(4);            
				tablaDatosCFD.addCell(celdaN2);
           
				//Folio:
				celdaN2 = new PdfPCell(new Paragraph("Folio fiscal: " + datos.getFolioFiscalOrig(), font[5]));
				celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				celdaN2.setBorder(cellBorderType[0]);
				celdaN2.setColspan(2);            
				tablaDatosCFD.addCell(celdaN2);
				
		/*		//Dato No. de Aprobacion:
            	celdaN2 = new PdfPCell(new Paragraph("No. de Aprobaci\u00f3n:", font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
            
            	//Dato No.A:
            	celdaN2 = new PdfPCell(new Paragraph( datos.getNoAprobacion().toString(), font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);*/
            
            	/*//Anio. de Aprobacion:
            	celdaN2 = new PdfPCell(new Paragraph("A\u00f1o de aprobaci\u00f3n:", font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
            
            	//Dato anio.A:
            	celdaN2 = new PdfPCell(new Paragraph(datos.getgetAnoAprobacion().toString(), font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);*/
				            
				//Fecha y hora:
				celdaN2 = new PdfPCell(new Paragraph("Fecha y hora de certificaci\u00f3n", font[5]));
				celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				//celdaN2.setColspan(4);            
				tablaDatosCFD.addCell(celdaN2);
            
				//Obtenemos el formato de la fecha para l afcatura
                                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                               	celdaN2 = new PdfPCell(new Paragraph( formatoFecha.format(datos.getFecha()) , font[5]));
				celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				celdaN2.setBorder(cellBorderType[0]);
				//celdaN2.setColspan(4);            
				tablaDatosCFD.addCell(celdaN2);
                
				//Lugar de Expedicion:
            	celdaN2 = new PdfPCell(new Paragraph("No. Certificado EDUCAL:", font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
            
            	//Dato lugar de Experdicion:
            	celdaN2 = new PdfPCell(new Paragraph( datos.getNoCertificado(), font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            	celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
				
            	//Certificado:
            	celdaN2 = new PdfPCell(new Paragraph("Secuencia:", font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_LEFT);
				celdaN2.setBorder(cellBorderType[0]);
				//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
            
            	//Dato Certificado:
            	celdaN2 = new PdfPCell(new Paragraph( "Falta" , font[5]));
            	celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				celdaN2.setBorder(cellBorderType[0]);
            	//celdaN2.setColspan(4);            
            	tablaDatosCFD.addCell(celdaN2);
            	
            	celdaN3 = new PdfPCell();
                celdaN3.addElement(tablaDatosCFD);
                celdaN3.setBorder(0);                        
                
                celdaN3.setCellEvent(round);
            	
            	tablaN2.addCell(celdaN3);    	
            tablaMaestra.addCell(tablaN2);
 
		return tablaMaestra;
    }


    private PdfPTable cuerpo() throws DocumentException, MalformedURLException, IOException {
        PdfPTable tablaN1, tablaN2,   tablaCuerpo   ;
        PdfPCell celdaN1, celdaN2;
        Concepto concepto;
        RoundRectangle round = new RoundRectangle();
        tablaCuerpo = new PdfPTable(1);
       
        	
            tablaN1 = new PdfPTable(6);
            tablaN1.setWidthPercentage(100);
            tablaN1.setWidths(cuerpoDimension);
            

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // ENCABEZADO DE LA TABLA MAESTRA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("CODIGO", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[2]);
            celdaN1.setBorderWidth(.5f);
            tablaN1.addCell(celdaN1);
            

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("T\u00cdTULO", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[4]);
            celdaN1.setBorderWidth(.5f);
            tablaN1.addCell(celdaN1);

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("CANTIDAD", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[4]);
            tablaN1.addCell(celdaN1);

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("UNIDAD", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[4]);
            tablaN1.addCell(celdaN1);

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("PRECIO", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[4]);
            tablaN1.addCell(celdaN1);

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph("IMPORTE", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaN1.setBorder(cellBorderType[4]);

            celdaN1.setBorderWidth(.5f);
            tablaN1.addCell(celdaN1);

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // CONCEPTOS DE LA FACTURA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            //Nivel 1
            //celdaN1 = new PdfPCell();
            //celdaN1.setMinimumHeight(proporcion[1]);
            //celdaN1.setColspan(6);
            //celdaN1.setBorder(cellBorderType[4]);

            //Nivel 2
 
            for (int x = 0; x < datos.getConceptos().getConcepto().size(); x++) {
                concepto = datos.getConceptos().getConcepto().get(x);

                for (int y = 0; y < 6; y++) {
                    // Nivel 2
                	if(y == 0){
                                if(concepto.getNoIdentificacion() != null){
                                    celdaN1 = new PdfPCell(new Paragraph(concepto.getNoIdentificacion().toUpperCase(), font[6])); //codigo
                                }
                                else{
                                    celdaN1 = new PdfPCell(new Paragraph(" ", font[6])); //codigo
                                }
                	}
                	else if(y == 1){
                		celdaN1 = new PdfPCell(new Paragraph(concepto.getDescripcion().toUpperCase(), font[6])); //titulo
                	}
                	else if(y == 2){
                                celdaN1 = new PdfPCell(new Paragraph(concepto.getCantidad().toString().toUpperCase(), font[6])); //cantidad
                        }
                	else if(y == 3){
                		celdaN1 = new PdfPCell(new Paragraph(concepto.getUnidad().toUpperCase(), font[6])); //unidad
                        }
                	else if(y == 4){
                                celdaN1 = new PdfPCell(new Paragraph(concepto.getValorUnitario().toString().toUpperCase(), font[6])); //precio
                        }
                	else if(y == 5){
                                celdaN1 = new PdfPCell(new Paragraph(concepto.getImporte().toString().toUpperCase(), font[6])); //importe
                        }
               
                	
                	celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
                	celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
                   //celdaN1.setBorder();
                	celdaN1.setBorderWidth(.5f);
                	celdaN1.setBorderWidthBottom(0);
                	celdaN1.setBorderWidthTop(0);
                	celdaN1.setBorderWidthRight(0);
                    if(y== 5){
                   celdaN1.setBorderWidthRight(0);
                    }
                    
                    if(y == 0){
                        celdaN1.setBorderWidthLeft(0);
                         }
                    if(y == 1){
                        celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
                         }
                    if (y == 4 | y == 5) {
                    	celdaN1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }

                    //Nivel 2
                    tablaN1.addCell(celdaN1);
                }
            } 
            if ( datos.getConceptos().getConcepto().size() < 25){
            	int auxFicticio = 25 - datos.getConceptos().getConcepto().size();
            	   for (int x = 0; x < auxFicticio ; x++) {
                       for (int y = 0; y < 6; y++) {
                           // Nivel 2
                       	celdaN1 = new PdfPCell(new Paragraph(" ", font[6]));
                       	celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
                       	celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
                          //celdaN1.setBorder();
                       	celdaN1.setBorderWidth(.5f);
                       	celdaN1.setBorderWidthBottom(0);
                       	celdaN1.setBorderWidthTop(0);
                       	celdaN1.setBorderWidthRight(0);
                           if(y== 5){
                          celdaN1.setBorderWidthRight(0);
                           }
                           
                           if(y== 0){
                               celdaN1.setBorderWidthLeft(0);
                                }

                           if (y == 4 | y == 5) {
                           	celdaN1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                           }

                           //Nivel 2
                           tablaN1.addCell(celdaN1);
                       }
                   }
            }
            if(/*datos.getLeyendaFactura() != "" && datos.getLeyendaFactura() != null*/ true){////////////////////////////////IMPORTANTE FALTA AGREGARLE LA CONDICION DE LOS RECIBOS
         	   celdaN1 = new PdfPCell(new Paragraph(" Este CFDI contiene informaci\u00f3n de los recibos:" , font[7])); ////////////IMPORTANTE FALTA AGREGARLE LA INFORMACION DE LOS RECIBOS
                celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaN1.setColspan(6);
                celdaN1.setBorderWidthBottom(0);
                celdaN1.setBorderWidthRight(0);
                celdaN1.setBorderWidthLeft(0);
                tablaN1.addCell(celdaN1);
         }if (datos.getFormaDePago().startsWith("Pago en")) {
                celdaN1 = new PdfPCell(new Paragraph("Forma de pago: " + datos.getFormaDePago(), font[8]));
                celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaN1.setColspan(6);
                celdaN1.setBorderWidthLeft(0);
                celdaN1.setBorderWidthRight(0);
                tablaN1.addCell(celdaN1);
        } else {
                celdaN1 = new PdfPCell(new Paragraph(" "));
                celdaN1.setColspan(3);
                celdaN1.setBorder(cellBorderType[0]);
                celdaN1 = new PdfPCell();
                celdaN1.setColspan(6);
                Paragraph parrafo = new Paragraph("Pago  "/* + datos.getNumeroPago() + " de " + datos.getTotalNumeroPagos()*/, font[7]);///////IMPORTANTE FALTA AGREGARLE NUMERO TOTAL DE PAGOS
                parrafo.setAlignment(Element.ALIGN_CENTER);
                tablaN2 = new PdfPTable(1);
                celdaN2 = new PdfPCell(parrafo);
                eliminaBordesCelda(celdaN2);
                celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaN2.addCell(celdaN2);
 
                    parrafo = new Paragraph("Monto total de la operaci\u00f3n  " + datos.getSubTotal().toString(), font[7]);
                    parrafo.setAlignment(Element.ALIGN_CENTER);
                    celdaN2 = new PdfPCell(parrafo);
                    eliminaBordesCelda(celdaN2);
                    celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaN2.addCell(celdaN2);
                    
                    if( datos.getImpuestos().getTotalImpuestosTrasladados() != null){
                        parrafo = new Paragraph("IVA (del total)  " + datos.getImpuestos().getTotalImpuestosTrasladados().toString(), font[7]);
                        parrafo.setAlignment(Element.ALIGN_CENTER);
                        celdaN2 = new PdfPCell(parrafo);
                        eliminaBordesCelda(celdaN2);
                        celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        tablaN2.addCell(celdaN2);
                    }
           
                    parrafo = new Paragraph("TOTAL  " + datos.getTotal().toString(), font[7]);
                    parrafo.setAlignment(Element.ALIGN_CENTER);
                    celdaN2 = new PdfPCell(parrafo);
                    eliminaBordesCelda(celdaN2);
                    celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaN2.addCell(celdaN2);
                    /*if(Integer.parseInt(datos.getNumeroPago()) > 1){ ////////REANUDAR CUANDO SE TENGA LOS NUMERO DE PAGO
                    	parrafo = new Paragraph("La parcialidad ampara el pago a cuenta del CFD "+datos.getFolioFiscalOrig()+" expedido el "+datos.getFechaFolioFiscalOrig(), font[7]);
                    	parrafo.setAlignment(Element.ALIGN_CENTER);
                    	celdaN2 = new PdfPCell(parrafo);
                    	eliminaBordesCelda(celdaN2);
                    	celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    	tablaN2.addCell(celdaN2);
                    }*/
      
      
                celdaN1.addElement(tablaN2);
                celdaN1.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaN1.setBorderWidthLeft(0);
                celdaN1.setBorderWidthRight(0);
 
                tablaN1.setWidthPercentage(100);
                tablaN1.addCell(celdaN1);
            }
        	
          
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // IMPORTE TOTAL CON LETRA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            // Nivel 1
            Numero_a_Letra num_let = new Numero_a_Letra();
            celdaN1 = new PdfPCell(
                    new Paragraph("CANTIDAD CON LETRA: " + num_let.Convertir( datos.getTotal().toString(), true), font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaN1.setBorder(cellBorderType[1]);
            celdaN1.setBorderWidthTop(0);
            celdaN1.setColspan(2);
            tablaN1.addCell(celdaN1);

            // Nivel 1
            celdaN1 = new PdfPCell(new Paragraph(" ", font[6]));
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaN1.setBorder(cellBorderType[1]);
            celdaN1.setBorderWidthTop(0);
            celdaN1.setColspan(4);
            tablaN1.addCell(celdaN1);

 
           
           
            tablaCuerpo.setWidthPercentage(100); 
               tablaCuerpo.setSplitLate(false);
         
            
               celdaN1 = new PdfPCell();
               celdaN1.addElement(this.encabezado());
               celdaN1.setBorder(0);
           
            tablaCuerpo.addCell(celdaN1);
            
            
            
 
            
      

            celdaN1 = new PdfPCell();
            celdaN1.addElement(tablaN1);
            celdaN1.setBorder(0);
            
            
            
            celdaN1.setCellEvent(round);
            tablaCuerpo.addCell(celdaN1);
            tablaCuerpo.setKeepTogether(true);
            
 
            //tablaCuerpo.set
          

         //   proporcion[1] = tablaN1.getTotalHeight();

        

       
        return tablaCuerpo;
    }
    private void eliminaBordesCelda(PdfPCell celda) {
        celda.disableBorderSide(Rectangle.LEFT);
        celda.disableBorderSide(Rectangle.RIGHT);
        celda.disableBorderSide(Rectangle.TOP);
        celda.disableBorderSide(Rectangle.BOTTOM);
    }

	private PdfPTable pie() throws DocumentException, BadElementException, MalformedURLException, IOException {
        PdfPTable tablaN1, tablaN2;
        PdfPCell celdaN1, celdaN2;
        Paragraph parrafo;
        Image img;
        tablaN1 = new PdfPTable(3);
        PdfPTable tablaRedonda = new PdfPTable(1);
        float[] widths = {0.5f, 0.5f};
        float[] widthT = {1f};
        String textoPie = "RECIB\u00CD A MI ENTERA CONFORMIDAD LA MERCANC\u00CDA O EL SERVICIO CUYO IMPORTE APARECE AL CALCE, EL CUAL PAGAR\u00C9 INCONDICIONALMENTE A LA ORDEN DE EDUCAL S.A. DE C.V. EN SU DOMICILIO SOLIDARIAMENTE CON LA PERSONA QUE FIRME A MI RUEGO O EN MI NOMBRE.";
        

            tablaN1.setWidthPercentage(100);
            tablaN1.setSplitLate(false);
            tablaN1.setWidths(pieDimension);
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // PRIMERA CELDA DE LA TABLA MAESTRA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // Nivel 1 - Celda Central Superio del pie de la pagina
            celdaN1 = new PdfPCell();
            celdaN1.setMinimumHeight(2);
            celdaN1.setColspan(3);
            celdaN1.setBorder(0);
            // Nivel 1 - Se carga celdaN1 en tablaN1
            tablaN1.addCell(celdaN1);
            // Nivel 1 - Celda Central Superio del pie de la pagina
            tablaN2 = new PdfPTable(1);
            tablaN2.setWidthPercentage(100);
            celdaN1 = new PdfPCell(new Paragraph("Observaciones:", font[8]));
            celdaN1.setBorderWidth(0);
            tablaN2.addCell(celdaN1);
            celdaN1 = new PdfPCell(new Paragraph(/*datos.getObservaciones()*/ " ", font[7]));//////IMPORTANTE FALTAN LAS OBSERVACIONES
            celdaN1.setBorderWidth(0);
            tablaN2.addCell(celdaN1);
            celdaN1 = new PdfPCell();
            celdaN1.setBorder(cellBorderType[0]);
            celdaN1.setColspan(3);
            celdaN1.addElement(tablaN2);
            RoundRectangle round = new RoundRectangle();
            celdaN1.setCellEvent(round);
            tablaN1.addCell(celdaN1);
            // Nivel 1 - Celda Central Superio del pie de la pagina

            
            // Nivel 2 - Inicializar Tabla
            tablaN2 = new PdfPTable(1);
            tablaN2.setWidthPercentage(100);
            tablaN2.setSplitLate(false);
            tablaN2.setWidths(widthT);
            if (datos.getNumCtaPago()!=null && !datos.getNumCtaPago().trim().equals("")){
            	//celdaN2 = new PdfPCell(new Paragraph("METODO DE PAGO: " + datos.getNumCtaPago()+ "\n"+ "NUMERO DE CUENTA DE PAGO: "+datos.getNumCtaPago(), font[7]));
            	celdaN2 = new PdfPCell(new Paragraph("M\u00c9TODO DE PAGO: " +datos.getMetodoDePago().toUpperCase()/*+ datos.getMetodoPago()*/, font[7]));//////IMPORTANTE QUITAR UNA VEZ PUESTO EL METODO DE PAGO
            }else{
            	celdaN2 = new PdfPCell(new Paragraph("M\u00c9TODO DE PAGO: " + datos.getMetodoDePago().toUpperCase() /*+ datos.getMetodoPago()*/, font[7])); /////IMPORTANTE falta poner el tipo de pago efectrivo cheuqe transferencia	
            }
            celdaN2.setVerticalAlignment(Element.ALIGN_JUSTIFIED);
            celdaN2.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            celdaN2.setBorder(0);            
            tablaN2.addCell(celdaN2);
            celdaN2 = new PdfPCell(new Paragraph("FORMA DE PAGO: " + datos.getFormaDePago(), font[7]));
            celdaN2.setVerticalAlignment(Element.ALIGN_JUSTIFIED);
            celdaN2.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            celdaN2.setBorder(0);            
            tablaN2.addCell(celdaN2);
            
      
           celdaN1 = new PdfPCell();
    
           celdaN1.setBorder(0);
           celdaN1.setColspan(3);
             celdaN1.addElement(tablaN2);
 
        
            tablaN1.addCell(celdaN1);
       

          
            celdaN1 = new PdfPCell();
            celdaN1.setMinimumHeight(2);
            celdaN1.setColspan(3);
            celdaN1.setBorder(0);
            // Nivel 1 - Se carga celdaN1 en tablaN1
            tablaN1.addCell(celdaN1);

            // Nivel 1 - Celda Logotipo IPN esquina superior izquierda
            celdaN1 = new PdfPCell();
            celdaN1.setMinimumHeight(80);
            celdaN1.setBorder(cellBorderType[0]);
            
            //img = Image.getInstance(recursos.getProperty("url.img.logo.qr"));
            img = Image.getInstance(QRCodeJava.getQrImage(datos.getSello(), 200, 200).toByteArray());
            //img = Image.getInstance(datos.getRutaImagenCodigo());/////////////IMPORTANTE FALTA AGREGAR LA RUTA DEL CODIGO QR
            //img.scalePercent(116);
            img.setGrayFill(1f);
            img.setAlignment(1);

            // Nivel 1 - Se agrega la imagen a la celda
            celdaN1.addElement(img);

            // Nivel 1 - Se agrega la celda a la tabla
            tablaN1.addCell(celdaN1);

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // SEGUNDA CELDA DE LA TABLA MAESTRA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            // Nivel 1 - Celda Central Superio del Encabezado de la pagina
            celdaN1 = new PdfPCell();
            celdaN1.setBorder(cellBorderType[0]);
            celdaN1.setVerticalAlignment(Element.ALIGN_CENTER);
            celdaN1.setHorizontalAlignment(Element.ALIGN_LEFT);


            //==============================================================
            //SELLO DIGITAL CFD
            //==============================================================
            parrafo = new Paragraph("SELLO ORIGINAL CFDI: ", font[8]);
            celdaN1.addElement(parrafo);
            parrafo = new Paragraph(datos.getSello(), font[7]);
            celdaN1.addElement(parrafo);
            
            //==============================================================
            //SELLO ORIGINAL SAT
            //==============================================================
            parrafo = new Paragraph("SELLO ORIGINAL SAT: ", font[8]);
            celdaN1.addElement(parrafo);
            parrafo = new Paragraph(datos.getSello() + "falta", font[7]);
            celdaN1.addElement(parrafo);

            //==============================================================
            //CADENA ORIGINAL
            //==============================================================
            parrafo = new Paragraph("CADENA ORIGINAL:", font[8]);
            celdaN1.addElement(parrafo);

            parrafo = new Paragraph(this.getCadenaOriginal(), font[7]);///////IMPORTANTE FALTA PONER LA CADENA ORIGINAL |XXXX|XXXX|XXX|XXX|XXX|XXX|XX|XX|XX|X|X|X
            celdaN1.addElement(parrafo);


            celdaN1.addElement(new Paragraph(" ",font[11]));
           /* parrafo = new Paragraph("La reproducci\u00F3n ap\u00F3crifa de este comprobante constituye" +
                    " un delito en los t\u00E9rminos de las disposiciones fiscales.", font[7]);
            parrafo.setAlignment(Element.ALIGN_LEFT);
            celdaN1.addElement(parrafo);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaCer = null;
            String fechaCert = null;
            Calendar cal=Calendar.getInstance();
            fechaCer =  format.parse(datos.getFechaCertificacion().replace('T', ' '));
            cal.setTime(fechaCer);
            fechaCert = formatoFecha.format(cal.getTime());
            parrafo = new Paragraph("Este comprobante tendr\u00e1 una vigencia de dos a\u00F1os contados a " +
                    "partir de la fecha de aprobaci\u00F3n de la asignaci\u00F3n de folios, la cual es: " +fechaCert ,font[7]);
            
            parrafo.setAlignment(Element.ALIGN_LEFT);
            celdaN1.addElement(parrafo);*/


            // Nivel 1 - Se carga celdaN1 en tablaN1
            tablaN1.addCell(celdaN1);

            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // TERCERA CELDA DE LA TABLA MAESTRA
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            int noIvas = datos.getImpuestos().getTraslados().getTraslado().size(); //IMPORTANTE en caso de que se tuviera n ivas
            
                    celdaN1 = new PdfPCell();
	            celdaN1.setBorder(cellBorderType[0]);
                    // celdaN1.setVerticalAlignment(Element.ALIGN_TOP);
	
	            // Nivel 2 - Inicializar Tabla
	            tablaN2 = new PdfPTable(2);
	            tablaN2.setWidthPercentage(100);
	            tablaN2.setSplitLate(false);
	            tablaN2.setWidths(widths);
	
	            // Nivel 2 - Etiqueta SubTotal
	            celdaN2 = new PdfPCell(new Paragraph("SubTotal", font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setBorder(cellBorderType[2]);
	            celdaN2.setMinimumHeight(15);
	
	            // Nivel 2
	            tablaN2.addCell(celdaN2);
	
	            //Nivel 2 - Datos del subTotal
	            celdaN2 = new PdfPCell(new Paragraph(datos.getSubTotal().toString(), font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            celdaN2.setBorder(cellBorderType[4]);
	            
	            tablaN2.addCell(celdaN2);
	            
                    if(datos.getDescuento() !=null){
	            // Nivel 2 - Etiqueta Descuentos
	            celdaN2 = new PdfPCell(new Paragraph("Descuentos", font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setBorder(cellBorderType[2]);
	            celdaN2.setMinimumHeight(15);
	
	            // Nivel 2
	            tablaN2.addCell(celdaN2);
	
	            //Nivel 2 - Datos del descuento
	            celdaN2 = new PdfPCell(new Paragraph(datos.getDescuento().toString(), font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            celdaN2.setBorder(cellBorderType[4]);
	
	            // Nivel 2
	            tablaN2.addCell(celdaN2);
                    }
                    
                    for(int indiceIvas = 0; indiceIvas < noIvas; indiceIvas++){
                        //Nivel 2 - Datos de la tasa de impuesto
                        Traslado t = datos.getImpuestos().getTraslados().getTraslado().get(indiceIvas);
                        celdaN2 = new PdfPCell(new Paragraph(t.getImpuesto() + " [" + t.getTasa().toString() + "%]" , font[6]));
                        celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
                        celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celdaN2.setBorder(0);
                        celdaN2.setMinimumHeight(15);
                        tablaN2.addCell(celdaN2);
	          
                        //Nivel 2 - Datos Tasa
                        celdaN2 = new PdfPCell(new Paragraph(t.getImporte().toString(), font[6]));
                        celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
                        celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celdaN2.setBorder(cellBorderType[4]);
                        tablaN2.addCell(celdaN2);
                    }
                    
	            //Nivel 2
	            celdaN2 = new PdfPCell(new Paragraph("Total", font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setBorder(cellBorderType[1]);
	            celdaN2.setMinimumHeight(15);
	
	            // Nivel 2
	            tablaN2.addCell(celdaN2);
	
	            //Nivel 2 - Dato Total
	            celdaN2 = new PdfPCell(new Paragraph(datos.getTotal().toString(), font[6]));
	            celdaN2.setVerticalAlignment(Element.ALIGN_CENTER);
	            celdaN2.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            celdaN2.setBorder(cellBorderType[3]);
	
	            // Nivel 2
	            tablaN2.addCell(celdaN2);

                    //Se agrega contenido a la  celula
	            celdaN1.addElement(tablaN2);
 
                    //celdaN1.setCellEvent(round);
	
	            //Se agrega celula a la tabla maestra
	            tablaRedonda = new PdfPTable(1);
		    celdaN1.setCellEvent(round);
	            tablaRedonda.addCell(celdaN1);
	            celdaN1 = new PdfPCell();
	            celdaN1.setBorder(0);
	            tablaRedonda.addCell(celdaN1);
	            tablaRedonda.addCell(celdaN1);
	            tablaRedonda.setWidthPercentage(100);
	            
	            celdaN1 = new PdfPCell(new Paragraph("", font[7]));
	            celdaN1.setBorder(0);
	            celdaN1.addElement(tablaRedonda);
		           
	            tablaN1.addCell(celdaN1);
	             
            celdaN1 = new PdfPCell();
            parrafo = new Paragraph(textoPie, font[7]);
            celdaN1.setBorder(cellBorderType[0]);
            celdaN1.addElement(parrafo);
            celdaN1.setColspan(3);
            tablaN1.addCell(celdaN1);
           


            // Nivel 1
            celdaN1 = new PdfPCell();
            celdaN1.setBorder(cellBorderType[0]);
            parrafo = new Paragraph("Este documento es una representaci\u00F3n de un CFDI", font[5]);
            parrafo.setAlignment(Element.ALIGN_CENTER);
            celdaN1.addElement(parrafo);
            celdaN1.setColspan(3);
            // Nivel 1 - Se carga celdaN1 en tablaN1
            tablaN1.addCell(celdaN1);

            
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
            // FIN DE CARGA DE TABLAS Y CELDAS
            // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

            // Se agrega tabla al documento
        
        return tablaN1;
    }

    private ByteArrayOutputStream setPageNumbers(ByteArrayOutputStream baos,float x, float y) throws DocumentException, IOException {
		ByteArrayOutputStream BA = new ByteArrayOutputStream();
		
			PdfReader reader = new PdfReader(baos.toByteArray());
			PdfStamper stamper = new PdfStamper(reader, BA);
			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			int j = reader.getNumberOfPages();
			for (int i = 1; i <= j; i++) {
				PdfContentByte over = stamper.getOverContent(i);
				over.beginText();
				over.setFontAndSize(bf, 5);
				over.setTextMatrix(x, y);
				over.showText("P\u00c1GINA " + i + " DE " + j);
				over.endText();
				over.closePath();
			}
			//reader.close();
			//baos.close();
			stamper.close();
			return pagina(BA);
		
	}

	private void waterMark(PdfStamper stamper, int pag) throws DocumentException, BadElementException, MalformedURLException, IOException {
		PdfGState gstate;
		Image img;
		PdfWriter writer = stamper.getWriter();
		
			float width = writer.getPageSize().getWidth();
                        float height = writer.getPageSize().getHeight();
			img = Image.getInstance(recursos.getProperty("url.img.logo.educal"));
			img.setGrayFill(0.75f);
                        img.scalePercent(200);
                        float xi = img.getWidth();
                        float yi = img.getHeight();
			float x = ((width - xi)/2)-80;
			float y = (height - yi)/2;
			img.setAbsolutePosition(x, y);
			gstate = new PdfGState();
			gstate.setFillOpacity(0.3f);
			gstate.setStrokeOpacity(0.3f);
			PdfContentByte contentunder = stamper.getUnderContent(pag);
			contentunder.saveState();
			contentunder.setGState(gstate);
			contentunder.addImage(img);
			//contentunder.endText();
			contentunder.restoreState();
		
	}

    private ByteArrayOutputStream  pagina(ByteArrayOutputStream baos) throws IOException, DocumentException {
    	ByteArrayOutputStream BA = new ByteArrayOutputStream();
        
            PdfReader reader = new PdfReader(baos.toByteArray());

             PdfStamper stamper = new PdfStamper(reader,BA);
             int j=reader.getNumberOfPages();
            
             for(int i=1;i<=j;i++){
            
                     this.waterMark(stamper,i);

             }
             baos.close();
             stamper.close();
             reader.close();
             return BA;
             
}
    
    public static void setCadenaOriginal(String cadenaOriginal) {
        CFDv32Pdf.cadenaOriginal = cadenaOriginal;
    }

    public static String getCadenaOriginal() {
        return cadenaOriginal;
    }
 
    public static void crearPdf_static(Comprobante comprobante, String cadenaOriginal) throws IOException, DocumentException{
       
                CFDv32Pdf pdf = new CFDv32Pdf(cadenaOriginal);           	
                java.io.FileOutputStream fos = null;
                String nombreArchivo = pdf.recursos.getProperty("salidaPDFv32");
                fos = new java.io.FileOutputStream(nombreArchivo);
                //System.out.println(data.generaCadenaOriginal());
               
                        System.out.println("Comenzar a crear pdf.");
                        fos.write(pdf.createPdf(comprobante).toByteArray());           
                        System.out.println("Creado pdf.");
                        //PdfReader reader = new PdfReader(nombreArchivo);
                        //PdfEncryptor.encrypt(reader,fos,PdfWriter.STANDARD_ENCRYPTION_128,"","",PdfWriter.ALLOW_PRINTING);
                        fos.close();
                        System.out.println("Cerrando pdf.");                               
                        //Executable.openDocument("D:\\F-O2G00058PDF.pdf");                    
                
    }
    
    public void crearPdf(Comprobante comprobante, String cadenaOriginal) throws FileNotFoundException, IOException, DocumentException{	        	
  	     
                this.setCadenaOriginal(cadenaOriginal);
                java.io.FileOutputStream fos = null;
                String nombreArchivo = recursos.getProperty("salidaPDFv32");
                fos = new java.io.FileOutputStream(nombreArchivo);
             
                //System.out.println(data.generaCadenaOriginal());
          
                    System.out.println("Comenzar a crear pdf.");
                    fos.write(this.createPdf(comprobante).toByteArray());
                    
                    System.out.println("Creado pdf.");
                    //sPdfReader reader = new PdfReader(nombreArchivo);
                    //PdfEncryptor.encrypt(reader,fos,PdfWriter.STANDARD_ENCRYPTION_128,"","",PdfWriter.ALLOW_PRINTING);
                    fos.close();
                    System.out.println("Cerrando pdf.");
                               
                    //Executable.openDocument("D:\\F-O2G00058PDF.pdf");
                    
                    
            
    }
    
    public static void main(String[] args) throws Exception {
        //Comprobante compCFD = null;
    	
    	/*CFDIdocument data = new CFDIdocument();
        data.setRutaImagenCodigo("C:\\pdf\\qr.png");
        data.setFolio("1");
        data.setRfcE("IPN811229H26");
        data.setCalleE("AV. MIGUEL OTHON DE MENDIZABAL");
        data.setColoniaE("LA ESCALERA");
        data.setPaisE("MEXICO");
        data.setEstadoE("DISTRITO FEDERAL");
        data.setCodigoPostalE("07320");
        data.setMunicipioE("GUSTAVO A. MADERO");
        data.setNoCertificado("00001000000104914474");
        data.setFecha("2012");
        data.setCalleExpEn("AV. JUAN DE DIOS BATIZ");
        data.setNoExteriorExpEn("S/N");
        data.setCodigoPostalExpEn("07738");
        data.setColoniaExpEn("ZACATENCO");
        data.setMunicipioExpEn("GUSTAVO A. MADERO");
        data.setEstadoExpEn("DISTRITO FEDERAL");
        data.setPaisExpEn("MEXICO");
        data.setFolioIPN("F-O2G00058");
        data.setTipoDeComprobante("XXX");
        data.setFormaDePago("Pago e una sola exhibicion");
        data.setNumeroPago("1");
        data.setTotalNumeroPagos("2");
        data.setSubtotalCompra("3316.00");
        data.setIvaCompra("0");
        data.setTotalCompra("3316.00");
        data.setCondicionesDePago("---");
        //data.setFacturaRaiz("12345");
        //data.setFechaFacturaRaiz("12 de Febrero de 2011");
        //data.setMontoFacturaRaiz("100,000");

        data.setTipoDeDocumento("FACTURA");

        data.setSubTotal("456.50");
        data.setDescuento("0.00");
        data.setTotal("456.50");
        data.setRfcR("IPN811229H26");
        data.setNombreR("INSTITUTO POLITECTNICO NACIONAL U.ZACATENCO");
        data.setCalleR("AV. JUAN DE DIOS BATIZ");
       // data.setNoExteriorR("11");
        data.setNoInteriorR(" ");
        data.setColoniaR("COL. ZACATENCO");
        data.setCodigoPostalR("07738");
        data.setLeyendaFactura("Este CFD contiene informacion de los recibos: O2G00468,O2G00469,O2G00470,O2G00471");
        data.setLocalidadR("GUSTAVO A. MADERO");
        data.setMunicipioR("DISTRITO FEDERAL");
       // data.setEstadoR("MEXICO");
        data.setFechaExpedicion("LUNES, ABRIL 16, 2012 19:56:59 HRS.");
        data.setPaisR("MEXICO");
        //data.setCodigoPostalR("00000");
        data.setNoIvas(1);  //el numero de ivas que hay que poner
        
        //auxDetalle = 'Esta factura contiene informacion de los recibos: ';
        String conceptos[] = {"1962", "1", "Pza", " REPOSICION DE GAFETE CON TARJETA INTELIGENTE PARA CONTROL DE ACCESOS", "104.50", "104.50"};
        
        data.setConceptoPDF(conceptos);
        String conceptos1[] = {"1962", "1", "Pza", " REPOSICION DE GAFETE CON TARJETA INTELIGENTE PARA CONTROL DE ACCESOS", "104.50", "104.50"};
        
        data.setConceptoPDF(conceptos1);
        
        String conceptos2[] = {"2125", "37", "Pza", " MULTA DE BIBLIOTECA POR DEVOLUCION TARDIA DE LIBROS PRESTADOS A DOMICILIO", "5.50", "203.50"};
        
        data.setConceptoPDF(conceptos2);
        
        String conceptos3[] = {"2125", "8", "Pza", " MULTA DE BIBLIOTECA POR DEVOLUCION TARDIA DE LIBROS PRESTADOS A DOMICILIO", "5.50", "44.00"};
        
        data.setConceptoPDF(conceptos3);
         
        data.setTotalImpuestoTrasladado("0.00");
        data.setFechaCertificacion("2012-04-16T19:56:59");
        //data.setFinCadenaOriginal();
        //data.setSelloDigital("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        //data.setSelloDigital("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        data.setIvaLetra("0");
        data.setTotalLetra("CUATROCIENTOS CINCUENTA Y SEIS PESOS 50/100 M.N.");
        data.setIvaLetraUno("0");
        data.setIvaLetraDos("16");
        data.setImpuestosTrasladadosUno("0.00");
        data.setImpuestosTrasladadosDos("50.00");
        data.setObservaciones(" ");
       // data.setFechaCertificacion("hoy");
        data.setSelloSAT("QnS0FPDX9M3CHvhaDPvOGCRUhqw7kViBR7cJLc2fsHwVvjIi7drVInReTOIdTReWWiyAswt/yoP7SgxD0llUcoHyYlTVPq+tS0UDWVcLPox72jv36gu6Ns3HyKnimo+g/iWVhxHspvdiOTeong/9SaWx/q6BsWNE2wn9cw7bVpo=");
        data.setSelloCFDI("DmainmfL5c7iwnUv7+tVsXeznFkiyySHRVpALiMkCcgATWeCoVvlCg1gbqSe486P7f+BZcQo1EV3P+7gPDFsuhzdIUNLi8uYYxbZymGdhpf0ZGUumXvdLlbtX2G3vOvJ5sVycViKjblOEaINbjomHtVWFbFRXNKWOJxPZsfWmnU=");
        data.setFolioFiscal("116371e4-928f-470a-9176-f603d46ac059");
        data.setNoCertificadoSAT("00001000000102993929");
    //    data.setFechaCertificacion("2011-08-31 10:17:02");
        StringBuffer cadenaCompleta = new StringBuffer("||");
        cadenaCompleta.append("1.0|");
        cadenaCompleta.append("116371e4-928f-470a-9176-f603d46ac059|");
        cadenaCompleta.append("2012-04-16T19:56:59|");
        cadenaCompleta.append("QnS0FPDX9M3CHvhaDPvOGCRUhqw7kViBR7cJLc2fsHwVvjIi7drVInReTOIdTReWWiyAswt/yoP7SgxD0llUcoHyYlTVPq+tS0UDWVcLPox72jv36gu6Ns3HyKnimo+g/iWVhxHspvdiOTeong/9SaWx/q6BsWNE2wn9cw7bVpo=|");
        cadenaCompleta.append("00001000000102993929");
        cadenaCompleta.append("||");
        data.setTotalNumeroPagos("2");
        data.setObservaciones(" ");
        data.setNoIvas(2);
        data.setNumCtaPago(" ");
        data.setLugarExpedicion("AV. JUAN DE DIOS BATIZ");
        data.setMetodoPago("EFECTIVO, CHEQUE,EFECTIVO, TRANSFERENCIA");
        data.setFolioFiscalOrig("1b2954b6-7168-49b1-9df1-47c81e639bcb");
        data.setFechaFolioFiscalOrig("2011-12-27T17:54:53");
       	// data.setCadenaComplementoSAT(cadenaCompleta.toString());       
        //data.setRegimenFiscal("PERSONA MORAL CON FINES NO LUCRATIVOS");
        
         */
    	
        /*CFDv22Pdf pdf = new CFDv22Pdf();
    	
    
            	
    	java.io.FileOutputStream fos = null;
        String nombreArchivo = "C:\\CFDv22PDF.pdf";
        fos = new java.io.FileOutputStream(nombreArchivo);
     
        //System.out.println(data.generaCadenaOriginal());
        try {
        	System.out.println("Comenzar a crear pdf.");
            fos.write(pdf.createPdf(comprobante).toByteArray());
            
            System.out.println("Creado pdf.");
            //sPdfReader reader = new PdfReader(nombreArchivo);
            //PdfEncryptor.encrypt(reader,fos,PdfWriter.STANDARD_ENCRYPTION_128,"","",PdfWriter.ALLOW_PRINTING);
            fos.close();
            System.out.println("Cerrando pdf.");
                       
            //Executable.openDocument("D:\\F-O2G00058PDF.pdf");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //System.out.println(data.generaCadenaOriginal());
    } 
    
}