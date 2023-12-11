import Vue from "vue";
import Main from './Main.vue'
import store from "./store";
import vuetify from "./plugins/vuetify";
import router from "./router";
import VueSplit from 'vue-split-panel';

import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader 

/**
 * 로딩 컴포넌트
 * https://github.com/ankurk91/vue-loading-overlay/tree/v3.x
 */
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";
import i18n from "./i18n";
import {toDateFormat} from "./utils/utils";

Vue.use(Loading);
Vue.use(VueSplit);

/**
 * 전역필터
 */
Vue.filter('toDateFormat', (value) => {
    return toDateFormat(value);
});

Vue.config.productionTip = false;

Vue.use(vuetify, {
    iconfont: 'mdi'
});

new Vue({
    router,
    vuetify,
    // render: (h) => h(App),
    render: (h) => h(Main),
    i18n,
    store,
}).$mount("#app");
