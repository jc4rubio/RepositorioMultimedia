/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.listItems;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.modelo.utilidades.Formato;

/**
 * Esta clase representa un elemento Usuario en un listado de usuarios.
 * @author Juan Carlos
 */
@XmlRootElement
public class UsuarioListItem { 
    
    private Integer id;                     // Identificador en Bd del usuario (Bd: primary key)
    private String username;                // Identificador del usuario en la aplicación (Bd: unique)
    private Timestamp fecha_creacion;       // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion;   // Fecha de la última modificación del registro en BD
    private String uri;                     // URI del usuario que representa el Item
   
    /**
     * Constructor vacío.
     * Necesario para el uso de la librería JAXB
     */
    public UsuarioListItem() {}
    
    /**
     * Constructor completo.
     * 
     * @param id
     * @param username
     * @param fecha_creacion
     * @param fecha_modificacion
     * @param uri
     */
    public UsuarioListItem(Integer id, String username, Timestamp fecha_creacion, Timestamp fecha_modificacion, String uri){
        this.id= id;
        this.username = username;
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
        String username_cad =  Formato.toJSON(getUsername(),"null");
        String fechaCreacion_cad =  Formato.toJSON(getFechaCreacion(),"");
        String fechaModificacion_cad = Formato.toJSON(getFechaModificacion(),"");
        String uri_cad =  Formato.toJSON(getUri(),"null");
   
        toString = "{\"id\":"+id_cad+", \"username\":"+username_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+", \"uri\":"+uri_cad+"}";
       
        return toString; 
    }
    

    /* Getters & Setters
      ------------------*/ 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
    
}
