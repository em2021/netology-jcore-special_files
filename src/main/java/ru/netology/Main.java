package ru.netology;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        list = parseXML("data.xml");
        json = listToJson(list);
        writeString(json, "data2.json");
        String newJSON = readString("new_data.json");
        list = jsonToList(newJSON);
        for (Employee e : list) {
            System.out.println(e.toString());
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> employeeList = new ArrayList<Employee>();
        try (FileReader fr = new FileReader(fileName);
             CSVReader csvreader = new CSVReader(fr)) {
            ColumnPositionMappingStrategy cpms = new ColumnPositionMappingStrategy();
            cpms.setType(Employee.class);
            cpms.setColumnMapping(columnMapping);
            CsvToBeanBuilder csvtbBuilder = new CsvToBeanBuilder(csvreader);
            CsvToBean csvtb = csvtbBuilder
                    .withMappingStrategy(cpms)
                    .build();
            employeeList = csvtb.parse();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String outputFileName) {
        try (FileWriter writer = new FileWriter(outputFileName)) {
            writer.write(json);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        List<Employee> employeeObjectList = new ArrayList<>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(new File(fileName));
            Node root = xmlDoc.getDocumentElement();
            NodeList nlist = root.getChildNodes();
            for (int i = 0; i < nlist.getLength(); i++) {
                if (Node.ELEMENT_NODE == nlist.item(i).getNodeType()) {
                    Element employee = (Element) nlist.item(i);
                    if (employee.getTagName().equals("employee")) {
                        String[] employeeFields = {"id", "firstName", "lastName", "country", "age"};
                        String[] employeeValues = new String[5];
                        NodeList employeeNodes = employee.getChildNodes();
                        for (int j = 0; j < employeeFields.length; j++) {
                            for (int k = 0; k < employeeNodes.getLength(); k++) {
                                if (Node.ELEMENT_NODE == employeeNodes.item(k).getNodeType()) {
                                    Element ev = (Element) employeeNodes.item(k);
                                    if (ev.getTagName().equals(employeeFields[j])) {
                                        employeeValues[j] = ev.getTextContent();
                                    }
                                }
                            }
                        }
                        employeeObjectList.add(new Employee(Long.parseLong(employeeValues[0]),
                                employeeValues[1],
                                employeeValues[2],
                                employeeValues[3],
                                Integer.parseInt(employeeValues[4])));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println(ex.getMessage());
        }
        return employeeObjectList;
    }

    public static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = new JSONArray();
            jsonArray = (JSONArray) parser.parse(json);
            Gson gson = new GsonBuilder().create();
            for (Object o : jsonArray) {
                employeeList.add(gson.fromJson(o.toString(), Employee.class));
            }
        } catch (ParseException | IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        return employeeList;
    }
}