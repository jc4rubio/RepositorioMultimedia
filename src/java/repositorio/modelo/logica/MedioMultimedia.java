/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.config.Constantes;
import static repositorio.config.Constantes.MAX_SIZE_ESTADO;
import static repositorio.config.Constantes.MAX_SIZE_FILENAME;
import static repositorio.config.Constantes.MAX_SIZE_PATH;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.Persistencia;
import repositorio.modelo.logica.listItems.MedioListItem;
import repositorio.modelo.utilidades.Formato;

/**
 * Esta clase representa un medio multimedia: audio o video.
 * @author Juan Carlos
 */
@XmlRootElement
public class MedioMultimedia implements Persistencia { 
    
    private Integer id;         // Identificador en BD del registro
    private MediaFile mediaFile;// Archivo representativo del medio
    private String titulo;      // Titulo del medio
    private Estados estado;     // Estado del medio
    private Usuario usuario;    // Propietario del medio
    
    private Timestamp fecha_creacion;       // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion;   // Fecha de modificación del registro en BD  

    public enum Estados {NUEVA_INSTANCIA, EN_COLA, PROCESANDO, OK, ERROR}; // Posibles estados del procesamiento del medio

    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    
    // Constructor básico
    public MedioMultimedia(){
        this.id= 0;
        this.mediaFile = null;
        this.titulo = "";
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = null;
    }
    
    /**
     * Constructor orientado a cargar/actualizar/borrar
     * Campo id: primary key del registro en BD
     * @param id
     */
    public MedioMultimedia(Integer id) {
        this.id = id;
        this.mediaFile = null;
        this.titulo = "";
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = null;   
    }
    
    /**
     * Constructor orientado a guardar (1)
     * El parámetro 'mediaFile' representa el archivo multimedia.
     * @param mediaFile
     */
    public MedioMultimedia(MediaFile mediaFile) {
        this.id = 0;
        this.mediaFile = mediaFile;
        this.titulo = "";
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = null;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    
    /**
     * Constructor orientado a guardar(2)
     * El parámetro 'fullpath_medio' indica la ruta del archivo multimedia.
     * @param fullpath_medio
     */
    public MedioMultimedia (String fullpath_medio) {
        this.id = 0;
        this.mediaFile = new MediaFile(fullpath_medio);
        this.titulo = "";
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = null;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    
    // Otros constructores 1 (Con id)
    public MedioMultimedia (Integer id, String fullpath_medio, String titulo, Usuario usuario) {
        this.id = id;
        this.mediaFile = new MediaFile(fullpath_medio);
        this.titulo = titulo;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = usuario;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    public MedioMultimedia (Integer id, MediaFile mediaFile, String titulo, Usuario usuario) {
        this.id = id;
        this.mediaFile = mediaFile;
        this.titulo = titulo;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = usuario;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    // Otros constructores 2 (Sin id)
    public MedioMultimedia (String fullpath_medio, String titulo, Usuario usuario) {
        this.id = 0;
        this.mediaFile = new MediaFile(fullpath_medio);
        this.titulo = titulo;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = usuario;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    public MedioMultimedia (MediaFile mediaFile, String titulo, Usuario usuario) {
        this.id = 0;
        this.mediaFile = mediaFile;
        this.titulo = titulo;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.usuario = usuario;
        this.fecha_creacion = null;
        this.fecha_modificacion = null;
    }
    
    // Constructor completo
    public MedioMultimedia (Integer id, MediaFile mediaFile, String titulo, Estados estado, Usuario usuario, Timestamp fechaCreacion, Timestamp fechaModificacion) {
        this.id = id;
        this.mediaFile = mediaFile;
        this.titulo = titulo;
        this.estado = estado;
        this.usuario = usuario;
        this.fecha_creacion = fechaCreacion;
        this.fecha_modificacion = fechaModificacion;
    }
    
    /* toString() -> JSON
      -------------------*/
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON/JACKSON.
     * Ojo! No se utiliza en esta versión. Todos los medios, tanto en listado como en elemento individual, se representan por la clase MedioListItem en los servicios REST.
     * @return 
     */
    @Override
    public String toString() {

        String toString;
        
        String id_cad = Formato.toJSON(this.getId(),"null");
        String mediaFile_cad = Formato.toJSON(this.getMediaFile().getAbsolutePath(),"");
        String titulo_cad =  Formato.toJSON(this.getTitulo(),"");
        String estado_cad =  Formato.toJSON(this.getEstado().toString(),"");
        String usuario_cad = Formato.toJSON(this.getUsuario().toString(),"");
        String fechaCreacion_cad =  Formato.toJSON(this.getFechaCreacion(),"");
        String fechaModificacion_cad = Formato.toJSON(this.getFechaModificacion(),"");
        
        toString = "{\"id\":"+id_cad+", \"mediaFile\":"+mediaFile_cad+", \"titulo\":"+titulo_cad+", \"estado\":"+estado_cad+", \"usuario\":"+usuario_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+"}"; // Sin uri
        
        return toString;
    }
    
    /**
     * Procedimiento para validar los datos antes de guardar en base de datos.
     *
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void validar()throws ExcepcionesRepositorio { 
        
        if (this.mediaFile.getAbsolutePath().length()>Constantes.MAX_SIZE_PATH) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL PATH NO PUEDE EXCEDER DE "+MAX_SIZE_PATH+" CARACTERES.");   
        }
        if (this.mediaFile.getFullName().equals("")) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_LOGICO, "¡EL NOMBRE DE ARCHIVO EN EL PATH NO PUEDE ESTAR VACÍO! Puede que el medio seleccionado no exista.");
        }
        if (this.mediaFile.getFullName().length()>Constantes.MAX_SIZE_FILENAME) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL NOMBRE DEL MEDIO NO PUEDE EXCEDER DE "+MAX_SIZE_FILENAME+" CARACTERES.");   
        }
        if (String.valueOf(this.estado).length()>Constantes.MAX_SIZE_ESTADO) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL ESTADO DEL MEDIO NO PUEDE EXCEDER DE "+MAX_SIZE_ESTADO+" CARACTERES.");   
        }
        if (String.valueOf(this.titulo).length()>Constantes.MAX_SIZE_TITULO) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL ESTADO DEL MEDIO NO PUEDE EXCEDER DE "+MAX_SIZE_ESTADO+" CARACTERES.");   
        }

    }

    /* Métodos de acceso a Base de Datos --> Interfaz Persistencia
       -----------------------------------------------------------*/
  
    /**
     * - Control de versiones -
     * Version 1. Carga del medio a partir del campo único 'filename'. El problema es que no es único.
     * Revisión: Elimino el método cargarId de la interfaz. No lo implemento para esta clase, ya que no hay un campo característico diferente del id.
     * Version 2. Carga a partir del id.
     * @param cn
     * @throws ExcepcionesRepositorio 
     */
    @Override
    public void cargar(Connection cn) throws ExcepcionesRepositorio {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Condición para suponer que el registro existe en BD
        if (this.id > 0) {
            
            // Variables a cargar y valores por defecto
            String path_bd;
            MediaFile mediaFile_tmp = null;
            String estado_bd = "";
            String titulo_bd = "";
            Integer usuario_id;
            Usuario usuario_tmp = null;
            // boolean usuario_ok = false;
            Timestamp fechaCreacion_bd = null;
            Timestamp fechaModificacion_bd = null;
            
            // Consulta en BD
            String query;
            Statement st;
            ResultSet rs;
            try {

                query="SELECT path, estado, titulo, usuario_id, fecha_creacion, fecha_modificacion FROM medios WHERE medio_id="+Formato.toSQL(this.id);  //System.out.println("Sentencia: "+query);
                st = cn.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()) {
                        
                    path_bd = rs.getString("path");
                    estado_bd = rs.getString("estado");
                    titulo_bd = rs.getString("titulo");
                    usuario_id = (Integer) rs.getInt("usuario_id");
                    fechaCreacion_bd = rs.getTimestamp("fecha_creacion");
                    fechaModificacion_bd = rs.getTimestamp("fecha_modificacion");

                    // Instanciar medio asociado
                    mediaFile_tmp = new MediaFile(path_bd); // Creo el medio a partir del path. El resto de valores van implícitos (filename y extension)

                    // Cargar usuario asociado -> *DUDA: Cargar el usuario completo o solo el ID?
                    usuario_tmp = new Usuario(usuario_id);
                    //usuario_ok = usuario_tmp.cargar(cn); // Comentar esta linea si no quiero cargar todo el usuario.

                    setPersistenciaOK(true); // Carga OK   
                }
                rs.close();
                st.close();

                // Asignar valores.
                this.mediaFile = mediaFile_tmp;
                this.titulo = titulo_bd;
                this.estado = Estados.valueOf(estado_bd);
                //if(usuario_ok) {
                    // Podría asignar el usuario aunque la carga no sea válida. Para no mantener el usuario anterior, previo a la carga.
                    this.usuario = usuario_tmp;
                //}  
                this.fecha_creacion = fechaCreacion_bd;
                this.fecha_modificacion = fechaModificacion_bd;
                   
            }
            catch (SQLException e) {
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
            }
       }       
    }

    @Override
    public void guardar(Connection cn) throws ExcepcionesRepositorio {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Validar los datos antes de continuar
        this.validar();
        
        // Acceso a BD
        String query;
        Statement st;
        try {
            // Comprobamos si el medio existe ya en base de datos
            if (!this.existe(cn)){   // No existe: NUEVO MEDIO
                query = "INSERT INTO medios (filename, path, estado, titulo, usuario_id) VALUES("+
                        Formato.toSQL(this.mediaFile.getFullName())+","+
                        Formato.toSQL(this.mediaFile.getAbsolutePath())+","+
                        Formato.toSQL(this.estado)+","+
                        Formato.toSQL(this.titulo)+","+
                        Formato.toSQL((this.usuario!=null)?this.usuario.getId():0)+")";
            }
            else { // Existe: ACTUALIZAR MEDIO 
                query = "UPDATE medios SET filename = "+Formato.toSQL(this.mediaFile.getFullName())+", " +
                        "path = "+Formato.toSQL(this.mediaFile.getAbsolutePath())+", "+
                        "estado = "+Formato.toSQL(this.estado)+", "+
                        "titulo = "+Formato.toSQL(this.titulo)+", "+
                        "usuario_id = "+Formato.toSQL((this.usuario!=null)?this.usuario.getId():0)+" "+
                        "WHERE medio_id = "+Formato.toSQL(this.id);
            }
            System.out.println("Sentencia: "+query);
            st = cn.createStatement();
            st.executeUpdate(query,Statement.RETURN_GENERATED_KEYS); 
            
            // Obtener ID autoincremental del registro insertado (UPDATE)
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                setId((Integer) rs.getInt(1));
                System.out.println("\n+ Id del registro insertado: "+getId()+"\n");
                setPersistenciaOK(true); // Guardado OK   
            }
            rs.close();
            st.close();
       }
       catch (SQLException e) {  
            throw new  ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al guardar datos en BD: "+e.getMessage(), e);   
       } 
    }

    @Override
    public void borrar(Connection cn) throws ExcepcionesRepositorio {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
                
        // Se delega la función en el método análogo de la clase MedioListItem
        MedioListItem medio = new MedioListItem(getId());
        medio.borrar(cn,null);
        setPersistenciaOK(medio.isPersistenciaOK());
        
    }
    
    /* Otros métodos de acceso a Base de Datos
       ---------------------------------------*/
    
    public boolean existe(Connection cn) throws ExcepcionesRepositorio {
        
        boolean flag = false; // Respuesta por defecto
        
        // Acceso a BD
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT medio_id FROM medios WHERE medio_id ="+Formato.toSQL(this.id);  //System.out.println("Sentencia: "+query); 
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {
                flag = true; // Existe
            }
            rs.close();
            st.close();
            
            return flag;
        }           
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
        }   
    }
    
     /* Getters & Setters
      ------------------*/ 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public MediaFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MediaFile mediaFile) {
        this.mediaFile = mediaFile;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFechaCreacion() {
        return fecha_creacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fecha_creacion = fechaCreacion;
    }

    public Timestamp getFechaModificacion() {
        return fecha_modificacion;
    }

    public void setFechaModificacion(Timestamp fechaModificacion) {
        this.fecha_modificacion = fechaModificacion;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }   
}
