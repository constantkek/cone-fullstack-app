package ee.cone;

import java.util.ArrayList;

public class FileData {
    private ArrayList<ArrayList<String>> fileData;

    FileData(ArrayList<ArrayList<String>> fileData) {
        this.fileData = fileData;
    }

    //Getters
    public ArrayList<ArrayList<String>> getFileData() {
        return fileData;
    }

    public void setFileData(ArrayList<ArrayList<String>> fileData) {
        this.fileData = fileData;
    }
}
