# Generated by Django 5.0.4 on 2024-04-15 18:57

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('hackapp_app', '0004_valoraciones_usuario'),
    ]

    operations = [
        migrations.AlterField(
            model_name='usuario',
            name='sms_code',
            field=models.IntegerField(default=999999, null=True),
        ),
    ]