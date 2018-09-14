/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.conversion;

import repositorio.modelo.logica.PerfilMultimedia;
import repositorio.modelo.logica.MedioMultimedia;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import repositorio.modelo.excepciones.ExcepcionesRepositorio;
import repositorio.modelo.logica.MediaFile;
import repositorio.modelo.logica.MedioMultimedia.Estados;
import repositorio.modelo.logica.RecursoMultimedia;
import repositorio.modelo.logica.Repositorio;

/**
 * Clase orientada a ejecutar el proceso de conversión en un hilo independiente.
 * @author Juan Carlos
 */
public class Conversion implements Runnable{

    private final MedioMultimedia medio_origen;
    private final MediaFile carpeta_destino;
    private final List<PerfilMultimedia> perfilesMultimedia;  
    private final Repositorio miRepositorio;
    
    /**
     * Conversión con un solo perfil. 
     * @param medio_origen
     * @param carpeta_destino
     * @param perfil 
     * @param miRepositorio 
     */
    public Conversion(MedioMultimedia medio_origen, MediaFile carpeta_destino, PerfilMultimedia perfil, Repositorio miRepositorio) {
        this.medio_origen = medio_origen;
        this.carpeta_destino = carpeta_destino;
        this.perfilesMultimedia = new ArrayList<>(1);
        this.perfilesMultimedia.add(perfil);
        this.miRepositorio = miRepositorio;
    }
    
    /**
    * Conversión con múltiples perfiles.
    * @param medio_origen
    * @param carpeta_destino
    * @param perfilesMultimedia 
    * @param miRepositorio 
    */
    public Conversion(MedioMultimedia medio_origen, MediaFile carpeta_destino, List<PerfilMultimedia> perfilesMultimedia, Repositorio miRepositorio) {
        this.medio_origen = medio_origen;
        this.carpeta_destino = carpeta_destino;
        this.perfilesMultimedia = perfilesMultimedia;
        this.miRepositorio = miRepositorio;
    }
    
    @Override
    public void run() {

        PerfilMultimedia perfil_n;
        RecursoMultimedia recurso_n;
        List<RecursoMultimedia> recursos;

        String path_destino_n;
        MediaFile carpeta_destino_n;
        FFMPEG miFFMPEG;
        boolean errores = false;
        recursos = new ArrayList<>();
            
        try {
            miRepositorio.crearConexion(); // Abrir conexión con base de datos
            
            /* Parte I. Preparar la conversión (Nuevo código)
               ----------------------------------------------*/
            
            for (Iterator<PerfilMultimedia> it = perfilesMultimedia.iterator(); it.hasNext();) { // Para cada perfil
                
                // Obtener perfil
                perfil_n = it.next();
                
                // Definir el path específico para cada recurso (subdirectorio)
                path_destino_n = carpeta_destino.getAbsolutePath()+ File.separator+perfil_n.getNombre();
                
                // Instanciar el MediaFile destino
                carpeta_destino_n= new MediaFile (path_destino_n);
                
                // Comprobar que el path destino existe
                if (!carpeta_destino_n.exists()) {
                    if (!carpeta_destino_n.mkdirs()) { // Crear directorio si no existe. Ojo! No es capaz de crear el directorio base pero si los subdirectorios: ¿Permisos?
                        throw new ExcepcionesRepositorio(ExcepcionesRepositorio.ERROR_FICHERO,"No se ha podido crear el subdirectorio "+path_destino_n+" para el perfil "+perfil_n.getNombre());
                    }
                }

                // Crear recurso enésimo y guardar en BD con estado EN_COLA.
                recurso_n = new RecursoMultimedia(carpeta_destino_n, medio_origen, perfil_n);
                recurso_n.setEstado(Estados.EN_COLA);
                recurso_n.guardar(miRepositorio.getConnection());
                recursos.add(recurso_n);
                System.out.println("El recurso se ha almacenado correctamente en BD.\n");
                
            }
            
            /* Parte II. Conversión
               --------------------*/
            
            for (int indice=0; indice < perfilesMultimedia.size(); indice++) { // Para cada perfil. (Utilizo un bucle for clásico porque necesito el índice)
                
                /* Fase 1. Preparar la conversión
                   ------------------------------*/
                // Obtener perfil enésimo
                perfil_n = perfilesMultimedia.get(indice);
                
                // Obtener recurso enésimo
                recurso_n = recursos.get(indice);

                // Cargar recurso y definir el nuevo estado
                recurso_n.setEstado(Estados.PROCESANDO);
                recurso_n.guardar(miRepositorio.getConnection());
                System.out.println("El recurso se ha actualizado correctamente en BD.\n");
                

                /* Fase 2. Conversión
                   ------------------*/   
                System.out.println("\nConvirtiendo archivo "+medio_origen.getMediaFile().getFullName()+" usando el perfil: "+perfil_n.getNombre()+"\n");

                // Muestra el comando ffmpeg utilizado para esta conversión (Opcional)
                String ffmpeg_command = FFMPEG.toFFMPEG(recurso_n);
                System.out.println("Comando ffmpeg: \n"+ffmpeg_command+"\n");

                // Conversión
                miFFMPEG = new FFMPEG();
                miFFMPEG.convertir(recurso_n);
                if (miFFMPEG.getErrorCode()==0) {
                    System.out.println("Perfil \""+perfil_n.getNombre()+"\": Archivo convertido con éxito!");
                    // Actuación -> Actualizar recurso en BD con estado OK.
                    System.out.println("\nActualizando estado en BD.");
                    recurso_n.setEstado(Estados.OK);
                    recurso_n.guardar(miRepositorio.getConnection());
                        // recurso_n.borrar(miRepositorio.getConnection()); // Probar el método -> Funciona
                }
                else {
                    System.out.println("¡Ha habido un problema de conversión con el perfil "+perfil_n.getNombre()+"!");
                    errores = true;
                    // Actuación -> Actualizar recurso en BD con estado ERROR
                    System.out.println("\nActualizando estado en BD.");
                    recurso_n.setEstado(Estados.ERROR);
                    recurso_n.guardar(miRepositorio.getConnection());
                } 
            }
            
            /* Parte III. Evaluación de la conversión
               --------------------------------------*/
            System.out.println("\n--------------------------------------------------");
            if (!errores) {
                System.out.println(" -> La conversión ha finalizado sin errores.");
                // Actuación -> Cambiar el estado en base de datos
                System.out.println("\nActualizando estado en BD.");
                medio_origen.setEstado(MedioMultimedia.Estados.OK);
                medio_origen.guardar(miRepositorio.getConnection());
            } 
            else {
                System.out.println(" -> La conversión ha finalizado con errores.");
                // Actuación -> Cambiar el estado en base de datos
                System.out.println("\nActualizando estado en BD.");
                medio_origen.setEstado(MedioMultimedia.Estados.ERROR);
                medio_origen.guardar(miRepositorio.getConnection());
            }
            System.out.println("--------------------------------------------------\n");
              
        } catch (ExcepcionesRepositorio exR) {
            System.out.println("ERROR: Excepción propia!\n"+exR.getMensajeError());
            Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, null, exR);
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println("ERROR: \n"+ex.getMessage());
            Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                // 1) Cerrar conexión con base de datos
                miRepositorio.cerrarConexion();
                
                // 2) Borrar archivo temporal subido  
                System.out.println("Borrando archivo de origen: "+medio_origen.getMediaFile().getAbsolutePath()+"\n");
                medio_origen.getMediaFile().delete();
       
            } catch (ExcepcionesRepositorio ex)  {
                Logger.getLogger(Conversion.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}