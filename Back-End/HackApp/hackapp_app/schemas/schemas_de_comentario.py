class schema:

    oMainSchema = {
       "type": "object",
       "properties": {
           "comentario": {"type": "string"},
           "valoracion": {
               "type": "number",
               "minimum": 0,
               "maximum": 5,
               "multipleOf": 0.5
           }
       },
       "required": ["comentario", "valoracion"]
    }   
