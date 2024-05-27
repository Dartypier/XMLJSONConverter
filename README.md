# XJC: XMLJSONConverter

<p align="center"><img src="logo-black.png" alt="demo gif" /></p>
<p align="center">
    <a href="#" alt="static code analysis">
        <img src="https://img.shields.io/badge/Embold%20rating-4.04/5-brightgreen" /></a>
</p>
<br/>

This tool allows a seamless conversion from XML/JSON to JSON/XML without the need of XSD (XML Schema). 
- conversion from XML to JSON is always doable
- conversion from JSON to XML is doable, but JSON file must be organized in a defined structured way (readable by the tool, see below)

This tool also presents an API that can be used by external Java programs to get an internal representation of XML/JSON data document in input, to allow their elaboration. It's also possible to write out the extracted elaborated data to an output document.

<p align="center"><img src="demo.gif" alt="demo gif" /></p>

## CLI Usage
This particular use allows to convert an input XML/JSON file to the respective JSON/XML document.

To convert specify the input file (`.xml`, `.railml`, `.json`) and then specify the output file.

Example:
```bash
java -jar XMLJSONConverter input-file.xml output-file.json
```

## API Usage
The tool presents a library that can be used by Java programs to extract XML/JSON data into Java objects. It is also possible to write out elaborated obects to an XML/JSON file.

You can add the jar file to your Java project to use the library. The package that you should use is `it.unifi.API`. Inside you have the following classes:
- `ObjElement`: this class defines the base type used by the library. Every ObjElement has a `name`, a `value`, an `attributesList`, a `namespacesList`, a `elementsList`. 

    The structure followed is the same of an XML element as you can see. In particular `elementsList`contains the children of the actual ObjElement. 
- `ObjAttribute`: defines a `name` and a `value` for an attribute.
- `ObjNamespace`: defines a `prefix` (`null` if the namespace is default) and the `URI` of the namespace.

All these classes defines getters and setters.

`Conversion` is the actual class that allows to extract XML or JSON data from a document to a root ObjElement. The `XMLToInternal` and `JSONToInternal` methods returns an `ObjElement` that contains its attributes and namespaces, and also it contains a list of its children `ObjElements`. You can see that this structure is recursive because every `ObjElement` contains its children `ObjElements` each with their namespaces, attributes and children elements.

The returned root `ObjElement` can thus be used to traverse other elements and manipulate their values. Once the manipulation is done you can also write out all the elements into an output file with `InternalToJSON` or `InternalToXML` methods that take as parameters the root `ObjElement` and the output file path.

## Supported json format
While every well-formed XML file in input is supported, that's not the case for JSON, because JSON data structure is not as rigid as XML, so to convert from JSON to XML is necessary to follow the following format for JSON data:
```json
{
    "root": "root_value",
    "namespaces": [
      {"prefix_1": "URI"},
      ...
    ],
    "attributes": [
      {"attribute_1": "attribute_value"},
      ...
    ],
    "elements": [
      {
        "child_element_1": "child_value"
      },
      ...
    ]
}
```

You can see that the JSON document is divided recursively in 4 sections:
  - `"element_name": "value"` This part define an element and its value (if it has no value, put `null` as value). The element with his value will be converted in XML as it is. [**This  is the only mandatory section, the other 3 sections are optional**]
  - `"namespaces":[...]`. This part will define a list of namespaces for the element in this scope. Every namespace is represented here as an object (i.e. in curly braces).
    - as said, every element in this list is an object that represent a namespace, with `"prefix":"URI"`. If you want to define the XML _default namespace_ (ie `xmlns`), you must set `"":"URI"`, so that `prefix` is an empty string. [**This section is optional**]
  - `"attributes":[...]`. This part will define a list of attributes for the element in this scope. Every attribute here is represented as an object (i.e. in curly braces). [**This section is optional**]
  - `"elements":[...]`. This part is the recursive part. Here will be defined the children elements of the actual element, in the way the previous section already described.[**This section is optional**]

## Benchmark
The tool is one of the fastest in its category. The tool successfully converted 1GB XML file into a JSON file in 1.20m. With these large files you should modify Java -Xms parameter to allow an higher consumption of RAM. For this particular test 8G of RAM (Java Heap size) has been set:

```bash
java -Xmx8G -jar XMLJSONConverter input-file.xml output-file.json
```


## Limitations
- mixed content is not supported:
  - XML elements cannot be mixed (`mixed=true` schemas not supported)
  - JSON (due to XML conversion) cannot have more than one plain `"property":value` field per object. But it can contain other elements as a list with other single field `"property":value`
- every json value is treated and forced to be a String, that because XML treats every value as a text-element and the tool is not informed about the XML schema. 
  - So when converting from XML to JSON, every JSON value will be converted as a String
  - JSON values in input are not required to be String in Json `element_name` value (non String value for value in `namespaces` and `attributes` are not valid)

