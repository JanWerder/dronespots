import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import VueCirrus from "vue-cirrus";
import "vue-cirrus/dist/vue-cirrus.css";
import Notifications from "vue-notification";
import VueImg from "v-img";
import i18n from "./i18n";
import GAuth from "vue-google-oauth2";
import SweetModal from "sweet-modal-vue/src/plugin.js";
import VueCookies from "vue-cookies";
import VueElementLoading from "vue-element-loading";

const gauthOption = {
  clientId: process.env.VUE_APP_CLIENT_ID,
  scope: "profile email",
  prompt: "select_account",
};

Vue.use(GAuth, gauthOption);
Vue.use(VueCirrus);
Vue.use(Notifications);
Vue.use(VueImg);
Vue.use(SweetModal);
Vue.use(VueCookies);

Vue.component("VueElementLoading", VueElementLoading);

Vue.$cookies.config('7d')

Vue.config.productionTip = false;

new Vue({
  router,
  i18n,
  render: (h) => h(App),
}).$mount("#app");
