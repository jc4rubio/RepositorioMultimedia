/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package org.netbeans.rest.application.config;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 * 
 * @author Juan Carlos
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        resources.add(repositorio.controlador.filtros.CORSResponseFilter.class); // Filtro CORS
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(repositorio.servicios.MedioService.class);
        resources.add(repositorio.servicios.PasarelaService.class);
        resources.add(repositorio.servicios.PerfilService.class);
        resources.add(repositorio.servicios.RecursoService.class);
        resources.add(repositorio.servicios.UsuarioService.class);
    }
    
}
