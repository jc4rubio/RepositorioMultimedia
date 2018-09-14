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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import repositorio.config.Constantes;

/**
 * Repositorio multimedia: Gestión de la conexión a base de datos.
 * @author Juan Carlos
 */
public class Repositorio {
    
    private Connection cn;
    
    /**
     * Constructor principal del repositorio.
     *
     */
    public Repositorio(){
        this.cn = null;
    }
    
    /* Métodos de gestión de Base de datos
    ----------------------------------*/
     
    /**
     * Crea la conexión utilizando el archivo de configuración por defecto, "configuracion.properties", ubicado en el paquete "repositorio.config"
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public void crearConexion() throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException   {
        
        this.crearConexion(null);

    }
    /**
     * Crea una conexión cn con los datos necesarios obtenidos del fichero de configuración definido como parámetro.
     * Si el parámetro es nulo se utiliza el archivo de ocnfiguración por defecto.
     *
     * @param rutaFicheroConfiguracion
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void crearConexion(String rutaFicheroConfiguracion) throws ExcepcionesRepositorio {
        
        Properties prop = new Properties();
        System.out.println("\n[ Abriendo conexión con BD...");
        try {
            
            // Configuración por defecto vs Configuración explícita
            if (rutaFicheroConfiguracion == null) {

                // Acceso al archivo de configuración por defecto.
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                InputStream input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+Constantes.CONFIG_BD_FILE);
                System.out.println(" - Classloader 1: Archivo de configuracion cargado");
                if (input == null) { 
                    String error = " - Classloader 2: input null! No se ha podido cargar el archivo de configuración general: "+Constantes.CONFIG_FILE+" en el path "+Constantes.CONFIG_PATH;
                    System.out.println(error);
                    throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_IO,error);
                }
                prop.load(input); 
            }
            else {
                // Cargar fichero de configuración especificado por 'rutaFicheroConfiguracion'
                // Comprobación previa: ¿Se ha especificado un archivo de configuración en concreto o solo el path hacia el directorio de configuraciones -> Config por defecto.
                if (!rutaFicheroConfiguracion.endsWith(Constantes.PROPERTIES_EXTENSION)) // Fichero de configuración no especifidado. Solo se ha especificado el path del directorio donde se encuentra el archivo de configuración por defecto.
                    rutaFicheroConfiguracion= rutaFicheroConfiguracion.concat(Constantes.CONFIG_BD_FILE);

                System.out.println(" - Cargar fichero desde path explícito:\n"
                                    + "Path completo del fichero de configuración: \n"+rutaFicheroConfiguracion);
                FileInputStream input = new FileInputStream(rutaFicheroConfiguracion);
                System.out.println(" - FileInputStream 1: Archivo de configuracion cargado");
                if (input ==null) {
                    String error = " => FileInputStream 2: input null! No se ha podido cargar el archivo de configuración: "+rutaFicheroConfiguracion;
                    System.out.println(error);
                    throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_IO, error);
                }
                prop.load(input);    
            }

            // Cargar datos del fichero de configuración
            String driver_jdbc, base, ip, port, data_base, user, password, db_url;

            driver_jdbc = prop.getProperty("driver_jdbc");
            base = prop.getProperty("base");
            ip = prop.getProperty("ip");
            port = prop.getProperty("port");
            data_base = prop.getProperty("data_base");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            
            db_url = base+"://"+ip+":"+port+"/"+data_base;
            System.out.println(" - Url conexion BD: \n   "+db_url);
            System.out.println(" - Datos cargados del fichero de configuración");
            
            // Definición del driver (Si el driver es anterior a 4.0)
            Class.forName(driver_jdbc);
            this.cn = DriverManager.getConnection(db_url, user, password);
            System.out.println(" => Conexión creada ]");
        }      
        catch (SQLException e) {
            String error = "Fallo en la conexión. Imposible conectar a la base de datos! ]";
            System.out.println(" => "+error);
           throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_SQL, error, e); 
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(Repositorio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Cierra la conexión con base de datos.
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void cerrarConexion() throws ExcepcionesRepositorio {
       
        System.out.println("\n[Cerrando conexión con BD...");
        try {
           if (this.cn != null && !this.cn.isClosed()) { // Añado segunda clausula para evitar cerrar la conexión cuando realmente ya está cerrada.
            Statement st = cn.createStatement();        
            st.executeQuery("commit");
            this.cn.close();
               System.out.println(" => Conexión cerrada ]");
           }
        }
        catch (SQLException e) {
            String error = "Fallo en la conexión. Imposible conectar a la base de datos!";
            System.out.println("=> "+error);
            System.out.println("=> "+e.toString());
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
    public void cargar(Persistencia objeto) throws ExcepcionesRepositorio {     
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
