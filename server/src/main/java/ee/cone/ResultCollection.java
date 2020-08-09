package ee.cone;

import ee.cone.components.Image;
import ee.cone.components.Library;
import ee.cone.components.Module;
import ee.cone.components.Source;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ResultCollection {
    private Parser parser;

    private ArrayList<Module> listOfModules;
    private ArrayList<Source> listOfSources;
    private ArrayList<Image> listOfImages;
    private ArrayList<Library> listOfLibs;

    public ResultCollection(Parser parser) {
        this.parser = parser;
    }

    public ArrayList<Module> getListOfModules() {
        return listOfModules;
    }

    public ArrayList<Source> getListOfSources() {
        return listOfSources;
    }

    public ArrayList<Image> getListOfImages() {
        return listOfImages;
    }

    public ArrayList<Library> getListOfLibs() {
        return listOfLibs;
    }

    private void libHandler(ArrayList<String> elem, String fileName) {
        Library library = new Library(elem.get(1), elem.get(2), elem.get(0).equals("C4EXT"), fileName);
        this.listOfLibs.add(library);
    }

    private void srcHandler(ArrayList<String> elem, String fileName) {
        Source source = new Source(elem.get(1), elem.get(2), fileName);
        this.listOfSources.add(source);
    }

    private void depHandler(ArrayList<String> elem, String key) {
        Module higher = null;
        Module lower = null;
        // Firstly we run through modules to check if such exists
        for (Module m : this.listOfModules) {
            // If we found a higher module that imports lower module
            if (elem.get(1).equals(m.getName())) {
                higher = m;
            }
            // If we found second module being imported to first module
            else if (elem.get(2).equals(m.getName())) {
                lower = m;
            }
        }
        // If they not exist yet, let's create them and push to the list
        if (higher == null) {
            higher = new Module(elem.get(1));
            this.listOfModules.add(higher);
        }
        if (lower == null) {
            lower = new Module(elem.get(2));
            this.listOfModules.add(lower);
        }
        // After that bind modules to each other
        higher.addImportedModule(key, lower);
        lower.addExportedModule(key, higher);
    }

    private void imgHandler(ArrayList<String> elem, String fileName) {
        String path = elem.get(2);
        String mainClass = getMainClass(path);
        String moduleName = getModuleName(path);
        Image image = new Image(elem.get(1), moduleName, mainClass, fileName);
        this.listOfImages.add(image);
    }

    // Binds modules with it's sources, libraries and images
    private void postHandler() {
        for (Module m : this.listOfModules) {
            for (Source s : this.listOfSources) {
                if (s.getModulePrefix().equals(m.getNamePrefix())) {
                    m.addSrc(s);
                }
            }
            for (Library l : this.listOfLibs) {
                if (m.getName().equals(l.getModuleName())) {
                    m.addLib(l);
                }
            }
            for (Image i : this.listOfImages) {
                if (m.getName().equals(i.getModuleName())) {
                    // Bind image and it's main class to each other
                    m.addImage(i);
                }
            }
        }
    }

    // Преобразование данных в удобоваримый вид
    public void pullFromParser() {
        this.listOfModules = new ArrayList<>();
        this.listOfSources = new ArrayList<>();
        this.listOfImages = new ArrayList<>();
        this.listOfLibs = new ArrayList<>();
        // Every map elem (filename + its data) is being handled in this cycle
        this.parser.getData().forEach((fileName, fileData) -> {
            // Here we are inside the file handling tags
            for (ArrayList<String> elem : fileData.getFileData()) {
                switch (elem.get(0)) {
                    case "C4LIB":
                    case "C4EXT":
                        this.libHandler(elem, fileName);
                        break;
                    case "C4SRC":
                        this.srcHandler(elem, fileName);
                        break;
                    case "C4DEP":
                        this.depHandler(elem, fileName);
                        break;
                    case "C4TAG":
                        this.imgHandler(elem, fileName);
                        break;
                    default:
                        break;
                }
            }
        });
        this.postHandler();
    }

    // Create JSON from our modules data
    /* JSON:
    [{
    moduleName,
    libs: [{ moduleName, mavenPath, isExternal }],
    images: [{ imageName, moduleName, mainClass }],
    sources: [{ modulePrefix, localPath }],
    imported: [{ moduleName }],
    exported: [{ moduleName }]
    }]
    */
    public JSONArray modulesToJSON() {
        pullFromParser();
        JSONArray modules = new JSONArray();
         /*
         JSON contains all modules, which contains it's
         libs, sources, images and module dependencies
         */
        for (Module m : this.listOfModules) {
            JSONObject jsonModule = new JSONObject();
            jsonModule.put("moduleName", m.getName());

            // Adding libs
            JSONArray libs = new JSONArray();
            m.getLibs().forEach((library) -> {
                JSONObject jsonLib = new JSONObject();
                jsonLib.put("mavenPath", library.getMavenPath());
                jsonLib.put("isExternal", library.isExternal());
                jsonLib.put("moduleName", library.getModuleName());
                libs.add(jsonLib);
            });
            jsonModule.put("libs", libs);

            // Adding sources
            JSONArray sources = new JSONArray();
            m.getSources().forEach((source) -> {
                JSONObject jsonSource = new JSONObject();
                jsonSource.put("localPath", source.getLocalPath());
                jsonSource.put("modulePrefix", source.getModulePrefix());
                sources.add(jsonSource);
            });
            jsonModule.put("sources", sources);

            // Adding images
            JSONArray images = new JSONArray();
            m.getImages().forEach((image) -> {
                JSONObject jsonImage = new JSONObject();
                jsonImage.put("imageName", image.getName());
                jsonImage.put("mainClass", image.getMainClass());
                jsonImage.put("moduleName", image.getModuleName());
                images.add(jsonImage);
            });
            jsonModule.put("images", images);

            // Adding import dependencies
            JSONArray imported = new JSONArray();
            m.getModsImported().forEach((fileName, importedModules) -> {
                importedModules.forEach((module) -> {
                    JSONObject jsonImportedModule = new JSONObject();
                    jsonImportedModule.put("moduleName", module.getName());
                    imported.add(jsonImportedModule);
                });
            });
            jsonModule.put("imported", imported);

            // Adding export dependencies
            JSONArray exported = new JSONArray();
            m.getModsExported().forEach((fileName, exportedModules) -> {
                exportedModules.forEach((module) -> {
                    JSONObject jsonExportedModule = new JSONObject();
                    jsonExportedModule.put("moduleName", module.getName());
                    exported.add(jsonExportedModule);
                });
            });
            jsonModule.put("exported", exported);

            modules.add(jsonModule);
        }
        return modules;
    }

    public String getModuleName(String path) {
        return path.split("\\." + getMainClass(path))[0];
    }
    public String getMainClass(String path) {
        String[] splicedPath = path.split("\\.");
        return splicedPath[splicedPath.length - 1];
    }
}