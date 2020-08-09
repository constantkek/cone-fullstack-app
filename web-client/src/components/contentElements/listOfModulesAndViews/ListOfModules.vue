<template>
  <div class="modules" v-bind:class="{active:moduleSelected}">
    <ul class="rounded" v-if="moduleSelected === false">
      <ListItem
        v-for="item of data"
        :key="item.index"
        v-bind:module="item"
        v-on:selectModule="selectModule($event)"
      />
    </ul>
    <div class="modules_diagram" v-else-if="moduleSelected === true">
      <DiagramOfView
        v-bind:modName="moduleName"
        v-bind:moduleData="selectedModule"
        v-on:backOnModules="backModules"
      ></DiagramOfView>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import ListItem from "./ListItem";
import DiagramOfView from "./../view/DiagramOfView";
export default {
  data() {
    return {
      moduleName: "",
      moduleSelected: false,
      selectedModule: {}
    };
  },
  props: ["data"],
  components: { ListItem, DiagramOfView },
  methods: {
    selectModule(module) {
      this.moduleSelected = true;
      this.$emit("hideSearch");
      this.$emit("hideBtn");
      this.moduleName = module.moduleName;
      let url = "http://localhost:8080/api/modules/" + module.moduleName;
      axios({
        method: "get",
        url: url,
        mode: "cors"
      }).then(response => (this.selectedModule = response.data));
    },
    backModules() {
      this.moduleSelected = false;
      this.$emit("hideSearch");
      this.$emit("hideBtn");
    }
  }
};
</script>

<style >
ul {
  margin-left: 0px;
  padding-left: 0px;
}
.modules {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.modules.active {
  text-align: center;
  color: #f8f8f2;
}

.moduleName.back {
  padding-right: 5%;
}
.dia_btn.back {
  margin-left: 1%;
}

.rounded {
  counter-reset: li;
  list-style: none;
  font: 12px "Trebuchet MS", "Lucida Sans";
  padding: 0 30px;
  text-shadow: 0 1px 0 rgba(255, 255, 255, 0.5);
}

.rounded li {
  position: relative;
  display: block;
  padding: 0.4em 0.4em 0.4em 0px;
  margin-top: 0.5em;
  background: #44475a;
  color: #f8f8f2;
  text-decoration: none;
  border-radius: 0.3em;
  transition: 0.3s ease-out;
}
.rounded li:hover {
  background: #ff79c6;
}
.rounded li:hover:before {
  transform: rotate(360deg);
}
.rounded li:before {
  content: counter(li);
  counter-increment: li;
  position: absolute;
  left: -1.3em;
  top: 50%;
  margin-top: -1.3em;
  background: #6272a4;
  height: 2em;
  width: 2em;
  line-height: 2em;
  border: 0.3em solid #44475a;
  text-align: center;
  font-weight: bold;
  border-radius: 2em;
  transition: all 0.3s ease-out;
}

.modules_diagram {
  width: 100%;
}
</style>