/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica;

import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.Persistencia;
import repositorio.modelo.utilidades.Formato;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase representativa del usuario gestor del repositorio multimedia.
 * - Control de versiones -
 * Version 1.0 8/11/2016
 * Version 2.0 29/11/2016
 * @author Juan Carlos
 */

//@SuppressWarnings("JPQLValidation")
@XmlRootElement
public class Usuario implements Persistencia {

    private Integer id;                     // Identificador en Bd del usuario (Bd: primary key)
    private String username;                // Identificador 1 del usuario en la aplicación (Bd: unique)
    private String password;   
    private String nombre;   
    private String apellidos;
    private Date birthdate;
    private String correo;                  // Identificador 2 del usuario en la aplicación (Bd: unique)
    private Integer telefono;   
    private String genero;
    private Timestamp fecha_creacion;       // Fecha de creación del registro en BD
    private Timestamp fecha_modificacion;   // Fecha de modificación del registro en BD
   
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    
    // Tamaño de datos en BD
    private static final int MAX_SIZE_USERNAME=50;
    private static final int MAX_SIZE_PASSWORD=50;
    private static final int MAX_SIZE_NOMBRE=50;
    private static final int MAX_SIZE_APELLIDOS=50;
    private static final int MAX_SIZE_CORREO=100;
 
    /* Constructores
       -------------*/
    
    // Constructor vacío
    public Usuario() {
        this.telefono = null; 
    }

    public Usuario(Integer id) {
        this.id = id;
        this.telefono = null;
    }

    public Usuario(Integer id, String username) {
        this.id = id;
        this.username = username;
        this.telefono = null;
    }
    
    public Usuario(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.telefono = null;
    }
    
    public Usuario(String username) {
        this.username = username;
        this.telefono = null;
    }
    
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
        this.telefono = null;
    }
    
    public Usuario(String username, String password, String correo) {
        this.username = username;
        this.password = password;
        this.correo = correo;
        this.telefono = null;
    }
    
    // Constructor completo
    public Usuario(String username, String password, String nombre, String apellidos, Date birthdate, String correo, Integer telefono, String genero) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.birthdate = birthdate;
        this.correo = correo;
        this.telefono = telefono;
        this.genero = genero;
    } 
    
    // toString() -> JSON
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON/JAXB.
     * @return 
     */
    @Override
    public String toString() {

        //telefono = null;
        String toString;
        String id_cad = (this.id != null)?this.id.toString():"null";
        String username_cad = (this.username != null)?this.username:"null";
        String password_cad= (this.password != null)?this.password:"null";
        String nombre_cad = (this.nombre != null)?this.nombre:"null";
        String apellidos_cad = (this.apellidos != null)?this.apellidos:"null";
        String birthdate_cad = (this.birthdate != null)?this.birthdate.toString():"null";
        String correo_cad = (this.correo != null)?this.correo:"null";
        String telefono_cad = (this.telefono != null)?this.telefono.toString():"null";
        String genero_cad = (this.genero != null)?this.genero:"null";
        String fechaCreacion_cad = (fecha_creacion != null)?this.fecha_creacion.toString().substring(0, this.fecha_creacion.toString().length()-2):"null";
        String fechaModificacion_cad = (fecha_modificacion != null)?this.fecha_modificacion.toString().substring(0, this.fecha_modificacion.toString().length()-2):"null";

        toString = "{\"id\":"+id_cad+", \"username\":\""+username_cad+"\", \"password\":\""+password_cad+"\", \"nombre\":\""+nombre_cad+"\", \"apellidos\":\""+apellidos_cad+"\", \"birthdate\":\""+birthdate_cad+"\", \"correo\":\""+correo_cad+"\", \"telefono\":"+telefono_cad+", \"genero\":\""+genero_cad+"\", \"fechaCreacion\":\""+fechaCreacion_cad+"\", \"fechaModificacion\":\""+fechaModificacion_cad+"\"}"; // Sin uri
        
        return toString;
    }

   
    /**
     * Procedimiento para validar los datos antes de guardar en base de datos.
     *
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void validar()throws ExcepcionesRepositorio
    {    
        if (this.username.length()>MAX_SIZE_USERNAME) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL USERNAME NO PUEDE EXCEDER DE "+MAX_SIZE_NOMBRE+" CARACTERES.");   
        }
        if (this.username.equals("")) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL CAMPO USERNAME NO PUEDE ESTAR EN BLANCO!");
        }
        // Resto de validaciones...
        if (this.password.length()>MAX_SIZE_PASSWORD) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "LA PASSWORD NO PUEDE EXCEDER DE "+MAX_SIZE_PASSWORD+" CARACTERES.");   
        }
        if (this.nombre.length()>MAX_SIZE_NOMBRE) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL NOMBRE NO PUEDE EXCEDER DE "+MAX_SIZE_NOMBRE+" CARACTERES.");   
        }
        if (this.apellidos.length()>MAX_SIZE_APELLIDOS) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "LOS APELLIDOS NO PUEDEN EXCEDER DE "+MAX_SIZE_APELLIDOS+" CARACTERES.");   
        }
        if (this.correo.length()>MAX_SIZE_CORREO) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_TAMANYO_DATOS, "EL CORREO NO PUEDE EXCEDER DE "+MAX_SIZE_CORREO+" CARACTERES.");   
        }

    }

    /* Métodos de acceso a Base de Datos -> Interfaz Persistencia
       ----------------------------------------------------------*/
 
    /**
     * Carga el usuario a partir del campo "user".
     * Devuelve TRUE si la carga es correcta.
     * Ojo! Queda pendiente si modificar el código para que la consulta sea a partir del id, utilizando previamente el método cargarID.
     * Es un rodeo pero se ciñe a la "política" de acceso definida para operar con la base de datos.
     * @param cn
     * @throws ExcepcionesRepositorio 
     */
    @Override
    public void cargar(Connection cn) throws ExcepcionesRepositorio {
       
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Acceso a BD
        String query;
        Statement st;
        ResultSet rs;
        try {
            
            query="Select * from USUARIOS where usuario_id="+Formato.toSQL(this.id);  //System.out.println("Sentencia: "+query);
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {

                //this.id = (Integer) rs.getInt("usuario_id"); //  El id ya está pre-establecido antes de cargar.
                this.username = rs.getString("username");
                this.password = rs.getString("password");
                this.nombre = rs.getString("nombre");
                this.apellidos = rs.getString("apellidos");
                this.birthdate = (Date) rs.getDate("birthdate");
                this.correo = rs.getString("correo");
                this.telefono = (Integer) rs.getInt("telefono");
                if(rs.wasNull()) {
                    this.telefono = null; // Si un int es nulo, ResulSet devuelve 0. Esta sentencia corrige este funcionamiento.
                } 
                this.genero = rs.getString("genero");
                this.fecha_creacion = rs.getTimestamp("fecha_creacion");
                this.fecha_modificacion = rs.getTimestamp("fecha_creacion");

                setPersistenciaOK(true); // Carga OK   
            }
            rs.close();
            st.close();
        }
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
       }       
    }
    
    /*
     * Guarda o actualiza el usuario en base de datos, dependiendo si éste existe o no previamente.
     * Utiliza el campo id como identificador.
     * @param cn
     * @throws ExcepcionesRepositorio 
     */
    @Override
    public void guardar(Connection cn) throws ExcepcionesRepositorio {
       
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Validar los datos antes de continuar
        this.validar();
        
        // Acceso a BD
        String query;
        Statement st;
        Integer usuario_id;
        try {
            // Comprobamos si el usuario existe ya en base de datos
            usuario_id = this.cargarID(cn); 
            if (usuario_id<1) { // No existe: NUEVO USUARIO

                query = "INSERT INTO usuarios (username, password, nombre, apellidos, birthdate, correo, telefono, genero) VALUES("+
                        Formato.toSQL(this.username)+","+
                        Formato.toSQL(this.password)+","+
                        Formato.toSQL(this.nombre)+","+
                        Formato.toSQL(this.apellidos)+","+
                        Formato.toSQL(this.birthdate)+","+
                        Formato.toSQL(this.correo)+","+
                        Formato.toSQL(this.telefono)+","+
                        Formato.toSQL(this.genero)+")";

            }
            else { // Existe: ACTUALIZAR USUARIO

                query = "UPDATE usuarios SET username = "+Formato.toSQL(this.username)+", " +
                        "password = "+Formato.toSQL(this.password)+", "+
                        "nombre = "+Formato.toSQL(this.nombre)+", "+
                        "apellidos = "+Formato.toSQL(this.apellidos)+", " +
                        "birthdate = "+Formato.toSQL(this.birthdate)+", " +
                        "correo = "+Formato.toSQL(this.correo)+", " +
                        "telefono = "+Formato.toSQL(this.telefono)+", " +
                        "genero = "+Formato.toSQL(this.genero)+" " +
                        "WHERE usuario_id = "+Formato.toSQL(usuario_id);

            }
            System.out.println("Sentencia: "+query);
            st = cn.createStatement();
            st.executeUpdate(query);
            st.close();
           
       }
       catch (SQLException e) {  
            throw new  ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al guardar datos en BD: "+e.getMessage(), e);   
       }
    }

    /**
     * Borra el usuario en BD a partir del id. 
     * @param cn
     * @throws ExcepcionesRepositorio 
     */
    @Override
    public void borrar(Connection cn) throws ExcepcionesRepositorio {
        
        // Comprobacion previa: Evitar que el ID sea null. ¿Existe el usuario en BD?
        if (this.id == null || this.id <1) 
            this.id = this.cargarID(cn); // Si es null se recupera de BD el id correspondiente al username actual.
        
        // Si el id es 0 damos por hecho que el usuario no existe. (Valor devuelto por this.cargarID(cn)
        if (this.id != 0) {
     
            String query;
            Statement st;
            try {
                query= "DELETE FROM usuarios WHERE usuario_id="+Formato.toSQL(this.id); //System.out.println("Sentencia: "+query);
                st = cn.createStatement();
                st.executeUpdate(query);
                st.close();
            }
            catch(SQLException e) {
                throw new  ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS,"Error al borrar datos del usuario "+this.username+" en la BD: "+e.getMessage(),e);   
            }
        }
    }

    /* Otros métodos de acceso a Base de Datos
       ---------------------------------------*/

    /**
     * Devuelve el valor id del objeto almacenado en base de datos a partir del campo único "user".
     * @param cn
     * @return 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
    */
    //@Override
    public Integer cargarID(Connection cn) throws ExcepcionesRepositorio {
        
        
        Integer id_user= 0; // Id por defecto
        
        // Acceso a Bd
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT usuario_id FROM usuarios WHERE username ="+Formato.toSQL(this.username);  //System.out.println("Sentencia: "+query); 
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()){
                id_user = (Integer)rs.getInt("usuario_id"); // Id cargado
            }
            rs.close();
            st.close();
            
            return id_user;
        }           
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
        }   
    }
  
    /**
     * Comprueba que las credenciales (user y password) del usuario existen en base de datos.
     * Además, si el usuario existe, asigna el valor del id a la instancia actual.
     * @param cn
     * @return 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public boolean checkAccess(Connection cn) throws ExcepcionesRepositorio {
        
        boolean access =false; // Respuesta por defecto
        
        // Acceso a Bd
        String query;
        Statement st;
        ResultSet rs;
        try {
            query="SELECT usuario_id FROM usuarios WHERE username ="+Formato.toSQL(this.username)+" AND password ="+Formato.toSQL(this.password);  //System.out.println("Sentencia: "+query);         
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {
                access = true; // Usuario válido
                this.id = (Integer)rs.getInt("usuario_id"); // Opción extra para optimizar el funcionamiento de la web. Cargando el id puedo agregarlo a la HttpSession tras el login. Así, tengo el id disponible para aplicarlo a los medios subidos. 
            }
            rs.close();
            st.close();
                 
            return access;
        }
        catch (SQLException e) {
           throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
        }   
    }
    
    /**
     * Comprueba si un usuario existe en base de datos, a partir del campo único 'user'.
     * @param cn
     * @return
     * @throws ExcepcionesRepositorio 
     */
    public boolean existe (Connection cn) throws ExcepcionesRepositorio{
        
        boolean existe = false; // Respuesta por defecto: ¿true?
        
        // 1.Cargar Id
        int id_usuario = (int) this.cargarID(cn);
        
        // 2. Evaluar Id
        if (id_usuario>0) {
            existe = true; // Existe
        }
        return existe;
    }
    
    /**
     * Método que comprueba si un usuario es único en base de datos.
     * @param cn
     * @return
     * @throws ExcepcionesRepositorio
     */
    public boolean unico (Connection cn) throws ExcepcionesRepositorio {
    
        boolean unico = true; // Respuesta por defecto: false?
        
        // Acceso a Bd
        String query;
        Statement st;
        ResultSet rs;
        try {
            // Comprobar usuario
            query="SELECT usuario_id FROM usuarios WHERE username ="+Formato.toSQL(this.username);  //System.out.println("Sentencia: "+query);         
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {
                unico = false;  // Usuario duplicado 
            } else {
                // Comprobar correo
                query="SELECT usuario_id FROM usuarios WHERE correo ="+Formato.toSQL(this.correo);  //System.out.println("Sentencia: "+query);         
                rs = st.executeQuery(query);
                if(rs.next()) {
                    unico = false; // Correo duplicado
                }
            }
            rs.close();
            st.close();
        }
        catch (SQLException e) {
           throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
        }   
        
        return unico;
    }
    
    /**
     * Método que comprueba si un correo es único en base de datos.
     * @param cn
     * @return
     * @throws ExcepcionesRepositorio
     */
    public boolean isCorreoUnico (Connection cn) throws ExcepcionesRepositorio {
    
        boolean unico = true; // Respuesta por defecto: false?
        
        // Acceso a Bd
        String query;
        Statement st;
        ResultSet rs;
        try {
            // Comprobar correo
            query="SELECT usuario_id FROM usuarios WHERE correo ="+Formato.toSQL(this.correo);  //System.out.println("Sentencia: "+query);         
            st = cn.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) {
                unico = false; // Correo duplicado
            }
            rs.close();
            st.close();
        }
        catch (SQLException e) {
           throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e); 
        }   
        
        return unico;
    }
    
     /*  Getters and setters
       --------------------*/
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Timestamp getFecha_creacion() {
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
