package edu.ipn.codigoQR;

public class CodigoBidimensionalVO {

	private String informacion;
	private String rutaFinalImagen;
/*GET*/
	public String getRutaFinalImagen()
	{
	    return this.rutaFinalImagen;
	}
	
	public String getInformacion()
	{
		return this.informacion;
		
	}

/*SET*/
	public void setInformacion(String informacion){
		this.informacion= informacion;
	}
	public void setRutaFinalImagen(String rutaFinalImagen)
	{
	    this.rutaFinalImagen = rutaFinalImagen;
	}
}
