package ee.cone.rest;

import ee.cone.Parser;
import ee.cone.RequestsHandler;
import ee.cone.ResultCollection;
import ee.cone.exceptions.AlreadyExistsException;
import ee.cone.exceptions.LineNotFoundException;
import ee.cone.view.View;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("api")
public class ComponentsController {
    private final Parser parser;
    private final ResultCollection rc;
    private final RequestsHandler handler;

    public ComponentsController() {
        this.parser = new Parser();
        this.rc = new ResultCollection(parser);
        this.handler = new RequestsHandler(parser, rc);
    }

    @GetMapping("modules")
    public JSONArray getModules() {
        return rc.modulesToJSON();
    }

    @GetMapping("modules/{moduleName}")
    public JSONObject getModuleView(@PathVariable String moduleName) throws ParseException, IOException {
        View view = handler.generateModuleView(moduleName);
        String result = parser.getGsonTool().toJson(view);
        result = result.replace("clazz", "class");
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(result);
    }

    @PostMapping(value = "create/lib",
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE } )
    public JSONObject createLib(@RequestBody JSONObject jsonBody) throws AlreadyExistsException, IOException {
        handler.addNewLibrary(jsonBody);
        return jsonBody;
    }

    @PostMapping(value = "create/image",
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE } )
    public JSONObject createImage(@RequestBody JSONObject jsonBody) throws AlreadyExistsException, IOException {
        handler.addNewImage(jsonBody);
        return jsonBody;
    }

    @PostMapping(value = "create/dep",
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE } )
    public JSONObject createDep(@RequestBody JSONObject jsonBody) throws AlreadyExistsException, IOException {
        handler.addNewDep(jsonBody);
        return jsonBody;
    }

    @DeleteMapping(value = "remove/lib")
    public JSONObject removeLib(@RequestBody JSONObject jsonBody) throws LineNotFoundException, IOException {
        handler.deleteLib(jsonBody);
        return jsonBody;
    }

    @DeleteMapping(value = "remove/image")
    public JSONObject removeImage(@RequestBody JSONObject jsonBody) throws LineNotFoundException, IOException {
        handler.deleteImage(jsonBody);
        return jsonBody;
    }

    @DeleteMapping(value = "remove/arrow")
    public JSONObject removeDep(@RequestBody JSONObject jsonBody) throws LineNotFoundException, IOException {
        handler.deleteDep(jsonBody);
        return jsonBody;
    }

//    @DeleteMapping(value = "remove/module")
//    public JSONObject removeModule(@RequestBody JSONObject jsonModule) {
//        // Delete it's libs
//        JSONArray jsonLibs = (JSONArray) jsonModule.get("libs");
//        jsonLibs.forEach((jsonLib) -> removeLib((JSONObject) jsonLib));
//        // Delete it's images
//        JSONArray jsonImages = (JSONArray) jsonModule.get("images");
//        jsonImages.forEach((jsonImage) -> removeImage((JSONObject) jsonImage));
//        // Delete it's import deps
//        JSONArray jsonImportDeps = (JSONArray) jsonModule.get("imported");
//        jsonImportDeps.forEach((jsonDep) -> removeDep((JSONObject) jsonDep));
//        // Delete it's export deps
//        JSONArray jsonExportDeps = (JSONArray) jsonModule.get("exported");
//        jsonExportDeps.forEach((jsonDep) -> removeDep((JSONObject) jsonDep));
//        return jsonModule;
//    }
}