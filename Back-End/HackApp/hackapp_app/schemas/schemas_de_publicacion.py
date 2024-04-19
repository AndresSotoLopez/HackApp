class schemas:

    oMainSchema = {
        "type": "object",
        "properties": {
            "nombre": {"type": "string"},
            "tipo_publicacion": {"type": "integer", "enum": [1, 2, 3]},
            "link_externo": {"type": ["string", "null"], "format": "uri"},
            "imagen": {"type": ["string", "null"]},
            "descripcion": {"type": "string"},
            "gravedad": {"type": ["integer", "null"]},
            "cve": {"type": ["string", "null"]},
            "probado": {"type": ["string", "null"]}
        },
        "required": ["nombre", "tipo_publicacion", "descripcion"]
    }

