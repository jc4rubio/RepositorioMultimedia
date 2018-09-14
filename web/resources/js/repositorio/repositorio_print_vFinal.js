/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */

/*---------------------------------------------------*
 * Añadir medios a la lista: Al principio            *
 *---------------------------------------------------*/
var addMedio = function (medio, callback) {
    //console.log("0 Preparando medio: "+medio.id);

    // 1. Correcciones / Operaciones intermedias
    // -----------------------------------------
    
    var medioPrint = correccionMedio(medio); // Medio con valores adaptados para mostrar en la interfaz.

    // 2. Imprimir medio: Creación de la vista
    // ---------------------------------------

    //console.log("1 Imprimiendo medio: "+medio.id);
    
    var contenedor = jQuery("<div/>", {
        id: "contenedor_medio"+medio.id
    });
    
    var item = jQuery("<div/>", {
        id: "medio"+medio.id,
        class: "medioItem"
    });

    // Título
    jQuery("<h3/>", {
        class: "texto_centrado",
        style: "color: black", // Pruebas
        text: medioPrint.titulo
    }).appendTo(item);

    // Estado
    jQuery("<h4>Estado: <b id='estadoMedio"+medio.id+"'>"+ medioPrint.estado + "</b>").appendTo(item);

    // Autor y fecha
    var fecha_html = (medioPrint.fechaCreacion_dia !== undefined && medioPrint.fechaCreacion_hora !==undefined)? " el&nbsp;<b>" +medioPrint.fechaCreacion_dia+ "</b> a&nbsp;las&nbsp;<b>"+medioPrint.fechaCreacion_hora+"</b>" : ""; 
    jQuery("<h4>Subido&nbsp;por&nbsp;"+medioPrint.usuarioNick+fecha_html+"<h4/>").appendTo(item);
    
    // Desplegable (Comentado en nuevo código)
    var desplegable_id = "desplegable_medio"+medio.id;
    jQuery("<img/>",{
        id: desplegable_id,
        class: "desplegarMedio",
        src: "./resources/images/expand.png",
        alt: "desplegar"   
    }).appendTo(item);

    // 3. Agregar el item al contenedor
    // ---------------------------------
    item.appendTo(contenedor);

    // 4. Declaración de la sublista
    // -----------------------------
    jQuery("<div/>", {
        id: "lista_recursos"+medio.id,
        class: "lista_recursos"
    }).appendTo(contenedor);
    
    // 5. Agregar evento al desplegable
    // ----------------------------------
    $("body").off("click", "#"+desplegable_id); // Elimina cualquier evento anterior. 
    $("body").on("click", "#"+desplegable_id, function(){
        console.log("Click en: #"+desplegable_id);
        gestionarRecursos(medio); // Mostrar/Ocultar recursos del medio.
    });
    
    // 6. Agregar el contenedor al principio del listado
    // -------------------------------------------------
    contenedor.prependTo("#lista_medios");
    
    // 7. Agregar función de actualización -> ¿Cómo lo hago?
    // -----------------------------------
    //medio.estado = "PROCESANDO";
    
//    if (medio.estado === "PROCESANDO"){
//        console.log("Print: Medio Procesando! -> Actualizar datos");
//        setTimeout(function(){
//            medio.contadorActualizacion=0;
//            actualizarMedio(medio);
//        },1000);
//    }

    // 7. Callback
    // -----------
    callback();

};

// Realiza la corrección de valores de un medio para mostrar en la interfaz
var correccionMedio = function (medio) {
    
    // Clonación
    var medioCorreccion = jQuery.extend({}, medio); // Copia superficial (Es suficiente)
//    var medioCorreccion = jQuery.extend(true, {}, medio); // Copia profunda
    
    // Titulo
    medioCorreccion.titulo = (medio.titulo !== undefined && medio.titulo !=="")? medio.titulo : "<i>Medio sin título</i>";

    // Usuario
    medioCorreccion.usuarioNick = (medio.usuarioNick !== undefined && medio.usuarioNick !=="")? medio.usuarioNick : "<i>desconocido</i>";
    medioCorreccion.usuarioNick = "<b>"+medioCorreccion.usuarioNick+"</b>"; // Usuario en negrita 
    
    // Estado
    medioCorreccion.conversionActiva = false; // Nuevo campo
    medioCorreccion.estado_OK = false; // Nuevo campo
    switch(medio.estado){
        case 'EN_COLA':
            medioCorreccion.estado = 'Esperando conversión';
            medioCorreccion.conversionActiva = true;
            break;
        case 'PROCESANDO': 
            medioCorreccion.estado = 'Procesando';
            medioCorreccion.conversionActiva = true;
            break;
        case 'OK': 
            medioCorreccion.estado = 'Disponible';
            medioCorreccion.estado_OK = true;
            break;
        case 'ERROR': 
            medioCorreccion.estado = 'Finalizado con errores'; 
            break;
        default:
            medioCorreccion.estado = 'Desconocido';   
    }
//    Debug: En internet explorer no funciona
//    console.log("Fecha creación: "+medio.fechaCreacion);
    
    // Fecha de creacion
    if (medio.fechaCreacion !== undefined && medio.fechaCreacion !==""){
        var d = new Date(Date.parse(medio.fechaCreacion));
//        console.log("Fecha pruebas: 2017-05-01 14:48:54");
//        var d = new Date(Date.parse("2017-05-01 14:48:54"));
        medioCorreccion.fechaCreacion_dia= d.getDate()+"-"+(d.getMonth()+1)+"-"+d.getFullYear(); // Nuevo atributo
        medioCorreccion.fechaCreacion_hora= d.getHours()+":"+((d.getMinutes()<10)?"0":"")+d.getMinutes()+"&nbsp;h"; // Nuevo atributo
    }
//    console.log("Fecha creación.dia: "+medioCorreccion.fechaCreacion_dia);
//    console.log("Fecha creación.hora: "+medioCorreccion.fechaCreacion_hora);
//    var d = new Date(Date.parse("2017-05-05 14:48:54"));
//    console.log("Fecha: 2017-05-05 14:48:54");
//     console.log("Fecha creación.dia: "+d.getDate());
//      console.log("Fecha creación.mes: "+d.getMonth());
//       console.log("Fecha creación.año: "+d.getFullYear());
//        console.log("Fecha creación.hora: "+d.getHours());
//         console.log("Fecha creación.minuto: "+d.getMinutes());
//    console.log(d);
    
    return medioCorreccion; 
};


var contador;
// Imprimir recurso
var addRecurso = function (recurso, callback) {
      
    // 1. Correcciones / Operaciones intermedias
    // -----------------------------------------
    
    var recursoPrint = correccionRecurso(recurso); // Recurso con valores adaptados para mostrar en la interfaz.

    /* 2. Vista html [[Perfil | Valor]| Icono ]
                     [[Estado | Valor]|       ]
     ------------------------------------------*/
    var contenido_html = "<div class='container-fluid'>";
        // Fila 1: Contenedor 
        contenido_html += "<div class='row'>";
            // Col 1, Subcontenedor: Perfil y estado
            contenido_html += "<div class='col-lg-9 col-md-9 col-sm-9 col-xs-9'>";
                contenido_html += "<div class='row texto_justificado_izquierda'>";
                    contenido_html += "<div class='col-lg-3 col-md-3 col-sm-3 col-xs-3'>";
                        contenido_html += "<div>Perfil&nbsp;<strong>"+recurso.perfilId+"</strong></div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-lg-9 col-md-9 col-sm-9 col-xs-9'>";
                        contenido_html += "<div id='perfil"+recurso.perfilId+"' class='perfilName'><strong>"+recurso.perfilName+"</strong></div>";
                    contenido_html += "</div>";       
                contenido_html += "</div>";
                contenido_html += "<div class='row texto_justificado_izquierda'>";
                    contenido_html += "<div class='col-lg-3 col-md-3 col-sm-3 col-xs-3'>";
                       contenido_html += "<div>Estado</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-lg-9 col-md-9 col-sm-9 col-xs-9'>";
                        contenido_html += "<div id='estadoRecurso"+recurso.id+"'>"+recursoPrint.estado+"</div>"; // Estado editado
                    contenido_html += "</div>";       
                contenido_html += "</div>";
            contenido_html += "</div>";

            // Col 2: Icono
            contenido_html += "<div class='col-lg-3 col-md-3 col-sm-3 col-xs-3'>";
                contenido_html += "<div id='linkVideo"+recurso.id+"'>"+recursoPrint.icono_recurso+"</div>";  // Icono dependiente del estado
            contenido_html += "</div>";
     
        contenido_html += "</div>";
    contenido_html += "</div>";
    
    // 3. Declaración del elemento html
    // --------------------------------

    // Elemento n
    var item = jQuery("<div/>", {
      id: "recursoItem"+recurso.id,
      class: "recursoItem"
    });
 
    // 4. Añadir código html al elemento
    // ---------------------------------
    item.append(contenido_html);

    // 5. Agregar elemento al final del listado
    // ----------------------------------------
    item.appendTo("#lista_recursos"+recurso.medioId);
    
    // 6. Crear Reproductor cuando el estado sea OK y agregarlo al listado 
    //    -> Requiere que el elemento esté agregado al listado (Paso 5)
    // -------------------------------------------------------------------------------
    if (recursoPrint.estado_OK) {
//        // Icono de reproducción
//        var playerLink = jQuery("<a/>",{       
//            id: "playerLink"+recurso.id,
//            class: "html5lightbox",
//            html: "<img id='player"+recurso.id+"' class='icono_player' src='./resources/images/player.ico' alt='Reproducir' />",
//            href: recurso.uriMultimedia  
//        });
        jQuery(recursoPrint.playerLink).html5lightbox(); // Habilitar evento en elemento playerLink. Imprescindible para reproducir el video en en la ventana emergente.
        recursoPrint.playerLink.appendTo("#linkVideo"+recurso.id); // Ojo! Da problemas en la versión móvil. No aparece el icono. Aunque si funciona.
    }
           
    // 7. Agregar evento al reproductor. Reproducir si el estado es 'OK'
    // ----------------------------------------------------------------
    var iconoID_n = "#linkVideo"+recurso.id;
    $("body").off("click", iconoID_n); // Elimina cualquier evento anterior.
    $("body").on("click", iconoID_n, function(){
        console.log("Click en: "+iconoID_n);
        if (recursoPrint.estado_OK){
            console.log('Reproduciendo recurso '+recurso.id);    
        } else {
            console.log('Este recurso no está disponible.');
        }
    });
    
    // 8. Callback
    // -----------
    callback();
    
};

// Realiza la corrección de valores de un recurso para mostrar en la interfaz
var correccionRecurso = function (recurso) {
  
    // Clonación
    var recursoCorreccion = jQuery.extend({}, recurso); // Copia superficial (Es suficiente)
//    var recursoCorreccion = jQuery.extend(true, {}, recurso); // Copia profunda

    // Estado
    recursoCorreccion.estado_OK = false; // Nuevo campo
    recursoCorreccion.icono_recurso; // Nuevo campo
    recursoCorreccion.conversionActiva = false; // Nuevo campo
    
    switch (recurso.estado){
        case "EN_COLA": 
            recursoCorreccion.estado = "Esperando conversión";
            recursoCorreccion.icono_recurso = "<img id='player"+recurso.id+"' class='icono_enCola' src='./resources/images/loading.png' alt='En cola' />";
            recursoCorreccion.conversionActiva = true;
            break;
        case "PROCESANDO": 
            recursoCorreccion.estado = "Procesando";
            recursoCorreccion.icono_recurso = "<img id='player"+recurso.id+"' class='icono_procesando' src='./resources/images/loading.png' alt='Procesando' />";
            recursoCorreccion.conversionActiva = true;
            break;
        case "OK": 
            recursoCorreccion.estado = "Disponible";
            recursoCorreccion.icono_recurso = ""; // No se define hasta que no se haya agregado el recurso al listado. Se crea el elemento más abajo en el atributo recursoCorreccion.playerLink.
            recursoCorreccion.estado_OK = true;
            break;    
        case "ERROR": 
            recursoCorreccion.estado = "Error en la conversión";
            recursoCorreccion.icono_recurso = "<img id='player"+recurso.id+"' class='icono_error' src='./resources/images/error.png' alt='Error' />";
            break;
        default: 
            recursoCorreccion.estado = "Desconocido";
            recursoCorreccion.icono_recurso = "<img id='player"+recurso.id+"' class='icono_error' src='./resources/images/error.png' alt='Error' />";
    }
    
    // Nuevo elemento:
    recursoCorreccion.playerLink = jQuery("<a/>",{       
        id: "playerLink"+recurso.id,
        class: "html5lightbox",
        html: "<img id='player"+recurso.id+"' class='icono_player' src='./resources/images/player.png' alt='Reproducir' />",
        href: recurso.uriMultimedia  
    });
    
    return recursoCorreccion; 
};


// Imprimir perfil (I)
var addPerfilItem = function (perfilItem) {                             

    // ID del icono desplegable
    var desplegable_id = "desplegable_perfil"+perfilItem.id;
    
    // 1. Correcciones / Operaciones intermedias
    // -----------------------------------------
    var nombre = (perfilItem.nombre ==="") ? "<i>Perfil sin nombre</i>": perfilItem.nombre;

    // 2. Imprimir perfilItem: Creación de la vista
    // --------------------------------------------
    
    // Declaración del contenedor del perfil
    var contenedor = jQuery("<div/>", {
        id: "contenedor_perfil"+perfilItem.id,
        class: "perfilItem"
    });

    // Cabecera: perfilItem
    var html_cabecera = "";
    //html_cabecera = "<div class='container-fluid'>";
        html_cabecera += "<div class='row'>";
            html_cabecera += "<div class='col-xs-2 col-sm-2 col-md-2 col-lg-2'>";
                html_cabecera += "<img id='"+desplegable_id+"' class='desplegarPerfil' src='./resources/images/plus.png' alt='Ver más' />";
            html_cabecera += "</div>";        
            html_cabecera += "<div class='col-xs-3 col-sm-3 col-md-3 col-lg-3 texto_justificado_izquierda'>";
                html_cabecera += "<div>Perfil <strong>"+perfilItem.id+"</strong></div>";
                html_cabecera += "<div>Formato</div>";
            html_cabecera += "</div>";        
            html_cabecera += "<div class='col-xs-7 col-sm-7 col-md-7 col-lg-7 texto_justificado_izquierda'>";
                html_cabecera += "<div id='perfil"+perfilItem.id+"' class='perfilName'><strong>"+nombre+"</strong></div>";
                html_cabecera += "<div><strong>"+perfilItem.formatoSalida+"</strong></div>";
            html_cabecera += "</div>";
            
        html_cabecera += "</div>";
    //html_cabecera += "</div>";
    
    // 3. Agregar el item al contenedor
    // ---------------------------------
    jQuery(html_cabecera).prependTo(contenedor);
    
    // 4. Declarar el cuerpo del perfil
    // --------------------------------
    jQuery("<div/>", {
        id: "cuerpo_perfil"+perfilItem.id,
        style: "display: none"
    }).appendTo(contenedor);   
    
    // 5. Agregar evento al desplegable
    // --------------------------------
    $("body").off("click", "#"+desplegable_id); // Elimina cualquier evento anterior.
    $("body").on("click", "#"+desplegable_id, function(){
        console.log("Click en: #"+desplegable_id);
        gestionarPerfiles(perfilItem);
    });
    
    // 6. Agregar perfil al listado
    // ----------------------------
    contenedor.appendTo("#lista_perfiles");

};

// Imprimir perfil (II)
var addPerfilCompleto = function (perfil) {
    
    var cuerpoID = "#cuerpo_perfil"+perfil.id; 
    
    // 1. Corrección de valores
    // ------------------------
    
    // Opción I: No mostrar descripción si no existe
    var mostrar_descripcion = (perfil.descripcion !== '')?true:false;
    
    // Opción II: Mostrar mensaje indicando que la descripción no existe
    //mostrar_descripción = true; // Descomentar para la opción II
    
    // Definición de la descripción (Es necesario declararla aunque no se use nunca en la opción I)
    var descripcion = (perfil.descripcion !== '')?perfil.descripcion:'<i>No hay detalles para este perfil</i>';
    
    // Parámetros de video
    var profile_video = (perfil.profile_video !== '')?perfil.profile_video:'<i>No definido</i>';
    var profileLevel_video = (perfil.profileLevel_video !== '')?perfil.profileLevel_video:'<i>No definido</i>';
    var codec_video = (perfil.codec_video !== '')?perfil.codec_video:'<i>No definido</i>';
    var fotogramas_video = (perfil.fotogramas_video !== '')?perfil.fotogramas_video:'<i>No definido</i>';
    var resolucion_video = (perfil.resolucion_video !== '')?perfil.resolucion_video:'<i>No definido</i>';
    var bitrate_video = (perfil.bitrate_video !== '')?perfil.bitrate_video:'<i>No definido</i>';
    
    // Parámetros de audio
    var codec_audio = (perfil.codec_audio !== '')?perfil.codec_audio:'<i>No definido</i>';
    var freqMuestreo_audio = (perfil.freqMuestreo_audio !== '')?perfil.freqMuestreo_audio:'<i>No definido</i>';
    var bitrate_audio = (perfil.bitate_audio !== '')?perfil.bitrate_audio:'<i>No definido</i>';
    var canales_audio = (perfil.canales_audio !== '')?perfil.canales_audio:'<i>No definido</i>';
    
    // 2. Definir el contenido html
    // ----------------------------
    var contenido_html = "<div class='row'>";              
        contenido_html += "<div class='col-xs-10 col-xs-offset-2 col-sm-10 col-sm-offset-2 col-md-10 col-md-offset-2 col-lg-10 col-lg-offset-2'>";
    
            // Descripción (Si existe)
            if (mostrar_descripcion){
                contenido_html += "<div class='row'>";
                    contenido_html += "<div class='col-xs-10 col-sm-10 col-md-10 col-lg-8'>";
                        // Fila 1
                        contenido_html += "<div class='texto_justificado_izquierda'>";
                            contenido_html += "Descripción:";
                        contenido_html += "</div>";
                        // Fila 2
                        contenido_html += "<div class='col-xs-offset-1 col-sm-offset-1 col-md-offset-1 col-lg-offset-1 texto_justificado_izquierda'>";
                            contenido_html += "<div style='margin-top: 10px; margin-bottom: 10px;'>"+descripcion+"</div>";
                        contenido_html += "</div>";
                    contenido_html += "</div>";
                contenido_html += "</div>";
            }

            // Video
            contenido_html += "<div class='row'>";
                contenido_html += "<div class='col-xs-10 col-sm-10 col-md-10 col-lg-8 perfilCompleto'>";

                    // Fila 1: Video
                    contenido_html += "<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong><span style='text-decoration: underline'>Video</span></strong></div>";
                    contenido_html += "</div>";

                    // Fila 2: Profile
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Profile</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+profile_video+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 3: Level
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Level</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+profileLevel_video+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 4: Codec
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Codec</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+codec_video+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 5: FPS
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>FPS</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+fotogramas_video+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 6: Resolución
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Resolución</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+resolucion_video+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 7: Tasa de bit
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Tasa binaria</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+bitrate_video+"</strong></div>";
                    contenido_html += "</div>";
    
                contenido_html += "</div>";
            contenido_html += "</div>";                    
                
            // Audio
            contenido_html += "<div class='row'>";
                contenido_html += "<div class='col-xs-10 col-sm-10 col-md-10 col-lg-8 perfilCompleto'>";

                    // Fila 1: Audio
                    contenido_html += "<div class='col-xs-12 col-sm-12 col-md-12 col-lg-12 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong><span style='text-decoration: underline'>Audio</span></strong></div>";
                    contenido_html += "</div>";

                    // Fila 2: Codec
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Codec</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+codec_audio+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 3: Frecuencia de muestreo
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Freq. Muestreo</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+freqMuestreo_audio+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 4: Tasa de bit
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Tasa binaria</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+bitrate_audio+"</strong></div>";
                    contenido_html += "</div>";

                    // Fila 5: Número de canales
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div>Nº de canales</div>";
                    contenido_html += "</div>";
                    contenido_html += "<div class='col-xs-6 col-sm-6 col-md-6 col-lg-6 texto_justificado_izquierda'>";
                        contenido_html += "<div><strong>"+canales_audio+"</strong></div>";
                    contenido_html += "</div>";
          
            contenido_html += "</div>";
        contenido_html += "</div>";
        
    contenido_html += "</div>";   

    // 3. Agregar cuerpo al contenedor
    // -------------------------------
    $(cuerpoID).append(contenido_html);

};

// Mostrar detalles del perfil en una ventana emergente sobre la lista de recursos (Versión futura)
// -------------------------------------------------------------------------------
//var mostrarDetallesPerfil = function (recurso) {
//    console.log("Mouse encima del nombre de perfil...: "+recurso.perfilId);
//};
            