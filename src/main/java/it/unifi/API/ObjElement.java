package it.unifi.API;

import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjElement {

    //element name
    private String name;
    //element value
    private String value;
    //parent element for actual ObjElement (null for root XML element)
    //this is used by the reader XML and JSON methods to have a parent object as a back pointer
    //when using the API for adding ObjElement is not necessary but eventually useful for backtracking
    private ObjElement parent;
    //List that contains attributes for element object
    private List<ObjAttribute> attributesList;
    //List that contains namespaces for element Object
    private List<ObjNamespace> namespacesList;
    //List that contains nested elements for element object
    private List<ObjElement> elementsList;

    public ObjElement(){
        //all fields initialized by default
        this.name=null;
        this.value=null;
        this.parent =null;
        this.attributesList=new ArrayList<>();
        this.elementsList=new ArrayList<>();
        this.namespacesList=new ArrayList<>();
    }
    public ObjElement(String name, String value, ObjElement parent, List namespacesList, List attributesList, List elementsList){
        this.name=name;
        this.value=value;
        this.parent = parent;
        this.namespacesList=namespacesList;
        this.attributesList=attributesList;
        this.elementsList=elementsList;
        //now taking care of assigning the actual ObjElement as parent for all elementList ObjElements
        for(ObjElement e:this.elementsList){
            e.setParent(this);
        }
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getValue() {return value;}
    public void setValue(String value) {this.value = value;}

    public ObjElement getParent() {return parent;}
    public void setParent(ObjElement parent) {this.parent = parent;}

    public List<ObjAttribute> getAttributesList() {return attributesList;}
    public List<ObjElement> getElementsList() {return elementsList;}
    public List<ObjNamespace> getNamespaceList() {return namespacesList;}

    public void addAttributes(Iterator<Attribute> it){
        while(it.hasNext()){
            //next xml attribute
            Attribute at = it.next();
            //create it.unifi.main.ObjAttribute and set name, value
            ObjAttribute oat = new ObjAttribute();
            //check if it has prefix or not
            if(at.getName().getPrefix().isEmpty())
                oat.setName(at.getName().getLocalPart());
            else
                oat.setName(at.getName().getPrefix()+":"+at.getName().getLocalPart());

            oat.setValue(at.getValue());
            //add it.unifi.main.ObjAttribute to attributesList
            attributesList.add(oat);
        }
    }
    public void addNamespaces(Iterator<Namespace> it){
        //same implementation as before, but for namespaces
        while(it.hasNext()){
            Namespace ns = it.next();
            ObjNamespace ons = new ObjNamespace();
            //if the namespace is the default one
            //there is no prefix, so this field is null
            ons.setPrefix(ns.getPrefix());
            ons.setURI(ns.getNamespaceURI());
            namespacesList.add(ons);
        }
    }
}
