package it.unifi.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;

//This API class has static methods used to
//get internal object representation from input XML/JSON file
//get XML/JSON out representation from internal representation
public class Conversion {

    //conversion methods
    public static void internalToJSON(String outPath, ObjElement rootElement, boolean enablePretty) throws IOException {
        //All the times disableHtmlEscaping() is active, to prevent GSON converting special characters to
        //unicde escapes
        Gson gson;
        if(enablePretty){
            Gson gsonInner = new GsonBuilder()
                    .registerTypeAdapter(ObjNamespace.class, new ObjNamespaceAdapter())
                    .registerTypeAdapter(ObjAttribute.class, new ObjAttributeAdapter())
                    .create();

            gson = new GsonBuilder()
                    .registerTypeAdapter(ObjElement.class, new ObjElementAdapter(gsonInner))
                    .setPrettyPrinting()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }
        else {
            Gson gsonInner = new GsonBuilder()
                    .registerTypeAdapter(ObjNamespace.class, new ObjNamespaceAdapter())
                    .registerTypeAdapter(ObjAttribute.class, new ObjAttributeAdapter())
                    .create();

            gson = new GsonBuilder()
                    .registerTypeAdapter(ObjElement.class, new ObjElementAdapter(gsonInner))
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .create();
        }

        //write to json
        Writer fileWriter = new FileWriter(outPath);
        gson.toJson(rootElement, fileWriter);
        fileWriter.close();
    }

    public static ObjElement XMLToInternal(String inPath) throws FileNotFoundException, XMLStreamException {
        //this method make the internal (java objects) representation on the XML file
        //create and arraylist of arraylist for memorizing recursively the objElement
        //at each step
        ArrayList<ArrayList<ObjElement>> recursiveObjBlocks = new ArrayList<>();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream(inPath));

        //mantains a reference to the root element, because its mantains
        //all other objects references: is the returned entry point ObjElement
        ObjElement rootElement = null;

        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();

            if(nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                ObjElement e = new ObjElement();
                //keeps track of rootElement only
                if(rootElement == null) {rootElement = e;}
                //set element prefix:name
                if(startElement.getName().getPrefix().isEmpty())
                    e.setName(startElement.getName().getLocalPart());
                else
                    e.setName(startElement.getName().getPrefix()+":"+startElement.getName().getLocalPart());
                //set element attributes
                e.addAttributes(startElement.getAttributes());
                //set element namespaces
                e.addNamespaces(startElement.getNamespaces());

                //preparing ArrayList
                //adding element's own arraylist
                recursiveObjBlocks.add(new ArrayList<>());
                //adding new it.unifi.main.ObjElement to its arrayLIst and the precedent
                recursiveObjBlocks.get(recursiveObjBlocks.size()-1).add(e);
                //check if -2 range exist (for root element it doesn't exist) and adding parent (root) ObjElement
                if(recursiveObjBlocks.size()>=2) {
                    recursiveObjBlocks.get(recursiveObjBlocks.size() - 2).add(e);
                    //parent element for actual e ObjElement is the first one in the precedent ArrayList
                    e.setParent(recursiveObjBlocks.get(recursiveObjBlocks.size() - 2).get(0));
                }
                else{
                    //no parent element for XML Root document element
                    e.setParent(null);
                }
            }
            if(nextEvent.isCharacters()){
                String str = nextEvent.asCharacters().getData().trim();
                //if the value is WhiteSpace, it is ignored
                if(!(nextEvent.asCharacters().isIgnorableWhiteSpace() || nextEvent.asCharacters().isWhiteSpace())){
                    //add string value to the current object
                    //first element in last arrayList of reursiveObjBlocks
                    recursiveObjBlocks.get(recursiveObjBlocks.size()-1).get(0).setValue(str);

                }
            }
            if(nextEvent.isEndElement()){
//                EndElement endElement = nextEvent.asEndElement();
                //its own block is the last array block. Now removing itself from the ArrayList
                ObjElement e = recursiveObjBlocks.get(recursiveObjBlocks.size()-1).remove(0);
                //add all last block elements to the element elementList
                e.getElementsList().addAll(recursiveObjBlocks.get(recursiveObjBlocks.size()-1));
                //removing the just cloned arrayList from blocks
                recursiveObjBlocks.remove(recursiveObjBlocks.size()-1);
            }
        }

        return rootElement;
    }

    public static ObjElement JSONToInternal(String inPath) throws IOException {
        //read from JSON to rootElement
        Gson gsonInner = new GsonBuilder()
                .registerTypeAdapter(ObjNamespace.class, new ObjNamespaceAdapter())
                .registerTypeAdapter(ObjAttribute.class, new ObjAttributeAdapter())
                .create();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ObjElement.class, new ObjElementAdapter(gsonInner))
                .serializeNulls()
                .create();

        Reader fileReader = new FileReader(inPath);
        ObjElement rootElement = gson.fromJson(fileReader, ObjElement.class);
        fileReader.close();
        return rootElement;
    }

    public static void internalToXML(String outPath, ObjElement rootElement, boolean prettify) throws IOException, XMLStreamException, TransformerException {
        //this method convert internal representation of objects to XML file
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();

        Writer sw = new StringWriter();
        XMLEventWriter writer = output.createXMLEventWriter(sw);

        //define the xml document
        writer.add(eventFactory.createStartDocument());

        //set default namespace for the root element if defined
        //NOTE: seems that declaring the namespace directly in the createStartElement method
        //is sufficient to declare the default xmlns="..." namespace, thus not requiring
        //setDefaultNamespace method
//        String defaultUri = null;
//        for(it.unifi.main.ObjNamespace ns: rootElement.getNamespaceList()){
//            if(ns.getPrefix().isEmpty())
//                defaultUri = ns.getURI();
//            break;
//            //this is the URI of the default namespace (xmlns="...")
//            //indeed it has no prefix
//        }
//        if(defaultUri!=null){
//            writer.setDefaultNamespace(defaultUri);
//        }

        //call recursive method from rootElement
        traverseXML(rootElement, writer, eventFactory);

        //write to string and close
        writer.flush();
        writer.close();

        if(prettify){
            //prettify XML (does indentations)
            Transformer t = TransformerFactory.newInstance().newTransformer();
            //add indentations
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            //add line break before the root element
            t.setOutputProperty(OutputKeys.STANDALONE, "yes");
            Writer fw = new FileWriter(outPath);
            StreamSource ss = new StreamSource(new StringReader(sw.toString()));
            StreamResult sr = new StreamResult(fw);
            t.transform(ss, sr);
            //write to file and close
            fw.flush();
            fw.close();
        }
        else {
            Writer fw = new FileWriter(outPath);
            fw.write(sw.toString());
            //write to file and close
            fw.flush();
            fw.close();
        }
    }

    //helper for internalToXML
    private static void traverseXML(ObjElement rootElement, XMLEventWriter writer, XMLEventFactory eventFactory) throws XMLStreamException {
        //recursive function

        //only for the rootelement assign defaultNamespace xmlns to prevet binding errors
        String defaultUri = null;
        for(ObjNamespace ns: rootElement.getNamespaceList()){
            if(ns.getPrefix().isEmpty())
                defaultUri = ns.getURI();
            break;
            //this is the URI of the default namespace (xmlns="...")
            //indeed it has no prefix
        }
        //now only the rootElement, that has default namespace xlmns="..."
        //will have defaultUri not null, so this prevents error binding default namespace
        writer.add(eventFactory.createStartElement("" , defaultUri, rootElement.getName()));
        //add namespaces
        for(ObjNamespace ns : rootElement.getNamespaceList()){
            writer.add(eventFactory.createNamespace(ns.getPrefix(), ns.getURI()));
        }
        //add attributes
        for(ObjAttribute at : rootElement.getAttributesList()){
            writer.add(eventFactory.createAttribute(at.getName(), at.getValue()));
        }
        //add (nested) elements
        for(ObjElement obj: rootElement.getElementsList()){
            traverseXML(obj, writer, eventFactory);
        }

        //add value (base case of recursion)
        if(rootElement.getValue()!=null){
            writer.add(eventFactory.createCharacters(rootElement.getValue()));
        }

        //closing
        writer.add(eventFactory.createEndElement("", "", rootElement.getName()));

    }
}
