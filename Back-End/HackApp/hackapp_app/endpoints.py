from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import Usuario, Publicacion, Comentario, Solicitud, Sesiones
import json, hashlib
from .functions import funciones_de_usuario, funciones_de_publicacion
from .schemas import schemas_de_usuario, schemas_de_publicacion

class usuarios: 

    @csrf_exempt
    def auth (request) :

        if request.method == 'POST':
            oData = json.loads(request.body)

            #Validar Json enviado 
            if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oAuthSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)

            sUser = oData.get('user', None)
            sPassword = oData.get('password', None)

            if not sUser or not sPassword:
                return JsonResponse({"Error": "Faltan valores o campos invalidos"}, status=400)
            
            if len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
                return JsonResponse({"Error": "Token no valido"}, status=401)
            
            try:
                oUsuario = Usuario.objects.get(email=sUser) if '@' in sUser else Usuario.objects.get(username=sUser)
            except Usuario.DoesNotExist:
                return JsonResponse({"Error": "Usuario no encontrado"}, status=404)
            
            if oUsuario.password != hashlib.sha384(sPassword.encode()).hexdigest():
                return JsonResponse({"Error": "Contraseña no valida"}, status=401)

            sToken = funciones_de_usuario.usuario.gen_token()
            Sesiones(usuario=oUsuario, token=sToken).save()

            return JsonResponse({"token" : sToken}, status=200)

        if request.method == 'DELETE':

            try:
                sToken = request.headers.get('token')
            except:
                return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
            
            if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
                return JsonResponse({"Error": "Token no valido"}, status=401)

            Sesiones.objects.filter(token=sToken).delete()
            return JsonResponse({"Info" : "Cuenta cerrada existosamente"}, status=200)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    def cuenta (request) :

        if request.method == "POST":
            oData = json.loads(request.body)

            #Validar Json enviado 
            if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)

            sUsername = oData.get('username')
            sEmail = oData.get('email')
            nTelefono = oData.get('telefono')

            if Usuario.objects.filter(username=sUsername).exists():
                return JsonResponse({"Error": "Nombre de usuario en uso"}, status=401)
        
            if Usuario.objects.filter(email=sEmail).exists():
                return JsonResponse({"Error": "Email en uso"}, status=401)
        
            if Usuario.objects.filter(telefono=nTelefono).exists():
                return JsonResponse({"Error": "Numero de telefono en uso"}, status=401)

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

            return JsonResponse({"Info" : "Cuenta eliminada existosamente"}, status=200)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    def cambio_de_datos (request) :
        if request.method != "POST":
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        #Validar Json enviado
        if not funciones_de_usuario.usuario.comprobar_body_data(oData, schemas_de_usuario.schemas.oCambioDatosSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
    
        try:
            sToken = request.headers.get('token')
        except:
            return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
            
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

        oUsuario.save()
        return JsonResponse({"Info": "Datos guardados correctamente"}, status=200)
    
    def get_usuario (request, username) :
        if request.method != 'GET':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        try:
            sToken = request.headers.get('token')
        except:
            return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
        
        if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
            return JsonResponse({"Error": "Token no valido"}, status=401)

        if not Sesiones.objects.filter(token=sToken).exists():
            return JsonResponse({"Error": "Token no encontrado"}, status=404)
        
        oUsuario = Usuario.objects.get(username=username).to_json()

        return JsonResponse(oUsuario, status=200)
    
class publicaciones():

    @csrf_exempt
    def nuevaPublicacion(request):
        if request.method!= 'POST':
            return JsonResponse({"Error": "Metodo no permitido"}, status=405)
        
        oData = json.loads(request.body)

        #Validar Json enviado
        if not funciones_de_publicacion.publicacion.comprobar_body_data(oData, schemas_de_publicacion.schemas.oMainSchema):
                return JsonResponse({"Error": "Error en JSON enviada"}, status=400)
        
        try:
            sToken = request.headers.get('token')
        except:
            return JsonResponse({"Error": "No se ha mandado el token"}, status=400)
        
        if sToken is None or len(sToken) != funciones_de_usuario.usuario.nLongitudToken:
            return JsonResponse({"Error": "Token no valido"}, status=401)
    
        funciones_de_publicacion.publicacion.crear_publicaciones(oData, sToken)
        return JsonResponse({"Info": "Publicacion creada correctamente"}, status=200)
    
