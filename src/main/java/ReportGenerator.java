import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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

        SettingsParser settingsParser = new SettingsParser(settingsPath);
        settingsParser.init();

        PageBuilder builder = new PageBuilder(sourcePath, settingsParser);
        System.out.println(builder.build());

        try {
            commandLine = parser.parse(options, args);

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Incorrect args. Exit.");
        }

    }
}
