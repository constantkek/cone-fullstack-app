<template>
  <div class="list_of_views">
    <table class="list_table" border="1" v-if="!moduleSelected & !isView">
      <tr>
        <th>Название модуля</th>
        <th></th>
      </tr>
      <tr v-for="item of data" :key="item.index">
        <th>{{item.moduleName}}</th>
        <td>
          <button class="btn" v-on:click="selectModule(item)">Выбрать</button>
        </td>
      </tr>
    </table>
    <table class="list_table" border="1" v-if="!moduleSelected & isView">
      <tr>
        <th>Название вью</th>
        <td></td>
      </tr>
      <tr v-for="item of views" :key="item.index">
        <th>{{item}}</th>
        <td>
          <button class="btn" v-on:click="selectView(item)">Выбрать</button>
        </td>
      </tr>
    </table>
    <DiagramOfView
      v-if="moduleSelected"
      v-bind:modName="moduleName"
      v-bind:moduleData="selectedModule"
      v-on:backOnModules="backModules"
    ></DiagramOfView>
  </div>
</template>

<script>
import axios from "axios";
import DiagramOfView from "./../view/DiagramOfView";
export default {
  props: {
    data: {},
    views: {},
    isView: {}
  },
  data() {
    return {
      moduleName: "",
      moduleSelected: false,
      selectedModule: {}
    };
  },
  methods: {
    selectModule(module) {
      this.moduleSelected = true;
      this.moduleName = module.moduleName;
      let url = "http://localhost:8080/api/modules/" + module.moduleName;
      axios({
        method: "get",
        url: url,
        mode: "cors"
      }).then(response => (this.selectedModule = response.data));
      this.$emit("hideBtn");
      this.$emit("hideSearch");
    },
    selectView(module) {
      this.moduleName = module;
      this.moduleSelected = true;
      let url = "http://localhost:8080/api/views/" + module;
      axios({
        method: "get",
        url: url
      }).then(response => (this.selectedModule = response.data));
      this.$emit("hideBtn");
      this.$emit("hideSearch");
    },
    backModules() {
      this.moduleSelected = false;
      this.$emit("hideBtn");
      this.$emit("hideSearch");
    }
  },
  components: {
    DiagramOfView
  }
};
</script>

<style >
.list_of_views {
  width: 100%;
  margin-top: 1%;
  display: flex;
  justify-content: center;
}

.list_table {
  width: 100%;
  font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;
  border-collapse: collapse;
}

.list_table td,
.list_table th {
  border: 1px solid #ddd;
  padding: 8px;
}

.list_table tr:nth-child(even) {
  background-color: #383a59;
}

.list_table tr:hover {
  background-color: #6272a4;
}

.list_table th {
  padding-top: 12px;
  padding-bottom: 12px;
  text-align: left;
  color: white;
}

.btn {
  margin-left: 5px;
  background-color: #282a36; /* Blue background */
  border: 1px solid #ddd; /* Remove borders */
  color: white; /* White text */
  padding: 5px 7px; /* Some padding */
  font-size: 11px; /* Set a font size */
  cursor: pointer; /* Mouse pointer on hover */
}

/* Darker background on mouse-over */
.btn:hover {
  background-color: #bd93f9;
}
</style>