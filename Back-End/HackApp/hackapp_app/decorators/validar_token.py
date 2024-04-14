from functools import wraps
from django.http import JsonResponse
from hackapp_app.models import Sesiones

def validar_token (view_func) :
    @wraps(view_func)
    def wrapper(request, *args, **kwargs):
        # Validacion del token
        sToken = request.headers.get('token')

        nLongitudToken = 40

        if sToken is None or len(sToken) != nLongitudToken:
            return JsonResponse({"Error": "Token no válido"}, status=401)

        if not Sesiones.objects.filter(token=sToken).exists():
            return JsonResponse({"Error": "Token no encontrado"}, status=404)
        return view_func(request, *args, **kwargs)
    return wrapper
