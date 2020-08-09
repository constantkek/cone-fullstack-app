package ee.cone.rest;

import ee.cone.Parser;
import ee.cone.RequestsHandler;
import ee.cone.ResultCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("api")
public class ViewsController {
    private Parser parser;
    private ResultCollection rc;
    private RequestsHandler handler;

    public ViewsController() {
        this.parser = new Parser();
        this.rc = new ResultCollection(parser);
        this.handler = new RequestsHandler(parser, rc);
    }

    @GetMapping(value = "views")
    public JSONArray getViews() throws IOException {
        ArrayList<String> fileNames = handler.getViewsNames();
        JSONArray jsonViews = new JSONArray();
        jsonViews.addAll(fileNames);
        return jsonViews;
    }

    // OK
    @GetMapping(value = "views/{viewName}")
    public JSONObject getView(@PathVariable String viewName) throws IOException, ParseException {
        return handler.parseView(viewName);
    }

    // OK
    @PostMapping(value = "views/add/view")
    public String addView(@RequestBody String viewName) throws IOException {
        handler.addView(viewName);
        return viewName;
    }

    // FUTURE FEATURE
    @PostMapping(value = "views/add/node")
    public JSONObject addNodeToView(@RequestBody JSONObject jsonBody) throws IOException {
        handler.addNodeToView(jsonBody);
        return jsonBody;
    }

    // FUTURE FEATURE
    @PostMapping(value = "views/add/arrow")
    public JSONObject addLinkToView(@RequestBody JSONObject jsonBody) throws IOException {
        handler.addLinkToView(jsonBody);
        return jsonBody;
    }

    // FUTURE FEATURE
//    @DeleteMapping(value = "views/remove/arrow")
//    public JSONObject removeLinkFromView(@RequestBody JSONObject jsonBody) throws IOException {
//        if (jsonBody.get("absolute") != null) handler.deleteLink(jsonBody);
//        else handler.removeLinkFromView(jsonBody);
//        return jsonBody;
//    }

    @PostMapping(value = "views/update")
    public String updateView(@RequestBody String data) throws IOException, ParseException {
        System.out.println(data);
        return handler.updateView(data);
    }
}