class schemas():
    
    oAuthSchema = {
        "type": "object",
        "properties": {
            "user": {"type": "string"},
            "clave": {"type": "string"}
            },
        "required": ["user", "clave"]
    }

    oSchema = {
        "type": "object",
        "properties": {
            "nombre": {"type": "string", "maxLength": 20},
            "apellidos": {"type": "string", "maxLength": 50},
            "Usuario": {"type": "string", "maxLength": 50},
            "biografia": {"type": ["string", "null"], "maxLength": 200},
            "email": {"type": "string", "format": "email"},
            "clave": {"type": "string", "maxLength": 100},
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
        "required": ["nombre", "apellidos", "Usuario", "email", "clave"]
    }

    oCambioDatosSchema = {
        "type": "object",
        "properties": {
            "Usuario": {"type": "string"},
            "clave": {"type": "string"},
            "email": {"type": "string"},
            "nombre": {"type": "string"},
            "apellidos": {"type": "string"},
            "biografia": {"anyOf": [
                {"type": "string"},
                {"type": "null"}
            ]},
            "notificaciones": {"type": "boolean"},
            "cuenta_privada": {"type": "boolean"},
            "avatar": {"type": "string"}
        }
    }

    oCambioPassSchema = {
        "type": "object",
        "properties": {
            "telefono": {"type": "string"},
            "clave": {"type": "string"},
        },
        "required": ["telefono", "clave"]
    }
