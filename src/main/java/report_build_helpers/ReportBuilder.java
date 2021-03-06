package report_build_helpers;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import page_elements.Row;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirill
 */
public class ReportBuilder {

    Row titleRow;
    Row rowDivider;
    SettingsParser settingsParser;
    File file;
    List<String[]> parcedSrcFile = new LinkedList<>();

    public ReportBuilder(String path, SettingsParser settingsParser) {
        file = new File(path);
        this.settingsParser = settingsParser;
    }

    public ArrayList<Row> build() {

        generateRowDivider();
        generateTitleRow();
        readSrcFile();

        return splitReportByPages();
    }

    public void writeToFile(String path, ArrayList<Row> report) {
        File file = new File(path);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    fos, "UTF-16"));
            for (Row row : report)
                out.append(row.getRow());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.out.println("Error while closing stream: " + e);
            }
        }
        System.out.println("Report done (" + path + ")");
    }

    private void generateRowDivider() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < settingsParser.getPageWidth(); i++)
            sb.append(PageBuilderConsts.ROW_DIVIDER);
        sb.append(PageBuilderConsts.NEXT_LINE);
        rowDivider = new Row(sb.toString(), 1);
    }

    private void generateTitleRow() {
        titleRow = new RowBuilder(settingsParser.getTitlesRow(),settingsParser).build();
    }

    private void readSrcFile() {
        TsvParserSettings settings = new TsvParserSettings();
        TsvParser tsvParser = new TsvParser(settings);

        try {
            parcedSrcFile = tsvParser.parseAll(new InputStreamReader(new FileInputStream(file), "UTF-16"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Source data file not found " + e);
            System.exit(1);
        }
    }

    private ArrayList<Row> splitReportByPages() {
        ArrayList<Row> result = new ArrayList<>();
        int pageHeight = settingsParser.getPageHeight();
        for (String[] item : parcedSrcFile) {
            result.add(new RowBuilder(item, settingsParser).build());
        }
        ArrayList<Row> finalResult = new ArrayList<>();
        finalResult.add(titleRow);
        finalResult.add(rowDivider);
        finalResult.add(result.get(0));
        for (int i = 1; i < result.size(); i++) {
            if (getCurrentPageHeight(finalResult) + rowDivider.getHeight() + result.get(i).getHeight() < pageHeight) {
                finalResult.add(rowDivider);
                finalResult.add(result.get(i));
            } else {
                finalResult.add(new Row(PageBuilderConsts.PAGES_SEPARATOR, 0));
                finalResult.add(titleRow);
                finalResult.add(rowDivider);
                finalResult.add(result.get(i));
            }
        }

        return finalResult;
    }

    private int getCurrentPageHeight(ArrayList<Row> rows) {
        int height = 0;
        for (Row row : rows) {
            height += row.getHeight();
            if (PageBuilderConsts.PAGES_SEPARATOR.equals(row.getRow())) {
                height = 0;
            }
        }
        return height;
    }
}
