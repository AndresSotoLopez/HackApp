from django.db import models
from django.utils import timezone
from django.core.validators import MinValueValidator

# Create your models here.
class Usuario(models.Model):
    nombre = models.CharField(max_length=20)
    apellidos = models.CharField(max_length=50)
    username = models.CharField(max_length=50, unique=True)
    biografia = models.CharField(max_length=200, null=True, blank=True)
    email = models.EmailField(unique=True)
    password = models.CharField(max_length=100)
    ct = models.IntegerField(default=0)
    telefono = models.IntegerField(default=999999999)
    avatar = models.URLField(max_length=100, null=True)
    posts = models.IntegerField(default=0, validators=[
            MinValueValidator(0)
        ])
    seguidores = models.IntegerField(default=0, validators=[
            MinValueValidator(0)
        ])
    seguidos = models.IntegerField(default=0, validators=[
            MinValueValidator(0)
        ])
    cuenta_privada = models.BooleanField(default=False)
    notificaciones = models.BooleanField(default=True)
    sms_code = models.IntegerField(default=999999, null=True)

    def get_name(self):
        return self.username
    
    def get_avatar(self):
        return self.avatar
    
    def to_json(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'apellidos': self.apellidos,
            'username': self.username,
            'email': self.email,
            'password': self.password,
            'ct': self.ct,
            'telefono': self.telefono,
            'avatar': self.avatar,
            'posts': self.posts,
            'seguidores': self.seguidores,
            'seguidos': self.seguidos,
            'cuenta_privada': self.cuenta_privada,
            'notificaciones': self.notificaciones,
            'sms_code': self.sms_code,
        }
    
class Publicacion(models.Model):
    nombre = models.CharField(max_length=20, null=False)
    TIPO_OPCIONES = (
        (1, 'Noticia'),
        (2, 'Exploit'),
        (3, 'Foro')
    )
    tipo_publicacion = models.IntegerField(choices=TIPO_OPCIONES, default=0)
    link_externo = models.URLField(max_length=100, null=True)
    imagen = models.URLField(max_length=100, null=True)
    descripcion = models.CharField(max_length=200)
    fecha = models.DateField(default=timezone.now)
    GRAVEDAD_OPCIONES = (
        (1, 'Low'),
        (2, 'Medium'),
        (3, 'High'),
        (4, 'Critical'),
    )
    gravedad = models.IntegerField(choices=GRAVEDAD_OPCIONES, default=0, null=True)
    cve = models.CharField(max_length=13, unique=True, null=True)
    probado = models.CharField(max_length=200, null=True)
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)

    def __str__(self):
        return self.nombre
    
    def to_json(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'tipo_publicacion': self.tipo_publicacion,
            'link_externo': self.link_externo,
            'imagen': self.imagen,
            'descripcion': self.descripcion,
            'fecha': self.fecha,
            'gravedad': self.gravedad,
            'cve': self.cve,
            'probado': self.probado,
            'usuario': self.usuario.nombre,
            'avatar': self.usuario.avatar,
        }

class Comentario(models.Model):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE, null=False)
    publicacion = models.ForeignKey(Publicacion, on_delete=models.CASCADE, null=False)
    comentario = models.CharField(max_length=200)
    valoracion = models.DecimalField(default=0, max_digits=2, decimal_places=1)

    def to_json(self):
        return {
            'id': self.id,
            'usuario': self.usuario.nombre,
            'avatar': self.usuario.avatar,
            'comentario': self.comentario,
            'valoracion': self.valoracion,
        }
    
class Solicitud(models.Model):
    seguidor = models.ForeignKey(Usuario, on_delete=models.CASCADE, null=False, related_name='seguidor')
    seguido = models.ForeignKey(Usuario, on_delete=models.CASCADE, null=False, related_name='seguido')
    estado = models.BooleanField(default=False)

    def to_json(self):
        return {
            'id': self.id,
            'seguido': self.seguido.nombre,
            'avatar': self.seguido.avatar
        }

class Sesiones(models.Model):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE, null=False)
    token = models.CharField(max_length=20, unique=True)

class Valoraciones(models.Model):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE, null=False, related_name='usuario')
    comentario = models.ForeignKey(Comentario, on_delete=models.CASCADE, null=False)
    valoracion = models.DecimalField(default=0, max_digits=2, decimal_places=1)