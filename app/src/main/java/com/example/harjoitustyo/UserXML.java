package com.example.harjoitustyo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class UserXML {

    public UserXML(String userId, String userName, String userEmail, String userUsername, String userPassword)
            throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("user");
        doc.appendChild(rootElement);

        Element info = doc.createElement("info");
        info.setTextContent(userId);
        rootElement.appendChild(info);


        Element name = doc.createElement("name");
        name.setTextContent(userName);
        rootElement.appendChild(name);


        Element email = doc.createElement("email");
        email.setTextContent(userEmail);
        rootElement.appendChild(email);


        Element username = doc.createElement("username");
        username.setTextContent(userUsername);
        rootElement.appendChild(username);


        Element password = doc.createElement("password");
        password.setTextContent(userPassword);
        rootElement.appendChild(password);


        try (FileOutputStream output =
                     new FileOutputStream("c:\\test\\staff-dom.xml")) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeXml(Document doc, OutputStream output)throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);
    }

}
