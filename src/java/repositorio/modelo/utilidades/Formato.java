/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.utilidades;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import repositorio.modelo.logica.MedioMultimedia.Estados;

/**
 * Esta clase ofrece una serie de procedimientos útiles para dar formato a los datos.
 * Contiene concretamente una serie de funciones toSQL que preparan datos para ser introducidos en lenguaje SQL en Base de Datos. 
 * @author Juan Carlos Rubio Garcia
 */
public class Formato {
    
    /**
     * Esta función prepara un entero INT a un tipo NUMBER de SQL, aunque por defecto en nuestra lógica son equivalentes, ya que la BD acepta datos de tipo INT tal como están en lenguaje java.
     * @param numero
     * @return String
     */
    public static String toSQL(Integer numero) {  
        String num = "NULL";  //String num = "0";
        if (numero != null) num = String.valueOf(numero);
        return num;
    }
    
    /**
     * Esta función formatea una cadena STRING como una cadena de tipo VARCHAR de SQL.
     * @param cadena
     * @return String
     */
    public static String toSQL(String cadena) {
        String cad = "NULL";
        if (cadena != null) {
            
            // Corrección de algunos caracteres de escape. Hay más pero no los voy a contemplar: \b, \n, \r, \t, \Z
            //System.out.println("Pre format: "+cadena);
            cad = Formato.escapeCharacters(cadena);
            cad = "'"+cad+"'";
            //System.out.println("Post format: "+cad);
        }
        return cad;
    }
    
    public static String escapeCharacters(String cadena){
        
        String cad = "NULL";
         if (cadena != null) {
            // Corrección de algunos caracteres de escape. Hay más pero no los voy a contemplar: \b, \n, \r, \t, \Z
            //System.out.println("Pre format: "+cadena);
            cad = cadena;
            cad = cad.replace("\\", "\\\\");    //  \\  A backslash (“\”) character.
            cad = cad.replace("0x00", "\\0");   //  \0  An ASCII NUL (0x00) character.
            cad = cad.replace("\'", "\\'");     //  \'  A single quote (“'”) character.
            cad = cad.replace("%", "\\%");      //  \%  A “%” character. See note following the table.
            //cad = cad.replace("_", "\\"+"_");   //  \_  A “_” character. See note following the table.  Si aplico esta corrección no funciona bien.
            //System.out.println("Post format: "+cad);
        }
        return cad;
    }
    
    /**
     * Esta función formatea un Estado (ver clase MedioMultimedia) como una cadena de tipo VARCHAR de SQL.
     * @param estado
     * @return String
     */
    public static String toSQL(Estados estado) {
        String cad = "NULL";
        if (estado != null) 
            cad = "'"+String.valueOf(estado)+"'";
        return cad;
    }

    /**
     * Esta función formatea una fecha DATE, como una fecha valida para lenguaje SQL dada en una cadena de tipo String.
     * @param fecha
     * @return String
     */
    public static String toSQL(Date fecha) {
        String cad = "NULL"; //"'0000-00-00'";
        if (fecha != null) {
            SimpleDateFormat formatofecha= new SimpleDateFormat("yyyy-MM-dd");
            cad = "'"+formatofecha.format(fecha)+"'";
        }
        return cad;
    }
    
    /**
     * Método para convertir fechas. Date to String.
     * Utiliza el formato "dd/mm/yyyy" para representar la fecha.
     * @param fecha
     * @return String
     */
    public static String formatearFecha(Date fecha) {
        String fechaCadena = "";
        if (fecha != null) {
            SimpleDateFormat formatofecha= new SimpleDateFormat("dd/MM/yyyy");
            fechaCadena = formatofecha.format(fecha);
        }
        return fechaCadena;
    }
    
    /**
     * Método para convertir fechas. String to Date.
     * Requiere el formato "yyyy/MM/dd" para representar la fecha de entrada.
     * @param fecha
     * @return String
     */
    public static Date formatearFecha(String fecha) {
        
        Date fechaDate = null;
        if (fecha!= null) {
            SimpleDateFormat formatofecha= new SimpleDateFormat("yyyy-MM-dd");          
            try {
                fechaDate = formatofecha.parse(fecha);
            }
            catch (ParseException ex) {
                Logger.getLogger(Formato.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fechaDate;
    }

    /* Conversión a JSON
       ----------------- */
    
    /**
     * Método para ajustar los valores necesarios a formato JSON.
     * Contempla las cadenas nulas y cadenas vacías.
     * El valor por defecto es "".
     * Hace uso de la función escapeCharacters de esta clase para tratar los
     * caracteres especiales en JSON.
     * 
     * @param cadena
     * @return
     */
    public static String toJSON(String cadena) {
        String cad="";
        if (cadena!=null && !cadena.equals("")) {
            cad = Formato.escapeCharacters(cadena);
        }
        return "\""+cad+"\"";
    }
    
    /**
     * Método para ajustar los valores necesarios a formato JSON.
     * Contempla las cadenas nulas y cadenas vacías.
     * Permite definir el valor por defecto cuando la cadena de entrada sea nula o vacía.
     * Hace uso de la función escapeCharacters de esta clase para tratar los
     * caracteres especiales en JSON.
     *
     * @param cadena
     * @param porDefecto
     * @return
     */
    public static String toJSON(String cadena, String porDefecto) {
        String cad=porDefecto;
        if (cadena!=null && !cadena.equals("")) {
            cad = Formato.escapeCharacters(cadena);  
        }
        return "\""+cad+"\"";
    }
  
//    /**
//     * Método para ajustar los valores necesarios a formato JSON.
//     * Contempla las cadenas nulas y cadenas vacías.
//     * El valor por defecto es "".
//     * Hace uso de la función escapeCharacters de esta clase para tratar los caracteres especiales en JSON.
//     * @param cadena
//     * @return
//     */
//    public static String toJSONescape(String cadena) {
//        String cad="";
//        if (cadena!=null && !cadena.equals("")) {
//            cad = Formato.escapeCharacters(cadena);
//        }
//        return "\""+cad+"\"";
//    }
//    
//    /**
//     * Método para ajustar los valores necesarios a formato JSON.
//     * Contempla las cadenas nulas y cadenas vacías.
//     * Permite definir el valor por defecto cuando la cadena de entrada sea nula o vacía.
//     * Hace uso de la función escapeCharacters de esta clase para tratar los caracteres especiales en JSON.
//     * @param cadena
//     * @param porDefecto
//     * @return
//     */
//    public static String toJSONescape(String cadena, String porDefecto) {
//        String cad=porDefecto;
//        if (cadena!=null && !cadena.equals("")) {
//            cad = Formato.escapeCharacters(cadena);
//        }
//        return "\""+cad+"\"";
//    }
    

    /**
     * Método para ajustar un número a formato JSON.
     * Contempla los valores nulos.
     * El valor por defecto es "null".
     * @param numero
     * @return
     */
    public static String toJSON(Integer numero) {
        String cad="null";
        if (numero!=null) {
            cad= numero.toString();  
        }
        return cad;
    }
    
    /**
     * Método para ajustar un número a formato JSON.
     * Contempla los valores nulos.
     * Permite definir el valor por defecto cuando el número de entrada es null.
     * @param numero
     * @param porDefecto
     * @return
     */
    public static String toJSON(Integer numero, String porDefecto) {
        String cad=porDefecto;
        if (numero!=null) {
            cad= numero.toString();  
        }
        return cad;
    }
    
    /**
     * Método para ajustar los valores necesarios a formato JSON.
     * Contempla las cadenas nulas y cadenas vacías.
     * El valor por defecto es "null".
     * @param hora
     * @return
     */
    public static String toJSON(Timestamp hora) {
        String cad="null";
        if (hora!=null) {
            cad= hora.toString().substring(0, hora.toString().length()-2);
        }
        return "\""+cad+"\"";
    }
    
    /**
     * Método para ajustar los valores necesarios a formato JSON.
     * Contempla las cadenas nulas y cadenas vacías.
     * Permite definir el valor por defecto cuando el número de entrada es null.
     * @param hora
     * @param porDefecto
     * @return
     */
    public static String toJSON(Timestamp hora, String porDefecto) {
        String cad=porDefecto;
        if (hora!=null) {
            //cad= hora.toString(); // Versión completa: 2017-03-24 19:49:33.0
            cad= hora.toString().substring(0, hora.toString().length()-2); // Versión para quitar el .0 de la hora. Con el .0 no funciona en Edge. En Internet Explorer no funciona.
        }
        return "\""+cad+"\"";
    }
    
    /*---------------------------------------------*/
    
//    /**
//     * Esta función es una medida de seguridad para datos nulos, ya que al pasarle una cadena
//     * de tipo String nula, devuelve la cadena vacia "".
//     * @param valor
//     * @return String
//     */
//    public static String tratarStringNull(String valor) {
//   
//        return (valor!=null)?valor:"";
//    }
//    
//    /**
//     * Esta función es una medida de seguridad para datos nulos, ya que al pasarle una cadena
//     * de tipo String nula, devuelve la cadena segunda cadena pasada por parámetro.
//     * Si ésta es nula devuelve la cadena vacia "".
//     * @param valor
//     * @param valorNulo
//     * @return String
//     */
//    public static String tratarStringNull(String valor, String valorNulo) {
//
//        return (valor!=null)?valor:(valorNulo!=null)?valorNulo:"";
//    }
    
    
    // Ya no lo utilizo. Uso Formato.escapeCharacters(...)
//    /**
//     * Método para tratar las cadenas que contengan una única barra y requieran
//     * duplicarla para no afectar su valor al realizar operaciones tipo parse.
//     * @param path
//     * @return String
//     */
//    public static String pathToJSON(String path){
//
//       return path.replace("\\", "\\\\");
//    }
    
    
    
     /**
     * Esta función es una medida de seguridad para datos nulos, ya que al pasarle una cadena de tipo String nula, devuelve la cadena vacia "".
     * @param cadena
     * @return String
     */
//    public static Date  tratarFecha(String fecha_cadena) {
//        Date fecha= null;
//        if(fecha_cadena != "0000-00-00") {    // por aquí!
//        
//            // fecha= (Date) DateFormat.parse();
//        } 
//        return fecha;
//   }


}
