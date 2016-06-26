import com.google.common.base.Splitter;

import java.util.*;

/**
 * Created by kirill
 */
public class RowBuilder {

    SettingsParser settingsParser;
    String[] str;

    RowBuilder(String[] str, SettingsParser parser) {
        this.str = str;
        settingsParser = parser;
    }

    public Row build() {
        return transformRow(generateRow(str));
    }



    private ArrayList<String[]> generateRow(String[] inputRow) {
        ArrayList<String[]> result = new ArrayList<>();
        int rowsNumber = 1;
        ArrayList<String> generatedRow = new ArrayList<>();
        Collections.addAll(generatedRow, inputRow);

        for (int i = 0; i < generatedRow.size(); i++) {
            int colWidth = settingsParser.getColumns().get(i).getWidth();
            String rowItem = generatedRow.get(i);
            if (rowItem.length() <= colWidth) {
                String[] splittedStr = new String[]{rowItem};
                result.add(i, splittedStr);
            } else {
                String[] splittedStr = fitToWidth(rowItem, colWidth);
                if (splittedStr.length > rowsNumber)
                    rowsNumber = splittedStr.length;
                result.add(i, splittedStr);
            }

        }

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
            } else {
                for(final String token : Splitter
                        .fixedLength(width)
                        .split(splitList.get(i))) {
                    i++;
                    result.add(token);
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private Row transformRow(ArrayList<String[]> row) {
        int max = 1;
        int colWidth;

        for (String[] item : row) {
            if (item.length > max) {
                max = item.length;
            }
        }

        ArrayList<String[]> result = new ArrayList<>();
        for (int i = 0; i < row.size(); i++)
            result.add(i, new String[max]);

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
                } else if (i < row.get(j).length) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(row.get(j)[i]);
                    for (int k = 0; k < colWidth - row.get(j)[i].length(); k++)
                        sb.append(PageBuilderConsts.SPACE);
                    result.get(j)[i] = sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int k = 0; k < colWidth; k++)
                        sb.append(PageBuilderConsts.SPACE);
                    result.get(j)[i] = sb.toString();
                }
            }
        }


        return decorateRow(result, max);
    }

    private Row decorateRow(ArrayList<String[]> row, int max) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max; i++) {

            for (int j = 0; j < row.size(); j++) {
                sb.append(PageBuilderConsts.COLUMN_DIVIDER)
                        .append(PageBuilderConsts.SPACE)
                        .append(row.get(j)[i])
                        .append(PageBuilderConsts.SPACE);
                if (j == row.size() - 1)
                    sb.append(PageBuilderConsts.COLUMN_DIVIDER)
                            .append(PageBuilderConsts.NEXT_LINE);
            }
        }
        return new Row(sb.toString(), max);
    }
}
