package mx.bigdata.sat.Pdf;


import java.awt.Color;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;



public class RoundRectangle implements  PdfPCellEvent {
	public void cellLayout(PdfPCell cell, Rectangle rect,PdfContentByte[] canvas) {
		PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
		cb.setColorStroke(BaseColor.BLACK);
		cb.setLineWidth(1);
		cb.setLeading(20);
		cb.roundRectangle(rect.getLeft() , rect.getBottom(),rect.getWidth() , rect.getHeight() , 3);//10
		cb.stroke();
	}
}