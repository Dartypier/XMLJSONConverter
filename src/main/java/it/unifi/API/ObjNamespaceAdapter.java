package it.unifi.API;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class ObjNamespaceAdapter extends TypeAdapter<ObjNamespace> {

    @Override
    public void write(JsonWriter writer, ObjNamespace objNamespace) throws IOException {
        writer.beginObject();
        writer.name(objNamespace.getPrefix());
        writer.value(objNamespace.getURI());
        writer.endObject();
    }

    @Override
    public ObjNamespace read(JsonReader reader) throws IOException {
        reader.beginObject();
        //prefix can be "" if it is a default namespace in input
        //then it's stored as a null String
        String prefix = null;
        String URI = null;

        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token == JsonToken.NAME){
                //read json property name
                if(reader.peek().toString().equals("")){
                    //if prefix is "" (means no prefix) put null
                    reader.nextName();
                    prefix = null;
                }
                else
                    prefix = reader.nextName();
            }
            else if(token == JsonToken.STRING){
                URI = reader.nextString();
            }
            //none of the above cases (throw error)
            else{
                throw new JsonParseException("Not a valid JSON document: "+reader.toString());
            }
        }
        reader.endObject();

        ObjNamespace objNamespace = new ObjNamespace();
        objNamespace.setPrefix(prefix);
        objNamespace.setURI(URI);
        return objNamespace;
    }
}
