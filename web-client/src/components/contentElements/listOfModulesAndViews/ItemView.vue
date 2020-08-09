<template>
  <li>
    <div class="list_element">
      <span class="module_name">{{view}}</span>
      <button class="btn" v-on:click="$emit('selectSome', view)">Выбрать</button>
    </div>
  </li>
</template>

<script>
export default {
  props: {
    view: {
      type: String
    }
  }
};
</script>

<style scoped>
.list_element {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.module_name {
  padding-left: 20px;
}
.btn {
  margin-left: 5px;
  background-color: #282a36; /* Blue background */
  border: none; /* Remove borders */
  color: white; /* White text */
  padding: 5px 7px; /* Some padding */
  font-size: 11px; /* Set a font size */
  cursor: pointer; /* Mouse pointer on hover */
}

/* Darker background on mouse-over */
.btn:hover {
  background-color: #bd93f9;
}
li {
  justify-self: start;
  list-style-type: none;
}
</style>