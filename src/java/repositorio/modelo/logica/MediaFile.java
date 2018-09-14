/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica;

import java.io.File;
import repositorio.modelo.utilidades.Utilidades;

/**
 * Clase heredada de File a la que se le han añadido los campos name y extension.
 * Esto permite separar el atributo name de la clase padre en dos. De este modo es
 * posible obtener directamente el nombre o la extensión de un archivo dado.
 * @author Juan Carlos
 */
public class MediaFile extends File {
   
    private String name;
    private String extension;
    
    // Constructor
    public MediaFile (String fullpath) {
        super(fullpath);
        this.updatePathValues();
    }

    /**
     * Actualiza los valores de nombre y extensión del archivo.
     * - Control de versiones -
     * Version 1.0  Realiza el substring de forma interna.
     * Version 2.0  Hace uso de Utilidades para realizar el substring.
     */
    private void updatePathValues (){
        // Nombre y extensión del medio. Si el path es un directorio el nombre y la extensión serán la cadena vacía.
        String nm = "";
        String ext = "";
        if (this.isFile()) { // El medio es un archivo
            nm = Utilidades.getFileName(super.getName());       // Nombre del archivo
            ext = Utilidades.getFileExtension(super.getName()); // Extensión del archivo
        }
        this.name = nm;
        this.extension = ext;
    }
    
    /* Getters & Setters
      ------------------*/ 
      
    /**
     * Devuelve el nombre completo del archivo (incluyendo  la extensión)
     * Equivale al método getName() de la clase padre File.
     * @return
     */
    public String getFullName() {
        return super.getName();
    } 

   /**
     * Devuelve el nombre del archivo (sin  la extensión)
     * Método sobrescrito para evitar incluir la extensión del archivo en el nombre.
     * Si no existe devuelve "".
     * @return
     */
    @Override
    public String getName() {
        return name;
    } 

    /**
     * Devuelve la extensión del archivo. 
     * Si no existe devuelve "".
     * @return
     */
    public String getExtension() {
        return extension;
    }
    
}
