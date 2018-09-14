/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.conversion;

import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.RecursoMultimedia;

/**
 * Clase destinada a trabajar en la conversión de archivos multimedia.
 * 
 * - Control de versiones -
 * Version 1: El método toFFMPEG es dinámico. Da valor al atributo de instancia ffmpeg_command.
 * Version 2: El método toFFMPEG es estático. De esta forma desacoplo el uso de este método de la instancia.
 * Version 3: Tras la reestructuración. Trabajo con MediaFile en lugar de con MedioMultimedia ya que lo único que 
 *            me interesa para la conversión es el path.
 * Version 4: Separación del profile y su nivel en dos parámetros: profile y level
 * 
 * @author Juan Carlos
 */
public class FFMPEG {
   
    private String ffmpeg_command;
    private int errorCode;

    // Constructor
    public FFMPEG(){
        this.ffmpeg_command = null;
        this.errorCode = -1;
    }
    
    // Métodos
    // 1. Crear sentencia
    /**
     * Genera el comando de ejecución FFMPEG asociado al RecursoMultimedia requerido.
     * @param recurso
     * @return
     */
    public static String toFFMPEG (RecursoMultimedia recurso){
      
        // Comando ffmpeg
        String fc;
        
        // Pasos previos: Obtención de valores
        PerfilMultimedia perfil = recurso.getPerfilMultimedia();
        String path_origen = recurso.getMedioMultimediaOrigen().getMediaFile().getAbsolutePath();
        String path_destino = recurso.getFullPath_conversion();
        
        
        /*------------------------------------
          Composición de la sentencia ffmpeg 
        --------------------------------------*/
         
        fc = "ffmpeg ";
        //fc = fc.concat("-version"); // Comando básico para pruebas -> Muestra la versión de ffmpeg
        
        // Archivo de entrada
        fc = fc.concat("-i \""+path_origen+"\" ");

        // Parámetros generales
        // + Sobrescribir
        if (perfil.isSobrescribir()) {
            fc = fc.concat("-y ");
        } 
        else {
            fc = fc.concat("-n ");
        }
        
        /* + Formato de salida -> Me planteo eliminar esta opcion, ya que siempre hay 
             que definir explicitamente el formato del archivo de salida. 
             Esta opción solo actua de chequeo ante inconsistencias que solo pueden 
             darse cuando no se definan el nombre y la extensión del medio a convertir.*/
        if (perfil.isForzarFormato() && !perfil.getFormatoSalida().equals("")) {// Consistencia: Si se define el formato de salida, éste no debe estar vacío.
            fc = fc.concat("-f "+perfil.getFormatoSalida()+" "); 
        }
        
        // Parámetro necesario para utilizar el codec experimental AAC
        fc = fc.concat("-strict -2 ");

        // I. Tratamiento de video
        // -----------------------
        if (!perfil.isOnlyAudo()) {
            
            // Codec de video
            if (!perfil.getCodec_video().equals("")) {
                fc = fc.concat("-c:v "+perfil.getCodec_video()+" ");
            }
        
            // Tasa de cuadro
            if (!perfil.getFotogramas_video().equals("")) {
                fc = fc.concat("-r "+perfil.getFotogramas_video()+" ");
            }
            
            // Resolución
            if (!perfil.getResolucion_video().equals("")) {
                fc = fc.concat("-s "+perfil.getResolucion_video()+" ");
            }
            
            // Tasa binaria de video
            if (!perfil.getBitrate_video().equals("")) {
                fc = fc.concat("-b:v "+perfil.getBitrate_video()+" ");      
            }
            
            // Profile
            if (!perfil.getProfile_video().equals("")) {
                fc = fc.concat("-profile:v "+perfil.getProfile_video()+" ");
                
                // Level
                if (!perfil.getProfileLevel_video().equals("")) {
                    fc = fc.concat("-level "+perfil.getProfileLevel_video()+" ");
                }
            }
        }
        
        // II. Tratamiento de audio
        // ------------------------
        
        // Codec de audio
        if (!perfil.getCodec_audio().equals("")) {
            fc = fc.concat("-c:a "+perfil.getCodec_audio()+" ");
        }
        
        // Frecuencia de muestreo
        if (!perfil.getFreqMuestreo_audio().equals("")) {
            fc = fc.concat("-ar "+perfil.getFreqMuestreo_audio()+" ");
        }
        
        // Número de canales
        if (!perfil.getCanales_audio().equals("")) {
            fc = fc.concat("-ac "+perfil.getCanales_audio()+" ");
        }
        
        // Tasa binaria
        if (!perfil.getBitrate_audio().equals("")) {
            fc = fc.concat("-b:a "+perfil.getBitrate_audio()+" ");
        }
        
        // Profile
        if (!perfil.getProfile_audio().equals("")) {
            fc = fc.concat("-profile:a "+perfil.getProfile_audio()+" ");
            
            // Level
            if (!perfil.getProfileLevel_audio().equals("")) {
                fc = fc.concat("-level "+perfil.getProfileLevel_audio()+" ");
            }
        }
        // ------------------------
        
        // Archivo de salida
        fc = fc.concat("\""+path_destino+"\"");    
        
        /*------------------------------------*/
        
        return fc;
    }
    
    // 2. Convertir archivo
    /**
     * Ejecuta la sentencia asociada al RecursoMultimedia requerido y define el código de error asociado al proceso.
     * @param recurso
     */
    public void convertir (RecursoMultimedia recurso){
     
        // Composición del comando
        setErrorCode(-1);
        setFFMPEG_command(toFFMPEG(recurso));

        // Ejecución
        if (getFFMPEG_command() != null) {
            setErrorCode(Shell.execute(getFFMPEG_command()));
        }
        System.out.println("La sentencia de ejecución es:\n"+getFFMPEG_command());
    }
    
    /* Getters
      --------*/ 
    
    public String getFFMPEG_command() {
        return ffmpeg_command;
    }

    public int getErrorCode() {
        return errorCode;
    }
    
    /* Setters
      --------*/ 
    // Ojo! No encuentro sentido al uso de estos setters como públicos. Los implemento privados
    
    // La sentencia ffmpeg siempre se define antes de la conversión. No tiene sentido definirla desde fuera.
    private void setFFMPEG_command(String ffmpeg_command) {
        this.ffmpeg_command = ffmpeg_command;
    }

    // En cada ejecución se obtiene un nuevo código de error. Éste viene dado por la función shell. No tiene utilidad definirlo a mano desde fuera.
    private void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
}