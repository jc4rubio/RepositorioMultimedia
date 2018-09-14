/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.servicios;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.lists.PerfilesList;

/**
 * REST Web Service
 * Servicios:
 *  - Listado de perfiles
 *  - Perfil definido por id
 *
 * @author Juan Carlos
 */
@Path("perfil")
public class PerfilService {

    @Context
    private UriInfo context;

    /**
     * Crea una nueva instancia del servicio.
     */
    public PerfilService() {
    }
    
    /* Peticiones sin Id:   /perfil
       --------------------------------*/

    /**
     * 
     * Retrieves representation of an instance of repositorio.servicios.PerfilService
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfilesFromProperties() {
        
        /* Versión I. Carga de datos desde .properties
          --------------------------------------------*/
        
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        PerfilesList perfiles = new PerfilesList();
        
        // Cargar lista de perfiles
        perfiles.cargarFromProperties(context);
        
        // Evaluación de resultados
        if (perfiles.isPersistenciaOK()) {
            // Dos alternativas para generar el JSON: manual o JAXB
            response = Response.ok(perfiles.toString()).build(); // Respuesta OK -> Json manual: Devuelve un array de PerfilListItem  
            //response = Response.ok(perfiles).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? NO. No encuentro el origen del error:  24-Mar-2017 12:10:05.100 SEVERE [http-nio-8084-exec-301] org.apache.catalina.core.StandardWrapperValve.invoke El Servlet.service() para el servlet [org.netbeans.rest.application.config.ApplicationConfig] en el contexto con ruta [/RepositorioWeb] lanzó la excepción [org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException: MessageBodyWriter not found for media type=application/json, type=class repositorio.modelo.logica.lists.PerfilesList, genericType=class repositorio.modelo.logica.lists.PerfilesList.] con causa raíz
                                                      // org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException: MessageBodyWriter not found for media type=application/json, type=class repositorio.modelo.logica.lists.PerfilesList, genericType=class repositorio.modelo.logica.lists.PerfilesList.
        }
         
        return response;
    }
    
    // Si en un futuro los perfiles se almacenan en BD, habría que implementar esta nueva versión.
//    /**
//     * 
//     * Retrieves representation of an instance of repositorio.servicios.PerfilService
//     * @return an instance of java.lang.String
//     */
//    @GET
//    @Path("")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPerfilesFromBD() {
//         
//        /* Versión II. Carga de datos desde BD (Sin implementar)
//         -------------------------------------------------------*/
//        // ...
//      return Response.status(Response.Status.NOT_FOUND).build();
//    }

    /* Peticiones con Id:   /perfil/{id}
       --------------------------------*/

    /**
     *
     * @param perfil_id
     * @return
     */    
    @GET
    @Path("/{perfil_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerfilbyIdFromProperties(@PathParam("perfil_id") Integer perfil_id) {
        
        /* Versión I. Carga de datos desde .properties
         --------------------------------------------*/
        
        Response response= Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto 
        PerfilMultimedia perfil =  new PerfilMultimedia(perfil_id);
        perfil.cargarFromProperties();
        
        // Cargar datos del perfil
        if (perfil.isPersistenciaOK()){
            // Dos alternativas para generar el JSON: manual o JAXB
            response = Response.ok(perfil.toString()).build(); // Respuesta OK -> Json manual: Devuelve un PerfilMultimedia sin el atributo extra persistenciaOK
            //response = Response.ok(perfil).build(); // Respuesta OK -> Json compuesto por JAXB: Funciona ? Si. Devuelve un PerfilMultimedia (Incluye el atributo extra persistenciaOK)
        }
        
        return response;
     
    }
}
