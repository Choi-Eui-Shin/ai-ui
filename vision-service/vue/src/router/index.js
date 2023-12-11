import Vue from "vue";
import Router from "vue-router";
import Login from "../views/Login.vue";
import App from "../App.vue";
import store from "../store";
// import * as users from "../api/users";

Vue.use(Router);

export default new Router({
    routes: [
        {
            path: "/",
            name: "login",
            component: Login,
            beforeEnter(to,from,next) {
                // users.getAuthMethod().then((response) => {
                //     if (response.data.returnCode == true) {
                //         store.state.siteCode = response.data.result.siteCode;
                //         if ('NONE' === response.data.result.authMethod) {
                //             store.state.flagNone = true;
                //             next({name:'home'})
                //         }
                //         else {
                //             store.state.flagNone = false;
                //             next();
                //         }
                //     } else {
                //         store.state.flagNone = false;
                //         next();
                //     }
                // })
                // .catch(() => {
                //     store.state.flagNone = false;
                //     next();
                // });

                store.state.flagNone = true;
                next({name:'home'});
            }
        },
        {
            path: "/home",
            name: "home",
            component: App,
            beforeEnter(to,from,next) {
                // if(store.state.flagNone == false && store.state.userRole.length == 0) {
                if(store.state.flagNone == false && store.state.flagLogin == false) {
                    next({name:'login'})
                }
                else {
                    next();
                }
            }
        },
    ],
    mode: "hash"
});
