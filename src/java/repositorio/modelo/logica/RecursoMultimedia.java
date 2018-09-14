/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import repositorio.config.Constantes;
import static repositorio.config.Constantes.MAX_SIZE_ESTADO;
import static repositorio.config.Constantes.MAX_SIZE_FILENAME;
import static repositorio.config.Constantes.MAX_SIZE_PATH;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.Persistencia;
import repositorio.modelo.logica.listItems.RecursoListItem;
import repositorio.modelo.logica.MedioMultimedia.Estados;
import repositorio.modelo.utilidades.Formato;

/**
 * Clase destinada a representar los recursos multimedia. Un recurso representa
 * la enésima conversión de un medio origen. Siendo n el número de perfiles de 
 * conversión.
 * @author Juan Carlos
 */
public class RecursoMultimedia implements Persistencia {
    
    private Integer id;             // Identificador en BD del recurso
    private MediaFile path;         // Archivo representativo del recurso (path)
    private Estados estado;         // Estado del recurso
    private MedioMultimedia medio_origen;  // MedioMultimedia origen
    private PerfilMultimedia perfil;       // Perfil de conversión (Uso futuro)
    
    private Timestamp fecha_creacion;      // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion;  // Fecha de modificación del registro en BD
    
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    
    // Constructor básico
    public RecursoMultimedia(){
        this.id= 0;
        this.path = null;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = null;
        this.perfil = null;
    }
    
    /**
     * Constructor orientado a cargar/actualizar/borrar
     * Campo id: primary key del registro en BD
     * @param id
     */
    public RecursoMultimedia(Integer id) {
        this.id = id;
        this.path = null;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = null;
        this.perfil = null;   
    }
    
    /**
     * Constructor orientado a guardar (1)
     * El parámetro 'path' representa el archivo multimedia.
     * @param path_recurso
     */
    public RecursoMultimedia(MediaFile path_recurso) {
        this.id = 0;
        this.path = path_recurso;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = null;
        this.perfil = null;  
    }
    
    /**
     * Constructor orientado a guardar(2)
     * El parámetro 'fullpath_medio' indica la ruta del archivo multimedia.
     * @param fullpath_medio
     */
    public RecursoMultimedia (String fullpath_medio) {
        this.id = 0;
        this.path = new MediaFile(fullpath_medio);
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = null;
        this.perfil = null;   
    }
    
    // Otros constructores 1 (Con id)
    public RecursoMultimedia (Integer id, String fullpath_medio, MedioMultimedia medio_origen, PerfilMultimedia perfil) {
        this.id = id;
        this.path = new MediaFile(fullpath_medio);
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = medio_origen;
        this.perfil = perfil;   
    }
    public RecursoMultimedia (Integer id, MediaFile path, MedioMultimedia medio_origen, PerfilMultimedia perfil) {
        this.id = id;
        this.path = path;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = medio_origen;
        this.perfil = perfil;   
    }
    // Otros constructores 2 (Sin id)
    public RecursoMultimedia (String fullpath_medio, MedioMultimedia medio_origen, PerfilMultimedia perfil) {
        this.id = 0;
        this.path = new MediaFile(fullpath_medio);
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = medio_origen;
        this.perfil = perfil;   
    }
    public RecursoMultimedia (MediaFile path, MedioMultimedia medio_origen, PerfilMultimedia perfil) {
        this.id = 0;
        this.path = path;
        this.estado = Estados.NUEVA_INSTANCIA;
        this.medio_origen = medio_origen;
        this.perfil = perfil;   
    }
    
    //!
    // Constructor dedicado a la adaptación de un RecursoListItem
    public RecursoMultimedia(RecursoListItem recursoItem){
        this.id= recursoItem.getId();
        this.path = new MediaFile(recursoItem.getPath());
        this.estado = Estados.valueOf(recursoItem.getEstado());
        this.medio_origen = new MedioMultimedia(recursoItem.getMedioId());
        this.perfil = new PerfilMultimedia(recursoItem.getPerfilId(),recursoItem.getPerfilName());
        this.fecha_creacion = recursoItem.getFechaCreacion();
        this.fecha_modificacion = recursoItem.getFechaModificacion();    
    }
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * Ojo! No se utiliza en esta versión. Todos los recursos, tanto en listado como en elemento individual, se representan por la clase RecursoListItem en los servicios REST.
     * @return 
     */
    @Override
    public String toString() {
       
        String toString;
        
        String id_cad = Formato.toJSON(this.getId(),"null");
        String path_cad = Formato.toJSON(this.getMediaFile().getAbsolutePath(),"");
        String estado_cad =  Formato.toJSON(this.getEstado().toString(),"");
        String medioId_cad =  Formato.toJSON(this.getMedioMultimediaOrigen().getId(),"null");
        String perfilId_cad =  Formato.toJSON(this.getPerfilMultimedia().getId(),"null");
        String perfilName_cad =  Formato.toJSON(this.getPerfilMultimedia().getNombre(),"");
        String fechaCreacion_cad =  Formato.toJSON(this.getFechaCreacion(),"");
        String fechaModificacion_cad = Formato.toJSON(this.getFechaModificacion(),"");
        
        toString = "{\"id\":"+id_cad+", \"path\":"+path_cad+", \"estado\":"+estado_cad+", \"medioId\":"+medioId_cad+", \"perfilId\":"+perfilId_cad+", \"perfilName\":"+perfilName_cad+", \"fechaCreacion\":"+fechaCreacion_cad+", \"fechaModificacion\":"+fechaModificacion_cad+"}";

        return toString; 
    }
    
     /* Métodos
      ---------*/
    /**
     * Obtener path completo del recurso
     * Path: dado por el recurso; la carpeta definida para almacenar los medios convertidos.
     * Nombre: dado por el archivo de origen.
     * Extensión: definida por el perfil.
     * @return 
     */
    public String getFullPath_conversion() {
      
        String path_conversion = "";
        if ( path !=null) { // Path completo del archivo generado
            path_conversion = path.getAbsolutePath()+File.separator+this.getFileName_conversion(); 
        }
        return path_conversion;
    }
    
    /**
     * Obtener el nombre completo del recurso
     * Nombre: dado por el archivo de origen. Extra! Añado el id al nombre de archivo para que el archivo en disco sea único.
     * Extensión: definida por el perfil.
     * @return 
     */
    public String getFileName_conversion() {

        String fileName_conversion = "";
        if ( medio_origen != null && perfil != null) { // Filename del archivo generado
            fileName_conversion = "ID"+this.getMedioMultimediaOrigen().getId()+"-"+medio_origen.getMediaFile().getName() +"." + perfil.getFormatoSalida(); 
        }
        return fileName_conversion;
    }
    
    /**
     * Procedimiento para validar los datos antes de guardar en base de datos.
     *
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void validar()throws ExcepcionesRepositorio { 
        
        // Path
        if (this.getFullPath_conversion().length()>Constantes.MAX_SIZE_PATH) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL PATH NO PUEDE EXCEDER DE "+MAX_SIZE_PATH+" CARACTERES.");   
        }
        // Nombre de archivo
        if (this.getFileName_conversion().equals("")) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_LOGICO, "¡EL NOMBRE DE ARCHIVO EN EL PATH NO PUEDE ESTAR VACÍO! Puede que el medio seleccionado no exista.");
        }
        if (this.getFileName_conversion().length()>Constantes.MAX_SIZE_FILENAME) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL NOMBRE DEL MEDIO NO PUEDE EXCEDER DE "+MAX_SIZE_FILENAME+" CARACTERES.");   
        }
        // Estado
        if (String.valueOf(this.estado).length()>Constantes.MAX_SIZE_ESTADO) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL ESTADO DEL MEDIO NO PUEDE EXCEDER DE "+MAX_SIZE_ESTADO+" CARACTERES.");   
        }

    }
    
    /* Métodos de acceso a Base de Datos --> Interfaz Persistencia
       -----------------------------------------------------------*/

    /**
     * 
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
            MediaFile path_tmp = null;
            String estado_bd = "ERROR";
            Integer medio_origen_id;
            MedioMultimedia medio_origen_tmp = null;
            Integer perfil_id;
            PerfilMultimedia perfil_tmp = null;
            Timestamp fecha_creacion_bd = null;
            Timestamp fecha_modificacion_bd = null;

            // Consulta en BD
            String query;
            Statement st;
            ResultSet rs;
            try {
                
                query="SELECT path, estado, medio_id, perfil_id, fecha_creacion, fecha_modificacion FROM recursos WHERE recurso_id="+Formato.toSQL(this.id);  //System.out.println("Sentencia: "+query);
                st = cn.createStatement();
                rs = st.executeQuery(query);
                if(rs.next()) {

                    // Asignar valores temporales
                    path_bd = rs.getString("path");
                    estado_bd = rs.getString("estado");
                    medio_origen_id = (Integer) rs.getInt("medio_id");
                    perfil_id = (Integer) rs.getInt("perfil_id");
                    fecha_creacion_bd = rs.getTimestamp("fecha_creacion");
                    fecha_modificacion_bd = rs.getTimestamp("fecha_modificacion");

                    // Instanciar path asociado
                    path_tmp = new MediaFile(path_bd); // Creo el archivo multimedia a partir del path. El resto de valores van implícitos (filename y extension)

                    // Instanciar medio origen asociado
                    medio_origen_tmp = new MedioMultimedia(medio_origen_id);
                    medio_origen_tmp.cargar(cn); // Opcional (pruebas)

                    // Instanciar perfil asociado (uso futuro)
                    perfil_tmp = new PerfilMultimedia(perfil_id);
                    //perfil_tmp.cargar(cn); // Sin implementar

                    setPersistenciaOK(true); // Carga OK   
                }
                rs.close();
                st.close(); 

                // Asignar valores cargados a los atributos de la clase
                this.path = path_tmp;
                this.estado = Estados.valueOf(estado_bd);
                this.medio_origen = medio_origen_tmp;
                this.perfil = perfil_tmp;
                this.fecha_creacion = fecha_creacion_bd;
                this.fecha_modificacion = fecha_modificacion_bd;

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
        ResultSet rs;
        try {
            // Comprobamos si el medio existe ya en base de datos
            if (!this.existe(cn)){   // No existe: NUEVO MEDIO
                query = "INSERT INTO recursos (filename, path, estado, medio_id, perfil_id) VALUES("+
                        Formato.toSQL(this.getFileName_conversion())+","+
                        Formato.toSQL(this.getFullPath_conversion())+","+
                        Formato.toSQL(this.estado)+","+
                        Formato.toSQL((this.medio_origen!=null)?this.medio_origen.getId():0)+","+ Formato.toSQL((this.perfil!=null)?this.perfil.getId():0)+")";
            }
            else { // Existe: ACTUALIZAR MEDIO 
                query = "UPDATE recursos SET filename = "+Formato.toSQL(this.getFileName_conversion())+", " +
                        "path = "+Formato.toSQL(this.getFullPath_conversion())+", " +
                        "estado = "+Formato.toSQL(this.estado)+", "+
                        "medio_id = "+Formato.toSQL((this.medio_origen!=null)?this.medio_origen.getId():0)+", "+ // *! Cambiar a la forma: Si es nulo no modifico el campo.
                        "perfil_id = "+Formato.toSQL((this.perfil!=null)?this.perfil.getId():0)+" "+
                        "WHERE recurso_id = "+Formato.toSQL(this.id);
            }
            System.out.println("\nBD: Sentencia: "+query);
            st = cn.createStatement();
            st.executeUpdate(query,Statement.RETURN_GENERATED_KEYS); 
            
            // Obtener ID autoincremental del registro insertado (INSERT)
            rs = st.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
                System.out.println("\n+ Id del registro insertado: "+this.id+"\n"); 
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
              
        // Se delega la función en el método análogo de la clase RecursoListItem
        RecursoListItem recurso = new RecursoListItem(getId());
        recurso.borrar(cn,null);
        setPersistenciaOK(recurso.isPersistenciaOK());
        
    }
    
    /* Otros métodos de acceso a Base de Datos
       ---------------------------------------*/
    
    public boolean existe(Connection cn) throws ExcepcionesRepositorio {
        
        boolean flag = false; // Respuesta por defecto
        
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT recurso_id FROM recursos WHERE recurso_id ="+Formato.toSQL(this.id);  //System.out.println("Sentencia: "+query); 
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
        return path;
    }

    public void setMediaFile(MediaFile path) {
        this.path = path;
    }
    
    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public MedioMultimedia getMedioMultimediaOrigen() {
        return medio_origen;
    }

    public void setMedioMultimediaOrigen(MedioMultimedia medio_origen) {
        this.medio_origen = medio_origen;
    }

    public PerfilMultimedia getPerfilMultimedia() {
        return perfil;
    }

    public void setPerfilMultimedia(PerfilMultimedia perfil) {
        this.perfil = perfil;
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
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
}