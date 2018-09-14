<%-- 
    Document   : home
    Created on : 19-dic-2016, 13:35:35
    Author     : Juan Carlos
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Repositorio Multimedia</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!--Icono-->
        <link rel="shortcut icon" href="resources/images/favicon.png" type="image/x-icon">
        <link rel="icon" href="resources/images/favicon.png" type="image/x-icon">
        <!--Estilos-->
        <link href="./resources/css/repositorio/estilo.css" rel="stylesheet" type="text/css"/>
        <link href="./resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <!--Librerías-->
        <script src ="./resources/js/jquery-3.1.1.min.js" type="text/javascript"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/jquery-form-validator/2.3.26/jquery.form-validator.min.js"></script>
        <!--<script src="./resources/js/jquery.form-validator.min.js" type="text/javascript"></script>-->    
    </head>
    <body>
       
        <header>
            <h1>Repositorio Multimedia</h1>            
        </header>
       
        <main>
             <!-- Capa envoltorio: Permite crear un footer responsive-->
            <div id="wrap"> 
                <div class="container-fluid"> 
                    <div class="row">
                        <div class="col-xs-12 col-xs-offset-0 col-sm-10 col-sm-offset-1 col-md-10 col-md-offset-1 col-lg-8 col-lg-offset-2">
                            <!--Navegación superior-->
                            <table align="center" class="tabla_navegacion"> <!--align="center" obsoleto! No encuentro un comportamiento similar con css.-->
                                <tr>
                                    <!--Columna 1. Inicio-->
                                    <td class="td_ancho2">
                                        <a href="home.jsp">
                                            <img src = "./resources/images/home.png" alt="Inicio" title="Inicio" class="icono_navegacion float_izquierda">
                                        </a>
                                    </td>
                                    <!--Columna 2. Titulo del contenido-->
                                    <td>
                                        <h2 class="texto_centrado" id="titulo_contenido">Subir Medio</h2>
                                    </td>
                                    <!-- Columna 3. Cerrar sesión-->
                                    <td class="td_ancho2">
                                        <a href="logout" id="confirmation">
                                            <img src = "./resources/images/logout.png" alt="Cerrar sesión" title="Cerrar sesión" class="icono_navegacion float_derecha">
                                        </a>
                                    </td>
                                </tr>
                            </table>
                            <hr/>
                        
                            <!-- CUERPO: Formulario para seleccionar medio -->
                            <div id="mi_contenido">
                                <div class="row">
                                    <div class="col-xs-10 col-xs-offset-1 col-sm-6 col-sm-offset-3 col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4">
                                        <form method="post" action="upload" enctype="multipart/form-data" id="formulario_upload" style="margin-top: 50px;">
                                            <fieldset> 

                                                <!--Título-->
                                                <div class="form-group">
                                                   <!--<label for="titulo" >Título</label>-->
                                                   <input class="form-control" type="text" name="titulo" id="titulo" placeholder="Título del medio*" data-validation="required">
                                                </div>

                                                <!--Selección del archivo-->
                                                <div class="form-group">                                                   
                                                    <!--<label for="input_explorar">Seleccione el archivo:</label>-->
                                                    <input type="file" name="file" class="form-control-file" id="input_explorar" aria-describedby="fileHelp"  data-validation="required">
                                                    <br />
                                                    <!-- Formatos válidos-->
<!--                                                    <small id="fileHelp" class="form-text text-muted">Formatos válidos (Video|Audio):</small>
                                                    <br />
                                                    <small> .avi .mp4 | .wav .mp3 .wma <a id="verFormatos" style="cursor: pointer;"> (Ver todos)</a></small>-->
                                                </div>
                                                
                                                <!--Formatos válidos-->
                                                <div class="form-group">
                                                    <small id="fileHelp" class="form-text text-muted">
                                                        Formatos válidos (Video|Audio): .avi .mp4 | .wav .mp3 .wma <a id="verFormatos" style="cursor: pointer;"> (Ver todos)</a>
                                                    </small>
                                                </div>
                                                
                                                <!--Botón Subir-->
                                                <div class="form-group">
                                                    <!--<div class="col-xs-11 col-sm-11 col-md11 col-lg-11">-->
                                                        <br />
                                                        <!--<br />-->
                                                        <button type="submit" class="btn btn-primary pull-right" id="input_subir" >Subir</button>
                                                    <!--</div>-->
                                                </div>
                                            </fieldset>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        
        <footer class="footer-basic-centered">
            <h4>Máster en Ingeniería de Telecomunicación</h4>
            <h6>Proyecto Fin de Máster</h6>
        </footer>
        
        <!-- Scripts-->
        <script type="text/javascript"> 
            
            // Validación del formulario de subida de medios
                    
            // + Opción 1: jQuery form validator -> Ojo! Requiere conexión a Internet.
            $.validate({
                lang: 'es'
            });

            // + Opción 2: Comprobación manual
//            $("#formulario_upload").submit(function () {
//
//                // Obtener los datos del formulario
//                var path = $("#input_explorar").val();// Trim: Elimina los espacios en blanco al principio y al final
//                var titulo = $.trim($("#titulo").val()); // Trim: Elimina los espacios en blanco al principio y al final                             º
//
//                // Comprobar si algún campo obligatorio está vacío
//                if (path  === "") {
//                    alert("Debe seleccionar un archivo!");
//                    return false;
//                }
//                if (titulo  === "") {
//                    alert("El título del medio es obligatorio!");
//                    return false;
//                }
//            });
    
            // Confirmación al cierre de sesión
            $("#confirmation").on("click", function () {
                return confirm("Realmente desea salir?");   
            });
            
            // Mostrar formatos
            $("#verFormatos").on("click", function () { // Propuesta de Mejora: Mostrar el listado de formatos disponibles en otra página.
                //// Ojo! A tener en cuenta: No todos los formatos coinciden con la extensión. Por ejemplo: matroska: [.mkv, .mka, .mks, .mk3d]. Esta lista blanca no deja de ser un precario filtro de seguridad. Hay soluciones mucho más sofisticadas. (http://www.acunetix.com/websitesecurity/upload-forms-threat/)
                //var formatos = '"3dostr", "4xm", "aa", "aac", "ac3", "acm", "act", "adf", "adp", "ads", "adx", "aea", "afc", "aiff", "aix", "alaw", "alias_pix", "amr", "anm", "apc", "ape", "apng", "aqtitle", "asf", "asf_o", "ass", "ast", "au", "avi", "avisynth", "avr", "avs", "bethsoftvid", "bfi", "bfstm", "bin", "bink", "bit", "bmp_pipe", "bmv", "boa", "brender_pix", "brstm", "c93", "caf", "cavsvideo", "cdg", "cdxl", "cine", "concat", "data", "daud", "dcstr", "dds_pipe", "decklink", "dfa", "dirac", "dnxhd", "dpx_pipe", "dsf", "dshow", "dsicin", "dss", "dts", "dtshd", "dv", "dvbsub", "dvbtxt", "dxa", "ea", "ea_cdata", "eac3", "epaf", "exr_pipe", "f32be", "f32le", "f64be", "f64le", "ffm", "ffmetadata", "film_cpk", "filmstrip", "flac", "flic", "flv", "frm", "fsb", "g722", "g723_1", "g729", "gdigrab", "genh", "gif", "gsm", "gxf", "h261", "h263", "h264", "hevc", "hls", "applehttp", "hnm", "ico", "idcin", "idf", "iff", "ilbc", "image2", "image2pipe", "ingenient", "ipmovie", "ircam", "iss", "iv8", "ivf", "ivr", "j2k_pipe", "jacosub", "jpeg_pipe", "jpegls_pipe", "jv", "lavfi", "libgme", "libmodplug", "live_flv", "lmlm4", "loas", "lrc", "lvf", "lxf", "m4v", "matroska -> mkv", "webm", "mgsts", "microdvd", "mjpeg", "mlp", "mlv", "mm", "mmf", "mov", "mp4", "m4a", "3gp", "mp3", "mpc", "mpc8", "mpeg", "mpegts", "mpegtsraw", "mpegvideo", "mpjpeg", "mpl2", "mpsub", "msf", "msnwctcp", "mtaf", "mtv", "mulaw", "musx", "mv", "mvi", "mxf", "mxg", "nc", "nistsphere", "nsv", "nut", "nuv", "ogg", "oma", "paf", "pam_pipe", "pbm_pipe", "pcx_pipe", "pgm_pipe", "pgmyuv_pipe", "pictor_pipe", "pjs", "pmp", "png_pipe", "ppm_pipe", "psxstr", "pva", "pvf", "qcp", "qdraw_pipe", "r3d", "rawvideo", "realtext", "redspark", "rl2", "rm", "roq", "rpl", "rsd", "rso", "rtp", "rtsp", "s16be", "s16le", "s24be", "s24le", "s32be", "s32le", "s8", "sami", "sap", "sbg", "sdp", "sdr2", "sgi_pipe", "shn", "siff", "sln", "smjpeg", "smk", "smush", "sol", "sox", "spdif", "srt", "stl", "subviewer", "subviewer1", "sunrast_pipe", "sup", "svag", "swf", "tak", "tedcaptions", "thp", "tiertexseq", "tiff_pipe", "tmv", "truehd", "tta", "tty", "txd", "u16be", "u16le", "u24be", "u24le", "u32be", "u32le", "u8", "v210", "v210x", "vag", "vc1", "vc1test", "vfwcap", "vivo", "vmd", "vobsub", "voc", "vpk", "vplayer", "vqf", "w64", "wav", "wc3movie", "webm_dash_manife", "webp_pipe", "webvtt", "wsaud", "wsd", "wsvqa", "wtv", "wv", "wve", "xa", "xbin", "xmv", "xvag", "xwma", "yop", "yuv4mpegpipe"';
                var formatos = "3dostr, 4xm, aa, aac, ac3, acm, act, adf, adp, ads, adx, aea, afc, aiff, aix, alaw, alias_pix, amr, anm, apc, ape, apng, aqtitle, asf, asf_o, ass, ast, au, avi, avisynth, avr, avs, bethsoftvid, bfi, bfstm, bin, bink, bit, bmp_pipe, bmv, boa, brender_pix, brstm, c93, caf, cavsvideo, cdg, cdxl, cine, concat, data, daud, dcstr, dds_pipe, decklink, dfa, dirac, dnxhd, dpx_pipe, dsf, dshow, dsicin, dss, dts, dtshd, dv, dvbsub, dvbtxt, dxa, ea, ea_cdata, eac3, epaf, exr_pipe, f32be, f32le, f64be, f64le, ffm, ffmetadata, film_cpk, filmstrip, flac, flic, flv, frm, fsb, g722, g723_1, g729, gdigrab, genh, gif, gsm, gxf, h261, h263, h264, hevc, hls, applehttp, hnm, ico, idcin, idf, iff, ilbc, image2, image2pipe, ingenient, ipmovie, ircam, iss, iv8, ivf, ivr, j2k_pipe, jacosub, jpeg_pipe, jpegls_pipe, jv, lavfi, libgme, libmodplug, live_flv, lmlm4, loas, lrc, lvf, lxf, m4v, mkv, mka, mks, mk3d, webm, mgsts, microdvd, mjpeg, mlp, mlv, mm, mmf, mov, mp4, m4a, 3gp, mp3, mpc, mpc8, mpeg, mpegts, mpegtsraw, mpegvideo, mpjpeg, mpl2, mpsub, msf, msnwctcp, mtaf, mtv, mulaw, musx, mv, mvi, mxf, mxg, nc, nistsphere, nsv, nut, nuv, ogg, oma, paf, pam_pipe, pbm_pipe, pcx_pipe, pgm_pipe, pgmyuv_pipe, pictor_pipe, pjs, pmp, png_pipe, ppm_pipe, psxstr, pva, pvf, qcp, qdraw_pipe, r3d, rawvideo, realtext, redspark, rl2, rm, roq, rpl, rsd, rso, rtp, rtsp, s16be, s16le, s24be, s24le, s32be, s32le, s8, sami, sap, sbg, sdp, sdr2, sgi_pipe, shn, siff, sln, smjpeg, smk, smush, sol, sox, spdif, srt, stl, subviewer, subviewer1, sunrast_pipe, sup, svag, swf, tak, tedcaptions, thp, tiertexseq, tiff_pipe, tmv, truehd, tta, tty, txd, u16be, u16le, u24be, u24le, u32be, u32le, u8, v210, v210x, vag, vc1, vc1test, vfwcap, vivo, vmd, vobsub, voc, vpk, vplayer, vqf, w64, wav, wc3movie, webm_dash_manife, webp_pipe, webvtt, wsaud, wsd, wsvqa, wtv, wv, wve, xa, xbin, xmv, xvag, xwma, yop, yuv4mpegpipe";
                return alert("Formatos disponibles:\n"+formatos);
            });
            
            // Parpadeo del título en la recarga -> Opcional
            $().ready(function() {           

                // 1. Parpadeo del título
                $("#titulo_contenido").fadeOut(function(){
                    $("#titulo_contenido").fadeIn("slow");    
                });

                // 2. Mostrar contenido (Progresivo)
                $("#mi_contenido").fadeOut(function(){
                   $("#mi_contenido").css("visibility","visible");
                   $("#mi_contenido").fadeIn(500); // 0.5 seg
                });
            });
     
        </script>
        
    </body>
</html>

