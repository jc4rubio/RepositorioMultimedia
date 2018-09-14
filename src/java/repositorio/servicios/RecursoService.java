/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.servicios;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.listItems.RecursoListItem;
import repositorio.modelo.logica.lists.RecursosList;
import repositorio.modelo.utilidades.Formato;

/**
 * REST Web Service
 * Servicios:
 *  - Listado de recursos por medio
 *  - Recurso por medio y por perfil
 *  - Recurso por id
 *
 * @author Juan Carlos
 */
@Path("")
public class RecursoService {

    @Context
    private UriInfo context;

    /**
     * Crea una instancia del servicio.
     */
    public RecursoService() {
    }
    
    /**
     * Devuelve un listado con todos los recursos para el medio indicado por parámetro mediante su id en base de datos.
     * @param medio_id
     * @return an instance of java.lang.String
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    @GET
    @Path("recursos/{medio_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecursosByMedio(@PathParam("medio_id") String medio_id) throws ExcepcionesRepositorio {
          
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        RecursosList recursos = new RecursosList(Integer.valueOf(medio_id));
        
        // Acceso a BDD -> Recuperar listado de recursos para el medio 'medio_id'
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            
            // Cargar lista de recursos
            recursos.cargar(cn, context);
            
            // Evaluación de resultados -> La carga es correcta y la lista no es nula
            if (recursos.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(recursos.toString()).build(); // Respuesta OK -> Json manual: Devuelve un array de RecursoListItem.
                //response = Response.ok(recursos).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? Si, pero con diferente formato. Devuelve tres parámetros: 1. listaRecursos (Array de RecursoListItem), 2. mediId, 3. persistenciaOK
            }
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();
        }  
        return response;
    }
    
    /**
     * Devuelve un recurso definido por el medio y el perfil.
     * @param medio_id
     * @param perfil_id
     * @return an instance of java.lang.String
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @GET
    @Path("medio/{medio_id}/{perfil_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecursoByMedioAndPerfil(@PathParam("medio_id") Integer medio_id, @PathParam("perfil_id") Integer perfil_id) throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException {
        
        Repositorio miRepositorio = new Repositorio();
        RecursoListItem recurso;
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        
        // Acceso a BDD -> Recuperar el recurso especificado medio y por perfil (Carga manual: requiere obtener el id del recurso previamente)
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();

            // Sentencia SQL
            String query="SELECT recurso_id FROM recursos where medio_id="+Formato.toSQL(medio_id)+" and perfil_id="+Formato.toSQL(perfil_id);  //System.out.println("Sentencia: "+query);
            Statement st = cn.createStatement();
            try (ResultSet rs = st.executeQuery(query)) {
                if(rs.next()) {
                    
                    // Recurso encontrado en BD
                    Integer recurso_id = (Integer) rs.getInt("recurso_id");
                    
                    // Instanciar y cargar recurso
                    recurso = new RecursoListItem(recurso_id);
                    recurso.cargar(cn, context);
                    
                    // Verificación de carga 
                    if (recurso.isPersistenciaOK()) {        
                        // Dos alternativas para generar el JSON: manual o JAXB
                        response = Response.ok(recurso.toString()).build(); // Respuesta OK -> Json manual: Devuelve un RecursoListItem sin el atributo extra persistenciaOK
                        //response = Response.ok(recurso).build(); // Respuesta OK -> Json compuesto por JAXB. Funciona? Si. Devuelve un RecursoListItem (Incluye el atributo extra persistenciaOK)
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+e.getMessage(), e);
        } 
        finally {
            
            // Cerrar la conexión
            miRepositorio.cerrarConexion();
        }  

        return response;
    }
    
    /**
     * Devuelve un recurso definido por su id en base de datos.
     * @param recurso_id
     * @return an instance of java.lang.String
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
  
    @GET
    @Path("recurso/{recurso_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecursoById(@PathParam("recurso_id") Integer recurso_id) throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException {
        
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        RecursoListItem recurso = new RecursoListItem(recurso_id);
        
        // Acceso a BDD -> Cargar el recurso
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            
            // Cargar recurso
            recurso.cargar(cn, context);
            
            // Evaluación de resultados
            if (recurso.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(recurso.toString()).build(); // Respuesta OK -> Json manual: Devuelve un RecursoListItem sin el atributo extra persistenciaOK
                //response = Response.ok(recurso).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? Si. Devuelve un RecursoListItem (Incluye el atributo extra persistenciaOK)
            }
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(RecursoService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
    
}
