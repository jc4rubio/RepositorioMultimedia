/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.utilidades;

import java.io.File;
import javax.servlet.http.Part;

/**
 * Esta clase contiene algunas de las funciones comodín utilizadas en el resto del proyecto.
 * @author Juan Carlos
 */
public class Utilidades {
    
    /**
     * 
     * @param path
     * @return 
     */
    public static String getFilePath (String path) {
        
        int idx = path.lastIndexOf("/");
        String extension = (idx > 0) ? path.substring(0,idx) : "";
        
        return extension;
    }
    
    
    /**
     * 
     * @param path
     * @return 
     */
    public static String getFileExtension (String path) {
       
        int idx = path.lastIndexOf(".");
        String extension = "";
        // Condiciones: el path contiene "." y no es el último caracter de la cadena.
        if ((idx > 0) && (idx < path.length())) {
            extension = path.substring(idx+1); // Con idx+1 estoy descartando el "." de la extensión.
        }
        return extension;
    }
    
    /**
     * 
     * @param path
     * @return 
     */
    public static String getFileName(String path) {
       
        int idx = path.lastIndexOf(".");
        String filename = (idx > 0) ? path.substring(0,idx) : "";
        
        return filename;
    }
     
    /**
     * Función interna para obtener el nombre del archivo subido.
     * No es un código propio. Forma parte del módulo de subir archivo.
     * Fuente: https://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
     * 
     * He tenido que añadir una modificación para soportar la subida de archivos desde Internet Explorer y Edge.
     * En el campo filename, estos navegadores incluyen la ruta completa, en vez de únicamente el nombre del archivo.
     * @param part
     * @return 
     */
    public static String getFileName(final Part part) {
       
        System.out.println("Obteniendo nombre de archivo...");
        String filename;
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                
                // Substring 1
                System.out.println("content-disposition: "+content);
                filename = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                System.out.println("filename: "+filename);
                
                // En Explorer y en Edge, el campo filename contiene la ruta completa. Es necesario quitar el path y dejar solo el nombre del archivo!
                int index = filename.lastIndexOf(File.separator);
                if (index >0){
                    // Substring 2
                    System.out.println("Ojo! El navegador ha incluido el path completo en el campo filename");
                    filename = filename.substring(index+1);
                    System.out.println("filename*: "+filename);
                }
                return filename;
            }
        }
        return null;
    }
    
    
    /** EN DESUSO
     * Función que comprueba si un array de String contiene un cierto valor.
     * Devuelve true en caso afirmativo.
     * @param arr
     * @param targetValue
     * @return 
     *//*
    public static boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }*/
}
