import Vue from "vue";
import Toasted from "vue-toasted";

Vue.use(Toasted);

const errorBox = (message, duration = 4000) => {
    let opt = {
        theme: "bubble",
        position: "top-center",
        duration: duration,
    };

    return Vue.toasted.error(message, opt);
};

const messageBox = (message, duration = 3000) => {
    let opt = {
        theme: "outline",
        position: "top-center",
        duration: duration,
    };

    return Vue.toasted.show(message, opt);
};

const snackBar = (message, duration = 300) => {
    let opt = {
        theme: "toasted-primary",
        position: "top-center",
        duration: duration,
    };

    return Vue.toasted.show(message, opt);
};

export {
    errorBox,
    messageBox,
    snackBar
}