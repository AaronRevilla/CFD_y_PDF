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
package mx.bigdata.sat.cfd;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import mx.bigdata.sat.cfd.CFDv22;
import mx.bigdata.sat.cfd.examples.ExampleCFDv22Factory;
import mx.bigdata.sat.cfd.v22.schema.Comprobante;
import mx.bigdata.sat.security.KeyLoader;
import mx.bigdata.sat.Pdf.CFDv22Pdf;


/**
 *
 * @author AARON
 */
public class AccionCFD {
    
    private String _key = null;
    private String _contraseniaKey = null;
    private String _cer = null;
    private String _salida = null;
    
    public AccionCFD(String _key, String _contraseniaKey, String _cer, String _salida){
        this._key = _key;
        this._contraseniaKey = _contraseniaKey;
        this._cer = _cer;
        this._salida = _salida;
    }
        
    public void facturar() throws Exception{
        CFDv22 cfd = new CFDv22(ExampleCFDv22Factory.createComprobante());
        PrivateKey key = KeyLoader.loadPKCS8PrivateKey(new FileInputStream(_key), _contraseniaKey);
        X509Certificate cert = KeyLoader.loadX509Certificate(new FileInputStream(_cer));
        Comprobante sellado = cfd.sellarComprobante(key, cert);
        System.err.println(sellado.getSello());
        cfd.validar();
        cfd.verificar();
        System.out.println(_salida);
        cfd.guardar(new FileOutputStream(_salida));
        CFDv22Pdf.crearPdf_static(sellado, cfd.getCadenaOriginal());
        System.out.println("PDF creado");
    }
    
    public void facturar(Comprobante _comprobante) throws Exception{
        CFDv22 cfd = new CFDv22(_comprobante);
        PrivateKey key = KeyLoader.loadPKCS8PrivateKey(new FileInputStream(_key), _contraseniaKey);
        X509Certificate cert = KeyLoader.loadX509Certificate(new FileInputStream(_cer));
        Comprobante sellado = cfd.sellarComprobante(key, cert);
        System.err.println(sellado.getSello());
        cfd.validar();
        cfd.verificar();
        System.out.println(_salida);
        cfd.guardar(new FileOutputStream(_salida));
        System.out.println("XML creado");
        CFDv22Pdf.crearPdf_static(sellado, cfd.getCadenaOriginal());
        System.out.println("PDF creado");
    }
    
    public void testComp() throws Exception{
        this.facturar(ExampleCFDv22Factory.createComprobante());
    }
}
