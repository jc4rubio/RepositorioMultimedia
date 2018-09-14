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
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.interfaces.PersistenciaREST;
import repositorio.modelo.logica.MedioMultimedia.Estados;
import repositorio.modelo.logica.listItems.MedioListItem;

/**
 *
 * @author Juan Carlos
 */
@XmlRootElement
public class MediosList implements PersistenciaREST{
   
    private List<MedioListItem> listaMedios; // Lista de medios
    private boolean persistenciaOK;          // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return Lista de medios en formato JSON
     */
    @Override
    public String toString() {
       
        return getListaMedios().toString();
    }
    
    /* Métodos de acceso a Base de Datos -> Interfaz PersistenciaREST
       --------------------------------------------------------------*/
    
    @Override
    public void cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        
        setPersistenciaOK(false); // Estado de la operación por defecto
        
        // Lista de medios y elemento enésimo
        listaMedios = new ArrayList<>();
        MedioListItem item;

        // Variables cargadas de BD
        Integer medio_id;
        String titulo;
        Estados estado;
        Integer usuario_id;
        String usuario_nick;
        Timestamp fecha_creacion;
        Timestamp fecha_modificacion;
        String uri_medio; // URI del medio
        String uri_recursos; // URI del listado de recursos
        
        // Consulta en BD
        try {
            String query="SELECT m.medio_id, m.titulo, m.estado, m.usuario_id, u.username, m.fecha_creacion, m.fecha_modificacion FROM medios m left join usuarios u on u.usuario_id = m.usuario_id";
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()) {

                medio_id = (Integer) rs.getInt("medio_id");
                titulo = rs.getString("titulo");
                estado = Estados.valueOf(rs.getString("estado"));
                usuario_id = (Integer) rs.getInt("usuario_id");
                usuario_nick = rs.getString("username");
                fecha_creacion = rs.getTimestamp("fecha_creacion");
                fecha_modificacion = rs.getTimestamp("fecha_modificacion");

                // Construir uri
                uri_medio = context.getBaseUri().toString() + "medio/" + medio_id;
                uri_recursos = context.getBaseUri().toString() + "recursos/" + medio_id;

                // Instanciar MedioListItem
                item = new MedioListItem(medio_id, titulo, estado, usuario_id, usuario_nick, fecha_creacion, fecha_modificacion, uri_medio, uri_recursos);

                // Añadir medio al listado
                listaMedios.add(item);
            }
            rs.close();
            st.close();
            
            setPersistenciaOK(true);  // Carga OK. Ojo! Si el listado en BD está vacío la carga se considera correcta. Por eso está fuera del bucle.
        }
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
        }
        
    }
    
    // Métodos de la interfaz PersistenciaREST (No implementados)
    @Override
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /* Getters & Setters -> Ojo! Actualizar usando buenas prácticas (No devolver referencias)
      ------------------*/ 

    public List<MedioListItem> getListaMedios() {
        return listaMedios;
    }

    public void setListaMedios(List<MedioListItem> listaMedios) {
        this.listaMedios = listaMedios;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
   
}
