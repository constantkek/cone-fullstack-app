package ee.cone.components;

public class Image {

    private String moduleName;
    private String name;
    private String mainClass;
    private String fileName;

    public Image(String name, String moduleName, String mainClass, String fileName) {
        this.name = name;
        this.moduleName = moduleName;
        this.mainClass = mainClass;
        this.fileName = fileName;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getName() {
        return name;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public String getFileName() {
        return fileName;
    }
}
