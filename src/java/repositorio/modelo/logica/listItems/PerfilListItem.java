/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repositorio.modelo.logica.listItems;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.utilidades.Formato;

/**
 * Esta clase representa un PerfilMultimedia en un listado de perfiles.
 * @author Juan Carlos
 */
@XmlRootElement
public class PerfilListItem {
    
     // Parámetros BD
    private Integer id;
    private String nombre;
    private String formatoSalida;
    private Timestamp fecha_creacion;       // Solo para versión BD
    private Timestamp fecha_modificacion;   // Solo para versión BD
    private String uri;                     // URI del perfil que representa el Item
    
    // Constructor vacío
    public PerfilListItem(){    
    }
    
    // Constructor completo

    /**
     * Constructor orientado a perfiles almacenados en archivos .properties. (Versión actual)
     * @param id
     * @param nombre
     * @param formatoSalida
     * @param uri
     */
    public PerfilListItem(Integer id, String nombre, String formatoSalida, String uri) {
    
        this.id= id;
        this.nombre = nombre;
        this.formatoSalida = formatoSalida;
        this.uri = uri;
    }
    
    /**
     * Constructor orientado a perfiles almacenados en base de datos. (Versión futura)
     * @param id
     * @param nombre
     * @param formatoSalida
     * @param fecha_creacion
     * @param fecha_modificacion
     * @param uri
     */
    public PerfilListItem(Integer id, String nombre, String formatoSalida, Timestamp fecha_creacion, Timestamp fecha_modificacion, String uri) {
    
        this.id= id;
        this.nombre= nombre;
        this.formatoSalida = formatoSalida;
        this.fecha_creacion = fecha_creacion;
        this.fecha_modificacion = fecha_modificacion;
        this.uri = uri;
    }

    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return 
     */
    @Override
    public String toString() {
        
        String toString;
        
        String id_cad = Formato.toJSON(getId(),"null");
        String nombre_cad =  Formato.toJSON(getNombre(),"null");
        String formatoSalida_cad =  Formato.toJSON(getFormatoSalida(),"null");
        String uri_cad =  Formato.toJSON(getUri(),"null");

        if(getFechaCreacion()==null) { 
            // Versión .properties (actual)
            toString = "{\"id\":"+id_cad+", \"nombre\":"+nombre_cad+", \"formatoSalida\":"+formatoSalida_cad+", \"uri\":"+uri_cad+"}";
        }       
        else { 
            // Versión Base de Datos (futura)
            String fechaCreacion_cad =  Formato.toJSON(this.getFechaCreacion(),"");
            String fechaModificacion_cad = Formato.toJSON(this.getFechaModificacion(),"");
            toString = "{\"id\":"+id_cad+", \"nombre\":"+nombre_cad+", \"formatoSalida\":"+formatoSalida_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+", \"uri\":"+uri_cad+"}";
        }  
        return toString; 
    }
    
    /**
     * Método estático para cargar únicamente el nombre de un determinado perfil, definido en un archivo .properties
     * @param perfil_id
     * @return
     */
    public static String cargarNombrePerfilFromProperties(Integer perfil_id) {
    
        // Se delega la función en el método análogo de la clase PefilMultimedia
        return PerfilMultimedia.cargarNombrePerfilFromProperties(perfil_id);
    }
    
    // Getters & Seters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormatoSalida() {
        return formatoSalida;
    }

    public void setFormatoSalida(String formatoSalida) {
        this.formatoSalida = formatoSalida;
    }
 
    public Timestamp getFechaCreacion() {
        return fecha_creacion;
    }

    public void setFechaCreacion(Timestamp fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Timestamp getFechaModificacion() {
        return fecha_modificacion;
    }

    public void setFechaModificacion(Timestamp fecha_modificacion) {
        this.fecha_modificacion = fecha_modificacion;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
       
}
