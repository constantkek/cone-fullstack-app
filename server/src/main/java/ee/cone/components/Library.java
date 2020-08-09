package ee.cone.components;

public class Library {
    private Boolean isExternal;
    private String moduleName;
    private String mavenPath;
    private String fileName;

    public Library(String moduleName, String maven, Boolean isExternal, String fileName) {
        this.moduleName = moduleName;
        this.mavenPath = maven;
        this.isExternal = isExternal;
        this.fileName = fileName;
    }

    public String getMavenPath() {
        return mavenPath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Boolean isExternal() {
        return isExternal;
    }

    public String getFileName() {
        return fileName;
    }
}