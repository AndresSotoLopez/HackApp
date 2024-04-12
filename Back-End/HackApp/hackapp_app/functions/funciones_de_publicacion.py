from jsonschema import validate
from hackapp_app.models import Sesiones, Publicacion

class publicacion:

    def comprobar_body_data (oData, schema) :

        # Verificar si hay claves adicionales en oData
        for clave in oData.keys():
            if clave not in schema["properties"].keys():
                print(clave)
                return False

        # Validar el objeto oData con el schema
        try:
            validate(instance=oData, schema=schema)
            return True
        except:
            print("aqui")
            return False
        
    def crear_publicaciones (oData, sToken) :
        post = Publicacion(
            nombre=oData.get('nombre'),
            tipo_publicacion=oData.get('tipo_publicacion'),
            link_externo=oData.get('link_externo'),
            imagen=oData.get('imagen'),
            descripcion=oData.get('descripcion'),
            gravedad=oData.get('gravedad'),
            cve=oData.get('cve'),
            probado=oData.get('probado'),
            usuario=Sesiones.objects.get(token=sToken).usuario,
        )

        post.save()
