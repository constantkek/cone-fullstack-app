package ee.cone.components;

// Source doesn't know modules uses that source
// But module knows it's sources
public class Source {
    private String modulePrefix;
    private String localPath;
    private String fileName;

    public Source(String modulePrefix, String path, String fileName) {
        this.modulePrefix = modulePrefix;
        this.localPath = path;
        this.fileName = fileName;
    }

    // Getters
    public String getModulePrefix() {
        return modulePrefix;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getFileName() {
        return fileName;
    }
}