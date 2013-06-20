/*
 *  Copyright 2010 BigData.mx
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

package mx.bigdata.sat.cfd.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Properties;
import mx.bigdata.sat.cfd.AccionCFD;

import mx.bigdata.sat.cfd.CFDv22;
import mx.bigdata.sat.cfd.v22.schema.Comprobante;

import mx.bigdata.sat.security.KeyLoader;

public final class Main {
    
  public static void main(String[] args) throws Exception {
        Properties propiedades = new Properties();  
	propiedades.load(new FileInputStream("src/main/resources/config.properties"));  
        System.out.println(propiedades.getProperty("key") +""+ propiedades.getProperty("contraseniaKey")+""+ propiedades.getProperty("cer")+""+ propiedades.getProperty("salida"));
        AccionCFD acdf = new AccionCFD( propiedades.getProperty("key"), propiedades.getProperty("contraseniaKey"), propiedades.getProperty("cer"), propiedades.getProperty("salida"));
        acdf.testComp();
        System.out.println("Facturado");
  }
}