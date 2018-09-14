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
import repositorio.modelo.logica.MedioMultimedia;
import repositorio.modelo.logica.MedioMultimedia.Estados;
import repositorio.modelo.logica.lists.RecursosList;
import repositorio.modelo.utilidades.Formato;

/**
 * Esta clase representa un elemento MedioListItem en un listado de medios.
 * @author Juan Carlos
 */
@XmlRootElement
public class MedioListItem implements PersistenciaREST { 
    
    private Integer id;         // Identificador en BD del registro
    private String titulo;      // Titulo del medio
    private Estados estado;     // Estado del medio
    private Integer usuario_id; // Propietario del medio (id)
    private String usuario_nick; // Propietario del medio (id)
    private Timestamp fecha_creacion;     // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion; // Fecha de modificación del registro en BD
    private String uri_medio;             // URI correspondiente al medio en sí. Como elemento único del listado. -> MedioItem
    private String uri_recursos;          // URI correspondiente al listado de recursos asociado al medio. Ojo! Listado de recursos, no al medio en sí. El medio como tal no aporta más información a la parte cliente.
 
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
     /* Constructores
       --------------*/
    // Constructor vacío
    public MedioListItem(){};
    
    // Constructor con id
    public MedioListItem(Integer id){
        this.id = id;
    }
    
    // Constructor completo
    public MedioListItem(Integer id, String titulo, Estados estado,Integer usuario_id, String usuario_nick, Timestamp fecha_creacion, Timestamp fecha_modificacion, String uri_medio, String uri_recursos){
        this.id = id;
        this.titulo = titulo;
        this.estado = estado;
        this.usuario_id = usuario_id; 
        this.usuario_nick = usuario_nick; 
        this.fecha_creacion = fecha_creacion;
        this.fecha_modificacion = fecha_modificacion;
        this.uri_medio = uri_medio;
        this.uri_recursos = uri_recursos;
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
        String titulo_cad =  Formato.toJSON(getTitulo(),"");
        String estado_cad =  Formato.toJSON(getEstado().toString(),"");
        String usuarioId_cad = Formato.toJSON(getUsuarioId(),"null");
        String usuarioNick_cad = Formato.toJSON(getUsuarioNick(),"");
        String fechaCreacion_cad =  Formato.toJSON(getFechaCreacion(),"");
        String fechaModificacion_cad = Formato.toJSON(getFechaModificacion(),"");
        String uriMedio_cad = Formato.toJSON(getUriMedio(),"");
        String uriRecursos_cad = Formato.toJSON(getUriRecursos(),"");
      
        toString = "{\"id\":"+id_cad+", \"titulo\":"+titulo_cad+", \"estado\":"+estado_cad+", \"usuarioId\":"+usuarioId_cad+", \"usuarioNick\":"+usuarioNick_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+", \"uriMedio\":"+uriMedio_cad+", \"uriRecursos\":"+uriRecursos_cad+"}";
     
        return toString; 
    }
    
    /* Métodos de acceso a Base de Datos -> Interfaz PersistenciaREST
       --------------------------------------------------------------*/
      
    /**
     * Método cargar.
     * 
     * @param cn
     * @param context
     * @throws ExcepcionesRepositorio
     */
    @Override
    public void cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
       
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Consulta en BD
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT m.medio_id, m.titulo, m.estado, m.usuario_id, u.username, m.fecha_creacion, m.fecha_modificacion FROM medios m left join usuarios u on u.usuario_id = m.usuario_id where m.medio_id="+Formato.toSQL(this.id);
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {

                setId((Integer) rs.getInt("medio_id"));
                setTitulo(rs.getString("titulo"));
                setEstado(Estados.valueOf(rs.getString("estado")));
                setUsuarioId((Integer) rs.getInt("usuario_id"));
                setUsuarioNick(rs.getString("username"));
                setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                setFechaModificacion(rs.getTimestamp("fecha_modificacion"));

                // URIs del medio
                if (context != null){
                    setUriMedio(context.getBaseUri().toString() + "medio/" + getId()); // Crear URI del item
                    setUriRecursos(context.getBaseUri().toString() + "recursos/" + getId()); // Crear URI de los recursos
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
    
    // Métodos de la interfaz PersistenciaREST
    @Override
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio { 
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        System.out.println("=> Borrando medio "+getId());
        
        // Comprobacion previa: Existe el medio en BD
        if (existe(cn)) {
 
            // Ojo! Antes de borrar en base de datos hay que borrar los archivos (recursos) del disco. => Hecho
                 
            // 1. Borrar lista de recursos asociada al medio
            RecursosList lista_recursos = new RecursosList(getId());
            lista_recursos.borrar(cn, context);

            // 2. Borrar medio en BD
            String query;
            Statement st;
            try {   

                query= "DELETE FROM medios WHERE medio_id="+Formato.toSQL(this.id); //System.out.println("Sentencia: "+query);
                st = cn.createStatement();
                st.executeUpdate(query);
                st.close();
                
                setPersistenciaOK(true); // Borrado OK   

            }
            catch(SQLException e) {
                throw new  ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS,"Error al borrar el medio "+this.getId()+" de BD: \n"+e.getMessage(),e);   
            }
        }
    }
    
    /* Otros métodos de acceso a Base de Datos
     ---------------------------------------*/
    
    public boolean existe(Connection cn) throws ExcepcionesRepositorio {
        
         // Se delega la función en el método análogo de la clase MedioMultimedia
        MedioMultimedia medio = new MedioMultimedia(getId());
        return medio.existe(cn);
 
    }


    /* Getters & Setters
      ------------------*/ 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public Integer getUsuarioId() {
        return usuario_id;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuario_id = usuarioId;
    }
    
    public String getUsuarioNick() {
        return usuario_nick;
    }

    public void setUsuarioNick(String usuario_nick) {
        this.usuario_nick = usuario_nick;
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
    
    public String getUriMedio() {
        return uri_medio;
    }

    public void setUriMedio(String uri) {
        this.uri_medio = uri;
    }
    
    public String getUriRecursos() {
        return uri_recursos;
    }

    public void setUriRecursos(String uri_recursos) {
        this.uri_recursos = uri_recursos;
    }

    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
    
}