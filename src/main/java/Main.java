//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import javax.json.stream.JsonParser;
//import java.io.StringReader;
//import java.math.BigDecimal;
//
//public class Main {
//    public static void main(String[] args) {
//        JsonObject json = Json.createObjectBuilder()
//                .add("name", "Falco")
//                .add("age", BigDecimal.valueOf(3))
//                .add("biteable", Boolean.FALSE).build();
//
//        String result = json.toString();
//        System.out.println(result);
//
//        JsonReader jsonReader = Json.createReader(new StringReader("{\"name\":\"Falco\",\"age\":3,\"bitable\":false}"));
//        JsonObject jobj = jsonReader.readObject();
//        System.out.println(jobj);
//
////        JsonParser parser = Json.
//    }
//}
