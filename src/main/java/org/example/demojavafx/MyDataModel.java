package org.example.demojavafx;

public class MyDataModel {
    private String fileName;
    private String title;

    public MyDataModel(String fileName, String title) {
        this.fileName = fileName;
        this.title = title;
    }


    public String getFileName() {
        return fileName;
    }


    public String getTitle() {
        return title;
    }
}
