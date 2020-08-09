<template>
  <div class="content">
    <div class="content_header">
      <div class="change_type" v-if="isListShow">
        <button class="change_btn" v-if="isView" v-on:click="isView = false">Modules</button>
        <button class="change_btn" v-if="!isView" v-on:click="isView = true">Views</button>
      </div>
      <div v-if="needSearch" class="content_list_form">
        <input
          type="input"
          v-model="searchName"
          class="form__field"
          placeholder="Поиск..."
          name="module"
          id="module"
        />
      </div>
    </div>
    <TableOfView
      v-on:hideBtn="hideBtn"
      v-bind:data="filteredList"
      v-bind:isView="isView"
      v-bind:views="filteredViews"
      v-on:hideSearch="hideSearch"
    ></TableOfView>
    <!-- <div class="content_list" v-if="!isView">
      
      <ListOfModules
        v-on:hideBtn="hideBtn"
        v-on:hideSearch="hideSearch"
        v-bind:views="views"
        v-bind:data="filteredList"
      />
    </div>
    <div class="content_list" v-if="isView">
      <div v-if="needSearch" class="content_list_form">
        <input
          type="input"
          v-model="searchName"
          class="form__field"
          placeholder="Поиск..."
          name="module"
          id="module"
          required
        />
      </div>
      <ListOfViews
        v-on:hideBtn="hideBtn"
        v-on:hideSearch="hideSearch"
        v-bind:allViews="views"
        v-bind:views="filteredListView"
      ></ListOfViews>
    </div>-->
  </div>
</template>

<script>
import TableOfView from "./contentElements/listOfModulesAndViews/TableOfView";
import axios from "axios";
// import ListOfViews from "./contentElements/listOfModulesAndViews/ListOfViews";
// import ListOfModules from "./contentElements/listOfModulesAndViews/ListOfModules";
export default {
  data() {
    return {
      isListShow: true,
      isView: false,
      needSearch: true,
      searchName: "",
      dataFromApi: [],
      views: []
    };
  },
  mounted() {
    let modulesUrl = "http://localhost:8080/api/modules";
    let viewsUrl = "http://localhost:8080/api/views";
    const requestMod = axios.get(modulesUrl);
    const requestViews = axios.get(viewsUrl);
    axios.all([requestMod, requestViews]).then(
      axios.spread((...responses) => {
        const responseMod = responses[0];
        const responseViews = responses[1];
        this.dataFromApi = responseMod.data;
        this.views = responseViews.data;
        console.log(this.views);
      })
    );
  },
  computed: {
    filteredList() {
      return this.dataFromApi.filter(data =>
        data.moduleName.toLowerCase().includes(this.searchName.toLowerCase())
      );
    },
    filteredViews() {
      return this.views.filter(data =>
        data.toLowerCase().includes(this.searchName.toLowerCase())
      );
    }
  },
  methods: {
    hideSearch() {
      this.needSearch ? (this.needSearch = false) : (this.needSearch = true);
      // this.isListShow ? (this.needSearch = false) : (this.needSearch = true);
    },
    hideBtn() {
      this.isListShow ? (this.isListShow = false) : (this.isListShow = true);
    }
  },
  components: {
    // ListOfModules,
    // ListOfViews,
    TableOfView
  }
};
</script>

<style>
.content {
  height: 100%;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  /* grid-template-columns: 1fr 3.5fr; */
}

.content_list_form {
  margin-top: 1%;
  margin-left: 35%;
}

.form__field {
  border-radius: 3%;
  color: #f8f8f8;
  padding: 8px 2%;
  background: transparent;
  border-style: hidden;
  border-bottom: 2px solid #383a59;
}

.form__field::placeholder {
  color: #f8f8f8;
}

.form__field:hover {
  background: #44475a;
}

.form__field:focus {
  background: #44475a;
  border-bottom-color: #f8f8f8;
  outline: none;
}

.content_list_input {
  /* padding-right: 5%; */
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.content_header {
  display: flex;
  flex-direction: row;
}

.change_type {
  display: flex;
  justify-content: end;
  margin-top: 1%;
}
.change_btn {
  background: linear-gradient(to bottom, #4b495e 5%, #383a59 100%);
  background-color: #383a59;
  border-radius: 28px;
  border: 1px solid #bd93f9;
  display: inline-block;
  cursor: pointer;
  color: #ffffff;
  font-family: Arial;
  font-size: 15px;
  padding: 10px 40px;
  text-decoration: none;
  text-shadow: 0px 1px 0px #bd93f9;
}
.change_btn:hover {
  background: linear-gradient(to bottom, #383a59 5%, #bd93f9 100%);
  background-color: #bd93f9;
}

.change_btn:active {
  position: relative;
  top: 1px;
}

.change_btn:focus {
  outline: none;
}
</style>