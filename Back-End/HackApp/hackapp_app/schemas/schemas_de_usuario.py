class schemas():
    
    oAuthSchema = {
        "type": "object",
        "properties": {
            "user": {"type": "string"},
            "password": {"type": "string"}
            },
        "required": ["user", "password"]
    }

    oSchema = {
        "type": "object",
        "properties": {
            "nombre": {"type": "string", "maxLength": 20},
            "apellidos": {"type": "string", "maxLength": 50},
            "username": {"type": "string", "maxLength": 50},
            "biografia": {"type": ["string", "null"], "maxLength": 200},
            "email": {"type": "string", "format": "email"},
            "password": {"type": "string", "maxLength": 100},
            "ct": {"type": "integer", "default": 0},
            "telefono": {"type": "integer", "default": 999999999},
            "avatar": {"type": ["string", "null"], "format": "uri", "maxLength": 100},
            "posts": {"type": "integer", "default": 0, "minimum": 0},
            "seguidores": {"type": "integer", "default": 0, "minimum": 0},
            "seguidos": {"type": "integer", "default": 0, "minimum": 0},
            "cuenta_privada": {"type": "boolean", "default": False},
            "notificaciones": {"type": "boolean", "default": True},
            "sms_code": {"type": ["integer", "null"], "default": 999999, "unique": True}
        },
        "required": ["nombre", "apellidos", "username", "email", "password"]
    }

    oCambioDatosSchema = {
        "type": "object",
        "properties": {
            "username": {"type": "string"},
            "password": {"type": "string"},
            "email": {"type": "string"},
            "biografia": {"anyOf": [
                {"type": "string"},
                {"type": "null"}
            ]}
        }
    }
