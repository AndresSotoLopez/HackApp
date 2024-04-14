# Generated by Django 5.0.4 on 2024-04-14 18:36

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('hackapp_app', '0003_alter_comentario_valoracion_valoraciones'),
    ]

    operations = [
        migrations.AddField(
            model_name='valoraciones',
            name='usuario',
            field=models.ForeignKey(default=1, on_delete=django.db.models.deletion.CASCADE, related_name='usuario', to='hackapp_app.usuario'),
            preserve_default=False,
        ),
    ]