/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.Usuario;
import repositorio.modelo.utilidades.Formato;

/**
 * Servicio para el registro de usuarios.
 * @author Juan Carlos
 */
@WebServlet(name = "RegistroServlet", urlPatterns = {"/registro"})
public class RegistroServlet extends HttpServlet {

    private Repositorio miRepositorio;
    private String next_page;
    private String next_page_OK;
    private String next_page_ERROR;
    
    @Override
    public void init(ServletConfig servlet_config){
    
         // Instanciar la clase repositorio orientada a la gestión de Base de Datos
        miRepositorio = new Repositorio();

        // Siguiente página (por defecto)
        next_page_OK = servlet_config.getServletContext().getContextPath()+"/login.html";        // Usuario creado: Puede loguearse
        next_page_ERROR = servlet_config.getServletContext().getContextPath()+"/registro.html?"; // El registro no ha ido bien: vuelve a la página de registro
        next_page = next_page_ERROR; // Hasta que no esté confirmado que el registro ha ido bien, la siguiente página es la de registro.
    }
    
    @Override
    public void destroy(){

        // Cerrar conexión a base de datos (En caso de que se hubiese quedado abierta)
        try {
           miRepositorio.cerrarConexion();    
        } catch (ExcepcionesRepositorio ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        // Formato
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        // Recuperar parámetros 
        String user=request.getParameter("user");  
        String password=request.getParameter("password");  
        String correo=request.getParameter("correo");  
        String nombre=request.getParameter("nombre");  
        String apellidos=request.getParameter("apellidos");  
        String telefono=request.getParameter("telefono");
        String fecha_nacimiento=request.getParameter("fecha_nacimiento");  
        String genero=request.getParameter("genero");
        
        System.out.println("Datos del registro:: User: "+user+" Password: "+password+" Correo: "+correo+"\nNombre: "+nombre+"Apellidos: "+apellidos+" Teléfono: "+telefono+"\nFecha de nacimiento: "+fecha_nacimiento+" Género: "+genero);
 
        // Comprobar usuario
        PrintWriter writer = response.getWriter();
        Usuario usuario = new Usuario(user,password,correo);
        try {  
            miRepositorio.crearConexion(); // Abrir conexión a BD
            if (usuario.existe(miRepositorio.getConnection())) {
                // El correo ya existe en BD. Respuesta html del servlet
                System.out.println("El usuario "+user+" ya existe!");
                writer.println("El usuario introducido no está disponible. <br/><br/>");
                next_page = next_page_ERROR;    
            } else if (!usuario.isCorreoUnico(miRepositorio.getConnection())){
                // El correo ya existe en BD. Respuesta html del servlet
                System.out.println("El correo "+correo+" ya existe!");
                writer.println("El correo introducido no está disponible. <br/><br/>");
                next_page = next_page_ERROR;
            } else {
                // Nombre de usuario y correo disponible
                System.out.println("Procediendo con el registro...");
                usuario.setNombre(nombre);
                usuario.setApellidos(apellidos);
                usuario.setTelefono((telefono !=null && !telefono.equals(""))?Integer.parseInt(telefono):0);
                usuario.setBirthdate((fecha_nacimiento!= null && !fecha_nacimiento.equals("")?Formato.formatearFecha(fecha_nacimiento):null));
                usuario.setGenero(genero);
                usuario.guardar(miRepositorio.getConnection());
                
                // Respuesta html del servlet
                writer.println("El registro se ha realizado correctamente. <br/><br/>");
                next_page = next_page_OK;
            }
            miRepositorio.cerrarConexion(); // Cerrar conexión a BD
        } catch (ExcepcionesRepositorio | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            writer.println("Error al realizar el registro. Revise los campos introducidos.");
            System.out.println("Error en el registro!");
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            // Respuesta html del servlet: Enlace para la siguiente página (upload.jsp por defecto)
            writer.print("<a href="+next_page+"><img src=\"./resources/images/volver.png\" style=\"height: 35px\" alt=\"Volver\"/></a>");
            writer.close();

        }      
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(RegistroServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(RegistroServlet.class.getName()).log(Level.SEVERE, null, ex);
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
