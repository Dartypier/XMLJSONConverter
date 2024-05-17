# XMLJSONConverter
This tool allows a seamless conversion from XML/JSON to JSON/XML without the need of XSD (XML Schema). 
- conversion from XML to JSON is always doable
- conversion from JSON to XML is doable, but JSON file must be organized in a defined structured way (readable by the tool, see below)

<p align="center"><img src="demo.gif" alt="demo gif" /></p>

## Usage
To convert specify the input file (`xml`, `.railml`, `.json`) and then specify the output file.

Example:
```bash
java -jar XMLJSONConverter input-file.xml output-file.json
```

## Limitations
- mixed content is not supported:
  - XML elements cannot be mixed (`mixed=true` schemas not supported)
  - JSON (due to XML conversion) cannot have more than one plain `"property":value` field per object. But it can contain other elements as a list with other single field `"property":value`
- every json value is treated and forced to be a String, that because XML treats every value as a text-element and the tool is not informed about the XML schema. So when converting from XML to JSON, every JSON value will be a String

