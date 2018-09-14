/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.logica.conversion;

import java.io.IOException;

/**
 * Clase para simular la linea de comandos de windows y poder ejecutar en ella comandos nativos. Destinado al uso de la función ffmpeg.
 * Ojo: todo el flujo de salida de Process es de tipo ErrorStream en lugar de InputStream.
 * @author Juan Carlos
 */
public class Shell {
    
    public static int execute(String command) {
        
        int errorCode = -1;
        try { 
            
            String osName = System.getProperty("os.name" );
            String[] cmd = new String[3];
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = command;
            
            // Excepcion: Windows antiguos. En estas versiones la consola de comandos se llamaba command.com
            if( osName.equals( "Windows 95" ) || osName.equals( "Windows 98" ) || osName.equalsIgnoreCase("Windows ME" ) ) {
                cmd[0] = "command.com";
            }
            
            Runtime rt = Runtime.getRuntime();
             System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
            Process proc = rt.exec(cmd);
            
            // Any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "OUTPUT"); // Ojo! Toda la salida es de este tipo -> La salida de FFMPEG es de este tipo (ErrorStream). Por eso pongo "OUTPUT" en lugar de "ERROR", para no dar pie a confusión en el shell.       
            
            // Any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT1"); 
                
            // Start the threads
            errorGobbler.start();
            outputGobbler.start();
                                    
            // Any error???
            errorCode = proc.waitFor();
             System.out.println("ExitValue: " + errorCode);  // Código de error generado
            
        } catch (IOException | InterruptedException ex) { 
            System.out.println("Error de ejecución en Shell.java!: \n"+ex.getMessage());
        }
        
        return errorCode;
    }
    
}
