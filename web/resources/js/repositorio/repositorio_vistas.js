/*
 * Trabajo Fin de Máster. Repositorio Multimedia para aplicaciones móviles
 * Autor: Juan Carlos Rubio García
 * Tutor: Miguel Ángel Lozano Ortega
 * Máster en Ingeniería de Telecomunicación (2016/2017)
 * Universidad de Alicante
 */

/* --------------------------------------------------*
 * Gestón de las vistas.*
 * --------------------------------------------------*/

 /* Cargar recursos - mostrar/ocultar
  * ----------------------------------*/
  
// Posibles cambios
// - Actualizar periodicamente la lista si el estado del medio es PROCESANDO. -> Hecho
// - En tal caso, cambiar tambien el estado del medio en la lista. -> Hecho
// - Mostrar una descripción del perfil al poner el mouse encima. -> No se contempla, es muy costoso.
 var gestionarRecursos = function (medio) {
    
    // Capa a mostrar/ocultar
    var capaID = "#lista_recursos"+medio.id;

    // Comprobar estado
    var estado = $(capaID).css("display");
    //console.log("CapaID: "+capaID);
    //console.log("Display: "+estado);
    if (estado === "none") {
        // Carga los recursos, muestra la lista y cambia el desplegable
        console.log("Cargando recursos");
        getRecursos(medio); 
    } else {
        // Ocultar capa
        console.log("Ocultando la lista de recursos...");
        $(capaID).hide("slow");
        // Cambiar el desplegable
        var desplegableID = "#desplegable_medio"+medio.id;
        $(desplegableID).fadeOut("slow",function(){
            $(desplegableID).attr("src","./resources/images/expand.png");
            $(desplegableID).fadeIn("slow");     
        });  
    }                           
};

 /* Cargar perfil n - mostrar/ocultar contenido extra
  * -------------------------------------------------*/

var gestionarPerfiles = function (perfil) {
    
    // Capa a mostrar/ocultar
    var capaID = "#cuerpo_perfil"+perfil.id;

    // Comprobar estado
    var estado = $(capaID).css("display");
    //console.log("CapaID: "+capaID);
    //console.log("Display: "+estado);
    if (estado === "none") {
        // Carga los recursos, muestra la lista y cambia el desplegable
        console.log("Cargando perfil "+perfil.id);
        getPerfil(perfil); 
    } else {
        // Ocultar capa
        console.log("Ocultando perfil "+perfil.id);
        $(capaID).hide("slow");

        // Cambiar el desplegable
        var desplegableID = "#desplegable_perfil"+perfil.id;
        $(desplegableID).fadeOut("slow",function(){
            $(desplegableID).attr("src","./resources/images/plus.png");
            $(desplegableID).fadeIn("slow");     
        });  
    }                           
};