/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


/**
 * Clase orientada a ejecutar la línea de comandos de windows y leer la salida.
 * Fuente: http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html
 * @author -
 */
public class StreamGobbler extends Thread {

    private InputStream is;
    private String type;
    private OutputStream os;
    
    public StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }
    
    public StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }

    @Override
    public void run() {
        try { 
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ( (line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
                System.out.println(type + ">" + line); 
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());  
        }
    }
}

