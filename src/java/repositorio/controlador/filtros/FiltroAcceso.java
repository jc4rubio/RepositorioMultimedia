/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.filtros;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filtro para redireccionar al portal de login cuando el usuario intente acceder 
 * a un recurso privado sin autorización, o cuando la sesión haya expirado.
 * Además, gestiona redirecciones. Cuando un usuario con sesión intenta 
 * acceder por URL a la página de inicio, se redirige a la versión con sesión de la página de
 * bienvenida: home.jsp.
 * 
 * @author Juan Carlos
 */
@WebFilter(filterName = "FiltroAcceso", urlPatterns = {"/*"})
public class FiltroAcceso implements Filter {
    
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig;
    
    // URLs de análisis
    private final String base_page = "/RepositorioWeb/";
    private final String index_page = "/index.html";
    private final String home_page = "/home.jsp";
    private final String login_page = "/login.html";
    private final String upload_page = "/upload.jsp";
    private final String uploadServlet_page = "/upload";
        
    
    // Constructor
    public void FiltroAcceso() {
        this.filterConfig = null;
    }
    
    /**
     * Init method for this filter
     * @param filterConfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig= filterConfig;
    }

    /**
     * Actuación del filtro.
     * 
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String uri = ((HttpServletRequest) request).getRequestURI(); // URI destino
        String contextPath = ((HttpServletRequest) request).getContextPath();
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Obtiene la sesión pero no la crea si no existe

        // URLs de redirección
        String next_page_login = contextPath+login_page;
        String next_page_home = contextPath+home_page;
        
        // Log del proceso
        System.out.println("\n[ Filtrando...");
        System.out.println(" => URL: "+uri);
        
        boolean welcome = false;
        boolean index = false;
        boolean restringido = false;
 
        // Clasificación de URLs
        if (uri.endsWith(home_page) || uri.endsWith(upload_page) || uri.endsWith(uploadServlet_page) ){
            System.out.println(" - Recurso restringido");
            restringido = true;
        } else { 
            System.out.println(" - Recurso público");
            if (uri.endsWith(base_page)){
                System.out.println(" - Página de bienvenida");
                welcome = true;
            } else {
                if (uri.endsWith(index_page)){
                    System.out.println(" - index.html");
                    index = true;
                }
            }
        }
        
        // Datos sesión
        boolean sesionActiva = (session != null && session.getAttribute("user") != null);
        if (sesionActiva) // Log
            System.out.println(" - Hay sesion");
        else
            System.out.println(" - No hay sesión");

        // Acción
        if (sesionActiva && (welcome || index)) {
            System.out.println(" -> Acceso denegado!* Redirección a home.jsp ]"); 
            // Ojo!* Al ser el index una página html a veces se guarda en caché y el filtro no actúa, 
            // ofreciendo la versión sin sesión. No es un fallo de seguridad por lo que no contemplo
            // solucionarlo (Podría pasar la página index.html a formato .jsp)
            res.sendRedirect(next_page_home);
        } else {
            if (!sesionActiva && restringido) {
                System.out.println(" -> Acceso denegado! Redirección a login.html ]");
                res.sendRedirect(next_page_login);
            } else {
                // Continúa la cadena de filtrado si no hay sesion ni usuario 
                // Permitir avanzar el proceso de paginación, encadenando el siguiente filtro si es necesario.
                System.out.println(" -> Acceso permitido! No hay redirección ]");
                chain.doFilter(request,response);  
            }
        }
    }

    /**
     * Return the filter configuration object for this filter.
     * @return 
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {        
    }

}
