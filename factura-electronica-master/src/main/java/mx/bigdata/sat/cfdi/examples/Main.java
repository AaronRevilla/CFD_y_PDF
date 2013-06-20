/*
 *  Copyright 2010-2011 BigData.mx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mx.bigdata.sat.cfdi.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;
import mx.bigdata.sat.Pdf.CFDv32Pdf;
import mx.bigdata.sat.cfd.AccionCFD;

import mx.bigdata.sat.cfdi.v32.schema.Comprobante;
import mx.bigdata.sat.cfdi.CFDv32;
import mx.bigdata.sat.security.KeyLoader;

public final class Main {
    
  public static void main(String[] args) throws Exception {
    Properties propiedades = new Properties();  
    propiedades.load(new FileInputStream("src/main/resources/config.properties"));
        
    CFDv32 cfd = new CFDv32(ExampleCFDv32Factory.createComprobante(),"mx.bigdata.sat.cfdi.examples");
    cfd.addNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
    PrivateKey key = KeyLoader.loadPKCS8PrivateKey(new FileInputStream( propiedades.getProperty("key")),propiedades.getProperty("contraseniaKey"));
    X509Certificate cert = KeyLoader.loadX509Certificate(new FileInputStream( propiedades.getProperty("cer")));
    Comprobante sellado = cfd.sellarComprobante(key, cert);
    System.err.println(sellado.getSello());
    cfd.validar();
    cfd.verificar();
    cfd.guardar(System.out);
    cfd.guardar(new FileOutputStream( propiedades.getProperty("salidaCFDv32")));
    
    CFDv32Pdf.crearPdf_static(sellado, cfd.getCadenaOriginal());
  }
}