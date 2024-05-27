package it.unifi.API;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class ObjAttributeAdapter extends TypeAdapter<ObjAttribute> {
    @Override
    public void write(JsonWriter writer, ObjAttribute objAttribute) throws IOException {
        writer.beginObject();
        writer.name(objAttribute.getName());
        writer.value(objAttribute.getValue());
        writer.endObject();
    }

    @Override
    public ObjAttribute read(JsonReader reader) throws IOException {
        reader.beginObject();
        String name = null;
        String value = null;

        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token == JsonToken.NAME){
                //read json property name
                name = reader.nextName();
            }
            //treat every value as a string
            else if(token == JsonToken.STRING){
                value = reader.nextString();
            }
            //none of the above cases (throw error)
            else{
                throw new JsonParseException("Not a valid JSON document: "+reader.toString());
            }
        }
        reader.endObject();

        ObjAttribute objAttribute = new ObjAttribute();
        objAttribute.setName(name);
        objAttribute.setValue(value);
        return objAttribute;
    }
}
