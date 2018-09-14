/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.interfaces;

import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import java.sql.Connection;

/**
 * Interfaz implementada por los objetos: Métodos de acceso a base de datos.
 * @author Juan Carlos
 */
public interface Persistencia {
    
    /**
     * Implementa el método cargar para todos los objetos
     * Comprueba que el objeto exista en BD. Devuelve False si el objeto
     * no existe. 
     * @param cn
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void cargar(Connection cn) throws ExcepcionesRepositorio;
    
    /**
     * Implementa el método guardar para todos los objetos. 
     * @param cn
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void guardar(Connection cn) throws ExcepcionesRepositorio;

    /**
     * Implementa el método borrar para todos los objetos.
     * @param cn 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     */
    public void borrar(Connection cn) throws ExcepcionesRepositorio;
    
    
    // Estos métodos se quedan fuera de la interfaz. No son útiles en todos los objetos.
    // --------------------------------------------------------------------------------
    /**
     * Devuelve el valor id del objeto (primary key) almacenado en base de datos a partir del campo característico del objeto (unique/primary).
     * Este método funciona como auxiliar al resto de los métodos orientados a persistencia, ya que el id es el campo
     * referencia para los métodos cargar, guardar (actualizar) y borrar. [Ojo! Hay veces que se carga directamente desde el campo único de referencia y no del id.
     * Permite además comprobar que el objeto existe o no. Cuando el objeto no existe el id devuelto es 0.
     * @param cn
     * @return 
     * @throws repositorio.modelo.excepciones.ExcepcionesRepositorio
     * 
     * Revisión: Realmente me interesa este método? No siempre tengo un campo único característico que no sea el id.
    */
    //public Integer cargarID(Connection cn) throws ExcepcionesRepositorio;
    
    //public boolean existe (Connection cn) throws ExcepcionesRepositorio;

}
