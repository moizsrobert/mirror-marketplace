package com.mirrors.mirrorsbackend.exception;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExceptionLogger {
    private static final String PATH = "src/main/resources/static/error/";
    private static final LocalDate DATE = LocalDate.now();

    private ExceptionLogger() {

    }

    public static <E extends RuntimeException> void logException(E exception) {
        writeToXML(exception);
    }

    private static <E extends RuntimeException> void writeToXML(E exception) {
        try {
            File XMLPath = new File(PATH + DATE + ".xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document;
            Element root;
            if (XMLPath.exists()) {
                document = db.parse(XMLPath);
                root = document.getDocumentElement();
            } else {
                document = db.newDocument();
                root = document.createElement("exceptions");
                document.appendChild(root);
            }

            Element exceptionElement = document.createElement("exception");
            exceptionElement.setAttribute("class", exception.getClass().getSimpleName());
            root.appendChild(exceptionElement);

            Element userElement = document.createElement("user");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("USER"));
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(r ->r.getAuthority().equals("ADMIN"));
            if (isUser || isAdmin)
                userElement.setTextContent(authentication.getName());
            else
                userElement.setTextContent("anonymous");
            exceptionElement.appendChild(userElement);

            Element timestampElement = document.createElement("timestamp");
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
            timestampElement.setTextContent(LocalTime.now().format(dateTimeFormat));
            exceptionElement.appendChild(timestampElement);

            for (Field field : exception.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()))
                    continue;
                field.setAccessible(true);
                Element fieldElement = document.createElement(field.getName());
                fieldElement.setAttribute("class", field.getType().getSimpleName());
                fieldElement.setTextContent(String.valueOf(field.get(exception)));
                exceptionElement.appendChild(fieldElement);
            }

            Element ipElement = document.createElement("ip");
            String ip;
            if (authentication.getDetails() instanceof WebAuthenticationDetails)
                ip = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            else
                ip = "unknown";
            ipElement.setTextContent(ip);
            exceptionElement.appendChild(ipElement);

            Element descriptionElement = document.createElement("description");
            descriptionElement.setTextContent(exception.getMessage());
            exceptionElement.appendChild(descriptionElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileWriter(XMLPath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException | IOException | SAXException | TransformerException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
