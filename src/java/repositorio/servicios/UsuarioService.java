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
import repositorio.modelo.logica.Usuario;
import repositorio.modelo.logica.lists.UsuariosList;

/**
 * REST Web Service
 *
 * @author Juan Carlos
 */
@Path("usuario")
public class UsuarioService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UsuarioService
     */
    public UsuarioService() {
    }
    
     /**
     * Retrieves representation of an instance of repositorio.servicios.UsuarioService
     * @return an instance of java.lang.String
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarios() throws ExcepcionesRepositorio {
  
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        UsuariosList usuarios = new UsuariosList();
        
        // Acceso a BDD -> Recuperar listado de usuarios
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            usuarios.cargar(cn, context);
            
             // Evaluación de resultados
            if (usuarios.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(usuarios.toString()).build(); // Respuesta OK
                //response = Response.ok(usuarios).build(); // Respuesta OK -> Json compuesto por JAXB. Funciona ? NO. Error: 24-Mar-2017 12:07:29.281 SEVERE [http-nio-8084-exec-257] org.apache.catalina.core.StandardWrapperValve.invoke El Servlet.service() para el servlet [org.netbeans.rest.application.config.ApplicationConfig] en el contexto con ruta [/RepositorioWeb] lanzó la excepción [org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException: MessageBodyWriter not found for media type=application/json, type=class repositorio.modelo.logica.lists.UsuariosList, genericType=class repositorio.modelo.logica.lists.UsuariosList.] con causa raíz
                                                          // org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException: MessageBodyWriter not found for media type=application/json, type=class repositorio.modelo.logica.lists.UsuariosList, genericType=class repositorio.modelo.logica.lists.UsuariosList.
            }
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
 
     /**
     * Devuelve un listado con todos los recursos para el medio indicado por parámetro mediante su id en base de datos.
     * @param usuario_id
     * @return an instance of java.lang.String
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    @GET
    @Path("/{usuario_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioById(@PathParam("usuario_id") Integer usuario_id) throws IOException, ClassNotFoundException, ExcepcionesRepositorio, InstantiationException, IllegalAccessException {
        
        Repositorio miRepositorio = new Repositorio();
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto 
        Usuario usuario =  new Usuario(usuario_id);
        
        // Acceso a BDD -> Recuperar datos del usuario
        try {
            // Crear conexión
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            usuario.cargar(cn);
            
             // Evaluación de resultados
            if (usuario.isPersistenciaOK()) {
                // Dos alternativas para generar el JSON: manual o JAXB
                response = Response.ok(usuario.toString()).build(); // Respuesta OK -> Json manual: Devuelve un Usuario sin el atributo extra persistenciaOK
                //response = Response.ok(usuario).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? Si. Devuelve un Usuario (Incluye el atributo extra persistenciaOK)
            }
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(UsuarioService.class.getName()).log(Level.SEVERE, null, ex);
        } 
        finally {
            // Cerrar la conexión
            miRepositorio.cerrarConexion();  
        }
        return response;
    }
    
}

   
