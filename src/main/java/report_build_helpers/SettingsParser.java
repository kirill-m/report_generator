package report_build_helpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import page_elements.Column;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by kirill
 */
public class SettingsParser {

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    File file;
    DocumentBuilder db;
    Document doc;

    private int pageWidth, pageHeight;
    List<Column> columns = new LinkedList<>();

    public SettingsParser(String path) {
        file = new File(path);
    }

    public void init() throws IOException, SAXException, ParserConfigurationException {
        db = dbf.newDocumentBuilder();
        if (file.exists()) {
            doc = db.parse(file);
            doc.getDocumentElement().normalize();
            getPageSize();
            getColumnsInfo();
        } else {
            System.out.println("Settings file not found. Exit.");
            System.out.println(file.getAbsolutePath());
            System.exit(1);
        }
    }

    private void getPageSize() {
        NodeList nodeList = doc.getElementsByTagName("page");
        Node page = nodeList.item(0);
        if (page.getNodeType() == Node.ELEMENT_NODE) {
            NodeList pageNode = page.getChildNodes();
            Element pageEl = (Element) pageNode;
            NodeList widthNode = pageEl.getElementsByTagName("width");
            Element widthEl = (Element) widthNode.item(0);
            NodeList width = widthEl.getChildNodes();
            this.pageWidth = Integer.parseInt((width.item(0)).getNodeValue());

            NodeList heightNode = pageEl.getElementsByTagName("height");
            Element heightEl = (Element) heightNode.item(0);
            NodeList height = heightEl.getChildNodes();
            this.pageHeight = Integer.parseInt((height.item(0)).getNodeValue());
        }
    }

    private void getColumnsInfo() {
        NodeList columnsNodeList = doc.getElementsByTagName("columns");
        NodeList columnList = columnsNodeList.item(0).getChildNodes();
        Element columnsEl = (Element) columnList;
        NodeList columns = columnsEl.getElementsByTagName("column");

        for (int i = 0; i < columns.getLength(); i++) {
            Node column = columns.item(i);

            if (column.getNodeType() == Node.ELEMENT_NODE) {
                Element col = (Element) column;
                NodeList titleNode = col.getElementsByTagName("title");
                Element titleEl = (Element)titleNode.item(0);
                NodeList titleTmp = titleEl.getChildNodes();

                NodeList widthNode = col.getElementsByTagName("width");
                Element widthEl = (Element)widthNode.item(0);
                NodeList widthTmp = widthEl.getChildNodes();

                String title = titleTmp.item(0).getNodeValue();
                Integer width = Integer.parseInt(widthTmp.item(0).getNodeValue());
                this.columns.add(new Column(title, width));
            }
        }
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String[] getTitlesRow() {
        String[] row = new String[columns.size()];
        for (int i = 0; i < row.length; i++)
            row[i] = columns.get(i).getTitle();

        return row;
    }
}

