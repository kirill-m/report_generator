package page_elements;

/**
 * Created by kirill
 */
public class Column {
    private String title;
    private int width;

    public Column(String title, int width) {
        this.title = title;
        this.width = width;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }
}
