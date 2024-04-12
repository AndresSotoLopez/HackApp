from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import Usuario, Publicacion, Comentario, Solicitud, Sesiones
import json, secrets, hashlib, jsonschema
from jsonschema import validate
from .schemas import schemas_de_usuario
from .functions import funciones_de_usuario

class usuarios: 

    @csrf_exempt
    def auth (request) :

        if request.method == 'POST':
            oData = json.loads(request.body)

            #Validar Json enviado 
            try:
                validate(instance=oData, schema=schemas_de_usuario.schemas.oAuthSchema)
            except jsonschema.ValidationError as e:
                return JsonResponse({"Error": e.message}, status=400)

            sUser = oData.get('user', None)
            sPassword = oData.get('password', None)

            if not sUser or not sPassword:
                return JsonResponse({"Error": "Faltan valores o campos invalidos"}, status=400)
            
            if len(sToken) != 20:
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
            sToken = request.headers.get('token', None)

            if not sToken:
                return JsonResponse({"Error": "Faltan valores o campos invalidos"}, status=400)

            if not Sesiones.objects.filter(token=sToken).exists():
                return JsonResponse({"Error": "Token no encontrado"}, status=404)

            Sesiones.objects.filter(token=sToken).delete()
            return JsonResponse({"Info" : "Cuenta cerrada existosamente"}, status=200)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)
    
    @csrf_exempt
    def cuenta (request) :

        if request.method == "POST":
            oData = json.loads(request.body)

            #Validar Json enviado 
            try:
                validate(instance=oData, schema=schemas_de_usuario.schemas.oSchema)
            except jsonschema.ValidationError as e:
                return JsonResponse({"Error": e.message}, status=400)

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
            sToken = request.headers.get('token', None)

            if not sToken:
                return JsonResponse({"Error": "Faltan valores o campos invalidos"}, status=400)
            
            if len(sToken) != 20:
                return JsonResponse({"Error": "Token no valido"}, status=401)

            if not Sesiones.objects.filter(token=sToken).exists():
                return JsonResponse({"Error": "Token no encontrado"}, status=404)

            Sesiones.objects.get(token=sToken).usuario.delete()

            return JsonResponse({"Info" : "Cuenta eliminada existosamente"}, status=200)
        
        return JsonResponse({"Error": "Metodo no permitido"}, status=405)