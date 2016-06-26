import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kirill
 */
public class ReportBuilder {

    RowBuilder rowBuilder;
    ArrayList<String> titleRow = new ArrayList<>();
    String rowDivider;
    SettingsParser settingsParser;
    File file;
    List<String[]> parcedSrcFile = new LinkedList<>();

    ReportBuilder(String path, SettingsParser settingsParser) {
        file = new File(path);
        this.settingsParser = settingsParser;
    }

    public ArrayList<ArrayList<String>> build() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        generateRowDivider();
        generateTitleRow();
        readSrcFile();

        for (String item : titleRow)
            System.out.print(item);
        System.out.print(rowDivider);

        ArrayList<String> divider = new ArrayList<String>();
        divider.add(rowDivider);
        for (String[] item : parcedSrcFile) {
            result.add(new RowBuilder(item, settingsParser).build());
            result.add(divider);
        }

        return result;
    }

    private void generateRowDivider() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < settingsParser.getPageWidth(); i++)
            sb.append(PageBuilderConsts.ROW_DIVIDER);
        sb.append(PageBuilderConsts.NEXT_LINE);
        rowDivider = sb.toString();
    }

    private void generateTitleRow() {
        titleRow = new RowBuilder(settingsParser.getTitlesRow(),settingsParser).build();
    }

    private void readSrcFile() {
        TsvParserSettings settings = new TsvParserSettings();
        TsvParser tsvParser = new TsvParser(settings);

        try {
            parcedSrcFile = tsvParser.parseAll(new InputStreamReader(new FileInputStream(file), "UTF-16"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
