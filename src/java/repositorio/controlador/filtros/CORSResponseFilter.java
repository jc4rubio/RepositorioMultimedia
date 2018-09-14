/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.filtros;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Este filtro modifica las cabeceras para permitir las peticiones de "dominio-cruzado".
 * Permite consumir los servicios REST cuando el servidor se encuentre en un dominio diferente. Es decir, permite
 * tener el servidor en una IP-Puerto distinta del cliente que los consume.
 * Por defecto el cliente limita este tipo de peticiones por seguridad.
 * 
 * Más info:
 * http://stackoverflow.com/questions/20035101/no-access-control-allow-origin-header-is-present-on-the-requested-resource
 * 
 * "Regular web pages can use the XMLHttpRequest object to send and receive data from remote servers, but they're limited 
 * by the same origin policy. Extensions aren't so limited. An extension can talk to remote servers outside of its origin, 
 * as long as it first requests cross-origin permissions."
 * 
 * @author Juan Carlos
 */
public class CORSResponseFilter implements ContainerResponseFilter {

        @Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

            System.out.println("\n[ Filtro CORS...]");
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();

            headers.add("Access-Control-Allow-Origin", "*");
            //headers.add("Access-Control-Allow-Origin", "http://podcastpedia.org"); //allows CORS requests only coming from podcastpedia.org		
            headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");			
            headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");
            
	}

}