<%-- 
    Document   : newjsp
    Created on : 31-mar-2017, 22:04:48
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
        <link href="resources/css/repositorio/estilo.css" rel="stylesheet" type="text/css"/>
        <link href="resources/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
        <!--Librerias-->
        <script src="./resources/js/jquery-3.1.1.min.js" type="text/javascript"></script>   
        <script src="./html5lightbox/jquery.js" type="text/javascript"></script>
        <script src="./html5lightbox/html5lightbox.js" type="text/javascript"></script>
        <!--Mis códigos jquery-->
        <script src="./resources/js/repositorio/repositorio_peticiones.js" type="text/javascript"></script>
        <script src="./resources/js/repositorio/repositorio_vistas.js" type="text/javascript"></script>
        <script src="./resources/js/repositorio/repositorio_print.js" type="text/javascript"></script>

    </head>
    <body>
       <header>
            <h1>Repositorio Multimedia</h1>
        </header>
        
        <main>
            <div id="wrap"> <!-- Capa envoltorio: Permite crear un footer responsive-->
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-xs-12 col-xs-offset-0 col-sm-10 col-sm-offset-1 col-md-10 col-md-offset-1 col-lg-8 col-lg-offset-2">
                            
                            <!--Navegación superior-->
                            <table align="center" class="tabla_navegacion"> <!--align="center" obsoleto! Pero no encuentro un comportamiento similar con css.-->
                                <tr>
                                    <!--Columna 1. Home-->
                                    <td class="td_ancho1">
                                        <img id="verMedios" src = "./resources/images/home.png" alt="Inicio"  title="Inicio" class="icono_navegacion float_izquierda">
                                    </td>
          
                                    <!--Columna 2. Ver perfiles-->
                                    <td class="td_ancho1">
                                        <img id="verPerfiles" src = "./resources/images/perfiles.png" alt="Ver perfiles" title="Ver perfiles" class="icono_navegacion float_izquierda"> 
                                    </td>
                                    

                                    <!--Columna 3. Título de la vista actual-->
                                    <td>
                                        <h2 class="texto_centrado" id="titulo_contenido">Listado de medios</h2>
                                    </td>
                                    
                                    <!--Columna 4. Subir medio-->
                                    <td class="td_ancho1" >
                                        <a href="upload.jsp">
                                            <img src = "./resources/images/upload.png" alt="Subir medio" title="Subir medio" class="icono_navegacion float_derecha">
                                        </a>
                                    </td>
                                    
                                    <!-- Columna 5. Cerrar sesión-->
                                    <td class="td_ancho1">
                                        <a href="logout" id="confirmation">
                                            <img src = "./resources/images/logout.png" alt="Cerrar sesión" title="Cerrar sesion" class="icono_navegacion float_derecha">
                                        </a>
                                    </td>

                                </tr>
                            </table>
                            <hr/>

                            <!--Alternativa con bootsrap: No se comporta mejor que la tabla-->
<!--                            <div class="row center-block">
                                <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1">
                                    <img id="verMedios" src = "./resources/images/home.png" alt="Inicio"  title="Inicio" class="icono_navegacion icono_home">  
                                </div>
                                <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 texto_centrado">
                                    <img id="verPerfiles" src = "./resources/images/perfiles.png" alt="Ver perfiles" title="Ver perfiles" class="icono_navegacion"> 
                                </div>
                                <div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
                                    <h2 class="texto_centrado" id="titulo_contenido1">Listado de medios</h2>
                                </div>
                                <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 texto_centrado">
                                     <img src = "./resources/images/upload.png" alt="Subir medio" title="Subir medio" class="icono_navegacion">
                                </div>
                                <div class="col-xs-1 col-sm-1 col-md-1 col-lg-1 texto_centrado">
                                    <img id="verPerfiles" src = "./resources/images/logout.png" alt="Cerrar sesión" title="Cerrar sesión" class="icono_navegacion"> 
                                </div>
                            </div>
                            <hr/>-->
                            
                            <!--Contenido: Ajuste responsive-->
                            <div class="row">
                                <div class="col-xs-12 col-xs-offset-0 col-sm-10 col-sm-offset-1 col-md-10 col-md-offset-1 col-lg-8 col-lg-offset-2">
                                    
                                    <!--Listado 1. Medios y recursos-->
                                    <div class="listado_principal" id="lista_medios">
                                    </div>

                                    <!--Listado 2. Perfiles-->
                                    <div class="listado_principal" id="lista_perfiles">
                                    </div>                                  
                                    
                                </div>
                            </div>
                        </div>    
                    </div>    
                </div>
            </div>
        </main>     
       
        <footer>       
            <h4>Máster en Ingeniería de Telecomunicación</h4>
            <h5>Proyecto Fin de Máster</h5>
        </footer>
        
        <script type="text/javascript"> 
  

            /* --------------------------------------------------*
             * Este código se ejecuta cuando el DOM está cargado.*
             * --------------------------------------------------*/
            $(function() {           
                
                // Listado por defecto
                getMedios();
                //getPerfiles();
      
                // Cargar medios
                $("#verMedios").on("click", getMedios);
                
                // Cargar perfiles
                $("#verPerfiles").on("click", getPerfiles);
                          
            });
            
            $("#confirmation").on("click", function () {
                return confirm("Realmente desea salir?");
            });

          
        </script>
        
    </body>
</html>