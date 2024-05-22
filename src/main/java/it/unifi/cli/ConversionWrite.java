package it.unifi.cli;

import javax.xml.stream.*;
import javax.xml.transform.TransformerException;
import java.io.*;

import it.unifi.API.Conversion;
import it.unifi.API.ObjAttribute;
import it.unifi.API.ObjElement;
import it.unifi.API.ObjNamespace;

//This class provides static methods for converting (using API) and writing to output file
class ConversionWrite {
    public static void writeXMLToJSON(String inputFile, String outputFile) throws XMLStreamException, IOException {
        //it.unifi.main.ProgressThread
        ProgressThread pthread = new ProgressThread();
        Thread runnerThread = new Thread(pthread);
        //if main crashes, the JVM closes all daemons threads
        runnerThread.setDaemon(true);
        runnerThread.start();

        ObjElement rootElement1 = Conversion.XMLToInternal(inputFile);
        Conversion.internalToJSON(outputFile, rootElement1, true);
        runnerThread.interrupt();
    }
    public static void writeJSONToXML(String inputFile, String outputtFile) throws IOException, XMLStreamException, TransformerException {
        //it.unifi.main.ProgressThread
        ProgressThread pthread = new ProgressThread();
        Thread runnerThread = new Thread(pthread);
        //if main crashes, the JVM closes all daemons threads
        runnerThread.setDaemon(true);
        runnerThread.start();

        ObjElement rootElement = Conversion.JSONToInternal(inputFile);
        Conversion.internalToXML(outputtFile, rootElement, true);
        runnerThread.interrupt();
    }

    //DEBUG methods
    public static void writeToFileRecursiveTraverse(String outPath, ObjElement rootElement) throws IOException {
        //This is a debug method that prints all the objects information (not XML nor JSON)
        //TODO: should manage exceptions
        FileWriter fw = new FileWriter(outPath);
        fw.write("##This is a debug internal representation, not a valid XML not JSON format##\n");
        recursiveTraverse(fw, rootElement);
        fw.close();
    }
    public static void recursiveTraverse(FileWriter fw, ObjElement rootElement) throws IOException {
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
            recursiveTraverse(fw, obj);
        }
    }
}
