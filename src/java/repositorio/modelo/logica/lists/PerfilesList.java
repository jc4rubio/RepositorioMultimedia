/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.config.Constantes;
import repositorio.modelo.logica.listItems.PerfilListItem;

/**
 *
 * @author Juan Carlos
 */
@XmlRootElement
public class PerfilesList { // No implementa la interfaz PersistenciaREST porque no trabaja con base de datos en esta versión

    private List<PerfilListItem> listaPerfiles;  // Lista de perfiles
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    /**
     * Constructor vacío.
     * Necesario para el uso de la librería JAXB
     */
    public PerfilesList(){};
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return 
     */
    @Override
    public String toString() {
       
        return getPerfilesList().toString();
    }
     
    public void cargarFromProperties(UriInfo context) {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Lista de perfiles y elemento enésimo
        listaPerfiles = new ArrayList<>();
        PerfilListItem item;

        // Cargar archivo de configuración
        Properties prop_config = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+Constantes.CONFIG_FILE);
        if (input != null) {
            try {  
                prop_config.load(input);
                System.out.println("Classloader: Archivo de configuracion general cargado");
                
                // Cargar el perfil solicitado
                Properties prop_perfil = new Properties();
                
                // Patrón de los archivos de configuración de los perfiles 
                // -> Ya no lo uso. Lo he trasladado a la clase Constantes. (¿Qué variables de entorno poner en una clase constante y cuales en properties? -> http://stackoverflow.com/questions/10801746/properties-file-vs-java-constants-file
                //String nombre_base_perfil =  prop_config.getProperty("nombre_base_perfil", "perfil_"); 
                
                // Número de perfiles definidos
                int numero_perfiles = (int) Integer.valueOf(prop_config.getProperty("numero_perfiles", "0"));
                
                // Variables cargadas de los properties
                Integer perfil_id;
                String nombre_perfil;
                String formato_salida;
                String uriPerfil; // URI del recurso
                
                for (Integer i=1; i<=numero_perfiles; i++) { 
                    
                    // Composición del nombre del perfil
                    String config_perfil = Constantes.FILENAME_BASE_PERFIL + i.toString() + ".properties"; // "repositorio/config/perfil_n.properties" con n: id

                    // Cargar propiedades del perfil
                    input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+config_perfil);
                    if (input != null) {
                    
                        prop_perfil.load(input);
                        System.out.println("Classloader: Archivo de configuracion cargado para el perfil: "+config_perfil);

                        // Obtener los valores para cada perfil
                        perfil_id= Integer.valueOf(prop_perfil.getProperty("id"));
                        nombre_perfil= prop_perfil.getProperty("nombre");
                        formato_salida= prop_perfil.getProperty("formatoSalida");
                        
                        // Componer la URI
                        uriPerfil = context.getBaseUri().toString() + "perfil/"+ perfil_id;
                    
                        // Instanciar el PerfilMultimedia a partir de los datos del fichero de configuración correspondiente
                        item = new PerfilListItem(perfil_id, nombre_perfil, formato_salida, uriPerfil);

                        // Añadir el perfil al listado
                        listaPerfiles.add(item);
                    }
                }
                setPersistenciaOK(true);  // Carga OK   
                
            } catch (IOException ex) {
                // No debería capturar nunca esta excepción ya que está controlada con el if previo. Si el input es null, el flag es false.
                Logger.getLogger(PerfilesList.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* Getters & Setters
      ------------------*/ 

    public List<PerfilListItem> getPerfilesList() {
        return listaPerfiles;
    }

    public void setPerfilesList(ArrayList<PerfilListItem> perfiles) {
        this.listaPerfiles = perfiles;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
    
}