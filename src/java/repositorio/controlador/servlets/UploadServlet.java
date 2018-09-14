/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.controlador.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import repositorio.config.Constantes;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.MediaFile;
import repositorio.modelo.logica.conversion.Conversion;
import repositorio.modelo.logica.MedioMultimedia;
import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.Repositorio;
import repositorio.modelo.logica.Usuario;
import repositorio.modelo.utilidades.Utilidades;

/**
 * Servicio de publicación de archivos multimedia en el repositorio.
 * 
 * - Control de versiones -
 * + Version 1.0
 *  - En la Version 1 estoy utilizando el directorio "C:\media(\perfilN_name)" para almacenar el contenido multimedia convertido
 *  - Este directorio y su definición puede variar en diferentes versiones. P.e: Cargar su valor de un .properties o utilizar otro directorio.
 * 
 * + Version 2.0
 *  - Adapta la conversión con diversos perfiles en hilos independientes
 *  - Hay dos variantes:
 *      - 2.1 Realizar todo el proceso en un hilo independiente (uni-hilo)
 *      - 2.2 Realizar cada conversión en un hilo independiente (multi-hilo) (Esta versión desaparece)
 * 
 * + Version 3.0
 *  - Se simplifican las tareas de carga de perfiles, delegando la función en los métodos de otras clases específicas.
 *  - Se quita la creación y cierre de sesión del init y el destroy. Se abre y cierra la sesión en cada peticion.
 *  - Se deja de limpiar el directorio de archivos temporales en el destroy para evitar problemas en la conversión. Se pospone hasta la finalización del proceso, en la clase Conversion.java.
 *  - Se limpia el directorio de archivos temporales en el init. Para evitar que queden archivos residuales.
 * @author Juan Carlos
 */
@WebServlet(name = "UploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class UploadServlet extends HttpServlet {

    private Repositorio miRepositorio;
    private String path_tmp;    // Path temporal donde se almacenan los archivos subidos
    private List<PerfilMultimedia> perfiles_multimedia; // Array de perfiles   
    private String path_media_base;
    private String next_page;
    private String next_page_OK;
    private String next_page_ERROR;
    private MedioMultimedia medio_subido; // En la versión 2.2 puede no ser necesario.
    

    /**
     * Prepara los elementos fundamentales para la subida y conversión de los medios:
     * - 1. Carga la configuración.
     * - 2. Prepara el directorio temporal donde se alojan los archivos subidos.
     * - 3. Comprueba que el directorio raíz donde se vayan a almacenar los medios convertidos existe.
     * - 4. Carga los perfiles desde ficheros .properties y los instancia en un array.
     * - 5. Define las páginas de redirección tras la realización de la tarea.
     * @param servlet_config 
     */
    @Override
    public void init(ServletConfig servlet_config) {
    
        System.out.println("\nUpload -> init()\n");
        
        // Instanciar el repositorio: Clase orientada a la gestión de Base de Datos
        miRepositorio = new Repositorio();
        
        /* 1) Cargar el fichero de configuración general
              ------------------------------------------*/
        Properties prop_config = new Properties();
        String fullpath_config = Constantes.CONFIG_PATH+Constantes.CONFIG_FILE;

        // Cargar archivo de configuración
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(fullpath_config);
        try {
            if (input==null)  { // *Duda: Merece la pena usar una excepción propia habiendo ya una IOException?
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_IO,"Archivo de configuración no encontrado en "+fullpath_config);
            }
            else {
                prop_config.load(input);   
            } 
        } catch (ExcepcionesRepositorio | IOException ex) {
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Classloader: Archivo de configuracion cargado");

        /* 2) Preparar el directorio temporal en el servidor para los archivos subidos
              -----------------------------------------------------------------------*/
        String subpath_tmp = prop_config.getProperty("path_tmp", "/uploadedFiles"); // Al usar 2 parámetros: getProperty("variable","valor por defecto")
        path_tmp =servlet_config.getServletContext().getRealPath(subpath_tmp); // Ojo! Estoy usando RealPath. Puede que de problemas cuando migre la aplicación (.war)
        System.out.println("El path de archivos temporales es:\n"+path_tmp); 
        // Comprobar que el path temporal definido existe
        File directory_tmp = new File(path_tmp);
        if (!directory_tmp.exists()) {
            if (!directory_tmp.mkdirs()) { // Crea el path si no existe.
                try {
                    throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_FICHERO,"No se ha podido crear el directorio "+path_tmp);
                } catch (ExcepcionesRepositorio ex) {
                    Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
        } else {
            // Si existe, se limpia para borrar posibles archivos residuales.
            try {
                System.out.println("Limpiando directorio: "+path_tmp+"\n");
                FileUtils.cleanDirectory(new File(path_tmp));
            } catch (IOException ex) {
                Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /* 3) Comprobar que el directorio principal para medios convertidos existe. Si no existe no se pueden crear los subdirectorios por el tema de los permisos. (Tomando 'C:\media' como referencia)
              -----------------------------------------------------------------*/
        path_media_base = prop_config.getProperty("path_media", "C:\\media"); // Cargar propiedad path_media y valor por defecto si no la encuentra.
        System.out.println("El path base de archivos subidos es:\n"+path_media_base); 
        // Comprobar que el path temporal definido existe
        File directory_media_base = new File(path_media_base);
        if (!directory_media_base.exists()) {
            if (!directory_media_base.mkdirs()) { // Crea el path si no existe.
                try {
                    throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_FICHERO,"No se ha podido crear el directorio "+path_media_base);
                } catch (ExcepcionesRepositorio ex) {
                    Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        /* 4) Cargar los perfiles multimedia
              -------------------------------*/
        // Vector de perfiles
        perfiles_multimedia = PerfilMultimedia.cargarPerfilesFromProperties();
        if(perfiles_multimedia.size()<1)  {

            // Gestión de errores en la carga de los perfiles.
            System.out.println("¡No se han podido cargar los perfiles de configuración. Se utilizará el perfil por defecto!");
            PerfilMultimedia perfil_por_defecto = new PerfilMultimedia(true);// Perfil por defecto
            perfiles_multimedia = new ArrayList<>(1);
            perfiles_multimedia.add(perfil_por_defecto);
        }
        
        /* 5) Definir las páginas de redirección
              -----------------------------------*/
        // Dirección de destino
        next_page_OK = servlet_config.getServletContext().getContextPath()+"/home.jsp"; // Página de bienvenida: Listado de medios (Versión con sesión).
        next_page_ERROR = servlet_config.getServletContext().getContextPath()+"/upload.jsp"; // Página de origen: Subir medio. 
        next_page = next_page_ERROR; // Página de redirección por defecto
        System.out.println("El destino de redireccion por defecto es:\n"+next_page+"\n");        
    }

    
    
    /**
     * Función principal del servlet: Procesa la petición de subida de ficheros.
     * Las tareas que realiza son:
     *  - 1. Procesa el archivo recibido por POST y lo guarda en el servidor, en un directorio temporal.
     *    - Código extraido de: https://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
     *  
     *  + Si el archivo es válido, continuar con la conversión.
     *   - 2. Insertar registro en BD con el estado PROCESANDO
     *   - 3. Iniciar el proceso de conversión
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        

        // Codificación
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
  
        /* 1) Procesar el archivo y guardarlo en el servidor
              ----------------------------------------------*/
        
        String titulo = request.getParameter("titulo");
        Part filePart = request.getPart("file");
        String fileName = Utilidades.getFileName(filePart);
        PrintWriter writer = response.getWriter();
        OutputStream out = null;
        InputStream filecontent = null;
        try {
            if (fileName.isEmpty()) { // El path del archivo está vacío.
                
                // Respuesta html del servlet
                writer.println("No ha seleccionado ningún archivo. <br/><br/>");
                next_page = next_page_ERROR;
                
            } else {
                
                // Composición del path del medio subido
                String full_path = path_tmp + File.separator + fileName;
                System.out.println("Ruta destino: "+full_path);
                
                // FILTRO ->  Listado de formatos disponibles (filtro de seguridad)
                String extension = Utilidades.getFileExtension(full_path);
                System.out.println("Extensión: "+extension);
    
                if (!Arrays.asList(Constantes.FORMATOS_ENTRADA).contains(extension)) {
                    // Formato no permitido. Termina el proceso.
                    System.out.println("Formato no válido!"); 

                    // Respuesta html del servlet
                    writer.println("El archivo especificado no tiene un formato válido para el repositorio. <br/><br/>");
                    writer.println("Puede consultar los formatos disponibles en la página anterior.<br/><br/>"); // Pendiente!
                    next_page = next_page_ERROR;
                }
                else {
                    // Formato válido: Continua el proceso.
                    System.out.println("Formato válido!");

                    // Recuperar el archivo
                    out = new FileOutputStream(new File(full_path));
                    filecontent = filePart.getInputStream();

                    int read;
                    final byte[] bytes = new byte[1024]; // Tamaño del buffer: 1 kbyte

                    while ((read = filecontent.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    System.out.println("Archivo subido: \'"+fileName+"\' en "+path_tmp+"\n"); 

                    // Instanciar el medio
                    medio_subido = new MedioMultimedia(full_path);
                    medio_subido.setTitulo(titulo);
                    
                    // Añadir el propietario al medio
                    int usuario_id = 0;
                    HttpSession session=request.getSession(false); // No crea la sesión si no existe
                    if (session != null) {
                        System.out.println("Upload Servlet: Sesion no nula.");
                        Object atributo = session.getAttribute("usuario_id");
                        usuario_id = (atributo!=null)?Integer.valueOf(atributo.toString()):0;
                    }
                    // Si el usuario es 0 puedo cancelar el proceso aquí...
                    // Si el atributo es nulo es que la sesión ha expirado: Debería controlar esto antes de iniciar la carga
                    // Conforme está si expira la sesión el filtro actúa y redirige a la página de violación de acceso.
                    // Puedo controlar el estado de la sesión en el filtro --> Hecho
                      // Según la actuación del filtro no llegaría hasta aquí una petición sin sesión ni usuario == null.
                      // Dejo la comprobación y la actuación correspondiente de todos modos.
                    
                    medio_subido.setUsuario(new Usuario(usuario_id));

                    /* 2) Base de datos: Insertar el registro asociado al medio
                    -----------------------------------------------------------*/
                    miRepositorio.crearConexion(); // Abrir conexión con base de datos
                    medio_subido.setEstado(MedioMultimedia.Estados.PROCESANDO);
                    medio_subido.guardar(miRepositorio.getConnection()); 
                    miRepositorio.cerrarConexion(); // Cerrar conexión con base de datos
                    System.out.println("El medio se ha almacenado correctamente en BD.\n");
                        // medio_subido.borrar(miRepositorio.getConnection()); // Probar el método -> Funciona


                    /* 3) Tarea de conversión
                       ----------------------*/

                    // Respuesta html del servlet
                    writer.println("Archivo \'" + fileName + "\' subido correctamente.<br/><br/>"); // Mensaje de confirmación de los pasos 1 y 2.
                    writer.println("Realizando la conversión en segundo plano....<br/><br/>");

                    // Conversión uni-hilo
                    MediaFile carpeta_destino = new MediaFile(path_media_base);
                    new Thread(new Conversion(medio_subido, carpeta_destino, perfiles_multimedia, miRepositorio)).start();
                    System.out.println(" -> Tarea de conversión...");
                    
                    // Página de redirección.
                    next_page = next_page_OK;
                    
                }
            }
            
        } catch (FileNotFoundException fne) { // Excepción: Archivo no encontrado
            
            // Respuesta html del servlet 
            writer.println("No se ha especificado ningún archivo. <br/><br/>");
            writer.println("ERROR: " + fne.getMessage()); 
            // Log
            System.out.println("ERROR: No se ha especificado ningún archivo.\n"+fne.getMessage());
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, fne);
        
        } catch (ExcepcionesRepositorio exR) { // Excepción propia: Validación de datos y acceso a BD
            
            // Respuesta html del servlet 
            writer.println("No se ha podido guardar el medio subido. <br/><br/>");
            writer.println("ERROR: " + exR.getMensajeError()); 
            // Log
            System.out.println("ERROR: \n"+exR.getMensajeError());
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, exR);
        
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) { // Otras excepciones
            
            // Respuesta html del servlet 
            writer.println("ERROR interno: " + ex.getMessage()); 
            // Log
            System.out.println("ERROR:\n"+ex.getMessage());
            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
    
            // Respuesta html del servlet: Enlace para la siguiente página (index.html por defecto)
            writer.print("<a href="+next_page+"><img src=\"./resources/images/volver.png\" style=\"height: 35px\" alt=\"Volver\"/></a>"); // Enlace de retorno en imagen.
            writer.close();

            // Cerrar operaciones de escritura
            if (out != null){
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }      
    }

    @Override
    public void destroy() {
        
        // 1) Cerrar conexión a base de datos --> Ya no es necesario en la última versión
        //    Se abre y se cierra la conexión en cada proceso/petición. Si se intenta cerrar aquí puede que lance una excepción, pero no afecta al funcionamiento de la aplicación.
        //    ¿Es preferible cerrarla dos veces a que se quede abierta? De momento comento el código
//        try {
//             miRepositorio.cerrarConexion();
//        } catch (ExcepcionesRepositorio ex) {
//            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // 2) Borrar todos los archivos temporales 
        //   *!Duda: ¿Y si se borran antes de que acabe la conversión? ¿Realizar esta tarea en la clase Conversion? 
        //   -> Resuelta: El destroy solo se ejecuta cuando finaliza el tiempo de vida del servlet. 
        //                El servlet tiene un periodo de vida mayor que la vista del usuario, por lo que puede irse a la lista principal y el servlet sigue activo. 
        
        // Resultado de la experimentación: Para archivos pesados, ha ocurrido que se ha finalizado el tiempo de vida antes de tereminar la conversión, dejando a medio el proceso.
        // Se translada este código a la clase conversión y al init().
        
//        System.out.println("\nUpload -> destroy()\n");
//        try {
//            System.out.println("Limpiando directorio: "+path_tmp+"\n");
//            FileUtils.cleanDirectory(new File(path_tmp));
//        } catch (IOException ex) {
//            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        super.destroy(); 
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
        
        processRequest(request, response);
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
        
        processRequest(request, response);
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

