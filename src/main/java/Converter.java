import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class Converter {
    private static Element person;
    private static Element family;
    private static Boolean isFamily;

    private static final String ADDRESS_TAG = "address";
    private static final String BORN_TAG = "born";
    private static final String CITY_TAG = "city";
    private static final String FAMILY_TAG = "family";
    private static final String FIRSTNAME_TAG = "firstname";
    private static final String LANDLINE_TAG = "landline";
    private static final String LASTNAME_TAG = "lastname";
    private static final String MOBILE_TAG = "mobile";
    private static final String NAME_TAG = "name";
    private static final String PEOPLE_TAG = "people";
    private static final String PERSON_TAG = "person";
    private static final String PHONE_TAG = "phone";
    private static final String STREET_TAG = "street";
    private static final String ZIPCODE_TAG = "zipcode";

    private static final String OUTPUT_FILENAME = "output.xml";

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("You need to provide the path to the file you want to convert.");
        } else {
            BufferedReader reader;
            String input_path = args[0];

            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement(PEOPLE_TAG);
                doc.appendChild(rootElement);

                // read input file
                reader = new BufferedReader(new FileReader(input_path));

                // read first line
                String line = reader.readLine();

                //process all lines from input file
                while (line != null) {
                    System.out.println("Processing line: " + line);

                    processLine(doc, rootElement, line);

                    //read next line
                    line = reader.readLine();
                }

                reader.close();

                // write dom document to file
                FileOutputStream output = new FileOutputStream(OUTPUT_FILENAME);
                writeXml(doc, output);
                System.out.println("File successfully converted to output.xml");

            } catch (TransformerException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeXml(Document doc, OutputStream outputStream) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // make the XML pretty
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outputStream);

        transformer.transform(source, result);
    }

    private static void processLine(Document doc, Element rootElement, String line) {
        String[] parts = line.split("\\|");

        if (parts[0].equalsIgnoreCase("P")) {
            isFamily = false;

            person = doc.createElement(PERSON_TAG);
            rootElement.appendChild(person);

            Element firstName = doc.createElement(FIRSTNAME_TAG);
            firstName.setTextContent(parts[1]);
            person.appendChild(firstName);

            Element lastName = doc.createElement(LASTNAME_TAG);
            lastName.setTextContent(parts[2]);
            person.appendChild(lastName);

        } else if (parts[0].equalsIgnoreCase("T")) {
            Element phone = doc.createElement(PHONE_TAG);

            Element mobile = doc.createElement(MOBILE_TAG);
            mobile.setTextContent(parts[1]);
            phone.appendChild(mobile);

            Element landline = doc.createElement(LANDLINE_TAG);
            landline.setTextContent(parts[2]);
            phone.appendChild(landline);

            if (isFamily) {
                family.appendChild(phone);
                person.appendChild(family);
            } else {
                person.appendChild(phone);
            }

        } else if (parts[0].equalsIgnoreCase("A")) {
            Element address = doc.createElement(ADDRESS_TAG);

            Element street = doc.createElement(STREET_TAG);
            street.setTextContent(parts[1]);
            address.appendChild(street);

            Element city = doc.createElement(CITY_TAG);
            city.setTextContent(parts[2]);
            address.appendChild(city);

            // if zip code is present
            if (parts.length == 4) {
                Element zipcode = doc.createElement(ZIPCODE_TAG);
                zipcode.setTextContent(parts[3]);
                address.appendChild(zipcode);
            }

            if (isFamily) {
                family.appendChild(address);
                person.appendChild(family);
            } else {
                person.appendChild(address);
            }

        } else if (parts[0].equalsIgnoreCase("F")) {
            isFamily = true;

            family = doc.createElement(FAMILY_TAG);

            Element name = doc.createElement(NAME_TAG);
            name.setTextContent(parts[1]);
            family.appendChild(name);

            Element born = doc.createElement(BORN_TAG);
            born.setTextContent(parts[2]);
            family.appendChild(born);

            person.appendChild(family);
        }
    }
}
