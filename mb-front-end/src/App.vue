<template>
  <div id="app">
    <img src="./assets/title.png" id="title-img">
    <router-view/>
  </div>
</template>

<script>
import Register from './components/Register'
import SignIn from './components/SignIn'
import Home from './components/Home'
export default {
  name: 'App',
  components: {
    Register,
    SignIn,
    Home
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
#title-img {
  width: 500px;
  margin-top: -50px;
}
</style>
