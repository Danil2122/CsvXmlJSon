import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException,
            ParseException {
        //CSV - JSON парсер
//        Первым делом в классе Main в методе main() создайте массив строчек columnMapping, содержащий информацию 
//        о предназначении колонок в CVS файле:
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
//        Далее определите имя для считываемого CSV файла:
        String fileName = "data.csv";
//        Далее получите список сотрудников, вызвав метод parseCSV():
        List<Employee> list = parseCSV(columnMapping, fileName);
//        Полученный список преобразуйте в строчку в формате JSON. Сделайте это с помощью метода listToJson(),
//        который вам так же предстоит реализовать самостоятельно.
        String json = listToJson(list);
        writeString(json, "data.json");

        //XML - JSON парсер
        List<Employee> listXml = parseXML("data.xml");
        String jsonXml = listToJson(listXml);
        writeString(jsonXml, "data2.json");

        //JSON парсер
        String jsonParse = readString("new_data.json");
        List<Employee> listJson = jsonToList(jsonParse);
        for (Employee employee : listJson){
            System.out.print(employee);
        }
    }

    private static List<Employee> jsonToList(String json) throws ParseException {
        List<Employee> employees = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(json); //получил массив

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        for (int i = 0; i < jsonArray.size(); i++) {
            String gg = jsonArray.get(i).toString();
            Employee employee = gson.fromJson(gg, Employee.class);
            employees.add(employee);
        }
        return employees;
    }

    private static String readString(String s) throws FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(s))) {
            String inputLine;
            // построчно считываем результат в объект StringBuilder
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
//                System.out.println(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    private static List<Employee> parseXML(String string) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = fac.newDocumentBuilder();
        Document doc = builder.parse(new File(string));

        Node root = doc.getDocumentElement();
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = root.getChildNodes();         //лист узлов
        for (int i = 0; i < nodeList.getLength(); i++) { //5 {id, firstname, lastname, country, age}
            Node node1 = nodeList.item(i);

            if (Node.ELEMENT_NODE == node1.getNodeType()) {
                Element element = (Element) node1;
                Employee employee = new Employee();

                employee.setId(Long.parseLong(getTagValue("id", element)));
                employee.setFirstName(getTagValue("firstName", element));
                employee.setLastName(getTagValue("lastName", element));
                employee.setCountry(getTagValue("country", element));
                employee.setAge(Integer.parseInt(getTagValue("age", element)));

                employees.add(employee);
            }
        }
        return employees;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public static void writeString(String json, String name) {

        try (FileWriter fileWriter = new FileWriter(name)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> String listToJson(List<T> list) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        return gson.toJson(list, listType);
    }
    //    private static String listToJson(List<Employee> list) {
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        return gson.toJson(list);
//    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class); //type the class
            strategy.setColumnMapping(columnMapping); //type the column
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();

        }
    }
}
//    public static void createCSV(){
//        List<String[]> colomnString = new ArrayList<>();
//        String[] columnString1 = "1,John,Smith,USA,25".split(",");
//        String[] columnString2 = "2,Ivan,Petrov,RU,23".split(",");
//        colomnString.add(columnString1);
//        colomnString.add(columnString2);
//
//        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv",true))){
//            writer.writeAll(colomnString);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

