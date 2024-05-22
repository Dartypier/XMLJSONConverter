package it.unifi.main;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

//manages user input from CLI and calls relatives Conversion methods
public class InputManager {
    private String inputFile;
    private String outputFile;

    public InputManager(String[] args) {
        if(args.length == 1){
            //check if there is help parameter
            if(args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("--help")) {
                helpPrompt();
                //exit program
                System.exit(0);
            }
            else{
                //do the conversion and return root ObjElement and all the tree structure
                throw new RuntimeException("Unknown argument: "+args[0]);
            }
        }
        else if(args.length == 2) {
            this.inputFile = args[0];
            this.outputFile = args[1];
        }
        //error
        else{
            throw new RuntimeException("Usage: <input-file> <output-file>");
        }
    }

    //does conversion call based on user input writing to an output file
    public void conversionCallWrite() throws XMLStreamException, IOException, TransformerException {
        if(inputFile.toLowerCase().contains("xml") || inputFile.toLowerCase().contains("railml")) {
            Conversions.convertXMLToJSON(inputFile, outputFile);
        }
        else if(inputFile.toLowerCase().contains("json")) {
            Conversions.convertJSONToXML(inputFile, outputFile);
        }
        else{
            helpPrompt();
            throw new RuntimeException("Unsupported input format");
        }
    }

    //does the conversion call based on user input, returning the objects tree structure (API use)
    public ObjElement conversionCallObj() throws XMLStreamException, IOException {
        if(inputFile.toLowerCase().contains("xml") || inputFile.toLowerCase().contains("railml")) {
            return Conversions.XMLToInternal(inputFile);
        }
        else if(inputFile.toLowerCase().contains("json")) {
            return Conversions.JSONToInternal(inputFile);
        }
        else{
            helpPrompt();
            throw new RuntimeException("Unsupported input format");
        }
    }

    void helpPrompt(){
        System.out.println("XML/JSON Converter by Jacopo Zecchi (Dartypier)");
        System.out.println("Usage: <input-file>.{xml|railml|json} <output-file>.*");
        System.out.println("Specify an input xml (railml) or JSON file. The output will be the respective JSON/XML format");
    }
}
