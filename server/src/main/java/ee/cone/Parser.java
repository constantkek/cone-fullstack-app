package ee.cone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ee.cone.view.View;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final Gson gson;
    // String is a name of json file, DataCollection contains it's data
    private Map<String, FileData> data;

    public void setData(Map<String, FileData> data) {
        this.data = data;
    }

    public Parser() {
        this.data = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.parseData("c4dep.main.json");
    }

    public Map<String, FileData> parseData(String path) {
        this.parse(path);
        this.addIncludeData(path);
        return this.data;
    }

    public void parse(String path) {
        try {
            FileReader fr = new FileReader(new File(path));
            ArrayList<ArrayList<String>> parsedData = gson.fromJson(fr, ArrayList.class);
            ArrayList<ArrayList<String>> noCommentsData = new ArrayList<>();
            try {
                parsedData.forEach((line) -> {
                    if (line.getClass() == ArrayList.class) {
                        noCommentsData.add(line);
                    }
                });
            } catch (Exception ignored) { }
            FileData fileData = new FileData(noCommentsData);
            this.data.put(path, fileData);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addIncludeData(String path) {
        ArrayList<String> paths = new ArrayList<>();
        this.data.forEach((fileName, fileData) -> fileData.getFileData().forEach((line) -> {
            if (line.get(0).equals("C4INC")) {
                paths.add(line.get(2));
            }
        }));
        for (String ph : paths) {
            this.parse(ph);
        }
    }

    public Map<String, FileData> getData() {
        return this.data;
    }

    public void dumpData(String fileName, ArrayList<ArrayList<String>> fileData) throws IOException {
        FileWriter fw = new FileWriter(new File(fileName));
        gson.toJson(fileData, fw);
        fw.close();
    }

    public View getView(String viewName) throws IOException {
        FileReader fr = fileReader("views/" + viewName + ".json");
        // Magic tool
        return gson.fromJson(fr, new TypeToken<View>() {}.getType());
    }

    public void dumpView(String viewName, View view) throws IOException {
        FileWriter fw = fileWriter("views/" + viewName + ".json");
        gson.toJson(view, fw);
        fw.close();
    }

    public FileWriter fileWriter(String viewName) throws IOException {
        return new FileWriter(new File(viewName));
    }

    public FileReader fileReader(String viewName) throws IOException {
        return new FileReader(new File(viewName));
    }

    public Gson getGsonTool() {
        return gson;
    }
}
