/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import repositorio.config.Constantes;
import repositorio.controlador.servlets.UploadServlet;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;

/**
 * Clase destinada a representar los diferentes perfiles de formato y calidad de los archivos multimedia.
 * @author Juan Carlos
 */
@XmlRootElement
public class PerfilMultimedia /*implements Persistencia*/ {
    
    // Parámetro de identificación tanto en BD como en Properties
    private Integer id;

    // Parámetros generales
    private String nombre;
    private String descripcion;
    private String formatoSalida;
    private boolean sobrescribir;
    private boolean forzarFormato;
    private boolean soloAudio;
    
    // Parámetros de video
    private String profile_video;
    private String profileLevel_video;
    private String codec_video;
    private String fotogramas_video;
    private String resolucion_video;
    private String bitrate_video;
    
    // Parámetros de audio
    private String profile_audio;
    private String profileLevel_audio;
    private String codec_audio;
    private String freqMuestreo_audio;
    private String canales_audio;
    private String bitrate_audio;
    
    // Verificacion del proceso de interacción con BD: cargar/guardar/borrar   => Es false por defecto. No necesito definirlo en el constructor.
    private boolean persistenciaOK;  
    

    /* Constructores
       -------------*/
    
    // Constructor vacío
    public PerfilMultimedia(){
        
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,"","","",true,false,false,"","","","","","","","","","","","");
    }
    // Constructor con id
    public PerfilMultimedia(Integer id){
        
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(id,"","","",true,false,false,"","","","","","","","","","","","");
    }
    // Constructor 1: Solo nombre
    public PerfilMultimedia(String nombre){
        
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,nombre,"","",true,false,false,"","","","","","","","","","","","");
    }
    
    // Constructor 1: Id y nombre
    public PerfilMultimedia(Integer id, String nombre){
        
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(id,nombre,"","",true,false,false,"","","","","","","","","","","","");
    }
    
    // Constructor predefinido para video: No lo utilizo
    public PerfilMultimedia(Boolean predefinido){

        // Posible perfil por defecto
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,"Perfil por defecto","",null,true,false,false,"baseline","3.0","h264","30","480x420","1500k","baseline","3.0","aac","22050","2","128k");
    }
    
    /*
    // Constructor predefinido para audio: No lo contemplo en esta versión.
    public PerfilMultimedia(boolean soloAudio){
        
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,"Perfil por defecto","",null,true,false,soloAudio,"baseline","3.0","h264","30","480x420","1500k","baseline","3.0","aac","22050","2","128k");
    }
    */
    // Constructor para perfil de audio con formato de salida implícito
    public PerfilMultimedia(String nombre, String profile_audio, String profileLevel_audio, String codec_audio, String freqMuestreo_audio, String canales_audio, String bitrate_audio) {
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0, nombre, "", null, true, false, true, null, null, null, null, null, null, profile_audio, profileLevel_audio, codec_audio, freqMuestreo_audio, canales_audio, bitrate_audio);
    }
    // Constructor de audio completo
    public PerfilMultimedia(String nombre, String formatoSalida, boolean sobrescribir, boolean forzarFormato, String profile_audio, String profileLevel_audio, String codec_audio, String freqMuestreo_audio, String canales_audio, String bitrate_audio, String perfilBaseline_audio) {
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,nombre,"",formatoSalida, sobrescribir, forzarFormato, true, null, null, null, null, null, null, profile_audio, profileLevel_audio, codec_audio, freqMuestreo_audio, canales_audio, bitrate_audio);
    }

    // Contructor para perfil de video con formato de salida implícito
    public PerfilMultimedia(String nombre, String profile_video, String profileLevel_video, String codec_video, String fotogramas_video, String resolucion_video, String bitrate_video, String profile_audio, String profileLevel_audio, String codec_audio, String freqMuestreo_audio, String canales_audio, String bitrate_audio) {
        // id,nombre,descripcion,formatoSalida,sobrescribir,forzarFormato,soloAudio,profile_video,profileLevel_video,codec_video,fotogramas_video,resolucion_video,bitrate_video,profile_audio,profileLevel_audio,codec_audio,freqMuestreo_audio,canales_audio,bitrate_audio
        this(0,nombre,"",null, true, false, false, profile_video, profileLevel_video, codec_video, fotogramas_video, resolucion_video, bitrate_video, profile_audio, profileLevel_audio, codec_audio, freqMuestreo_audio, canales_audio, bitrate_audio);
    }
    
    // Constructor completo
    public PerfilMultimedia(Integer id,String nombre, String descripcion, String formatoSalida, boolean sobrescribir, boolean forzarFormato, boolean soloAudio, String profile_video, String profileLevel_video, String codec_video, String fotogramas_video, String resolucion_video, String bitrate_video, String profile_audio, String profileLevel_audio, String codec_audio, String freqMuestreo_audio, String canales_audio, String bitrate_audio) {
        this.id= id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.formatoSalida = formatoSalida;
        this.sobrescribir = sobrescribir;
        this.forzarFormato = forzarFormato;
        this.soloAudio = soloAudio;
        
        this.profile_video = profile_video;
        this.profileLevel_video = profileLevel_video;
        this.codec_video = codec_video;
        this.fotogramas_video = fotogramas_video;
        this.resolucion_video = resolucion_video;
        this.bitrate_video = bitrate_video;
        
        this.profile_audio = profile_audio;
        this.profileLevel_audio = profileLevel_audio;
        this.codec_audio = codec_audio;
        this.freqMuestreo_audio = freqMuestreo_audio;
        this.canales_audio = canales_audio;
        this.bitrate_audio = bitrate_audio;
    }
    
     /*-------------*/
    
    /**
     * Representación del objeto en JSON.
     * Conversión a JSON. Alternativa al uso de la librería GSON/JAXB.
     * @return 
     */
    @Override
    public String toString() {
        String toString;
           
        String id_cad = (this.id != null)?this.id.toString():"null";
        String nombre_cad = (this.nombre != null)?this.nombre:"";
        String descripcion_cad = (this.descripcion != null)?this.descripcion:"";
        String formatoSalida_cad= (this.formatoSalida != null)?this.formatoSalida:"";
        String sobrescribir_cad = (this.sobrescribir == true)?"true":"false";
        String forzarFormato_cad = (this.forzarFormato == true)?"true":"false";
        String soloAudio_cad = (this.soloAudio == true)?"true":"false";
        String profile_video_cad = (this.profile_video != null)?this.profile_video:"";
        String profileLevel_video_cad = (this.profileLevel_video != null)?this.profileLevel_video:"";
        String codec_video_cad = (this.codec_video != null)?this.codec_video:"";
        String fotogramas_video_cad = (this.fotogramas_video != null)?this.fotogramas_video:"";
        String resolucion_video_cad = (this.resolucion_video != null)?this.resolucion_video:"";
        String bitrate_video_cad = (this.bitrate_video != null)?this.bitrate_video:"";
        String profile_audio_cad = (this.profile_audio != null)?this.profile_audio:"";
        String profileLevel_audio_cad = (this.profileLevel_audio != null)?this.profileLevel_audio:"";
        String codec_audio_cad = (this.codec_audio != null)?this.codec_audio:"";
        String freqMuestreo_audio_cad = (this.freqMuestreo_audio != null)?this.freqMuestreo_audio:"";
        String canales_audio_cad = (this.canales_audio != null)?this.canales_audio:"";
        String bitrate_audio_cad = (this.bitrate_audio != null)?this.bitrate_audio:"";
        
        // No disponibles en esta versión (Carga desde .properties)
        //String fechaCreacion_cad = (fecha_creacion != null)?this.fecha_creacion.toString().substring(0, this.fecha_creacion.toString().length()-2):"null";
        //String fechaModificacion_cad = (fecha_modificacion != null)?this.fecha_modificacion.toString().substring(0, this.fecha_modificacion.toString().length()-2):"null";
        
        toString = "{\"id\":"+id_cad+", \"nombre\":\""+nombre_cad+"\", \"descripcion\":\""+descripcion_cad+"\", \"formatoSalida\":\""+formatoSalida_cad+"\", \"sobrescribir\":\""+sobrescribir_cad+"\", \"forzarFormato\":\""+forzarFormato_cad+"\", \"soloAudio\":\""+soloAudio_cad+"\", \"profile_video\":\""+profile_video_cad+"\", \"profileLevel_video\":\""+profileLevel_video_cad+"\", \"codec_video\":\""+codec_video_cad+"\", \"fotogramas_video\":\""+fotogramas_video_cad+"\", \"resolucion_video\":\""+resolucion_video_cad+"\", \"bitrate_video\":\""+bitrate_video_cad+"\", \"profile_audio\":\""+profile_audio_cad+"\", \"profileLevel_audio\":\""+profileLevel_audio_cad+"\", \"codec_audio\":\""+codec_audio_cad+"\", \"freqMuestreo_audio\":\""+freqMuestreo_audio_cad+"\", \"canales_audio\":\""+canales_audio_cad+"\", \"bitrate_audio\":\""+bitrate_audio_cad+"\"}";
 
        return toString; 
    }

    /*----------------------------------------------------------------*/

    /**
     * Método cargar desde archivo properties a partir de un id específico.
     * @param id
     */
    public void cargarFromProperties(Integer id) {

        setPersistenciaOK(false);
        setId(id);
        cargarFromProperties();    
    }
    
     /**
     * Método cargar desde archivo properties a partir del id de la instancia.
     */
    public void cargarFromProperties() {

        setPersistenciaOK(false);
        
        // Comprobar que la instancia actual tiene un id no nulo
        if (getId() !=null) {
            // Compone el nombre del archivo de configuración y llama al método cargarFromPropertiesByName para realizar la carga.
            String filename_perfil = Constantes.CONFIG_PATH+Constantes.FILENAME_BASE_PERFIL+this.getId()+".properties";
            cargarFromProperties(filename_perfil);
        }  
    }
    
    /**
     * Método cargar desde archivo de propiedades a partir del nombre del archivo .properties.
     * @param filename_perfil
     */
    public void cargarFromProperties(String filename_perfil) {

        setPersistenciaOK(false);

        // Cargar el perfil solicitado
        Properties prop_perfil = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input;

        // Cargar propiedades del perfil
        input = classLoader.getResourceAsStream(filename_perfil);
        if (input != null){
            
            try {
                prop_perfil.load(input);
                System.out.println("Classloader: Archivo de configuracion cargado para el perfil: "+filename_perfil);

                // Instanciar el PerfilMultimedia a partir de los datos del fichero de configuración correspondiente
                this.id = Integer.valueOf(prop_perfil.getProperty("id"));
                this.nombre = prop_perfil.getProperty("nombre");
                this.descripcion = prop_perfil.getProperty("descripcion");
                this.formatoSalida = prop_perfil.getProperty("formatoSalida");
                this.sobrescribir = Boolean.valueOf(prop_perfil.getProperty("sobrescribir"));
                this.forzarFormato = Boolean.valueOf(prop_perfil.getProperty("forzarFormato"));
                this.soloAudio = Boolean.valueOf(prop_perfil.getProperty("soloAudio"));
                this.profile_video = prop_perfil.getProperty("profile_video");
                this.profileLevel_video = prop_perfil.getProperty("profileLevel_video");
                this.codec_video = prop_perfil.getProperty("codec_video");
                this.fotogramas_video = prop_perfil.getProperty("fotogramas_video");
                this.resolucion_video = prop_perfil.getProperty("resolucion_video");
                this.bitrate_video = prop_perfil.getProperty("bitrate_video");
                this.profile_audio = prop_perfil.getProperty("profile_audio");
                this.profileLevel_audio = prop_perfil.getProperty("profileLevel_audio");
                this.codec_audio = prop_perfil.getProperty("codec_audio");
                this.freqMuestreo_audio = prop_perfil.getProperty("freqMuestreo_audio");
                this.canales_audio = prop_perfil.getProperty("canales_audio");
                this.bitrate_audio = prop_perfil.getProperty("bitrate_audio");

                setPersistenciaOK(true); // Carga OK 
                
            } catch (IOException ex) {
                // No debería capturar nunca esta excepción ya que está controlada con el if previo. Si el input es null, el flag es false.
                Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* Métodos para acceder a los perfiles definidos en los .properties de forma estática
       ----------------------------------------------------------------------------------*/

    /**
     * Método estático para cargar únicamente el nombre de un determinado perfil, definido en un archivo .properties
     * @param perfil_id
     * @return
     */
    public static String cargarNombrePerfilFromProperties(Integer perfil_id) {
        
        // Declaración de variables
        Properties prop_perfil;
        ClassLoader classLoader;
        InputStream input;
        String fileName_perfil;
        String nombre_perfil;

        // Valor por defecto de la variable de retorno.
        nombre_perfil = null;
        
        try { 

            // Composición del nombre del perfil
            fileName_perfil = Constantes.FILENAME_BASE_PERFIL+String.valueOf(perfil_id)+Constantes.PROPERTIES_EXTENSION; // "repositorio/config/perfil_id.properties" con id: perfil_id
            
            // Cargar perfil
            classLoader = Thread.currentThread().getContextClassLoader();
            input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+fileName_perfil);

            // Excepción: archivo de configuración no encontrado.
            if (input == null){
                
                String error = "¡El perfil que intenta cargar no está definido!";
                System.out.println(error);
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_LOGICO,error);
            
            } else {
                 
                // Cargar propiedades del perfil
                prop_perfil = new Properties();
                prop_perfil.load(input);
                
                System.out.println("Classloader: Archivo de configuracion cargado para el perfil: "+fileName_perfil);

                // Cargar el nombre del perfil
                nombre_perfil = prop_perfil.getProperty("nombre");

            }
            
        } catch (IOException | ExcepcionesRepositorio ex) {
            Logger.getLogger(Repositorio.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        return nombre_perfil;
    }
    
    /**
     * Método estático orientado a cargar los perfilesMultimedia configurados en archivos.properties
     * y devolverlos en un array.
     * @return
     */
    public static List<PerfilMultimedia> cargarPerfilesFromProperties() {
        
        List<PerfilMultimedia> listaPerfiles = new ArrayList<>();
        PerfilMultimedia perfil_n;
        int numero_perfiles;
        String fileNamePerfil_n;

        try {  
            // Cargar archivo de configuración
            Properties prop_config = new Properties();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+Constantes.CONFIG_FILE);
            if (input == null) {
                System.out.println("No se ha podido cargar el archivo de configuración general: "+Constantes.CONFIG_FILE+" en el path "+Constantes.CONFIG_PATH);
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_IO,"No se ha podido cargar el archivo de configuración general: "+Constantes.CONFIG_FILE+" en el path "+Constantes.CONFIG_PATH);
            }
            prop_config.load(input);
            System.out.println("Classloader: Archivo de configuracion general cargado");

            // Cargar el perfil enésimo
            Properties prop_perfil = new Properties();
            numero_perfiles = (int) Integer.valueOf(prop_config.getProperty("numero_perfiles", "0"));
            if (numero_perfiles<1) {
                System.out.println("¡No hay suficientes perfiles para realizar la conversión de archivos!");
                throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_LOGICO,"¡No hay suficientes perfiles para realizar la conversión de archivos!");
            }

            // Cargar los perfiles y añadirlos a la lista
            for (int i=1; i<= numero_perfiles; i++){

                // Composición del nombre del perfil n
                fileNamePerfil_n = Constantes.FILENAME_BASE_PERFIL+String.valueOf(i)+Constantes.PROPERTIES_EXTENSION; // "repositorio/config/perfil_n.properties" con n: 1,2,3,...

                // Cargar propiedades del perfil n
                input = classLoader.getResourceAsStream(Constantes.CONFIG_PATH+fileNamePerfil_n);
                // Excepción: archivo de configuración no encontrado.
                if (input == null){
                    System.out.println("No se puede cargar el perfil multimedia: "+fileNamePerfil_n+" en el path "+Constantes.CONFIG_PATH);
                    //throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_IO,"No se puede cargar el perfil multimedia: "+fileNamePerfil_n+" en el path "+Constantes.CONFIG_PATH);
                    // No lanzo la excepción para que no detenga el proceso. Puede que falle un perfil, pero no tiene por qué afectar al resto.
                } else {
                    prop_perfil.load(input);

                    System.out.println("Classloader: Archivo de configuracion cargado para el perfil: "+fileNamePerfil_n);

                    // Instanciar el PerfilMultimedia a partir de los datos del fichero de configuración correspondiente
                    perfil_n = new PerfilMultimedia(Integer.valueOf(prop_perfil.getProperty("id"))); // Constructor por id
                    perfil_n.setNombre(prop_perfil.getProperty("nombre"));
                    perfil_n.setFormatoSalida(prop_perfil.getProperty("formatoSalida"));
                    perfil_n.setSobrescribir(Boolean.valueOf(prop_perfil.getProperty("sobrescribir")));
                    perfil_n.setForzarFormato(Boolean.valueOf(prop_perfil.getProperty("forzarFormato")));
                    perfil_n.setOnlyAudo(Boolean.valueOf(prop_perfil.getProperty("soloAudio")));
                    perfil_n.setProfile_video(prop_perfil.getProperty("profile_video"));
                    perfil_n.setProfileLevel_video(prop_perfil.getProperty("profileLevel_video"));
                    perfil_n.setCodec_video(prop_perfil.getProperty("codec_video"));
                    perfil_n.setFotogramas_video(prop_perfil.getProperty("fotogramas_video"));
                    perfil_n.setResolucion_video(prop_perfil.getProperty("resolucion_video"));
                    perfil_n.setBitrate_video(prop_perfil.getProperty("bitrate_video"));
                    perfil_n.setProfile_audio(prop_perfil.getProperty("profile_audio"));
                    perfil_n.setProfileLevel_audio(prop_perfil.getProperty("profileLevel_audio"));
                    perfil_n.setCodec_audio(prop_perfil.getProperty("codec_audio"));
                    perfil_n.setFreqMuestreo_audio(prop_perfil.getProperty("freqMuestreo_audio"));
                    perfil_n.setCanales_audio(prop_perfil.getProperty("canales_audio"));
                    perfil_n.setBitrate_audio(prop_perfil.getProperty("bitrate_audio"));

                    // Añadir perfil actual al array de perfiles
                    listaPerfiles.add(perfil_n);  
                }
            }
        } catch (IOException | ExcepcionesRepositorio ex) {
            Logger.getLogger(Repositorio.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        return listaPerfiles;
    }
    
    /*
      Método donde pretendo cambiar el modo de cargar los perfiles. 
      No sigo trabajando en él porque no encuentro el modo de hacer lo que quiero.
      La idea es obtener directamente todos los .properties que cumplan con el patrón "perfil_" como nombre de archivo,
      pero no consigo acceder al directorio del proyecto con rutas relativas.
      Al obtener los perfiles desde .properties de forma temporal, no sigo invirtiendo tiempo en este método.
      En un futuro los perfiles se obtendran de base de datos.
    */
    public static List<PerfilMultimedia> cargarPerfilesFromPropertiesPruebas() {
    
        List<PerfilMultimedia> listaPerfiles = new ArrayList<>();

        // Path
        File dir = new File("."); // "." -> Path raíz del servidor apache en este caso. Encuentro archivos como (catalina.bat, catalina.sh, commons-daemon-native-tar.gz, daemon.sh, ...). No consigo obtener un path relativo al proyecto.
//        File [] files = dir.listFiles((File dir1, String name) -> name.contains(Constantes.FILENAME_BASE_PERFIL));
        File [] files = dir.listFiles((File dir1, String name) -> !name.isEmpty()); // Obtengo todos los archivos del path definido

        System.out.println("----------------------------");
        System.out.println("Pruebas!");
        if (files != null){
            System.out.println("Tamaño del vector: "+files.length+"\nContenido:");
            for (File perfil : files) {
                System.out.println(perfil); // Imprime el listado de archivos encontrados en el path
            }
        }
        System.out.println("----------------------------");
       
        return listaPerfiles;
    }
    
    
    /* Getters & Setters
      ------------------*/ 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { 
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFormatoSalida() {
        return formatoSalida;
    }

    public void setFormatoSalida(String formatoSalida) {
        this.formatoSalida = formatoSalida;
    }

    public boolean isSobrescribir() {
        return sobrescribir;
    }

    public void setSobrescribir(boolean sobrescribir) {
        this.sobrescribir = sobrescribir;
    }
    
    public boolean isForzarFormato() {
        return forzarFormato;
    }

    public void setForzarFormato(boolean forzarFormato) {
        this.forzarFormato = forzarFormato;
    }

    public boolean isOnlyAudo() {
        return soloAudio;
    }

    public void setOnlyAudo(boolean soloAudio) {
        this.soloAudio = soloAudio;
    }

    public String getProfile_video() {
        return profile_video;
    }

    public void setProfile_video(String profile_video) {
        this.profile_video = profile_video;
    }

    public String getProfileLevel_video() {
        return profileLevel_video;
    }

    public void setProfileLevel_video(String profileLevel_video) {
        this.profileLevel_video = profileLevel_video;
    }
    
    public String getCodec_video() {
        return codec_video;
    }

    public void setCodec_video(String codec_video) {
        this.codec_video = codec_video;
    }

    public String getFotogramas_video() {
        return fotogramas_video;
    }

    public void setFotogramas_video(String fotogramas_video) {
        this.fotogramas_video = fotogramas_video;
    }

    public String getResolucion_video() {
        return resolucion_video;
    }

    public void setResolucion_video(String resolucion_video) {
        this.resolucion_video = resolucion_video;
    }

    public String getBitrate_video() {
        return bitrate_video;
    }

    public void setBitrate_video(String bitrate_video) {
        this.bitrate_video = bitrate_video;
    }

    public String getProfile_audio() {
        return profile_audio;
    }

    public void setProfile_audio(String profile_audio) {
        this.profile_audio = profile_audio;
    }

    public String getProfileLevel_audio() {
        return profileLevel_audio;
    }

    public void setProfileLevel_audio(String profileLevel_audio) {
        this.profileLevel_audio = profileLevel_audio;
    }
    
    public String getCodec_audio() {
        return codec_audio;
    }

    public void setCodec_audio(String codec_audio) {
        this.codec_audio = codec_audio;
    }

    public String getFreqMuestreo_audio() {
        return freqMuestreo_audio;
    }

    public void setFreqMuestreo_audio(String freqMuestreo_audio) {
        this.freqMuestreo_audio = freqMuestreo_audio;
    }

    public String getCanales_audio() {
        return canales_audio;
    }

    public void setCanales_audio(String canales_audio) {
        this.canales_audio = canales_audio;
    }

    public String getBitrate_audio() {
        return bitrate_audio;
    }

    public void setBitrate_audio(String bitrate_audio) {
        this.bitrate_audio = bitrate_audio;
    }
    
    public boolean isPersistenciaOK() { 
        return persistenciaOK;
    }

    public void setPersistenciaOK(boolean persistenciaOK) {
        this.persistenciaOK = persistenciaOK;
    }
 
}
