/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */

package repositorio.modelo.logica.lists;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.UriInfo;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.PersistenciaREST;
import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.listItems.RecursoListItem;
import repositorio.modelo.utilidades.Formato;

/**
 *
 * @author Juan Carlos
 */
public class RecursosList implements PersistenciaREST{
   
    private List<RecursoListItem> listaRecursos;
    private Integer medioId; // Medio 'padre' de la lista de recursos
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    
    /* Constructores
       --------------*/
    
    // Constructor vacío
    public RecursosList(){};
    
    // Constructor con id
    public RecursosList(Integer id){
        this.medioId = id;
    }
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return 
     */
    @Override
    public String toString() {
       
        return listaRecursos.toString();
    }
    
    /* Métodos de acceso a Base de Datos --> Interfaz Persistencia
       -----------------------------------------------------------*/
    
    /**
     *
     * @param cn
     * @param context
     * @throws ExcepcionesRepositorio
     */
    @Override
    public void cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Condición de carga -> Está definido el id del medio multimedia 'padre'
        if (getMedioId() != null) {

            // Lista de recurso y elemento enésimo
            listaRecursos = new ArrayList<>();
            RecursoListItem item;

            // Variables cargadas de BD
            Integer recurso_id;
            String path;
            String estado;
            Integer medio_id;
            Integer perfil_id;
            String perfil_name;
            Timestamp fecha_creacion;
            Timestamp fecha_modificacion;
            String uri_recurso; // URI del recurso
            String uri_multimedia; // URI del recurso multimedia
            
            // Consulta en BD
            try {
                String query="SELECT recurso_id, path, estado, medio_id, perfil_id, fecha_creacion, fecha_modificacion FROM recursos WHERE medio_id="+Formato.toSQL(getMedioId());  //System.out.println("Sentencia: "+query);
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(query);
                while(rs.next()) {
                    recurso_id = (Integer) rs.getInt("recurso_id");
                    path = rs.getString("path");
                    estado = rs.getString("estado");
                    medio_id = (Integer) rs.getInt("medio_id");
                    perfil_id = rs.getInt("perfil_id");
                    fecha_creacion = rs.getTimestamp("fecha_creacion");
                    fecha_modificacion = rs.getTimestamp("fecha_modificacion");

                    // Nombre de perfil
                    perfil_name = PerfilMultimedia.cargarNombrePerfilFromProperties(perfil_id);

                    // Uri del recurso
                    uri_recurso = context.getBaseUri().toString() + "recurso/" + recurso_id; // URI correspondiente al contenido multimedia del recurso (Servlet pasarela)

                    // Uri del recurso multimedia
                    uri_multimedia = context.getBaseUri().toString() + "media?recurso=" + recurso_id; // URI correspondiente al contenido multimedia del recurso (Servlet pasarela)

                    // Instanciar MedioListItem
                    item = new RecursoListItem(recurso_id, path, estado, medio_id, perfil_id, perfil_name, fecha_creacion, fecha_modificacion, uri_recurso, uri_multimedia);

                    // Añadir recurso al listado
                    listaRecursos.add(item);
                }
                rs.close();
                st.close();
                
                setPersistenciaOK(true);  // Carga OK. Ojo! Si el listado en BD está vacío la carga se considera correcta. Por eso está fuera del bucle.   
            }       
            catch (SQLException e) {
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
            }
        }
    }

    // Métodos de la interfaz PersistenciaREST 
    @Override
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
                  
        // 1. Cargar lista de recursos: Necesito los id y los path
        this.cargar(cn, context);
        
        // 2. Borrar recursos de base de datos
        for (RecursoListItem recurso_n : listaRecursos) { // Para cada elemento:
            recurso_n.borrar(cn, context);
        }
    }
    
    /* Getters & Setters -> Ojo! Actualizar usando buenas prácticas (no devolver referencias)
      ------------------*/ 

    public List<RecursoListItem> getListaRecursos() {
        return listaRecursos;
    }

    public void setListaRecursos(List<RecursoListItem> listaRecursos) {
        this.listaRecursos = listaRecursos;
    }

    public Integer getMedioId() {
        return medioId;
    }

    public void setMedioId(Integer medioId) {
        this.medioId = medioId;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
}
