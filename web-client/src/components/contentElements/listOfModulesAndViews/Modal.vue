<template>
  <transition name="modal">
    <div class="modal-mask">
      <div class="modal-wrapper">
        <div class="modal-container">
          <div class="modal-header">
            <div class="modal_input">
              <input type="input" v-model="modObj.name" placeholder="Название модуля" />
            </div>
            <div class="select_box">
              <select v-model="modObj.moduleType">
                <option value disabled selected>Выберите тип</option>
                <option>Module</option>
                <option>Lib</option>
                <option>Extra_lib</option>
                <option>Image</option>
              </select>
            </div>
            <div class="modal_ok">
              <button class="modal-default-button" v-on:click="closeModal">OK</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  data() {
    return {
      modObj: {
        name: "",
        moduleType: ""
      }
    };
  },
  methods: {
    closeModal() {
      this.$emit("addNode", this.modObj);
    }
  }
};
</script>

<style>
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: table;
  transition: opacity 0.3s ease;
}

.modal-wrapper {
  display: table-cell;
  vertical-align: middle;
}

.modal-container {
  width: 300px;
  margin: 0px auto;
  padding: 20px 30px;
  background-color: #44475a;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  font-family: Helvetica, Arial, sans-serif;
}

.modal-header h3 {
  display: flex;
  flex-direction: column;
  margin-top: 0;
  color: #42b983;
}

.modal-enter {
  opacity: 0;
}

.modal-leave-active {
  opacity: 0;
}

.modal-enter .modal-container,
.modal-leave-active .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}

.modal_input {
  margin-bottom: 5px;
}

.select_box {
  margin-bottom: 5px;
}
</style>