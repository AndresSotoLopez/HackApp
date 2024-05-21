from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import Usuario, Publicacion, Comentario, Solicitud, Sesiones
import json, hashlib
from hackapp_app.functions import funciones_de_usuario, funciones_de_publicacion, funciones_de_comentario
from hackapp_app.schemas import schemas_de_usuario, schemas_de_publicacion, schemas_de_comentario
from hackapp_app.decorators.validar_token import validar_token


class usuarios: 

    @csrf_exempt
    def auth (request) :

        if request.method == 'POST':
            oData = json.loads(request.body)

            # Validar Json enviado 
            if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oAuthSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)

            sUser = oData.get('user', None)
            sPassword = oData.get('password', None)

            if not sUser or not sPassword:
                return JsonResponse({"Error": "Faltan valores o campos invalidos"}, status=400)
            
            # Comprobaciñon del input del usuario.
            try:
                oUsuario = Usuario.objects.get(email=sUser) if '@' in sUser else Usuario.objects.get(username=sUser)
            except Usuario.DoesNotExist:
                return JsonResponse({"Error": "Usuario no encontrado"}, status=404)
            
            if oUsuario.password != hashlib.sha384(sPassword.encode()).hexdigest():
                return JsonResponse({"Error": "Contraseña no valida"}, status=401)

            # Generacion de token
            sToken = funciones_de_usuario.usuario.gen_token()
            Sesiones(usuario=oUsuario, token=sToken).save()

            return JsonResponse({"token" : sToken}, status=200)

        if request.method == 'DELETE':

            # Obtención y comprobación del token de usuario.
            try:
                sToken = request.headers.get('token')
            except:
                return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
            
            if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
                return JsonResponse({"Error": "Token no valido"}, status=401)

            Sesiones.objects.filter(token=sToken).delete()
            return JsonResponse({},status=204)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    def cuenta (request) :

        if request.method == "POST":
            oData = json.loads(request.body)

            # Validar Json enviado 
            if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)

            # Obtención y comprobación de los campos unique.
            sUsername = oData.get('username')
            sEmail = oData.get('email')
            nTelefono = oData.get('telefono')

            if Usuario.objects.filter(username=sUsername).exists():
                return JsonResponse({"Error": "Nombre de usuario en uso"}, status=401)
        
            if Usuario.objects.filter(email=sEmail).exists():
                return JsonResponse({"Error": "Email en uso"}, status=401)
        
            if Usuario.objects.filter(telefono=nTelefono).exists():
                return JsonResponse({"Error": "Numero de telefono en uso"}, status=401)

            # Creación del usuario.
            funciones_de_usuario.usuario.crear_usuario(oData)
            sToken = funciones_de_usuario.usuario.gen_token()

            oUsuario = Usuario.objects.get(email=sEmail) if '@' in sEmail else Usuario.objects.get(username=sUsername)
            Sesiones(usuario=oUsuario, token=sToken).save()

            return JsonResponse({"token" : sToken}, status=200)

        if request.method == "DELETE":
            try:
                sToken = request.headers.get('token')
            except:
                return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
            
            if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
                return JsonResponse({"Error": "Token no valido"}, status=401)

            if not Sesiones.objects.filter(token=sToken).exists():
                return JsonResponse({"Error": "Token no encontrado"}, status=404)

            Sesiones.objects.get(token=sToken).usuario.delete()

            return JsonResponse({},status=204)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    def cambio_de_datos (request) :
        if request.method != "POST":
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        # Validar Json enviado
        if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oCambioDatosSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
    
        sToken = request.headers.get('token')
            
        if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
            return JsonResponse({"Error": "Token no valido"}, status=401)

        if not Sesiones.objects.filter(token=sToken).exists():
            return JsonResponse({"Error": "Token no encontrado"}, status=404)
        
        oUsuario = Sesiones.objects.get(token=sToken).usuario

        if(oData.get('username')):
            oUsuario.username = oData.get('username')

        if(oData.get('email')):
            oUsuario.email = oData.get('email')

        if oData.get('biografia') == None:
            oUsuario.biografia = None 
        else:
            oUsuario.biografia = oData.get('biografia')

        if(oData.get('password')):
            oUsuario.password = hashlib.sha384(oData.get('password').encode()).hexdigest()

        if 'notificaciones' in oData:
            oUsuario.notificaciones = oData['notificaciones']
    
        if 'cuenta_privada' in oData:
            oUsuario.cuenta_privada = oData['cuenta_privada']

        oUsuario.save()
        return JsonResponse({"Info": "Datos guardados correctamente"}, status=200)
    
    @csrf_exempt
    def cambio_de_pass (request) :
        if request.method != "POST":
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        # Validar Json enviado
        if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oCambioPassSchema):
            return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
        
        oUsuario = Usuario.objects.get(telefono=oData["telefono"])

        if not oUsuario:
            return JsonResponse({"Error": "Usuario no encontrado"}, status=404)
        
        oUsuario.password = hashlib.sha384(oData.get('password').encode()).hexdigest()
        oUsuario.save()

        return JsonResponse({"Info": "Contraseña cambiada correctamente"}, status=200)
        
    
    def get_usuario (request, username) :
        if request.method != 'GET':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        # Validacion del token
        sToken = request.headers.get('token')
        
        if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
            return JsonResponse({"Error": "Token no valido"}, status=401)

        if not Sesiones.objects.filter(token=sToken).exists():
            return JsonResponse({"Error": "Token no encontrado"}, status=404)
        
        # Se obtienen los datos del usuario
        oUsuario = Usuario.objects.get(username=username).to_json()

        return JsonResponse(oUsuario, status=200)
    
class publicaciones():

    @validar_token
    @csrf_exempt
    def nuevaPublicacion(request):
        if request.method!= 'POST':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        #Validar Json enviado
        if not funciones_de_publicacion.publicacion.comprobar_body_data(oData, schemas_de_publicacion.schemas.oMainSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
        
        # Creación de la publicación.
        funciones_de_publicacion.publicacion.crear_publicaciones(oData, request.headers.get('token'))
        return JsonResponse({"Info": "Publicacion creada correctamente"}, status=200)
    
    @validar_token
    def get_publicacion (request, id) :
        if request.method!= 'GET':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        sUsername = request.GET.get("username", None)
        nTipoNoticia = request.GET.get("tipoNoticia", None)

        aPublicacion = []

        if id == 0:
            
            if sUsername or nTipoNoticia:
               aPublicacion = funciones_de_publicacion.publicacion.filtrar_publicaciones(sUsername, nTipoNoticia)
               if len(aPublicacion) == 0:
                   return JsonResponse({"Error": "No se encontraron publicaciones"}, status=404)

            else:
                for oPost in Publicacion.objects.all():
                    aPublicacion.append(oPost.to_json())
            
        else:
            try:
                oPost = Publicacion.objects.get(id=id).to_json()
            except Publicacion.DoesNotExist:
                return JsonResponse({"Error": "Publicacion no encontrada"}, status=404)
            
            aPublicacion.append(oPost)

        # Ordenación inversa para que se muestren primero los posts más recientes.
        aPublicacion.reverse()

        return JsonResponse(aPublicacion, status=200, safe=False)
    
    @validar_token
    @csrf_exempt
    def borrar_publicacion (request, id) :
        if request.method != "DELETE":
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
        # Obtener el post con el id dado.
        try:
            oPost = Publicacion.objects.get(id=id)
        except Publicacion.DoesNotExist:
            return JsonResponse({"Error": "Publicacion no encontrada"}, status=404)
        
        oPost.delete()
        return JsonResponse({},status=204)
    
class comentario:
    
    @validar_token
    @csrf_exempt
    def comentario (request, id) :  

        if request.method == 'POST':
            oData = json.loads(request.body)

            # Validar Json enviado
            if not funciones_de_comentario.comentario.comprobar_body_data(oData, schemas_de_comentario.schema.oMainSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
            
            # Validacion del id de la publicación a comentar.
            if not Publicacion.objects.filter(id=id).exists():
                return JsonResponse({"Error": "Publicacion no encontrada"}, status=404)
            
            if Publicacion.objects.get(id=id).tipo_publicacion != 3:
                return JsonResponse({"Error": "No se pueden comentar comentarios en este tipo de posts"}, status=400)
            
            funciones_de_comentario.comentario.crear_comentario(oData, request.headers.get('token'), id)
            return JsonResponse({"Info":"Comentario creado con exito"}, status=201)
        
        if request.method == 'GET':
            
            # Validacion del id de la publicación a obtener los comentarios.
            if not Publicacion.objects.filter(id=id).exists():
                return JsonResponse({"Error": "Publicacion no encontrada"}, status=404)
        
            if Publicacion.objects.get(id=id).tipo_publicacion != 3:
                return JsonResponse({"Error": "No se pueden comentar comentarios en este tipo de posts"}, status=400)
            
            aComentarios = funciones_de_comentario.comentario.get_comentarios(id)
            return JsonResponse(aComentarios, status=200, safe=False)
        
        if request.method == 'DELETE':
            
            try:
                Comentario.objects.get(id=id).delete()
                return JsonResponse({},status=204)
            except:
                return JsonResponse({"Error": "Comentario no encontrado"}, status=404)
           
        return JsonResponse({"Error": "Metodo no permitido"}, status=405) 
    
    @validar_token
    def valorar_comentarios(request, id):
        if request.method != 'POST':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        # Validar Json enviado
        if not funciones_de_comentario.comentario.comprobar_body_data(oData, schemas_de_comentario.schema.oValorarSchema):
                return JsonResponse({"Error": "Error en JSON enviado"}, status=400)
        
        # Validacion del id de la publicación a obtener los comentarios.
        if not Comentario.objects.filter(id=id).exists():
            return JsonResponse({"Error": "Comentario no encontrado"}, status=404)
        
        funciones_de_comentario.comentario.valorar_comentarios(oData, request.headers.get('token'), id)
        return JsonResponse({"Info": "Comentario valorado con exito"}, status=201)
    
class solicitudes:

    @validar_token
    @csrf_exempt
    def manejar_solicitudes(request):
        if request.method == 'POST':
        
            # Obtener query param del id de usuario a aceptar la solicitud
            nId = request.GET.get('id')
            if nId is None:
                return JsonResponse({"Error": "Parametro ID no proporcionado"}, status=400)
            
            sToken = request.headers.get('token')

            if not Solicitud.objects.filter(id=nId).exists():
                return JsonResponse({"Error": "Solicitud no encontrada"}, status=404)
            else:
                oSolicitud = Solicitud.objects.get(id=nId) 
                oSeguidor = Sesiones.objects.get(token=sToken).usuario
                oSeguido = Solicitud.objects.get(id=nId).seguido

                if Solicitud.objects.get(id=nId).estado:
                    return JsonResponse({"Error": "Esta solicitud ya ha sido aceptada"}, status=400)

                if oSeguidor != Solicitud.objects.get(id=nId).seguidor:
                    return JsonResponse({"Error": "No tienes permisos para aceptar esta solicitud"}, status=403)
                    
                oSolicitud.estado = True
                oSolicitud.save()
                oSeguido.seguidos += 1
                oSeguido.save()
                oSeguidor.seguidores += 1
                oSeguidor.save()
            return JsonResponse({},status=200)
        
        if request.method == 'GET':

            oUser = Sesiones.objects.get(token=request.headers.get('token')).usuario

            # Se obtienen todas las peticiones que tiene un usuario
            aSolicitud = Solicitud.objects.filter(seguidor=oUser)
            aSolicitudes = [oSolicitud.to_json() for oSolicitud in aSolicitud if oSolicitud.estado == False]

            return JsonResponse(aSolicitudes, status=200, safe=False)
        
        if request.method == 'DELETE':

            nId = request.GET.get('id')
            if nId is None:
                return JsonResponse({"Error": "Parametro ID no proporcionado"}, status=400)

            try:
                oSolicitud = Solicitud.objects.get(id=nId)
                oSolicitud.delete()
                return JsonResponse({},status=200)
            except:
                return JsonResponse({"Error": "Solicitud no encontrada"}, status=404)
            
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    @validar_token
    def mandar_peticiones(request):
        if request.method != 'POST':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        bEstado = False
        sUsername = request.GET.get('user', None)
        sToken = request.headers.get("token")

        if sUsername is None:
            return JsonResponse({"Error": "Parametro user no proporcionado"}, status=400)
        
        oSeguidor = Usuario.objects.get(username=sUsername)
        oSeguido = Sesiones.objects.get(token=sToken).usuario

        if oSeguidor.cuenta_privada:
            bEstado = True
        
        oNuevaSolicitud = Solicitud (
            seguidor = oSeguidor,
            seguido = oSeguido,
            estado = not bEstado
        )

        oNuevaSolicitud.save()
        return JsonResponse({},status=200)
    
    @validar_token
    def comprobar_seguimiento(request):
        if request.method != 'GET':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        nSeguido = request.GET.get('seguido', None)
        sSeguidor = request.GET.get('seguidor', None)
        sToken = request.headers.get("token")

        if nSeguido is None or sSeguidor is None:
            return JsonResponse({"Error": "Parametros no proporcionados"}, status=400)
        
        oSeguido = Sesiones.objects.get(token=sToken).usuario
        oSeguidor= Usuario.objects.get(username=nSeguido)

        print(Solicitud.objects.filter(seguidor=oSeguidor, seguido=oSeguido).exists())

        if Solicitud.objects.filter(seguidor=oSeguidor, seguido=oSeguido).exists():
            if Solicitud.objects.get(seguidor=oSeguidor, seguido=oSeguido).estado is True:
                return JsonResponse({"comprobacion" : True, "id" : Solicitud.objects.get(seguidor=oSeguidor, seguido=oSeguido).id}, status=200)
            else:
                return JsonResponse({"comprobacion" : False, "id" : Solicitud.objects.get(seguidor=oSeguidor, seguido=oSeguido).id}, status=200)
        else:
            return JsonResponse({"comprobacion" : "No existe la solicitud"}, status=404)
