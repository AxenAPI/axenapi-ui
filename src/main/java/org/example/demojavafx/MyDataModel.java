package org.example.demojavafx;

public class MyDataModel {
    private String title;
    private String absolutePath;
    private boolean selected;

    public MyDataModel(String fileName, String title, String absolutePath) {
        this.title = title;
        this.absolutePath = absolutePath;
    }

    public String getTitle() {
        return title;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
