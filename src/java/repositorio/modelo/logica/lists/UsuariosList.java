/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import repositorio.modelo.logica.listItems.UsuarioListItem;

/**
 *
 * @author Juan Carlos
 */
@XmlRootElement
public class UsuariosList implements PersistenciaREST {

    private List<UsuarioListItem> usuariosListItem; // Lista de usuarios
    private boolean persistenciaOK;  // Verificacion del proceso de interaccion con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    
   
    /**
     * Constructor vacío.
     * Necesario para el uso de la librería JAXB
     */
    public UsuariosList(){};
    
    /**
     * Representación del objeto en JSON.
     * Implementación manual. Alternativa al uso de la librería GSON.
     * @return 
     */
    @Override
    public String toString() {
       
        return getUsuariosListItem().toString();
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
        
        // Lista de usuarios y elemento enésimo
        usuariosListItem = new ArrayList<>();
        UsuarioListItem item;

        // Variables cargadas de BD
        Integer usuario_id;
        String username;
        Timestamp fecha_creacion;
        Timestamp fecha_modificacion;

        // URI del recurso
        String uriUsuario; 
        
        // Consulta en BD
        try {
            String query="SELECT usuario_id, username, fecha_creacion, fecha_modificacion FROM usuarios";  //System.out.println("Sentencia: "+query);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while(rs.next()) {

                usuario_id = (Integer) rs.getInt("usuario_id");
                username = rs.getString("username");
                fecha_creacion = rs.getTimestamp("fecha_creacion");
                fecha_modificacion = rs.getTimestamp("fecha_modificacion");

                // URI
                uriUsuario = context.getBaseUri().toString() + "usuario/"+ usuario_id;

                // Instanciar usuario
                item = new UsuarioListItem(usuario_id, username, fecha_creacion, fecha_modificacion, uriUsuario);

                // Añadir usuario al listado
                usuariosListItem.add(item);
            }
            rs.close();
            st.close();
            
            setPersistenciaOK(true);  // Carga OK. Ojo! Si el listado en BD está vacío la carga se considera correcta. Por eso está fuera del bucle.
        }
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
        }
    }

    @Override
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /* Getters & Setters
      ------------------*/ 

    public List<UsuarioListItem> getUsuariosListItem() {
        return usuariosListItem;
    }

    public void setUsuariosListItem(ArrayList<UsuarioListItem> usuariosListItem) {
        this.usuariosListItem = usuariosListItem;
    }
     
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
}