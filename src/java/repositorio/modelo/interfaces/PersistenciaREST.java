/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.interfaces;

import java.sql.Connection;
import javax.ws.rs.core.UriInfo;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;


/**
 * Interfaz implementada por los objetos: Métodos de acceso a base de datos.
 * @author Juan Carlos
 */
public interface PersistenciaREST {  
    
//    /**
//     * Implementa el método cargar para todos los objetos
//     * Comprueba que el objeto exista en BD. Devuelve False si el objeto
//     * no existe. Devuelve True si se ha encontrado una correspondencia en BD
//     * y la carga ha sido correcta.
//     * @param cn
//     * @param context
//     * @return 
//     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
//     */
//    public boolean cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio;
    
    /**
     * Implementa el método cargar para todos los objetos
     * Comprueba que el objeto exista en BD. 
     * @param cn
     * @param context 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void cargar(Connection cn, UriInfo context) throws ExcepcionesRepositorio;
    
    /**
     * Implementa el método guardar para todos los objetos. 
     * @param cn
     * @param context
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void guardar(Connection cn, UriInfo context) throws ExcepcionesRepositorio;

    /**
     * Implementa el método borrar para todos los objetos.
     * @param cn 
     * @param context 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void borrar(Connection cn, UriInfo context) throws ExcepcionesRepositorio;
    
}
