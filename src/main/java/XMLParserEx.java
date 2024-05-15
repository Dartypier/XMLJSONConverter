import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//STAX documentation to manage XML parse:
//https://docs.oracle.com/javase/tutorial/jaxp/stax/api.html

public class XMLParserEx {
    public static void main(String[] args) throws IOException, XMLStreamException {
        //Logger
        Logger logger = LoggerFactory.getLogger(XMLParserEx.class);
        logger.info("SW Version: {}", "1.0");

        //create and arraylist of arraylist for memorizing recursively the objElement
        //at each step
        ArrayList<ArrayList<ObjElement>> recursiveObjBlocks = new ArrayList<>();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(new FileInputStream("family.xml"));

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
                recursiveObjBlocks.add(new ArrayList<ObjElement>());
                //adding new ObjElement to each arrayLIst
                for (ArrayList<ObjElement> recursiveObjBlock : recursiveObjBlocks) {
                    recursiveObjBlock.add(e);
                }
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
                //its own block is the last array block
                ObjElement e = recursiveObjBlocks.get(recursiveObjBlocks.size()-1).remove(0);
                //add all last block to the element elementList
                e.getElementsList().addAll(recursiveObjBlocks.get(recursiveObjBlocks.size()-1));
                //removing the just cloned arrayList from blocks
                recursiveObjBlocks.remove(recursiveObjBlocks.size()-1);
            }
        }

        logger.info("Completed");
        writeToFile("out.txt", rootElement);
    }

    //This is a debug method that prints all the objects information
    //it is not a JSON format!
    public static void writeToFile(String outPath, ObjElement rootElement) throws IOException {

        //TODO: should manage exceptions
        File file = new File(outPath);
        FileWriter fw = new FileWriter(file);

        //write out rootElement
        fw.write(rootElement.getName()+" "+rootElement.getValue()+" ");
        for(ObjNamespace ns: rootElement.getNamespaceList()){
            fw.write(ns.getPrefix()+" "+ns.getURI()+"\n");
        }
        fw.write("\n");
        for(ObjAttribute attr: rootElement.getAttributesList()){
            fw.write(attr.getName()+" "+attr.getValue()+"\n");
        }
        fw.write("\n");

        //write out children
        for(ObjElement obj: rootElement.getElementsList()){
            fw.write(obj.getName()+" "+obj.getValue()+" ");
            for(ObjNamespace ns: obj.getNamespaceList()){
                fw.write(ns.getPrefix()+" "+ns.getURI()+"\n");
            }
            fw.write("\n");
            for(ObjAttribute attr: obj.getAttributesList()){
                fw.write(attr.getName()+" "+attr.getValue()+"\n");
            }
            fw.write("\n");
        }
        fw.close();
    }
}
