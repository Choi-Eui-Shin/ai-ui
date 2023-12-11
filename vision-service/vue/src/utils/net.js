import axios from "axios";
import Vue from "vue";

const instance = axios.create({
    timeout: 20000
});

instance.interceptors.request.use(
    function (config) {
        config.headers['jwt'] = "";

        config.loader = Vue.$loading.show({
            // Pass props by their camelCased names
            container: document.getElementById("app"),
            isFullPage: false,
            canCancel: false,
            color: "#f1c80b",
            loader: "spinner",
            width: 64,
            height: 64,
            backgroundColor: "#ffffff",
            opacity: 0.5,
            zIndex: 9999,
            lockScroll: true
        });

        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(
    function (response) {
        response.config.loader.hide();
        return response;
    },

    function (error) {
        error.config.loader.hide();

		if(error.response.status === 403) {
            window.location.href = "/";
        }

        return Promise.reject(error);
    }
);

export default instance;