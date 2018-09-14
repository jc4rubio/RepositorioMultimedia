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
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.listItems.MedioListItem;
import repositorio.modelo.logica.listItems.RecursoListItem;
import repositorio.modelo.logica.lists.MediosList;
import repositorio.modelo.logica.lists.RecursosList;

/**
 * REST Web Service
 * Servicios:
 *  - Listado de medios
 *  - Medio definido por id
 *
 * @author Juan Carlos
 */
@Path("")
public class MedioService {

    @Context
    private UriInfo context;
    
    /**
     * Crea una instancia del servicio.
     */
    public MedioService() {
    }

    /**
     * Retrieves representation of an instance of repositorio.servicios.MedioService
     * Si no hay resultados se devuelve una lista vacía.
     * Si la carga no ha ido bien, o la lista es nula se devuelve la respuesta por defecto: status NOT FOUND      
     * 
     * @return an instance of java.lang.String
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.sql.SQLException
     * @throws java.lang.IllegalAccessException
     */
    @GET
    @Path("medio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getListadoMedios() throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException, SQLException  {
       
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        MediosList medios = new MediosList();
        
        // Acceso a BDD -> Recuperar listado de medios
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            
            // Cargar lista de medios
            medios.cargar(cn, context);
            
            
            // *Pruebas
            // Pruebas para capturas de pantalla: Sublistado de medios
            // medios.setListaMedios(medios.getListaMedios().subList(0, 3)); // QUITAR EN LA VERSIÓN FINAL!*********
            // medios.setListaMedios(null); // *Pruebas en el cliente -> Response: NOT FOUND
            // medios.getListaMedios().clear();  // *Pruebas en el cliente -> Lista vacía (Me permite hacerlo porque estoy obteniendo la referencia, no una copia de la lista)
            
            // Evaluación de resultados
            if (medios.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(medios.toString()).build(); // Respuesta OK -> Json manual: Devuelve un array de MediosListItem  
                //response = Response.ok(medios).build(); //  Respuesta OK -> Json compuesto por JAXB: Funciona ? Si, pero con diferente formato. Devuelve dos parámetros: listaMedios (Array de MedioListItem), 2 persistenciaOK
            }          
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MedioService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
    
    @GET
    @Path("medio/{medio_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedioById(@PathParam("medio_id") Integer medio_id) throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException {
        
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        MedioListItem medio = new MedioListItem(medio_id);
        
        // Acceso a BDD -> Cargar el medio
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            
            // Cargar medio
            medio.cargar(cn, context);
            
            // Evaluación de resultados
            if (medio.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(medio.toString()).build(); // Respuesta OK -> Json manual: Devuelve un MedioListItem sin el atributo extra persistenciaOK
                //response = Response.ok(medio).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? Si. Devuelve un RecursoItem (Incluye el atributo extra persistenciaOK)
            }
            // Ojo! Si no hay resultados se devuelve la respuesta por defecto -> status NOT FOUND

        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MedioService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
    
    @DELETE
    @Path("medio/{medio_id}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response deleteMedioById(@PathParam("medio_id") Integer medio_id) throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException {
        
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        MedioListItem medio = new MedioListItem(medio_id);
        
        // Acceso a BDD
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            
            // Borrar medio
            medio.borrar(cn, context);

            // Evaluación de resultados
            if (medio.isPersistenciaOK()) {
                //response = Response.ok().build(); // Respuesta OK
                response = Response.status(Response.Status.OK).build(); // Respuesta OK
            } 

        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MedioService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
    
}
