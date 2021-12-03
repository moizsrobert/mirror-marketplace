package com.mirrors.mirrorsbackend.exception;

import com.mirrors.mirrorsbackend.model.marketplace_user.MarketplaceUserRole;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MarketplaceExceptionLogger {
    private static final MarketplaceExceptionLogger instance = new MarketplaceExceptionLogger();

    private DocumentBuilder documentBuilder;
    private Transformer transformer;
    private final String PATH = "src/main/resources/static/error/";

    private MarketplaceExceptionLogger() {
        initWriter();
    }

    public static MarketplaceExceptionLogger getInstance() {
        return instance;
    }

    public <E extends MarketplaceException> void logException(E exception) {
        LocalDate date = LocalDate.now();
        try {
            File XMLPath = new File(PATH + date + ".xml");
            Document document;
            Element root;
            if (XMLPath.exists()) {
                document = documentBuilder.parse(XMLPath);
                root = document.getDocumentElement();
            } else {
                document = documentBuilder.newDocument();
                root = document.createElement("exceptions");
                document.appendChild(root);
            }

            Element exceptionElement = document.createElement("exception");
            exceptionElement.setAttribute("class", exception.getClass().getSimpleName());
            root.appendChild(exceptionElement);

            Element userElement = document.createElement("user");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isUser = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(MarketplaceUserRole.USER.name()));
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(MarketplaceUserRole.ADMIN.name()));
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
                if (!field.isAnnotationPresent(XMLPresence.class))
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

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new FileWriter(XMLPath));
            transformer.transform(source, result);

        } catch (IOException | SAXException | TransformerException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initWriter() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            documentBuilder = dbf.newDocumentBuilder();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
