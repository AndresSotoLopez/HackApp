from jsonschema import validate
from hackapp_app.models import Sesiones, Publicacion

class publicacion:

    def comprobar_body_data (oData, schema) :

        # Verificar si hay claves adicionales en oData
        for oClave in oData.keys():
            if oClave not in schema["properties"].keys():
                return False

        # Validar el objeto oData con el schema
        try:
            validate(instance=oData, schema=schema)
            return True
        except:
            return False
        
    def crear_publicaciones (oData, sToken) :
        oPost = Publicacion(
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

        oPost.save()

    def filtrar_publicaciones (sUsername, nTipoNoticia) :

        aPublicacion = []

        if sUsername :

            for oPost in Publicacion.objects.all():
                if oPost.usuario.username == sUsername:
                    aPublicacion.append(oPost.to_json())

            return aPublicacion
        
        if nTipoNoticia :
            for oPost in Publicacion.objects.filter(tipo_publicacion=nTipoNoticia):
                aPublicacion.append(oPost.to_json())

            return aPublicacion

