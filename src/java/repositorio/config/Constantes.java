/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */
package repositorio.config;

/**
 * Constantes comunes del repositorio.
 * @author Juan Carlos
 */
public final class Constantes {
    
    // Archivos de configuración
    public static final String CONFIG_PATH = "repositorio/config/";
    public static final String CONFIG_FILE = "config.properties";
    public static final String CONFIG_BD_FILE = "configBD.properties";
    public static final String FILENAME_BASE_PERFIL = "perfil_";
    public static final String PROPERTIES_EXTENSION = ".properties";   
    
    // Formatos permitidos (Formatos que puede decodificar FFMPEG)
    // -----------------------------------------------------------
    // Ojo! A tener en cuenta: No todos los formatos coinciden con la extensión. 
    // Por ejemplo: matroska: [.mkv, .mka, .mks, .mk3d]. Falta revisar el resto de entradas donde el nombre del formato no coincide con la extensión. -> Trabajo futuro. La esencia es el protocolo de seguridad en sí.
    // Esta lista blanca no deja de ser un precario filtro de seguridad. Hay soluciones mucho más sofisticadas. (http://www.acunetix.com/websitesecurity/upload-forms-threat/)            
    public static final String[] FORMATOS_ENTRADA={"3dostr", "4xm", "aa", "aac", "ac3", "acm", "act", "adf", "adp", "ads", "adx", "aea", "afc", "aiff", "aix", "alaw", "alias_pix", "amr", "anm", "apc", "ape", "apng", "aqtitle", "asf", "asf_o", "ass", "ast", "au", "avi", "avisynth", "avr", "avs", "bethsoftvid", "bfi", "bfstm", "bin", "bink", "bit", "bmp_pipe", "bmv", "boa", "brender_pix", "brstm", "c93", "caf", "cavsvideo", "cdg", "cdxl", "cine", "concat", "data", "daud", "dcstr", "dds_pipe", "decklink", "dfa", "dirac", "dnxhd", "dpx_pipe", "dsf", "dshow", "dsicin", "dss", "dts", "dtshd", "dv", "dvbsub", "dvbtxt", "dxa", "ea", "ea_cdata", "eac3", "epaf", "exr_pipe", "f32be", "f32le", "f64be", "f64le", "ffm", "ffmetadata", "film_cpk", "filmstrip", "flac", "flic", "flv", "frm", "fsb", "g722", "g723_1", "g729", "gdigrab", "genh", "gif", "gsm", "gxf", "h261", "h263", "h264", "hevc", "hls", "applehttp", "hnm", "ico", "idcin", "idf", "iff", "ilbc", "image2", "image2pipe", "ingenient", "ipmovie", "ircam", "iss", "iv8", "ivf", "ivr", "j2k_pipe", "jacosub", "jpeg_pipe", "jpegls_pipe", "jv", "lavfi", "libgme", "libmodplug", "live_flv", "lmlm4", "loas", "lrc", "lvf", "lxf", "m4v", "mkv", "mka", "mks", "mk3d", "webm", "mgsts", "microdvd", "mjpeg", "mlp", "mlv", "mm", "mmf", "mov", "mp4", "m4a", "3gp", "mp3", "mpc", "mpc8", "mpeg", "mpegts", "mpegtsraw", "mpegvideo", "mpjpeg", "mpl2", "mpsub", "msf", "msnwctcp", "mtaf", "mtv", "mulaw", "musx", "mv", "mvi", "mxf", "mxg", "nc", "nistsphere", "nsv", "nut", "nuv", "ogg", "oma", "paf", "pam_pipe", "pbm_pipe", "pcx_pipe", "pgm_pipe", "pgmyuv_pipe", "pictor_pipe", "pjs", "pmp", "png_pipe", "ppm_pipe", "psxstr", "pva", "pvf", "qcp", "qdraw_pipe", "r3d", "rawvideo", "realtext", "redspark", "rl2", "rm", "roq", "rpl", "rsd", "rso", "rtp", "rtsp", "s16be", "s16le", "s24be", "s24le", "s32be", "s32le", "s8", "sami", "sap", "sbg", "sdp", "sdr2", "sgi_pipe", "shn", "siff", "sln", "smjpeg", "smk", "smush", "sol", "sox", "spdif", "srt", "stl", "subviewer", "subviewer1", "sunrast_pipe", "sup", "svag", "swf", "tak", "tedcaptions", "thp", "tiertexseq", "tiff_pipe", "tmv", "truehd", "tta", "tty", "txd", "u16be", "u16le", "u24be", "u24le", "u32be", "u32le", "u8", "v210", "v210x", "vag", "vc1", "vc1test", "vfwcap", "vivo", "vmd", "vobsub", "voc", "vpk", "vplayer", "vqf", "w64", "wav", "wc3movie", "webm_dash_manife", "webp_pipe", "webvtt", "wmv", "wsaud", "wsd", "wsvqa", "wtv", "wv", "wve", "xa", "xbin", "xmv", "xvag", "xwma", "yop", "yuv4mpegpipe"};

    // Tamaño de datos en BD
    public static final int MAX_SIZE_FILENAME=100;
    public static final int MAX_SIZE_PATH=400;
    public static final int MAX_SIZE_ESTADO=20;
    public static final int MAX_SIZE_TITULO=50;

}

