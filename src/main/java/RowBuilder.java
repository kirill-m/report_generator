import com.google.common.base.Splitter;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.*;
import java.util.*;

/**
 * Created by kirill
 */
public class RowBuilder {

    File file;
    SettingsParser settingsParser;
    List<String[]> parcedSrcFile = new LinkedList<>();
    ArrayList<String[]> rowTitles;
    String rowDivider;

    RowBuilder(String path, SettingsParser parser) {
        file = new File(path);
        settingsParser = parser;
    }

    public ArrayList<String[]> build() {
        readSrcFile();
        rowTitles = generateRow(settingsParser.getTitlesRow());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < settingsParser.getPageWidth(); i++)
            sb.append(PageBuilderConsts.ROW_DIVIDER);
        rowDivider = sb.toString();

        ArrayList<String[]> result = generateRow(new String[]{"5", "Юлианна-Оксана Сухово-Кобылина", "29/11/2009", });
        transformRow(result);
        return result;
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

    private ArrayList<String[]> generateRow(String[] inputRow) {
        ArrayList<String[]> result = new ArrayList<>();
        int rowsNumber = 1;
        ArrayList<String> generatedRow = new ArrayList<>();
        Collections.addAll(generatedRow, inputRow);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < generatedRow.size(); i++) {
            int colWidth = settingsParser.getColumns().get(i).getWidth();
            String rowItem = generatedRow.get(i);
//            sb.append(PageBuilderConsts.COLUMN_DIVIDER);
//            sb.append(PageBuilderConsts.SPACE);
            if (rowItem.length() <= colWidth) {
                String[] splittedStr = new String[]{rowItem};
                result.add(i, splittedStr);
//                sb.append(rowItem);
//                sb.append(PageBuilderConsts.SPACE);
//                for (int j = 0; j < (colWidth - rowItem.length()); j++)
//                    sb.append(PageBuilderConsts.SPACE);
            } else {
                String[] splittedStr = fitToWidth(rowItem, colWidth);
                if (splittedStr.length > rowsNumber)
                    rowsNumber = splittedStr.length;
                result.add(i, splittedStr);
                //sb.append(strToAdd);
                //sb.append(PageBuilderConsts.SPACE);
//                for (int j = 0; j < (colWidth - strToAdd.length()); j++)
//                    sb.append(PageBuilderConsts.SPACE);
                //System.out.println("strToAdd " + strToAdd.length());
                //sb.append(PageBuilderConsts.SPACE);
            }

        }
        //sb.append(PageBuilderConsts.COLUMN_DIVIDER);

        return result;
    }

    private String[] fitToWidth(String str, int width) {
        ArrayList<String> splitList = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(str, " -/", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            splitList.add(token);
        }

        String tmp;
        for (int i = 0; i < splitList.size() - 1;) {
            if(splitList.get(i).length() <= width) {
                tmp = splitList.get(i);
                if (" ".equals(tmp))
                    tmp = "";
                i++;
                while (tmp.length() + splitList.get(i).length() <= width) {
                    tmp += splitList.get(i);
                    i++;
                    if (i >= splitList.size())
                        break;
                }
                result.add(tmp);
                if (i == splitList.size() - 1) {
                    for(final String token : Splitter
                            .fixedLength(width)
                            .split(splitList.get(i))) {
                        i++;
                        result.add(token);
                    }
                }
                    //result.add(splitList.get(i));

            } else {
                for(final String token : Splitter
                        .fixedLength(width)
                        .split(splitList.get(i))) {
                    i++;
                    result.add(token);
                }
                System.out.println("Larger!");
            }
        }

        System.out.println();
        for (String item : result)
            System.out.println(item);

        return result.toArray(new String[result.size()]);
    }

    private ArrayList<String[]> transformRow(ArrayList<String[]> row) {
        int max = 1;
        int colWidth = 0;

        for (String[] item : row) {
            if (item.length > max) {
                max = item.length;
            }
        }

        ArrayList<String[]> result = new ArrayList<>();
        for (int i = 0; i < row.size(); i++)
            result.add(i, new String[max]);

        System.out.println();
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < row.size(); j++) {
                colWidth = settingsParser.getColumns().get(j).getWidth();

                if (row.get(j).length == max) {
                    if (i == max - 1) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(row.get(j)[i]);
                        for (int k = 0; k < colWidth - row.get(j)[i].length(); k++)
                            sb.append(PageBuilderConsts.SPACE);
                        result.get(j)[i] = sb.toString();
                    } else
                        result.get(j)[i] = row.get(j)[i];
                    //System.out.print(" " + row.get(j)[i] + " ");
                } else if (i < row.get(j).length) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(row.get(j)[i]);
                    for (int k = 0; k < colWidth - row.get(j)[i].length(); k++)
                        sb.append(PageBuilderConsts.SPACE);
                    result.get(j)[i] = sb.toString();
                    //System.out.print(sb.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0; k < colWidth; k++)
                        sb.append(PageBuilderConsts.SPACE);
                    result.get(j)[i] = sb.toString();
                    //System.out.print(sb.toString());
                }
            }
            //System.out.println();
        }
        decorateRow(result, max);
        return result;
    }

    private ArrayList<String> decorateRow(ArrayList<String[]> row, int max) {
        System.out.println("L000L");
        int maxLen = max;

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < row.size(); j++) {
                sb.append(PageBuilderConsts.COLUMN_DIVIDER)
                        .append(PageBuilderConsts.SPACE)
                        .append(row.get(j)[i])
                        .append(PageBuilderConsts.SPACE);
                if (j == row.size() - 1)
                    sb.append(PageBuilderConsts.ROW_DIVIDER)
                            .append(PageBuilderConsts.NEXT_LINE);
            }
            result.add(sb.toString());
        }

        for (String str : result)
            System.out.print(str);
        return result;
    }
}
