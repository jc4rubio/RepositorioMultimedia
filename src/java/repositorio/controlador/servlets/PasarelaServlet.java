/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.MediaFile;
import repositorio.modelo.logica.MedioMultimedia;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.listItems.RecursoListItem;

/**
 * Servicio de reproducción de recursos vía streaming. 
 * @author Juan Carlos
 */
@WebServlet(name = "PasarelaServlet", urlPatterns = {"/webresources/media"})
public class PasarelaServlet extends HttpServlet {

    /* 
     * "webresources": definido por la etiqueta javax.ws.rs.ApplicationPath("webresources") en la 
     * clase  "or.netbeans.rest.application.config.ApplicationConfig.java"
     * webresources/ engloba las uri de los servicios REST. 
     * Este servlet parte del mismo punto raíz para poder crear las URIs de los recursos a partir del context (UriInfo).
     */
    private Repositorio miRepositorio;
    private RecursoListItem recurso;

    @Override
    public void init() throws ServletException {
        miRepositorio = new Repositorio();
        System.out.println("Servlet inicializado");
    }
     
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        String recurso_id = request.getParameter("recurso");
        System.out.println("Id recibido "+recurso_id);
        
        // Respuesta por defecto
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            // Acceso a BDD -> Recuperar el recurso especificado por id
            miRepositorio.crearConexion();
        
            Connection cn = miRepositorio.getConnection();
            recurso = new RecursoListItem(Integer.valueOf(recurso_id));
            recurso.cargar(cn, null);
            
            if (recurso.isPersistenciaOK() && recurso.getEstado().equals(MedioMultimedia.Estados.OK.toString())){ // El recurso ha sido cargado correctamente y su estado es 'OK'

                // Instanciando archivo multimedia
                MediaFile file = new MediaFile(recurso.getPath());
                System.out.println("Ruta absoluta : "+file.getAbsolutePath());
                
                if (file.exists()){

                // Abrir InputStream
                inputStream = new FileInputStream(file);

                // Obtener el mime type
                String mimeType = getServletContext().getMimeType(file.getAbsolutePath());
                System.out.println("mimeType : "+mimeType);
                if(mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                // Preparar la respuesta
                response.setStatus(HttpServletResponse.SC_OK);  // Importante!
                response.setContentType(mimeType);
                response.setContentLength((int)file.length());
                response.setHeader("Content-Disposition", "filename=\""+file.getFullName()+"\"");
                
                // Buffer de streamming
                outputStream = response.getOutputStream();

                byte[] bytes = new byte[(int)file.length()];
                System.out.println("bytes.length : "+bytes.length);
                inputStream.read(bytes);
                outputStream.write(bytes);
                outputStream.flush();
                      
                System.out.println("Descarga completa");
                        
                } else {
                    // El recurso no está disponible en la ubicación definida en BD.
                    request.setAttribute("ERROR", "Archivo no disponible (II)");
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    System.out.println("¡¡ERROR!! Recurso no disponible (II). El archivo asociado al recurso no existe.");   
                }   
            } else {
                // El recurso no existe en Bd o no está disponible (Estado distinto de 'OK')
                request.setAttribute("ERROR", "Recurso no disponible (I)");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                System.out.println("¡¡ERROR!! Archivo no disponible (I). El recurso no está disponible.");
            }
            
        } catch (IOException | ClassNotFoundException | ExcepcionesRepositorio | InstantiationException | IllegalAccessException ex) {
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            Logger.getLogger(   PasarelaServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            
            try {

                // 1. Cerrar procesos de lectura/escritura
                if (inputStream != null) {
                    System.out.println("Cerrando InputStream...");
                    inputStream.close();
                }
                if (outputStream != null){
                    System.out.println("Cerrando OutputStream...");
                    outputStream.close();
                }
                
                // 2. Cerrar conexión a BD
                miRepositorio.cerrarConexion();
                
            } catch (ExcepcionesRepositorio | IOException ex) {
                Logger.getLogger(PasarelaServlet.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
}