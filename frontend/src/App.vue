<template>
    <div>
        <NavigationBar></NavigationBar>
        <div id="app">
            <router-view></router-view>
        </div>
    </div>
</template>

<script>
import NavigationBar from "./components/NavigationBar";
export default {
  name: 'app',
    components:{NavigationBar}
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
.b-pagination .page-link {
    position: relative;
    display: block;
    padding: .5rem .75rem;
    margin-left: -1px;
    line-height: 1.25;
    color: #343a40;
    background-color: #fff;
    border: 1px solid #dee2e6;
}
.b-pagination .page-item.active .page-link {
    z-index: 1;
    color: #fff;
    background-color: #343a40;
    border-color: #343a40;
}
</style>
