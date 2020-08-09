package ee.cone.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Module {
    // List of modules THIS module is using
    private Map<String, ArrayList<Module>> modsImported;
    // List of modules that are using THIS module
    private Map<String, ArrayList<Module>> modsExported;

    private ArrayList<Library> libs;
    private ArrayList<Source> sources;
    private ArrayList<Image> images;

    private String name;

    public Module() {
        this.libs = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.images = new ArrayList<>();
        this.modsImported = new HashMap<>();
        this.modsExported = new HashMap<>();
    }
    public Module(String name) {
        this.name = name;
        this.libs = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.images = new ArrayList<>();
        this.modsImported = new HashMap<>();
        this.modsExported = new HashMap<>();
    }

    public void addLib(Library library) {
        this.libs.add(library);
    }
    public void addSrc(Source source) {
        this.sources.add(source);
    }
    public void addImage(Image image) {
        this.images.add(image);
    }

    public void addImportedModule(String key, Module m) {
        this.modsImported.putIfAbsent(key, new ArrayList<>());
        this.modsImported.get(key).add(m);
    }
    public void addExportedModule(String key, Module m) {
        this.modsExported.putIfAbsent(key, new ArrayList<>());
        this.modsExported.get(key).add(m);
    }

    public String getName() {
        return name;
    }
    public String getNamePrefix() {
        // Needs to escape a dot
        return this.name.split("\\.")[0];
    }

    public Map<String, ArrayList<Module>> getModsImported() {
        return modsImported;
    }

    public Map<String, ArrayList<Module>> getModsExported() {
        return modsExported;
    }

    public ArrayList<Library> getLibs() {
        return libs;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public ArrayList<Image> getImages() {
        return images;
    }
}
