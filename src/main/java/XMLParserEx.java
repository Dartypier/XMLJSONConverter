import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//STAX documentation to manage XML parse:
//https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html

public class XMLParserEx {
    public static void main(String[] args) throws IOException, XMLStreamException {
        //Logger
        Logger logger = LoggerFactory.getLogger(XMLParserEx.class);
        logger.info("SW Version: {}", "1.0");

        //ProgressThread
        ProgressThread pthread = new ProgressThread();
        Thread runnerThread = new Thread(pthread);
        runnerThread.start();

        //create and arraylist of arraylist for memorizing recursively the objElement
        //at each step
        ArrayList<ArrayList<ObjElement>> recursiveObjBlocks = new ArrayList<>();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("po.xml"));

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

        logger.info("Completed");
//        writeToFile("out.txt", rootElement);

        //Conversion from Internal to JSON
        //TODO: add option to pretty on request (brings to big files)
        //TODO: should personalize serializer/deserializer to remove empty lists
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
//                .serializeNulls()
                .create();

        //write to json
        Writer fileWriter = new FileWriter("out.json");
        gson.toJson(rootElement, fileWriter);
        fileWriter.close();
        //read from json to rootElement
        Reader fileReader = new FileReader("out.json");
        rootElement = gson.fromJson(fileReader, ObjElement.class);
        fileReader.close();

        //DEBUG: check rootElement content
        writeToFile("out.txt", rootElement);


        //STOP PrgressThread when all
        //operations are completed
        runnerThread.interrupt();
    }

    //This is a debug method that prints all the objects information
    //it is not a JSON format!
    public static void writeToFile(String outPath, ObjElement rootElement) throws IOException {
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
}
