package com.revature.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLReader {

    private static XMLReader reader = new XMLReader();
    private static Database database;

    private XMLReader() {
        //databaseSet = new HashSet<>();

        File inputFile = new File("src/main/resources/aestivate.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;

        try {
            builder = dbFactory.newDocumentBuilder();
            doc = builder.parse(inputFile);
            NodeList databaseNodes = doc.getElementsByTagName("Database");
            database = XMLReader.parseXML(databaseNodes);
        } catch (ParserConfigurationException e) {
            System.out.println("Error in creating the builder");
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("SAX exception found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("aestivate.xml file not found in resources");
            e.printStackTrace();
        } catch (XMLParseException e) {
            System.out.println("Missing elements or attributes from database configuration in XML file");
            e.printStackTrace();
        }
    }

    private static Database parseXML(NodeList databaseNodes) throws XMLParseException {
        if (databaseNodes.getLength() == 0) throw new XMLParseException("No Database tags found");

        for (int i = 0; i < databaseNodes.getLength(); i++) {
            Database db = new Database();
            Node node = databaseNodes.item(i);

            // We made a NodeList of Database elements, so any item in the list will be an element
            Element database = (Element) node;
            db.setDatabase(database.getAttribute("name"));

            // The configuration should have one Url element, with an attribute url set to a string
            // To get there, get the Url element from database element, which there should be exactly one of,
            // cast as element, then get attribute for url, which is a string of the url.
            // This is the same for login name and password

            Element url = (Element) database.getElementsByTagName("Url").item(0);
            db.setUrl(url.getAttribute("url"));

            Element login = (Element) database.getElementsByTagName("Login").item(0);
            db.setLoginName(login.getAttribute("login"));

            Element password = (Element) database.getElementsByTagName("Password").item(0);
            db.setPassword(password.getAttribute("password"));

            // For minidle, maxidle, and max open, need to convert string to int
            Element minIdle = (Element) database.getElementsByTagName("MinIdle").item(0);
            db.setMinIdle(Integer.parseInt(minIdle.getAttribute("minIdle")));

            Element maxIdle = (Element) database.getElementsByTagName("MaxIdle").item(0);
            db.setMaxIdle(Integer.parseInt(maxIdle.getAttribute("maxIdle")));

            Element maxOpen = (Element) database.getElementsByTagName("maxOpenPreparedStatements").item(0);
            db.setMaxOpenPreparedStatements(Integer.parseInt(maxOpen.getAttribute("maxOpen")));

            if (Database.validate(db)) {
                throw new XMLParseException("Missing one or more elements or attributes from Database configuration" +
                        " in the XML file");
            }
            //System.out.println(db.toString());
            return db;
        }
        return null;
    }

    public static XMLReader getInstance() {
        return reader;
    }

    public static Database getDatabaseSet() {
        return database;
    }
}
