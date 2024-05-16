import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws XMLStreamException, IOException, TransformerException {
        //Logger
        Logger logger = LoggerFactory.getLogger(Main.class);
//        logger.info("SW Version: {}", "1.0");

        //EXECUTION EXAMPLES
        //ProgressThread
//        ProgressThread pthread = new ProgressThread();
//        Thread runnerThread = new Thread(pthread);
//        runnerThread.start();

        //Do XMLTInternal conversion and get rootElement
//        ObjElement rootElement1 = XMLToInternal("po.xml");

        //export json
//        internalToJSON("out.json", rootElement1, true, false);

        //Read from JSON
//        ObjElement rootElement2 = JSONToInternal("out.json");

        //DEBUG: check rootElement content
//        writeToFileRecursiveTraverse("out.txt", rootElement1);

        //export to XML
//        internalToXML("out.xml", rootElement2);

        //STOP PrgressThread when all operations are completed
//        runnerThread.interrupt();

        InputManager inputManager = new InputManager(args);
        inputManager.conversionCall();
    }
}
