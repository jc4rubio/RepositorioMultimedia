/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.servlets;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.Usuario;

/**
 * Servicio para el inicio de sesión de usuarios.
 * @author Juan Carlos
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private Repositorio miRepositorio;
    private String next_page;
    private String next_page_OK;
    private String next_page_ERROR;
    
    @Override
    public void init(ServletConfig servlet_config){
    
        // Instanciar la clase repositorio orientada a la gestión de Base de Datos
        miRepositorio = new Repositorio();
        
        // Siguiente página (por defecto)
        next_page_OK = servlet_config.getServletContext().getContextPath()+"/home.jsp";           // Login correcto
        next_page_ERROR = servlet_config.getServletContext().getContextPath()+"/login.html?error";  // Login incorrecto 
        next_page = next_page_ERROR;                                                                // Redirección por defecto
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Formato
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // Recuperar parámetros 
        String user=request.getParameter("user");  
        String password=request.getParameter("password");  
        System.out.println("Credenciales:: User: "+user+" Password: "+password);

        // *Atajo!!! Descomentar para desactivar el login
           // user="admin";password="admin"; // Usuario válido en BD
        
        // Comprobar usuario
        Usuario usuario = new Usuario(user,password);
        try {
            miRepositorio.crearConexion(); // Abrir conexión a BD
            if (usuario.checkAccess(miRepositorio.getConnection())) { // Devuelve boolean
                // Login correcto
                System.out.println("Login correcto!");
                HttpSession session=request.getSession();  // Crea la sesión si no existe
                session.setAttribute("user",user);
                session.setAttribute("usuario_id",usuario.getId());
                next_page = next_page_OK;         
            } else {
                // Login incorrecto
                System.out.println("Login incorrecto!");
                next_page = next_page_ERROR;            
            }
            // Redirección
            response.sendRedirect(next_page);
            
        } catch (ExcepcionesRepositorio | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // Cerrar conexión con BD
                miRepositorio.cerrarConexion(); 
            } catch (ExcepcionesRepositorio ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }  
        
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
