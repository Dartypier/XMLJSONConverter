package it.unifi.cli;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

class Main {
    public static void main(String[] args) throws XMLStreamException, IOException, TransformerException {
        //MAIN function for JAR execuion

        //Logger
//        Logger logger = LoggerFactory.getLogger(Main.class);
//        logger.info("SW Version: {}", "1.0");

        //EXECUTION EXAMPLES
        //it.unifi.main.ProgressThread
//        it.unifi.main.ProgressThread pthread = new it.unifi.main.ProgressThread();
//        Thread runnerThread = new Thread(pthread);
//        runnerThread.start();

        //Do XMLTInternal conversion and get rootElement
//        it.unifi.main.ObjElement rootElement1 = XMLToInternal("po.xml");

        //export json
//        internalToJSON("out.json", rootElement1, true, false);

        //Read from JSON
//        it.unifi.main.ObjElement rootElement2 = JSONToInternal("out.json");

        //DEBUG: check rootElement content
//        writeToFileRecursiveTraverse("out.txt", rootElement1);

        //export to XML
//        internalToXML("out.xml", rootElement2);

        //STOP PrgressThread when all operations are completed
//        runnerThread.interrupt();

        InputManager inputManager = new InputManager(args);
        inputManager.conversionCallWrite();
    }
}
