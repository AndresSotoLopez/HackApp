"""
URL configuration for HackApp project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/5.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from hackapp_app import endpoints

urlpatterns = [
    path('admin/', admin.site.urls),
    path('v1/auth', endpoints.usuarios.auth),
    path('v1/cuenta', endpoints.usuarios.cuenta),
    path('v1/cambioDatos', endpoints.usuarios.cambio_de_datos),
    path('v1/datosUsuario/<str:username>', endpoints.usuarios.get_usuario),
    path('v1/nuevaPublicacion', endpoints.publicaciones.nuevaPublicacion),
    path('v1/publicacion/<int:id>', endpoints.publicaciones.get_publicacion),
    path('v1/borrarPublicaciones/<int:id>', endpoints.publicaciones.borrar_publicacion),
]
