package it.unifi.API;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ObjElementAdapter extends TypeAdapter<ObjElement> {

    private final Gson gson;
    private final TypeAdapter<ObjNamespace> namespaceAdapter;
    private final TypeAdapter<ObjAttribute> attributeAdapter;

    //this will allow TypeAdapter delegation to specifc TypeAdapter Objects
    public ObjElementAdapter(Gson gson) {
        this.gson = gson;
        this.namespaceAdapter = gson.getAdapter(ObjNamespace.class);
        this.attributeAdapter = gson.getAdapter(ObjAttribute.class);
    }

    @Override
    public void write(JsonWriter writer, ObjElement objElement) throws IOException {
        writer.beginObject();
        //writes name:value
        writer.name(objElement.getName());
        writer.value(objElement.getValue());
        //writes namespaces
        if(!objElement.getNamespaceList().isEmpty()){
            writer.name("namespaces");
            writer.beginArray();
            for(ObjNamespace ns : objElement.getNamespaceList()){
                namespaceAdapter.write(writer, ns);
            }
            writer.endArray();
        }
        if(!objElement.getAttributesList().isEmpty()){
            writer.name("attributes");
            writer.beginArray();
            for(ObjAttribute a : objElement.getAttributesList()){
                attributeAdapter.write(writer, a);
            }
            writer.endArray();
        }
        if(!objElement.getElementsList().isEmpty()){
            writer.name("elements");
            writer.beginArray();
            //recursive call
            for(ObjElement childElement : objElement.getElementsList()){
                write(writer, childElement);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    public ObjElement read(JsonReader reader) throws IOException {
        //object data
        String name = null;
        String value = null;
        List<ObjNamespace> namespacesList = new ArrayList<>();
        List<ObjAttribute> attributesList = new ArrayList<>();
        List<ObjElement> elementsList = new ArrayList<>();

        reader.beginObject();

        while(reader.hasNext()){
            JsonToken token = reader.peek();
            if(token == JsonToken.NAME){
                String nameField = reader.nextName();
                //read json property name
                if(name==null){
                    name = nameField;

                }
                else{
                    //reads lists delegating
                    if(nameField.equals("namespaces")){
                        namespacesList = gson.fromJson(reader, new TypeToken<ArrayList<ObjNamespace>>(){}.getType());
                    }
                    else if(nameField.equals("attributes")){
                        attributesList = gson.fromJson(reader, new TypeToken<ArrayList<ObjAttribute>>(){}.getType());
                    }
                    else if(nameField.equals("elements")){
                        reader.beginArray();
                        while(reader.hasNext()){
                            elementsList.add(read(reader));
                        }
                        reader.endArray();
                    }
                }
            }
            //treat every value as a string
            else if(token == JsonToken.STRING){
                value = reader.nextString();
            }
            else if(token == JsonToken.NUMBER){
                //convert double and int to appropriate strings
                String num = String.valueOf(reader.peek());
                if(num.contains(".")){
                    value = String.valueOf(reader.nextDouble());
                }
                else{
                    value = String.valueOf(reader.nextLong());
                }
            }
            else if(token == JsonToken.BOOLEAN){
                value = String.valueOf(reader.nextBoolean());
            }
            else if(token == JsonToken.NULL){
                reader.nextNull();
            }
            //throw error (none of the above cases)
            else{
                throw new JsonParseException("Not a valid JSON document: "+reader.toString());
            }
        }
        reader.endObject();

        //note that the parameterized constructor takes care of assigning the parent for all
        //the ObjElement of elementsList
        return new ObjElement(name, value, null, namespacesList, attributesList, elementsList);
    }
}
