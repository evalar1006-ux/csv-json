package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // ---- Задача 1: CSV -> JSON ----
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> listFromCsv = parseCSV(columnMapping, "data.csv");
        writeString(listToJson(listFromCsv), "data.json");

        // ---- Задача 2: XML -> JSON ----
        List<Employee> listFromXml = parseXML("data.xml");
        writeString(listToJson(listFromXml), "data2.json");

        System.out.println("Готово! Файлы data.json и data2.json созданы.");
    }

    // ---------- CSV ----------
    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(0)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения CSV-файла: " + fileName, e);
        }
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));

            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element element = (Element) node;

                long id = Long.parseLong(getTagValue("id", element));
                String firstName = getTagValue("firstName", element);
                String lastName = getTagValue("lastName", element);
                String country = getTagValue("country", element);
                int age = Integer.parseInt(getTagValue("age", element));

                employees.add(new Employee(id, firstName, lastName, country, age));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга XML-файла: " + fileName, e);
        }
        return employees;
    }

    private static String getTagValue(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();
    }

    private static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи в файл: " + fileName, e);
        }
    }
}