/*
 * Copyright 2013 AARON.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mx.bigdata.sat.cfd.examples;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.xml.transform.stream.StreamSource;
import mx.bigdata.sat.cfd.AccionCFD;
import mx.bigdata.sat.cfd.v22.schema.Comprobante;

/**
 *
 * @author AARON
 */
public class TestCFD {    
       
    public TestCFD(){
    }
    
    public void test() throws Exception{
        Properties propiedades = new Properties();  
        propiedades.load(new StreamSource(getClass().getResourceAsStream("/config.properties")).getInputStream());
        
        //new StreamSource(getClass().getResourceAsStream(XSLT))
        
        String key = propiedades.getProperty("key");
        String passKey = propiedades.getProperty("contraseniaKey");
        String cer = propiedades.getProperty("cer");
        String salida = propiedades.getProperty("salida");
        AccionCFD acdf = new AccionCFD( key, passKey, cer, salida);
        acdf.facturar();
    }
    
    public void test(Comprobante comprobante) throws Exception{
        Properties propiedades = new Properties();  
        propiedades.load(new StreamSource(getClass().getResourceAsStream("/config.properties")).getInputStream());
        //propiedades.load(new FileInputStream(new File(getClass().getResource("/config.properties").toURI())));
        
        String key = propiedades.getProperty("key");
        String passKey = propiedades.getProperty("contraseniaKey");
        String cer = propiedades.getProperty("cer");
        String salida = propiedades.getProperty("salida");
        AccionCFD acdf = new AccionCFD( key, passKey, cer, salida);
        acdf.facturar(comprobante);
    }
    
}
