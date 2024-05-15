import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLParserEx {
    public static void main(String[] args) throws IOException, XMLStreamException {
        //Logger
        Logger logger = LoggerFactory.getLogger(XMLParserEx.class);
        logger.info("SW Version: {}", "1.0");

        //ProgressThread
        ProgressThread pthread = new ProgressThread();
        Thread runnerThread = new Thread(pthread);
        runnerThread.start();

        //Do XMLTInternal conversion and get rootElement
        ObjElement rootElement1 = XMLToInternal("po.xml");

        //Read from JSON
        ObjElement rootElement2 = JSONToInternal("out.json");

        //DEBUG: check rootElement content
        writeToFileRecursiveTraverse("out.txt1", rootElement1);

        //STOP PrgressThread when all operations are completed
        runnerThread.interrupt();
    }

    //This is a debug method that prints all the objects information (not XML nor JSON)
    public static void writeToFileRecursiveTraverse(String outPath, ObjElement rootElement) throws IOException {
        //TODO: should manage exceptions
        FileWriter fw = new FileWriter(outPath);
        recursiveTraverse(outPath, fw, rootElement);
        fw.close();
    }

    public static void recursiveTraverse(String outPath, FileWriter fw, ObjElement rootElement) throws IOException {
        //this is a recursive function
        //the rootElement is the root for every node (recursive POV)

        fw.write(rootElement.getName() +" "+rootElement.getValue()+"\n");
        for(ObjNamespace ns: rootElement.getNamespaceList()){
            fw.write(ns.getPrefix()+" "+ns.getURI()+"\n");
        }
        for(ObjAttribute attr: rootElement.getAttributesList()){
            fw.write(attr.getName()+" "+attr.getValue()+"\n");
        }
        for(ObjElement obj: rootElement.getElementsList()){
            recursiveTraverse(outPath, fw, obj);
        }
    }

    public static void internalToJSON(String outPath, ObjElement rootElement, boolean enablePretty, boolean disableEmptyNull) throws IOException {
        //TODO: disableEmptyNull should include custom serializer/deserializer to not include empty lists
        Gson gson;
        if(enablePretty && disableEmptyNull){
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();
        }
        else if(enablePretty && !disableEmptyNull){
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
        }
        else if(!enablePretty && disableEmptyNull){
            gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
        }
        else {
            gson = new GsonBuilder()
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
        //all other objects references
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
                recursiveObjBlocks.add(new ArrayList<ObjElement>());
                //adding new ObjElement to its arrayLIst and the precedent
                recursiveObjBlocks.get(recursiveObjBlocks.size()-1).add(e);
                //check if -2 range exist (for root element it doesn't exist)
                if(recursiveObjBlocks.size()>=2)
                    recursiveObjBlocks.get(recursiveObjBlocks.size()-2).add(e);
            }
            if(nextEvent.isCharacters()){
                String str = nextEvent.asCharacters().getData();
                //if the value is WhiteSpace, it is ignored
                if(!(nextEvent.asCharacters().isIgnorableWhiteSpace() || nextEvent.asCharacters().isWhiteSpace())){
                    //add string value to the current object
                    //first element in last arrayList of reursiveObjBlocks
                    recursiveObjBlocks.get(recursiveObjBlocks.size()-1).get(0).setValue(str);

                }
            }
            if(nextEvent.isEndElement()){
                EndElement endElement = nextEvent.asEndElement();
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
        Gson gson = new GsonBuilder().create();
        Reader fileReader = new FileReader("out.json");
        ObjElement rootElement = gson.fromJson(fileReader, ObjElement.class);
        fileReader.close();
        return rootElement;
    }
}
