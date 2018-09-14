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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Repositorio multimedia: Lógica de negocio.
 * Versión que utiliza un pool de conexiones.
 * Ojo! La implementación del pool está en pruebas. En la versión actual se utliza la clase Repositorio para realizar la conexión a base de datos.
 * @author Juan Carlos
 */
public class RepositorioPool {
    
   // private Context initContext; // Ojo! http://programandoointentandolo.com/2013/05/como-crear-un-pool-de-conexiones-en-tomcat.html
    private DataSource ds;
    private Connection cn;
    
    /**
     * Constructor principal del repositorio.
     *
     */
    public RepositorioPool(){
       // this.initContext = null;
        this.ds = null;
        this.cn = null;
    }
    
    /* Métodos de gestión de Base de datos
    ----------------------------------*/
     
    /**
     * Crea la conexión utilizando el archivo de configuración por defecto, ubicado en el paquete "repositorio.config"
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public void crearConexion() throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException   {

        System.out.println("\n[ Abriendo conexión con BD...");
        try {
            
//            // Variante al usar el pool
//            // ------------------------
//            initContext = new InitialContext();
//            ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/ConexionMySQL");
//            cn = ds.getConnection();
            // Variante al usar el pool
            // ------------------------
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/ConexionMySQL");
            cn = ds.getConnection();

            System.out.println(" => Conexión creada ]");
        }      
        catch (SQLException e) {
            String error = "Fallo en la conexión. Imposible conectar a la base de datos!";
            System.out.println(" => "+error);
            System.out.println(" => "+e.toString());
            System.out.println("]");
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_SQL, error, e); 
        } catch (NamingException ex) {
            Logger.getLogger(RepositorioPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  

    /**
     * Cierra la conexión creada en el procedimiento: crearConexion()
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void cerrarConexion() throws ExcepcionesRepositorio {
       
        System.out.println("\n[ Cerrando conexión con BD...");
        try {
           if (this.cn != null) {
            Statement st = cn.createStatement();        
            st.executeQuery("commit");
            this.cn.close();
               System.out.println(" => Conexión cerrada ]");
           }
        }
        catch (SQLException e) {
            String error = "Fallo en la conexión. Imposible conectar a la base de datos!";
            System.out.println(" => "+error);
            System.out.println(" => "+e.toString());
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_SQL, error, e); 
        }      
    }
    
    /* Métodos con acceso a Base de datos
       ----------------------------------*/
    
    /**
     * Guarda en base de datos el objeto que le pases como parametro.
     *
     * @param objeto
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void guardar(Persistencia objeto) throws ExcepcionesRepositorio {
       objeto.guardar(cn);
    }

    /**
     * Carga de base de datos el objeto que le pases como parametro.
     *
     * @param objeto
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void cargar( Persistencia objeto) throws ExcepcionesRepositorio {     
       objeto.cargar(cn);
    }
   
    /**
     * Borra el objeto de la base de datos que le pases como parámetro.
     *
     * @param objeto
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void borrar(Persistencia objeto) throws ExcepcionesRepositorio {
        objeto.borrar(cn);
    }
    
    /* Getters & Setters
    ------------------*/ 
    
    public Connection getConnection() {
        return cn;
    }

    public void setConnection(Connection cn) {
        this.cn = cn;
    }

}
