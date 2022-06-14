package ru.netology;

import com.google.gson.*;
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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

//      Task_1.5.1_CSV-JSON_parser
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileCSV = "data.csv";
        String csvToJson = "data.json";
        List<Employee> list = parseCSV(columnMapping, fileCSV);
        String json = listToJson(list);
        writeString(csvToJson, json);

//      Task_1.5.2_XML-JSON_parser
        String xmlToJson = "data2.json";
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(xmlToJson, json2);

//      Task_1.5.3_JSON parser
        String json3 = readString("data2.json");
        List<Employee> list3 = jsonToList(json3);
        for (Employee employee : list3) {
            System.out.println(employee);
        }
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employees = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            employees = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String fileName, String json) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String file) {

        List<Employee> list = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(file));

            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;

                    long id = Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());

                    list.add(new Employee(id, firstName, lastName, country, age));

                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String json) {
        JSONArray jsonArray = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(json));
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(bufferedReader);
                jsonArray = (JSONArray) obj;
            } catch (ParseException | ClassCastException e) {
                System.out.println("Ошибка приведения класса...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(jsonArray);
    }

    private static List<Employee> jsonToList(String json) {

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        Employee employee;
        List<Employee> list = new ArrayList<>();

        try {
            jsonArray = (JSONArray) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        for (int i = 0; i < Objects.requireNonNull(jsonArray).size(); i++) {
            employee = gson.fromJson(String.valueOf(jsonArray.get(i)), Employee.class);
            list.add(employee);
        }
        return list;
    }

}

