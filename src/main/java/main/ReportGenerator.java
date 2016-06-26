package main;

import org.apache.commons.cli.*;
import org.xml.sax.SAXException;
import page_elements.Row;
import report_build_helpers.ReportBuilder;
import report_build_helpers.SettingsParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kirill
 */
public class ReportGenerator {

    private static Options options = new Options();
    static final String DIRECTORY = System.getProperty("user.dir");

    static final String DEFAULT_SETTINGS_PATH = DIRECTORY + "/settings.xml";
    static final String DEFAULT_SOURCE_PATH = DIRECTORY + "/source-data.tsv";
    static final String DEFAULT_REPORT_PATH = DIRECTORY + "/report.txt";

    static {
        options.addOption(new Option("s", "settings",true, "Settings file path"));
        options.addOption(new Option("sr",  "source", true, "Source data file path"));
        options.addOption(new Option("r", "report", true, "Report file path"));
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        System.out.println("Working directory: " + DIRECTORY);
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        String settingsPath;
        String sourcePath;
        String reportPath;

        try {
            commandLine = parser.parse(options, args);
            settingsPath = commandLine.getOptionValue("s", DEFAULT_SETTINGS_PATH);
            if (!settingsPath.startsWith("/"))
                settingsPath = DIRECTORY + '/' + settingsPath;
            sourcePath = commandLine.getOptionValue("sr", DEFAULT_SOURCE_PATH);
            if (!sourcePath.startsWith("/"))
                sourcePath = DIRECTORY + '/' + sourcePath;
            reportPath = commandLine.getOptionValue("r", DEFAULT_REPORT_PATH);
            if (!reportPath.startsWith("/"))
                reportPath = DIRECTORY + '/' + reportPath;

            SettingsParser settingsParser = new SettingsParser(settingsPath);
            settingsParser.init();

            ReportBuilder reportBuilder = new ReportBuilder(sourcePath, settingsParser);
            ArrayList<Row> report = reportBuilder.build();
            reportBuilder.writeToFile(reportPath, report);

        } catch (ParseException e) {
            System.out.println("Incorrect args. Exit. " + e);
        }
    }
}
