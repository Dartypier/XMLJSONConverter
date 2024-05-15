//This class defines the namespaces defined in a XML element
//the default namespace (ie: 'xlmns:{URI}') has not 'prefix'
public class ObjNamespace {
    private String prefix;
    private String URI;

    public String getPrefix() {return prefix;}
    public void setPrefix(String prefix) {this.prefix = prefix;}

    public String getURI() {return URI;}
    public void setURI(String URI) {this.URI = URI;}
}
