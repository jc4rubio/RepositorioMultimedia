/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.listItems;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.PersistenciaREST;
import repositorio.modelo.logica.MediaFile;
import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.RecursoMultimedia;
import repositorio.modelo.utilidades.Formato;

/**
 * Esta clase representa un elemento RecursoListItem en un listado de recursos.
 * @author Juan Carlos
 */
@XmlRootElement
public class RecursoListItem implements PersistenciaREST { 
    
    private Integer id;         // Identificador en BD del recurso
    private String path;        // Ruta representativa del recurso (path)
    private String estado;      // Estado
    private Integer medio_id;   // Id del MedioMultimedia origen
    private Integer perfil_id;  // Id del Perfil de conversión
    private String perfil_name; // Nombre del perfil -> Agregado para incorporar este campo en el JSON. 
    private Timestamp fecha_creacion;       // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion;   // Fecha de modificación del registro en BD
    private String uri_recurso;     // URI correspondiente al recurso en sí. Como elemento único del listado. -> RecursoItem
    private String uriMultimedia;   // URI del servlet que da acceso a la reproducción del recurso
    
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    

    /* Constructores
       -------------*/
    
    // Constructor vacío
    public RecursoListItem(){};
    
    // Constructor con id
    public RecursoListItem(Integer id){
        this.id = id;
    }

    // Constructor completo
    public RecursoListItem(Integer id, String path, String estado,Integer medio_id, Integer perfil_id, String perfil_name, Timestamp fecha_creacion, Timestamp fecha_modificacion, String uri_recurso, String uri_multimedia){
        this.id= id;
        this.path = path;
        this.estado = estado;
        this.medio_id = medio_id; 
        this.perfil_id = perfil_id;
        this.perfil_name = perfil_name;
        this.fecha_creacion = fecha_creacion;
        this.fecha_modificacion = fecha_modificacion;
        this.uri_recurso = uri_recurso;
        this.uriMultimedia = uri_multimedia;
    }

    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return 
     */
    @Override
    public String toString() {
       
        String toString;
        
        String id_cad = Formato.toJSON(this.getId(),"null");
        String path_cad = Formato.toJSON(this.getPath(),"");
        String estado_cad =  Formato.toJSON(this.getEstado(),"");
        String medioId_cad =  Formato.toJSON(this.getMedioId(),"null");
        String perfilId_cad =  Formato.toJSON(this.getPerfilId(),"null");
        String perfilName_cad =  Formato.toJSON(this.getPerfilName(),"");
        String fechaCreacion_cad =  Formato.toJSON(this.getFechaCreacion(),"");
        String fechaModificacion_cad = Formato.toJSON(this.getFechaModificacion(),"");
        String uriRecurso_cad =  Formato.toJSON(this.getUriRecurso(),"null");
        String uriMultimedia_cad =  Formato.toJSON(this.getUriMultimedia(),"null");
        
        toString = "{\"id\":"+id_cad+", \"path\":"+path_cad+", \"estado\":"+estado_cad+", \"medioId\":"+medioId_cad+", \"perfilId\":"+perfilId_cad+", \"perfilName\":"+perfilName_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+", \"uriRecurso\":"+uriRecurso_cad+", \"uriMultimedia\":"+uriMultimedia_cad+"}";
      
        return toString; 
    }

    
    /* Métodos de acceso a Base de Datos -> Interfaz PersistenciaREST
       --------------------------------------------------------------*/
      
    @Override
    public void cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
       
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Consulta en BD
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT path, estado, medio_id, perfil_id, fecha_creacion, fecha_modificacion FROM recursos WHERE recurso_id="+Formato.toSQL(getId());  //System.out.println("Sentencia: "+query);
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {
                setPath(rs.getString("path"));
                setEstado(rs.getString("estado"));
                setMedioId((Integer) rs.getInt("medio_id"));
                setPerfilId(rs.getInt("perfil_id"));
                setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                setFechaModificacion(rs.getTimestamp("fecha_modificacion"));

                // Nombre de perfil
                setPerfilName(PerfilMultimedia.cargarNombrePerfilFromProperties(getPerfilId()));

                // URIs del recurso
                if (context != null) {
                    setUriRecurso(context.getBaseUri().toString() + "recurso/" + getId());             // URI del item
                    setUriMultimedia(context.getBaseUri().toString() + "media?recurso=" + getId());    // URI correspondiente al contenido multimedia del recurso (Servlet pasarela)
                }

                setPersistenciaOK(true); // Carga OK    
            }
            rs.close();
            st.close();
        }
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
        }
    }
    
    // Método no implementado
    @Override
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
    
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        System.out.println("=> Borrando recurso: "+getId());
        

        // Comprobacion previa: Existe el recurso en BD
        if (existe(cn)) {
            
            // Paso 1. Borrar archivo del disco
            MediaFile recurso_mf = new MediaFile(getPath());
            recurso_mf.delete();
            System.out.println("-> Archivo "+getPath()+" borrado");
            
            // Paso 2. Borrar elemento en base de datos
            String sentencia="DELETE FROM recursos WHERE recurso_id="+Formato.toSQL(getId());  //System.out.println("Sentencia: "+query);
            try (Statement st = cn.createStatement()) {
                st.executeUpdate(sentencia);
                st.close();
                setPersistenciaOK(true); // Delete OK 
            }
            catch (SQLException e) {
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al borrar registro en BD: "+e.getMessage(), e);
            }
        }
    }
    /* Otros métodos de acceso a Base de Datos
       ---------------------------------------*/
    public boolean existe(Connection cn) throws ExcepcionesRepositorio {
        
        // Se delega la función en el método análogo de la clase RecursoMultimedia
        RecursoMultimedia recurso = new RecursoMultimedia(getId());
        return recurso.existe(cn);
 
    }

    /* Getters & Setters
      ------------------*/ 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getMedioId() {
        return medio_id;
    }

    public void setMedioId(Integer medio_id) {
        this.medio_id = medio_id;
    }

    public Integer getPerfilId() {
        return perfil_id;
    }

    public void setPerfilId(Integer perfil_id) {
        this.perfil_id = perfil_id;
    }
    
    public String getPerfilName() {
        return perfil_name;
    }

    public void setPerfilName(String perfil_name) {
        this.perfil_name = perfil_name;
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

    public String getUriRecurso() {
        return uri_recurso;
    }

    public void setUriRecurso(String uri) {
        this.uri_recurso = uri;
    }
    
    public String getUriMultimedia() {
        return uriMultimedia;
    }

    public void setUriMultimedia(String uri) {
        this.uriMultimedia = uri;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
    
}