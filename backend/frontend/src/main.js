import Vue from 'vue'
import App from './App.vue'
import BootstrapVue from 'bootstrap-vue';
import VueRouter from 'vue-router';
import axios from 'axios';

import router from '@/router';

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

Vue.use(BootstrapVue);
Vue.use(VueRouter);
Vue.config.productionTip = false;

const client = axios.create({
  baseURL: 'http://localhost:8080',
  // headers: {'Access-Control-Allow-Origin' : '*',
  //           'Content-Type' : 'application/json'
  // },
});

Vue.prototype.$client = client;

new Vue({
  router,
  render: h => h(App),
}).$mount('#app')
