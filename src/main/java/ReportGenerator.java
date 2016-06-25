import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill
 */
public class ReportGenerator {

    private static Options options = new Options();
    //static String directory = System.getProperty("user.dir");
    static String directory = "/Users/kirill/Desktop/task/";
    static List<String[]> list = new ArrayList<>();

    static {
        options.addOption(new Option("s", "settings",true, "Settings file path"));
        options.addOption(new Option("sr",  "source", true, "Source data file path"));
        options.addOption(new Option("r", "report", true, "Report file path"));
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        String settingsPath = directory + "settings.xml";
        String sourcePath = directory + "source-data.tsv";
        String reportPath = directory + "report.txt";

        TsvParserSettings settings = new TsvParserSettings();
        TsvParser tsvParser = new TsvParser(settings);

        try {
            list = tsvParser.parseAll(new InputStreamReader(new FileInputStream(sourcePath), "UTF-16"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (String[] item : list) {
            for (String str : item) {
                System.out.print(str);
                System.out.print(" ");
            }
            System.out.println();
        }

        XmlParser xmlParser = new XmlParser(settingsPath);
        xmlParser.init();




        try {
            commandLine = parser.parse(options, args);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Incorrect args. Exit.");
        }

    }
}
