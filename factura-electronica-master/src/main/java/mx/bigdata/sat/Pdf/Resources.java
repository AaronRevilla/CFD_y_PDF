package mx.bigdata.sat.Pdf;

import java.util.ResourceBundle;

public class Resources {
    private ResourceBundle rb = null;
    private static Resources r=null;
    public Resources() {
        rb = ResourceBundle.getBundle("ApplicationResources");
    }
    public static Resources getInstance(){
        if(r==null)
            r = new Resources();
        return r;
    }

    public String getProperty(String p){
        return rb.getString(p);
    }
    public static void main(String args[]){
    Resources r = new Resources();
    System.out.println( r.getProperty("cfd.key"));
    }
}
