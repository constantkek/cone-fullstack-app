<template>
  <div class="view">
    <div class="viewName">
      <span class="module_name back">
        <h2>{{modName}}</h2>
      </span>
      <button class="dia_btn" @click="load()">Load</button>
      <button class="dia_btn" id="SaveButton" @click="save">Save</button>
      <button class="dia_btn" @click="showModal = true">Add Node</button>
      <button class="dia_btn back" @click="$emit('backOnModules')">Назад</button>
    </div>
    <div id="myDiagramDiv" style="border: solid 1px black; width:99%; height:700px; font-size:10px"></div>
    <Modal v-if="showModal" v-on:addNode="addNode"></Modal>
  </div>
</template>

<script>
import Modal from "./../listOfModulesAndViews/Modal";
import * as go from "gojs";
import axios from "axios";
export default {
  data() {
    return {
      showModal: false,
      viewName: ""
    };
  },
  props: {
    modName: {
      type: String
    },
    moduleData: {
      type: Object
    }
  },
  components: {
    Modal
  },

  mounted: function() {
    var $ = go.GraphObject.make;
    let myDiagram;
    myDiagram = $(
      go.Diagram,
      "myDiagramDiv", // create a Diagram for the DIV HTML element
      {
        LinkDrawn: maybeChangeLinkCategory, // these two DiagramEvents call a
        LinkRelinked: maybeChangeLinkCategory, // function that is defined below
        "undoManager.isEnabled": true
      }
    );

    // when the document is modified, add a "*" to the title and enable the "Save" button
    myDiagram.addDiagramListener("Modified", function(e) {
      console.log(e);
      var button = document.getElementById("SaveButton");
      if (button) button.disabled = !myDiagram.isModified;
      var idx = document.title.indexOf("*");
      if (myDiagram.isModified) {
        if (idx < 0) document.title += "*";
      } else {
        if (idx >= 0) document.title = document.title.substr(0, idx);
      }
    });

    // the regular node template, which supports user-drawn links from the main Shape
    myDiagram.nodeTemplate = $(
      "Node",
      "Auto",
      {
        locationSpot: go.Spot.Center,
        layerName: "Background"
      }, // always have regular nodes behind Links
      new go.Binding("location", "loc", go.Point.parse).makeTwoWay(
        go.Point.stringify
      ),
      $(
        "Shape",
        "RoundedRectangle",
        {
          fill: "#03a9f4",
          stroke: "#282A36",
          portId: "",
          fromLinkable: true,
          toLinkable: true,
          cursor: "pointer"
        },
        new go.Binding("fill", "color")
      ),
      $(
        "TextBlock",
        { margin: 5 }, // make some extra space for the shape around the text
        new go.Binding("text", "key")
      ) // the label shows the node data's key
    );

    // This is the template for a label node on a link: just an Ellipse.
    // This node supports user-drawn links to and from the label node.
    myDiagram.nodeTemplateMap.add(
      "LinkLabel",
      $(
        "Node",
        {
          selectable: false,
          avoidable: false,
          layerName: "Foreground"
        } // always have link label nodes in front of Links
      )
    );

    myDiagram.linkTemplate = $(
      go.Link,
      {
        routing: go.Link.Orthogonal, // may be either Orthogonal or AvoidsNodes
        curve: go.Link.JumpOver,
        corner: 10
      }, // rounded corners
      $(go.Shape),
      $(go.Shape, { toArrow: "Standard" })
    );

    // This template shows links connecting with label nodes as green and arrow-less.
    myDiagram.linkTemplateMap.add(
      "linkToLink",
      $(
        "Link",
        { relinkableFrom: true, relinkableTo: true },
        $("Shape", { stroke: "#2D9945", strokeWidth: 2 })
      )
    );

    // GraphLinksModel support for link label nodes requires specifying two properties.
    myDiagram.model = $(go.GraphLinksModel, {
      linkLabelKeysProperty: "labelKeys"
    });

    // Whenever a new Link is drawng by the LinkingTool, it also adds a node data object
    // that acts as the label node for the link, to allow links to be drawn to/from the link.
    // myDiagram.toolManager.linkingTool.archetypeLabelNodeData = {
    //   category: "LinkLabel"
    // };
    // this DiagramEvent handler is called during the linking or relinking transactions
    function maybeChangeLinkCategory(e) {
      var link = e.subject;
      var linktolink = link.fromNode.isLinkLabel || link.toNode.isLinkLabel;
      e.diagram.model.setCategoryForLinkData(
        link.data,
        linktolink ? "linkToLink" : ""
      );
    }
    this.diagram = myDiagram;
    this.load();
  },
  methods: {
    load() {
      console.log("imHere");
      this.viewName = this.moduleData.viewName;
      this.diagram.model = go.Model.fromJson(JSON.stringify(this.moduleData));
    },
    save() {
      let jsonForBack = JSON.parse(this.diagram.model.toJson());
      jsonForBack["viewName"] = this.viewName;
      this.diagram.isModified = false;
      console.log(JSON.stringify(jsonForBack));

      axios.post(
        "http://localhost:8080/api/views/update",
        JSON.stringify(jsonForBack),
        { headers: { "Content-Type": "text/plain" } }
      );
    },
    addNode: function(modObj) {
      var model = this.diagram.model;
      model.startTransaction();
      var color;
      switch (modObj.moduleType) {
        case "Lib":
          color = "#4CAF50 ";
          break;
        case "Image":
          color = "#FFCA28";
          break;
        case "Extra_lib":
          color = "#EF5350";
          break;
        case "Module":
          color = "#03A9F4";
          break;
      }
      var data = { loc: "0 0", color: color, key: modObj.name };
      model.addNodeData(data);
      model.commitTransaction("added Node and Link");
      this.showModal = false;
    }
  }
};
</script>

<style>
.module_name.back {
  margin-right: 2%;
  color: #f8f8f8;
}
.dia_btn {
  margin-right: 5px;
  background: linear-gradient(to bottom, #4b495e 5%, #383a59 100%);
  background-color: #383a59;
  border-radius: 28px;
  border: 1px solid #bd93f9;
  display: inline-block;
  cursor: pointer;
  color: #ffffff;
  font-family: Arial;
  font-size: 15px;
  padding: 5px 15px;
  text-decoration: none;
  text-shadow: 0px 1px 0px #bd93f9;
}
.dia_btn:hover {
  background: linear-gradient(to bottom, #383a59 5%, #bd93f9 100%);
  background-color: #bd93f9;
}
.dia_btn:active {
  position: relative;
  top: 1px;
}

.dia_btn:focus {
  outline: none;
}
.view {
  width: 100%;
  display: flex;
  flex-direction: column;
}
.viewName {
  margin-bottom: 1%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
</style>