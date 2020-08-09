<template>
  <div id="app">
    <Header />
    <Content />
  </div>
</template>

<script>
import Header from "./components/Header";
import Content from "./components/Content";
export default {
  name: "App",
  components: { Header, Content }
};
</script>

<style>
body {
  padding: 0;
  margin: 0;
  background-color: #282a36;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
