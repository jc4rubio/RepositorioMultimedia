/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.modelo.excepciones;

/**
 * Clase de Excepciones que hereda de Exception.
 * Útil para personalizar las excepciones que se generan en la aplicación.
 *
 * @author Juan Carlos Rubio García
 */
public class ExcepcionesRepositorio extends Exception {
    
    public static final int ERROR_SQL=1;
    public static final int ERROR_FICHERO=2;
    public static final int ERROR_IO=3;
    public static final int ERROR_TAMANYO_DATOS=4;
    public static final int ERROR_BASE_DE_DATOS=5;
    public static final int ERROR_LOGICO=6;
    public static final int ERROR_CONFIGURACION=7;
    public static final int ERROR_CONSISTENCIA=8;
    public static final int ERROR_CONVERSION=9;
    
    
    private int codigoError;
    private String mensajeError;
    private Exception excepcionCamuflada;
    
    //Constructores

    /**
     * Constructor básico. Llama al constructor de Exception.
     */
    public ExcepcionesRepositorio() {
        super();
    }

    /**
     * Constructor de campos: codigo y mensaje.
     *
     * @param codigo
     * @param mensaje
     */
    public ExcepcionesRepositorio(int codigo, String mensaje) {
        super();
        this.codigoError = codigo;
        this.mensajeError = mensaje;
    }   
    /**
     * Constructor de campos: codigo, mensaje y excepción camuflada.
     * 
     * @param codigo
     * @param mensaje
     * @param excepcion_camuflada
     */
    public ExcepcionesRepositorio(int codigo, String mensaje, Exception excepcion_camuflada) {
        super(mensaje);
        this.excepcionCamuflada = excepcion_camuflada;
        this.codigoError = codigo;      
    }
    
    //Metodos getter & setters

     /**
     * Devuelve el código de error.
     *
     * @return mensaje
     */
    public int getCodigoError() {
      int codigo;
      codigo = this.codigoError;
      return codigo;
    }
    
   /**
    * Establece el codigo de error.
     * @param codigo_error
    */
    public void setCodigoError(int codigo_error) {
      this.codigoError = codigo_error;
    }

    /**
     * Devuelve el mensaje de error.
     *
     * @return mensaje
     */
    public String getMensajeError() {
      String mensaje;
      mensaje = this.mensajeError;
      return mensaje;
    }
    
    /**
     * Establece el mensaje de error.
     *
     * @param mensaje
     */
    public void setMensajeError(String mensaje) {
      this.mensajeError = mensaje;
    }
    
    /**
     * Devuelve la excepción camuflada en la excepción.
     * @return
     */
    public Exception getExcepcionCamuflada() {
        return excepcionCamuflada;
    }

    /**
     * Establece la excepción camuflada en la excepción.
     * @param excepcionCamuflada
     */
    public void setExcepcionCamuflada(Exception excepcionCamuflada) {
        this.excepcionCamuflada = excepcionCamuflada;
    }
  
}
