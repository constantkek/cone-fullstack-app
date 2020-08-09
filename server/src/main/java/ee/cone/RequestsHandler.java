package ee.cone;

import com.google.gson.reflect.TypeToken;
import ee.cone.components.Image;
import ee.cone.components.Library;
import ee.cone.components.Module;
import ee.cone.exceptions.AlreadyExistsException;
import ee.cone.exceptions.InvalidDataException;
import ee.cone.exceptions.LineNotFoundException;
import ee.cone.exceptions.NotEnoughDataException;
import ee.cone.view.Link;
import ee.cone.view.Node;
import ee.cone.view.View;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestsHandler {
    private Parser parser;
    private ResultCollection rc;

    private final String EXT_COLOR = "#EF5350"; // red
    private final String LIB_COLOR = "#4CAF50"; // green
    private final String MOD_COLOR = "#03A9F4"; // blue
    private final String IMG_COLOR = "#FFCA28"; // orange

    public RequestsHandler(Parser parser, ResultCollection rc) {
        this.parser = parser;
        this.rc = rc;
    }

    public boolean addNewLibrary(JSONObject jsonLib) throws AlreadyExistsException, IOException {
        String moduleName = (String) jsonLib.get("moduleName");
        String mavenPath = (String) jsonLib.get("mavenPath");
        Boolean isExtended = (Boolean) jsonLib.get("isExtended");
        if (moduleName == null || mavenPath == null || isExtended == null) throw new NotEnoughDataException();
        // Build line
        ArrayList<String> line = new ArrayList<>();
        if (isExtended) {
            line.add("C4EXT");
        } else {
            line.add("C4LIB");
        }
        line.add(moduleName);
        line.add(mavenPath);
        // Push line to file
        return pushLineToFile(line, "c4dep.uses-generated.json");
    }

    public boolean addNewImage(JSONObject jsonImage) throws AlreadyExistsException, IOException {
        String imageName = (String) jsonImage.get("imageName");
        String mainClass = (String) jsonImage.get("mainClass");
        String moduleName = (String) jsonImage.get("moduleName");
        if (moduleName == null || mainClass == null || imageName == null) throw new NotEnoughDataException();
        // Build line
        ArrayList<String> line = new ArrayList<String>();
        line.add("C4TAG");
        line.add(imageName);
        line.add(moduleName + "." + mainClass);
        // Push line to file
        return pushLineToFile(line, "c4dep.uses-generated.json");
    }

    public boolean addNewDep(JSONObject jsonDep) throws AlreadyExistsException, IOException {
        String from = (String) jsonDep.get("from");
        String to = (String) jsonDep.get("to");
        if (to == null || from == null) throw new NotEnoughDataException();
        // Build line
        ArrayList<String> line = new ArrayList<String>();
        line.add("C4DEP");
        line.add(to);
        line.add(from);
        // Push line to file
        return pushLineToFile(line, "c4dep.uses-generated.json");
    }

    public boolean deleteLib(JSONObject jsonLib) throws LineNotFoundException, IOException {
        String moduleName = (String) jsonLib.get("moduleName");
        String mavenPath = (String) jsonLib.get("mavenPath");
        Boolean isExtended = (Boolean) jsonLib.get("isExtended");
        if (moduleName == null || mavenPath == null || isExtended == null) throw new NotEnoughDataException();
        // Build line to delete
        ArrayList<String> line = new ArrayList<>();
        if (isExtended) {
            line.add("C4EXT");
        } else {
            line.add("C4LIB");
        }
        line.add(moduleName);
        line.add(mavenPath);
        // Update line, if such exists
        for (Map.Entry<String, FileData> entry : parser.getData().entrySet()) {
            String fileName = entry.getKey();
            FileData fileData = entry.getValue();
            FileData updated = deleteLineFromData(line, fileData);
            if (updated != null) {
                parser.getData().put(fileName, updated);
                parser.dumpData(fileName, updated.getFileData());
                return true;
            }
        }
        return false;
    }

    public boolean deleteImage(JSONObject jsonImage) throws LineNotFoundException, IOException {
        String imageName = (String) jsonImage.get("imageName");
        String moduleName = (String) jsonImage.get("moduleName");
        String mainClass = (String) jsonImage.get("mainClass");
        if (imageName == null || moduleName == null || mainClass == null) throw new NotEnoughDataException();
        // Build line to delete
        ArrayList<String> line = new ArrayList<String>();
        line.add("C4TAG");
        line.add(imageName);
        line.add(moduleName + "." + mainClass);
        // Update line, if such exists
        for (Map.Entry<String, FileData> entry : parser.getData().entrySet()) {
            String fileName = entry.getKey();
            FileData fileData = entry.getValue();
            FileData updated = deleteLineFromData(line, fileData);
            if (updated != null) {
                parser.getData().put(fileName, updated);
                parser.dumpData(fileName, updated.getFileData());
                return true;
            }
        }
        return false;
    }

    public boolean deleteDep(JSONObject jsonDep) throws IOException {
        String from = (String) jsonDep.get("from");
        String to = (String) jsonDep.get("to");
        if (from == null || to == null) throw new NotEnoughDataException();
        // Build line to delete
        ArrayList<String> line = new ArrayList<String>();
        line.add("C4DEP");
        line.add(to);
        line.add(from);
        // Update line, if such exists
        for (Map.Entry<String, FileData> entry : parser.getData().entrySet()) {
            String fileName = entry.getKey();
            FileData fileData = entry.getValue();
            FileData updated = deleteLineFromData(line, fileData);
            if (updated != null) {
                parser.getData().put(fileName, updated);
                parser.dumpData(fileName, updated.getFileData());
                return true;
            }
        }
        return false;
    }

    // FUTURE FEATURE
    public void deleteLink(JSONObject jsonDep) throws LineNotFoundException, IOException {
        String from = (String) jsonDep.get("from");
        String to = (String) jsonDep.get("to");
        String type = getLinkType(from, to);
        if (to == null || from == null) throw new NotEnoughDataException();
        if (type == null) throw new LineNotFoundException();
        // Depending on type delete object in DB
        boolean isDeleted = deleteLinkDB(type, from, to);
        if (type.equals("img")) from = rc.getModuleName(from);
        deleteLinkInViews(from, to);
    }

    private boolean deleteLinkDB(String type, String from, String to) throws IOException {
        switch (type) {
            case "lib":
                JSONObject jsonLib = new JSONObject();
                jsonLib.put("moduleName", to);
                jsonLib.put("mavenPath", from);
                jsonLib.put("isExtended", false);
                return deleteLib(jsonLib);
            case "ext":
                JSONObject jsonExtLib = new JSONObject();
                jsonExtLib.put("moduleName", to);
                jsonExtLib.put("mavenPath", from);
                jsonExtLib.put("isExtended", true);
                return deleteLib(jsonExtLib);
            case "mod":
                JSONObject jsonModule = new JSONObject();
                jsonModule.put("to", to);
                jsonModule.put("from", from);
                return deleteDep(jsonModule);
            case "img":
                JSONObject jsonImage = new JSONObject();
                jsonImage.put("imageName", to);
                // "from" template is `${moduleName}.${mainClass}`
                jsonImage.put("moduleName", rc.getModuleName(from));
                jsonImage.put("mainClass", rc.getMainClass(from));
                return deleteImage(jsonImage);
            default:
                throw new NotEnoughDataException();
        }
    }

    public void addView(String viewName) throws IOException {
        if (viewName == null) throw new NotEnoughDataException();
        File file = new File("views/" + viewName + ".json");
        if (file.exists() && file.isFile()) throw new AlreadyExistsException();
        View view = new View(viewName);
        parser.dumpView(viewName, view);
    }

    public void addNodeToView(JSONObject jsonBody) throws IOException {
        String viewName = (String) jsonBody.get("viewName");
        String key = (String) jsonBody.get("key");
        String color = (String) jsonBody.get("color");
        String loc = (String) jsonBody.get("loc");
        if (key == null || color == null || loc == null || viewName == null) throw new NotEnoughDataException();
        // Get view data from JSON
        View view = parser.getView(viewName);
        if (view.getNodes().stream()
                .anyMatch(node -> node.getKey().equals(key))) {
            throw new AlreadyExistsException();
        } else {
            // Default params when creating
            view.getNodes().add(new Node(key, color, loc));
            parser.dumpView(viewName, view);
        }
    }

    // OK
    public void addLinkToView(JSONObject jsonBody) throws AlreadyExistsException, InvalidDataException, NotEnoughDataException, IOException {
        String viewName = (String) jsonBody.get("viewName");
        String from = (String) jsonBody.get("from");
        String to = (String) jsonBody.get("to");
        if (from == null || to == null || viewName == null) throw new NotEnoughDataException();
        // Get view data from JSON
        View view = parser.getView(viewName);
        if (view.getLinks().stream()
                .anyMatch(link -> link.getFrom().equals(from) && link.getTo().equals(to))) {
            throw new AlreadyExistsException();
        } else {
            Node nodeFrom = view.getNodes().stream()
                    .filter(node -> node.getKey().equals(from))
                    .findFirst()
                    .orElse(null);
            Node nodeTo = view.getNodes().stream()
                    .filter(node -> node.getKey().equals(to))
                    .findFirst()
                    .orElse(null);
            if (nodeFrom == null || nodeTo == null) throw new LineNotFoundException();
            String type = getLinkTypeByColor(nodeFrom, nodeTo);
            if (type == null) throw new InvalidDataException();
            view.getLinks().add(new Link(from, to));
            parser.dumpView(viewName, view);
            saveLinkDB(type, from, to);
        }
    }

    private String getLinkTypeByColor(Node nodeFrom, Node nodeTo) {
        if (nodeFrom.getColor().equals(LIB_COLOR) && nodeTo.getColor().equals(MOD_COLOR)) return "lib";
        if (nodeFrom.getColor().equals(EXT_COLOR) && nodeTo.getColor().equals(MOD_COLOR)) return "ext";
        if (nodeFrom.getColor().equals(MOD_COLOR) && nodeTo.getColor().equals(MOD_COLOR)) return "mod";
        if (nodeFrom.getColor().equals(MOD_COLOR) && nodeTo.getColor().equals(IMG_COLOR)) return "img";
        return null;
    }

    // FUTURE FEATURE
    public void updateNodeInView(JSONObject jsonBody) throws IOException {
        String viewName = (String) jsonBody.get("viewName");
        String key = (String) jsonBody.get("key");
        if (key == null || viewName == null) throw new NotEnoughDataException();
        String loc = (String) jsonBody.get("loc");
        // Get view data from JSON
        View view = parser.getView(viewName);
        Node node = view.getNodes().stream()
                .filter(n -> n.getKey().equals(key))
                .findFirst()
                .orElseThrow(LineNotFoundException::new);
        if (loc != null) node.setLoc(loc);
        parser.dumpView(viewName, view);
    }

    // FUTURE FEATURE
    public void removeNodeFromView(JSONObject jsonBody) throws NotEnoughDataException, IOException {
        String key = (String) jsonBody.get("key");
        String viewName = (String) jsonBody.get("viewName");
        if (key == null || viewName == null) throw new NotEnoughDataException();
        // Get view data from JSON
        View view = parser.getView(viewName);
        Node nodeToRemove = view.getNodes().stream()
                .filter(node -> node.getKey().equals(key))
                .findFirst()
                .orElse(null);
        if (nodeToRemove == null) throw new LineNotFoundException();
        view.getLinks().stream()
                .filter(link -> link.getFrom().equals(nodeToRemove.getKey()) || link.getTo().equals(nodeToRemove.getKey()))
                .forEach(view.getLinks()::remove);
        view.getNodes().remove(nodeToRemove);
        parser.dumpView(viewName, view);
    }

    public void removeLinkFromView(JSONObject jsonBody) throws NotEnoughDataException, IOException {
        String from = (String) jsonBody.get("from");
        String to = (String) jsonBody.get("to");
        String viewName = (String) jsonBody.get("viewName");
        if (from == null || to == null || viewName == null) throw new NotEnoughDataException();
        View view = parser.getView(viewName);
        Link linkToRemove = view.getLinks().stream()
                .filter(link -> link.getFrom().equals(from) && link.getTo().equals(to))
                .findFirst()
                .orElse(null);
        if (linkToRemove == null) throw new LineNotFoundException();
        view.getLinks().remove(linkToRemove);
        parser.dumpView(viewName, view);
    }

    private boolean pushLineToFile(ArrayList<String> line, String fileName) throws AlreadyExistsException, IOException {
        ArrayList<ArrayList<String>> fileData = parser.getData().get(fileName).getFileData();
        ArrayList<String> alreadyExist = fileData.stream()
                .filter(dataLine -> dataLine.toString().equals(line.toString()))
                .findFirst()
                .orElse(null);
        if (alreadyExist != null) return false;
        fileData.add(line);
        parser.dumpData(fileName, fileData);
        return true;
    }

    private FileData updateLineInData(ArrayList<String> searchLine,
                                      ArrayList<String> newLine,
                                      FileData fileData) throws LineNotFoundException {
        ArrayList<String> lineToUpdate = fileData.getFileData().stream()
                .filter(line -> line.toString().equals(searchLine.toString()))
                .findFirst()
                .orElse(null);
        if (lineToUpdate == null) return null;
        fileData.getFileData().remove(lineToUpdate);
        fileData.getFileData().add(newLine);
        return fileData;
    }

    private FileData deleteLineFromData(ArrayList<String> line, FileData fileData) {
        ArrayList<String> lineToDelete = fileData.getFileData().stream()
                .filter(dataLine -> dataLine.toString().equals(line.toString()))
                .findFirst()
                .orElse(null);
        if (lineToDelete == null) return null;
        fileData.getFileData().remove(lineToDelete);
        return fileData;
    }

    private void deleteLinkInViews(String from, String to) throws IOException {
        ArrayList<String> viewNames = getViewsNames();
        viewNames.forEach(name -> {
            try {
                JSONObject jsonDep = new JSONObject();
                jsonDep.put("viewName", name);
                jsonDep.put("from", from);
                jsonDep.put("to", to);
                removeLinkFromView(jsonDep);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ArrayList<String> getViewsNames() throws IOException {
        return (ArrayList<String>) Files.walk(Paths.get("views"))
                .filter(Files::isRegularFile)
                .filter(path -> !path.toAbsolutePath().toString().contains("savedModules"))
                .map(path -> path.toFile().getName().split(".json")[0])
                .collect(Collectors.toList());
    }

    public JSONObject parseView(String viewName) throws IOException, ParseException {
        FileReader fr = parser.fileReader("views/" + viewName + ".json");
        JSONObject view = (JSONObject) new JSONParser().parse(fr);
        fr.close();
        return view;
    }

    private String getLinkType(String from, String to) {
        rc.pullFromParser();
        if (rc.getListOfLibs().stream().anyMatch(lib -> lib.getMavenPath().equals(from) && !lib.isExternal()))
            return "lib";
        if (rc.getListOfLibs().stream().anyMatch(lib -> lib.getMavenPath().equals(from) && lib.isExternal()))
            return "ext";
        if (rc.getListOfModules().stream().anyMatch(mod -> mod.getName().equals(from)) &&
                rc.getListOfModules().stream().anyMatch(mod -> mod.getName().equals(to))) return "mod";
        if (rc.getListOfImages().stream().anyMatch(image -> image.getName().equals(to))) return "img";
        return null;
    }

    private boolean saveLinkDB(String type, String from, String to) throws IOException {
        switch (type) {
            case "lib":
                JSONObject jsonLib = new JSONObject();
                jsonLib.put("moduleName", to);
                jsonLib.put("mavenPath", from);
                jsonLib.put("isExtended", false);
                return addNewLibrary(jsonLib);
            case "ext":
                JSONObject jsonExtLib = new JSONObject();
                jsonExtLib.put("moduleName", to);
                jsonExtLib.put("mavenPath", from);
                jsonExtLib.put("isExtended", true);
                return addNewLibrary(jsonExtLib);
            case "mod":
                JSONObject jsonModule = new JSONObject();
                jsonModule.put("from", from);
                jsonModule.put("to", to);
                return addNewDep(jsonModule);
            case "img":
                JSONObject jsonImage = new JSONObject();
                jsonImage.put("imageName", to);
                // "from" template is `${moduleName}.${mainClass}`
                jsonImage.put("moduleName", rc.getModuleName(from));
                jsonImage.put("mainClass", rc.getMainClass(from));
                return addNewImage(jsonImage);
            default:
                throw new NotEnoughDataException();
        }
    }

    public View generateModuleView(String moduleName) throws IOException {
        rc.pullFromParser();
        File file = new File("views/savedModules/" + moduleName + ".json");
        if (file.exists()) return parser.getView("savedModules/" + moduleName);
        View view = new View("View of " + moduleName);
        Module root = rc.getListOfModules().stream()
                .filter(module -> module.getName().equals(moduleName))
                .findFirst()
                .orElse(null);
        view.addNode(new Node(moduleName, MOD_COLOR, "0 0"));
        if (root == null) throw new InvalidDataException();
        ArrayList<Library> libs = root.getLibs();
        ArrayList<Module> imported = root.getModsImported().values().stream()
                .reduce((curr, next) -> {
                    curr.addAll(next);
                    return curr;
                })
                .orElse(new ArrayList<>());
        ArrayList<Module> exported = root.getModsExported().values().stream()
                .reduce((curr, next) -> {
                    curr.addAll(next);
                    return curr;
                })
                .orElse(new ArrayList<>());
        ArrayList<Image> images = root.getImages();
        int nTotal = libs.size() + imported.size() + exported.size() + images.size();
        int it = 0;
        for (int i = 0; it < libs.size(); it++) {
            String loc = getLoc(it, nTotal);
            if (libs.get(i).isExternal()) view.addNode(new Node(libs.get(i).getMavenPath(), EXT_COLOR, loc));
            else view.addNode(new Node(libs.get(i).getMavenPath(), LIB_COLOR, loc));
            view.addLink(new Link(libs.get(i).getMavenPath(), moduleName));
            i++;
        }
        for (int i = 0; it < imported.size() + libs.size(); it++) {
            String loc = getLoc(it, nTotal);
            view.addNode(new Node(imported.get(i).getName(), MOD_COLOR, loc));
            view.addLink(new Link(imported.get(i).getName(), moduleName));
            i++;
        }
        for (int i = 0; it < exported.size() + imported.size() + libs.size(); it++) {
            String loc = getLoc(it, nTotal);
            view.addNode(new Node(exported.get(i).getName(), MOD_COLOR, loc));
            view.addLink(new Link(moduleName, exported.get(i).getName()));
            i++;
        }
        for (int i = 0; it < images.size() + exported.size() + imported.size() + libs.size(); it++) {
            String loc = getLoc(it, nTotal);
            view.addNode(new Node(images.get(i).getName(), IMG_COLOR, loc));
            view.addLink(new Link(moduleName, images.get(i).getName()));
            i++;
        }
        return view;
    }

    private String getLoc(int it, int nTotal) {
        double sin = Math.sin(2 * Math.PI / nTotal * it);
        double signX = Math.signum(Math.cos(2 * Math.PI / nTotal * it));
        return 300 * signX + " " + 200 * sin;
    }

    public String updateView(String data) throws IOException, ParseException {
        View updatedView = parser.getGsonTool().fromJson(data, new TypeToken<View>() {}.getType());
        if (updatedView.getViewName().contains("View of ")) {
            String moduleName = updatedView.getViewName().split("View of ")[1];
            View oldView = generateModuleView(moduleName);
            ArrayList<Node> newNodes = updatedView.getNodes();
            ArrayList<Node> oldNodes = oldView.getNodes();
            ArrayList<Link> newLinksCopy = new ArrayList<>((ArrayList<Link>) updatedView.getLinks().clone());
            ArrayList<Link> oldLinksCopy = new ArrayList<>((ArrayList<Link>) oldView.getLinks().clone());
            addAndDeleteChanges(newNodes, oldNodes, newLinksCopy, oldLinksCopy);
            parser.dumpView("savedModules/" + moduleName, updatedView);
        }
        else {
            View oldView = parser.getView(updatedView.getViewName());
            ArrayList<Node> newNodes = updatedView.getNodes();
            ArrayList<Node> oldNodes = oldView.getNodes();
            ArrayList<Link> newLinksCopy = new ArrayList<>((ArrayList<Link>) updatedView.getLinks().clone());
            ArrayList<Link> oldLinksCopy = new ArrayList<>((ArrayList<Link>) oldView.getLinks().clone());
            addAndDeleteChanges(newNodes, oldNodes, newLinksCopy, oldLinksCopy);
            parser.dumpView(updatedView.getViewName(), updatedView);
        }
        return data;
    }

    private void addAndDeleteChanges(ArrayList<Node> newNodes, ArrayList<Node> oldNodes, ArrayList<Link> newLinksCopy, ArrayList<Link> oldLinksCopy) throws IOException {
        ArrayList<Link> linksToAdd = new ArrayList<>();
        newLinksCopy.stream()
                .filter(newLink -> oldLinksCopy.stream().noneMatch(oldLink -> newLink.toString().equals(oldLink.toString())))
                .forEach(linksToAdd::add);
        ArrayList<Link> linksToDelete = new ArrayList<>();
        oldLinksCopy.stream()
                .filter(oldLink -> newLinksCopy.stream().noneMatch(newLink -> oldLink.toString().equals(newLink.toString())))
                .forEach(linksToDelete::add);
        commitChanges(newNodes, oldNodes, linksToAdd, linksToDelete);
        parser.setData(parser.parseData("c4dep.main.json"));
    }

    private void commitChanges(ArrayList<Node> newNodes, ArrayList<Node> oldNodes, ArrayList<Link> newLinksCopy, ArrayList<Link> oldLinksCopy) throws IOException {
        for (Link link : newLinksCopy) {
            Node nodeFrom = newNodes.stream()
                    .filter(node -> node.getKey().equals(link.getFrom()))
                    .findFirst()
                    .orElse(null);
            Node nodeTo = newNodes.stream()
                    .filter(node -> node.getKey().equals(link.getTo()))
                    .findFirst()
                    .orElse(null);
            if (nodeFrom == null || nodeTo == null) throw new LineNotFoundException();
            String type = getLinkTypeByColor(nodeFrom, nodeTo);
            if (type == null) throw new LineNotFoundException();
            saveLinkDB(type, nodeFrom.getKey(), nodeTo.getKey());
        }
        for (Link link : oldLinksCopy) {
            Node nodeFrom = oldNodes.stream()
                    .filter(node -> node.getKey().equals(link.getFrom()))
                    .findFirst()
                    .orElse(null);
            Node nodeTo = oldNodes.stream()
                    .filter(node -> node.getKey().equals(link.getTo()))
                    .findFirst()
                    .orElse(null);
            if (nodeFrom == null || nodeTo == null) throw new LineNotFoundException();
            String type = getLinkTypeByColor(nodeFrom, nodeTo);
            if (type == null) throw new LineNotFoundException();
            deleteLinkDB(type, nodeFrom.getKey(), nodeTo.getKey());
        }
    }
}
