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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.MediaFile;
import repositorio.modelo.logica.MedioMultimedia.Estados;
import repositorio.modelo.logica.listItems.RecursoListItem;

/**
 * REST Web Service
 * 
 * Este servicio pretendía dar acceso a los recursos multimedia a través de streaming.
 * Pero el servicio REST no me permite el streaming en sí, sino la descarga del fichero.
 * 
 * Este servicio queda relegado por el servlet PasarelaServlet.
 *
 * @author Juan Carlos
 */
@Path("media1")
public class PasarelaService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of pasarelaContenido
     */
    public PasarelaService() {
    }
    
     /**
     * Retrieves representation of an instance of repositorio.servicios.PasarelaService
     * @param recurso_id
     * @return an instance of java.lang.String
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    //@Produces(MediaType.TEXT_PLAIN)
    @Path("/{recurso_id}")
    public Response getMediaStream(@PathParam("recurso_id") Integer recurso_id) throws ExcepcionesRepositorio {
       
        Response response = Response.status(Response.Status.NOT_FOUND).build(); // Respuesta por defecto
        Repositorio miRepositorio = new Repositorio();
        RecursoListItem recurso = new RecursoListItem(recurso_id);

        try { // Acceso a BDD -> Recuperar el recurso especificado por id
            
            miRepositorio.crearConexion();
            Connection cn = miRepositorio.getConnection();
            recurso.cargar(cn, context);
            
            if(recurso.isPersistenciaOK()) { // Carga OK      
                
                if (recurso.getEstado().equals(Estados.OK.toString())){ // Estado OK
                    
                    String path = recurso.getPath();
                    System.out.println("Abriendo archivo: "+path);
                    
                    MediaFile file = new MediaFile(path);
                    
                    // Respuesta OK
                    response = Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                        .header("Content-Disposition", "filename=\"" + file.getFullName() + "\"" ) //optional
                        .build();   
                } else {
                    response = Response.ok("El recurso no está disponible!",MediaType.TEXT_PLAIN).build(); 
                }
            } else {
               response = Response.ok("No hay resultados!",MediaType.TEXT_PLAIN).build(); 
            }
        }
        catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PasarelaService.class.getName()).log(Level.SEVERE, null, ex);
            throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_BASE_DE_DATOS, "Error al cargar datos sobre la BD: "+ex.getMessage(), ex);
        }
        finally {      
            // Cerrar la conexión
            miRepositorio.cerrarConexion();
        }  

        return response; 
    }


    /**
     * PUT method for updating or creating an instance of PasarelaService
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
