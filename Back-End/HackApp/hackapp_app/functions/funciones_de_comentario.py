from jsonschema import validate
from hackapp_app.models import Sesiones, Comentario, Publicacion

class comentario:
    
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
        
    def crear_comentario (oData, sToken, id) :

        oComentario = Comentario(
            usuario = Sesiones.objects.get(token=sToken).usuario,
            publicacion = Publicacion.objects.get(id=id),
            comentario = oData.get('comentario'),
            valoracion = oData.get('valoracion'),
        )

        oComentario.save()

    def get_comentarios (id):

        aComentarios = []

        for oComentario in Comentario.objects.all():
            if oComentario.publicacion.id == id:
                aComentarios.append(oComentario.to_json())

        aComentarios.reverse()
        return aComentarios