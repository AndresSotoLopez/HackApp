import secrets, hashlib
from jsonschema import validate
from hackapp_app.models import Sesiones, Usuario

class usuario:

    nLongitudToken = 40
    
    def gen_token () :

        nLongitud = 20
        sToken = secrets.token_hex(nLongitud) # La funcion multiplica por 2 la longitud

        while Sesiones.objects.filter(token=sToken).exists():
            sToken = secrets.token_hex(nLongitud)
        
        return sToken;

    
    def crear_usuario (oData) :
        usuario = Usuario(
            nombre=oData.get('nombre'),
            apellidos=oData.get('apellidos'),
            Usuario=oData.get('Usuario'),
            biografia=oData.get('biografia'),
            email=oData.get('email'),
            clave=hashlib.sha384(oData.get('clave').encode()).hexdigest(),
            ct=oData.get('ct'), 
            telefono=oData.get('telefono'),
            avatar=oData.get('avatar'),
            posts=oData.get('posts'),
            seguidores=oData.get('seguidores'),
            seguidos=oData.get('seguidos'),
            cuenta_privada=oData.get('cuenta_privada')
        )

        usuario.save()

    def comprobar_body_data (oData, schema) :

        # Verificar si hay claves adicionales en oData
        for clave in oData.keys():
            if clave not in schema["properties"].keys():
                return False

        # Validar el objeto oData con el schema
        try:
            validate(instance=oData, schema=schema)
            return True
        except:
            return False
