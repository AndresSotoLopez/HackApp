import secrets, hashlib
from hackapp_app import models

class usuario:
    
    def gen_token () :

        nLongitud = 20
        sToken = secrets.token_hex(nLongitud) # La funcion multiplica por 2 la longitud

        while models.Sesiones.objects.filter(token=sToken).exists():
            sToken = secrets.token_hex(nLongitud)
        
        return sToken;

    
    def crear_usuario (oData) :
        usuario = models.Usuario(
            nombre=oData.get('nombre'),
            apellidos=oData.get('apellidos'),
            username=oData.get('username'),
            biografia=oData.get('biografia'),
            email=oData.get('email'),
            password=hashlib.sha384(oData.get('password').encode()).hexdigest(),
            ct=oData.get('ct'), 
            telefono=oData.get('telefono'),
            avatar=oData.get('avatar'),
            posts=oData.get('posts'),
            seguidores=oData.get('seguidores'),
            seguidos=oData.get('seguidos'),
            cuenta_privada=oData.get('cuenta_privada')
        )

        usuario.save()