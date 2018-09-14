/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */

/* --------------------------------------------------*
 * Peticiones al servidor.*
 * Version: RepositorioWeb
 * En esta versión se implementa el cliente y el servidor en la misma aplicación. Ambos comparten ubicación.
 * --------------------------------------------------*/

// -----------------------------------------------------------------------------

// ¿Dónde está el servidor? -> En la misma ubicación que el cliente => Opción I

// Opción I. URL Interdominio. -> Definición dinámica.
var loc = window.location;
var uriContext = loc.protocol + "//" + loc.host + "/" + loc.pathname.split('/')[1];
var uriBase = uriContext+"/webresources/";

// Opción II. URL Dominio externo. -> Definición manual.
//var uriBase = "http://192.168.1.10:8080/RepositorioWeb/webresources/"; // Interfaz Ethernet
//var uriBase = "http://192.168.1.20:8080/RepositorioWeb/webresources/"; // Interfaz Wi-Fi
console.log(uriBase);

// -----------------------------------------------------------------------------

// Mensajes de error
var mensajeError_medios = "No se ha podido cargar el listado de medios.";
var mensajeListaVacia_medios = "No hay medios.";
var mensajeError_recursos = "No se ha podido cargar el listado de recursos.";
var mensajeListaVacia_recursos = "No hay recursos para este medio";
var mensajeError_perfiles = "No se ha podido cargar el listado de perfiles.";
var mensajeListaVacia_perfiles = "No hay perfiles definidos.";

// Variables globales de actualización
var ultimaPeticionMedios = (new Date).getTime();
var ultimaPeticionRecursos = (new Date).getTime();
var ultimaPeticionPerfiles = (new Date).getTime();
var ultimaPeticionPerfil = (new Date).getTime();

var limiteSeguridad = 300;                  // Número de peticiones máximas antes de cancelar el proceso de actualización (evita exceso de peticiones en caso de que el servidor se quede colgado)
var periodoActualizacion_medio = 2000;      // Tiempo en milisegundos
var periodoActualizacion_recurso = 1000;    // Tiempo en milisegundos


// Obtiene los medios del servidor
var getMedios = function () {

    var uri = uriBase+"medio";
    $.getJSON(uri, console.log("getJSON: Obteniendo el listado de medios"))
    .done(function(medios){
        
        console.log("getJSON: OK!");
        console.log("Medios cargados: "+medios.length);
        $("#lista_medios").empty(); // Vaciar la lista actual
        if (medios.length >0){
            // Para cada medio:
            $.each(medios, function(index, medio) { // medio -> medios[index]  
                console.log("Indice: "+index+". Medio: "+medio.id);
                
                // Versión 1. Sin actualización (Consume menos recursos)
                // ----------------------------
                // addMedio(medio); // Agregar medio al principio del listado

                // Versión 2. Con actualización
                // ----------------------------
                addMedio(medio, function(){ // Agregar medio al principio del listado y actualizar si el estado es "PROCESANDO"
                    if (medio.estado === "PROCESANDO"){
                        console.log("Medio Procesando! -> Actualizar datos");
                        medio.contadorActualizacion=1;
                        actualizarMedio(medio);
                    } 
                }); 
                // ---
            });
        }
        else {
            // No hay medios
            console.log("No hay medios!");
            $("#lista_medios").text(mensajeListaVacia_medios);
        }   
    })
    .fail(function(){

        // Mensaje de error -> No se han podido cargar los datos
        console.log("getJSON: Fail!");
        $("#lista_medios").empty();
        $("#lista_medios").text(mensajeError_medios);

    })
    .always(function(){
        // La lista se muestra cuando los datos están ya cargados -> Evita el parpadeo
        $("#titulo_contenido").fadeOut(function() { // Efecto: Ocultar el título actual, cambiarlo y mostrarlo de nuevo.
            $(this).text("Listado de medios").fadeIn("slow"); // this -> #titulo_contenido
        });
        $("#lista_perfiles").fadeOut(function(){ // Efecto: cambiar la lista de perfiles por la de medios.
            $("#lista_medios").show("slow");
        });
        
        // Actualizar hora de la peticion*
        ultimaPeticionMedios = (new Date).getTime();
        console.log("Actualización de medios (Timestamp): "+ultimaPeticionMedios);
    });
};

// Obtiene los recursos de un determinado medio
var getRecursos = function (medio) {
 
    var listaRecursosID_n = "#lista_recursos"+medio.id;

    $.getJSON(medio.uriRecursos, console.log("getJSON: Obteniendo recursos para el medio "+medio.id))
    .done(function(recursos){
        console.log("getJSON: OK!");
        console.log("Recursos cargados: "+recursos.length);

        $(listaRecursosID_n).empty(); // Borra lista de recursos
        if (recursos.length >0){
            // Para cada recurso
            $.each(recursos, function(index, recurso) { // value == recursos[index]
                console.log("Indice: "+index+ " Recurso: "+recurso.id);
                
                // Versión 1. Sin actualización (Consume menos recursos)
                // ----------------------------
                // addRecurso(recurso); // Agregar recurso al listado

                // Versión 2. Con actualización
                // ----------------------------
                addRecurso(recurso, function(){ // Agregar recurso al listado y actualiza si el estado es "PROCESANDO" o "EN_COLA"
                    if (recurso.estado === "PROCESANDO" || recurso.estado === "EN_COLA"){
                        console.log("Recurso [Procesando/En cola]! -> Actualizar datos");
                        recurso.contadorActualizacion=1;
                        actualizarRecurso(recurso);
                    } 
                }); 
                // ---
            });
        }
        else {
            // No hay recursos
            console.log("No hay recursos!");
            $(listaRecursosID_n).text(mensajeListaVacia_recursos);
        }   
    })
    .fail(function(){

        console.log("getJSON: Fail!");
        // Mensaje de error -> Lista vacía
        $(listaRecursosID_n).empty();
        $(listaRecursosID_n).text(mensajeError_recursos);

    })
    .always(function(){

        // Desplegar la lista
        console.log("getJSON: Desplegando lista de recursos...");
        $(listaRecursosID_n).show("slow");

        // Cambiar el desplegable -> Esto ahora cambia!
        var desplegableID = "#desplegable_medio"+medio.id;
        console.log('Desplegable: '+desplegableID);
        $(desplegableID).fadeOut("slow",function(){
            //$(desplegableID).attr("src","./resources/images/contract.png"); // Al comentarlo, no se cambia el icono: es otra alternativa que no queda mal.
            $(desplegableID).fadeIn("slow");     
        });
        
        // Actualizar hora de la peticion*
        ultimaPeticionRecursos= (new Date).getTime();
    });
};


// Obtiene los perfiles del servidor
var getPerfiles = function () { //  Cargar listado de perfiles

    var uri = uriBase+"perfil";
    $.getJSON(uri, console.log("getJSON: Obteniendo el listado de perfiles"))
    .done(function(perfiles){
        console.log("getJSON: OK!");
        console.log("Perfiles cargados: "+perfiles.length);                  
        $("#lista_perfiles").empty();
        if (perfiles.length >0){
            // Para cada perfil:
            $.each(perfiles, function(index, perfilItem) { // perfilItem -> perfiles[index]  
                console.log("Indice: "+index+". Perfil: "+perfilItem.id+". Nombre: "+perfilItem.nombre);
                addPerfilItem(perfilItem); // Agregar perfil al listado
            });
        }
        else {
            // No hay perfiles
            console.log("No hay perfiles!");
            $("#lista_perfiles").text(mensajeListaVacia_perfiles);
        }   
    })
    .fail(function(){

        // Mensaje de error -> No se han podido cargar los datos
        console.log("getJSON: Fail!");
        $("#lista_perfiles").empty();
        $("#lista_perfiles").text(mensajeError_perfiles);

    })
    .always(function(){
        // La lista se muestra cuando los datos están ya cargados -> Evita el parpadeo
        $("#titulo_contenido").fadeOut(function() { // Efecto: Ocultar el título actual, cambiarlo y mostrarlo de nuevo.
            $(this).text("Listado de perfiles").fadeIn("slow");
        });
        $("#lista_medios").fadeOut(function(){ // Efecto: cambiar la lista de medios por la de perfiles.
            $("#lista_perfiles").show(1000); // Ajustar
        });
        
        // Actualizar hora de la peticion*
        ultimaPeticionPerfiles = (new Date).getTime();
    });
};

// Obtiene un perfil concreto del servidor
var getPerfil = function (perfilItem) {

    var capaID = "#cuerpo_perfil"+perfilItem.id;
    var desplegableID = "#desplegable_perfil"+perfilItem.id;

    $.getJSON(perfilItem.uri, console.log("getJSON: Obteniendo perfil "+perfilItem.id))
    .done(function(perfil){
        console.log("getJSON: OK!");                 
        $(capaID).empty();
        console.log("Uri: "+perfilItem.uri);
        console.log("Recibido el perfil: "+perfil.id);
        if (perfil !== null){
            // Mostrar perfil completo
            addPerfilCompleto(perfil); // Agregar detalles del perfil al item
        }
        else {
            console.log("No hay datos para este perfil!");
            // A diferencia de otras peticiones, no muestra un mensaje en la interfaz.
        }   
    })
    .fail(function(){

        // Mensaje de error -> No se han podido cargar los datos
        console.log("getJSON: Fail!");
        // A diferencia de otras peticiones, no muestra un mensaje de error en la interfaz.

    })
    .always(function(){
        // La lista se muestra cuando los datos están ya cargados -> Evita el parpadeo
        // Desplegar datos del perfil
        console.log("Mostrando: "+capaID);
        $(capaID).fadeOut(function() { //Efecto: Desvanece el perfil y lo vuelve a mostrar.
            $(capaID).show("slow");
        });
        
        // Cambiar el desplegable
        $(desplegableID).fadeOut("slow",function(){ // Efecto: Desvanecer el desplegable '+', cambiarlo por '-' y mostrarlo.
            $(desplegableID).attr("src","./resources/images/minus.png");
            $(desplegableID).fadeIn("slow");     
        });
        
        // Actualizar hora de la peticion*
        ultimaPeticionPerfil = (new Date).getTime();
         
    });
};


/* Funciones de actualización de datos
 * ----------------------------------- */

// Actualizar datos de un medio
var actualizarMedio = function(medio){
    
    console.log("Actualizando medio "+medio.id+" "+(medio.contadorActualizacion)+"/"+limiteSeguridad);
    
    var capaID_medio = "#estadoMedio"+medio.id; // Capa a actualizar

    // Petición
    console.log("UriMedio: "+medio.uriMedio);
    $.getJSON(medio.uriMedio, console.log("getJSON: Obteniendo datos del medio "+medio.id))
    .done(function(nuevoMedio){
        console.log("getJSON: OK!");
        var medioCorreccion = correccionMedio(nuevoMedio); // Medio con valores adaptados para mostrar en la interfaz.

        // Cambiar los valores de la interfaz
        if (nuevoMedio.estado !== medio.estado) {
            console.log("Actualizando valor del estado en "+capaID_medio+" a \""+medioCorreccion.estado+"\"");
            $(capaID_medio).text(medioCorreccion.estado); // Actualizar valores
        } else {
            console.log("El estado del medio no ha cambiado");
        }
        
        // Recursividad: ¿Los valores están actualizados?
        if (medioCorreccion.conversionActiva) {
            console.log("Los datos no están actualizados.");
            //console.log("Contador actual: "+medio.contadorActualizacion);
            console.log("Peticion Medios: "+ultimaPeticionMedios);
            console.log("Peticion Perfiles: "+ultimaPeticionPerfiles);
            
            /* Condicion de actualización y cuando se incumplen:
            // - No debe haber una petición de perfiles más reciente que la de medios -> Pinchar sobre la lista de perfiles
            // - No se debe superar el límite de peticiones de seguridad -> Si el servidor tarda demasiado en realizar la conversión o hay un error interno que provoque que no se cumpla la persistencia con base de datos
             */
            if (ultimaPeticionMedios >= ultimaPeticionPerfiles && medio.contadorActualizacion < limiteSeguridad){ 
                // Recursuvidad
                setTimeout(function(){
                    nuevoMedio.contadorActualizacion = medio.contadorActualizacion+1;
                    actualizarMedio(nuevoMedio); // Llamada recursiva
                },periodoActualizacion_medio);   // Periodo de actualización
            } else {
                console.log("Actualizacion cancelada");
            }
        } else {
            console.log("Valores actualizados. Proceso de conversión finalizado");
        }
        
    })
    .fail(function(){
        console.log("getJSON: Fail!"); 
    })
    .always(function(){

    });
    
};

// Actualizar datos de un medio
var actualizarRecurso = function(recurso){
    
    console.log("Actualización de recurso "+recurso.id+" "+(recurso.contadorActualizacion)+"/"+limiteSeguridad);
    
    var capaID_estado = "#estadoRecurso"+recurso.id;
    var capaID_linkVideo = "#linkVideo"+recurso.id;
    var capaID_listaRecursos = "#lista_recursos"+recurso.medioId;

    // Petición
    console.log("UriRecurso: "+recurso.uriRecurso);
    $.getJSON(recurso.uriRecurso, console.log("getJSON: Obteniendo datos del medio "+recurso.id))
    .done(function(nuevoRecurso){
        console.log("getJSON: OK!");
        var recursoCorreccion = correccionRecurso(nuevoRecurso); // Recurso con valores adaptados para mostrar en la interfaz.

        // Cambiar los valores de la interfaz
        if (nuevoRecurso.estado !== recurso.estado) {
            console.log("Los datos han cambiado: actualizar valores de la interfaz");
            console.log("Actualizando valor del estado en "+capaID_estado+" a "+recursoCorreccion.estado);
            $(capaID_estado).text(recursoCorreccion.estado); // Actualizar valores

            console.log("Actualizando valor del link en "+capaID_linkVideo+" a "+recursoCorreccion.icono_recurso);
            $(capaID_linkVideo).html(recursoCorreccion.icono_recurso); // Actualizar valores
            
            // Estado OK
            if (recursoCorreccion.estado_OK) {
                jQuery(recursoCorreccion.playerLink).html5lightbox(); // Habilitar evento en elemento playerLink. Imprescindible para reproducir el video en en la ventana emergente.
                recursoCorreccion.playerLink.appendTo(capaID_linkVideo);
            }
        } else {
            console.log("El estado del recurso no ha cambiado");
        }
        
        // Recursividad: ¿Los valores están actualizados?
        if (recursoCorreccion.conversionActiva) {
            console.log("Los datos no están actualizados. Proceso de conversión activo.");
            // Depuración:
//            console.log("Contador actual: "+recurso.contadorActualizacion);
//            console.log("Peticion Medios: "+ultimaPeticionMedios);
//            console.log("Peticion Recursos: "+ultimaPeticionRecursos);
//            console.log("Peticion Perfiles: "+ultimaPeticionPerfiles);
//            
            /* Condicion de actualización y cuando se incumplen:
            // - Lista de recursos debe estar visible -> Plegar la lista de recursos
            // - No debe haber una petición de perfiles más reciente que la de recursos -> Pinchar sobre la lista de perfiles
            // - No debe haber una petición de medios más reciente que la de medios -> Actualizar la lista de medios desde el icono home
            // - No se debe superar el límite de peticiones de seguridad -> Si el servidor tarda demasiado en realizar la conversión o hay un error interno que provoque que no se cumpla la persistencia con base de datos
             */
            if ($(capaID_listaRecursos).is(":visible") && ultimaPeticionRecursos >= ultimaPeticionPerfiles && 
                ultimaPeticionRecursos >= ultimaPeticionMedios && recurso.contadorActualizacion < limiteSeguridad) { 

                // Recursuvidad
                setTimeout(function(){
                    nuevoRecurso.contadorActualizacion = recurso.contadorActualizacion+1;
                    actualizarRecurso(nuevoRecurso); // Función recursiva
                },periodoActualizacion_recurso);
            } else {
                console.log("Actualizacion cancelada");
            }
        } else {
            console.log("Valores actualizados. Proceso de conversión finalizado");
        }
        
    })
    .fail(function(){
        console.log("getJSON: Fail!"); 
    })
    .always(function(){

    });
    
};