from jsonschema import validate
from hackapp_app.models import Sesiones, Comentario, Publicacion, Valoraciones
from statistics import mean

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
            valoracion = 0
        )

        oComentario.save()

    def get_comentarios (id):

        aComentarios = []

        for oComentario in Comentario.objects.all():
            if oComentario.publicacion.id == id:
                aComentarios.append(oComentario.to_json())

        aComentarios.reverse()
        return aComentarios
    
    def valorar_comentarios (oData,sToken, id) :

        # Crear nueva valoracion
        oComentario = Comentario.objects.get(id=id)
        nValoracion = oData.get('valoracion')

        oValoracion = Valoraciones (
            usuario = Sesiones.objects.get(token=sToken).usuario,
            comentario = oComentario,
            valoracion = nValoracion
        )

        oValoracion.save()

        # Actualizar valoracion del comentario

        aValoraciones = Valoraciones.objects.filter(comentario=oComentario)
        aTotalValoraciones = [float(nVal.valoracion) for nVal in aValoraciones]
            
        aTotalValoraciones.append(float(nValoracion))

        # Promedio de valoraciones
        nValoracion = mean(aTotalValoraciones)

        # Función para asignar el .5, natural anterior o posterior a la valoración media
        nParte_decimal = nValoracion - int(nValoracion)

        if 0.00 <= nParte_decimal <= 0.25:
            nValoracion = int(nValoracion)
        
        elif 0.26 <= nParte_decimal <= 0.75:
            nValoracion = int(nValoracion) + 0.5

        else:
            nValoracion = int(nValoracion) + 1

       # Asiganar valoracion a comentario 
        oComentario.valoracion = nValoracion
        
        oComentario.save()
