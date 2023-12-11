import Vue from "vue";
import Vuex from "vuex";
 
Vue.use(Vuex);
 
export default new Vuex.Store({
    state: {
        flagAdmin: "N",
        flagManager: "N",
        userRole: "",
        flagNone: false,    // 개발중에는 true로 변경하고 테스트한다.
        flagLogin: false,
        siteCode: "",
    },
    mutations: {
        setUserRole(state, role) {
            state.userRole = role;
            state.flagLogin = true;

            state.flagAdmin = "N";
            state.isManager = "N";

            if(role == 'ADMIN')
                state.flagAdmin = "Y";
            if(role == 'MANAGER')
                state.isManager = "Y";
        },

        setNone(state, none) {
            state.flagNone = none;
        },

        logout(state) {
            state.flagLogin = false;
        }
    },
    getters: {
        /**
         * 로그인 여부 확인
         * @param {*} state 
         */
        isLogin(state) {
            return state.flagLogin;
        },

        /**
         * 초기 로그인 여부
         * @param {*} state 
         * @returns 
         */
        isNone(state) {
            return state.flagNone;
        },

        /**
         * 관리자 여부
         * @param {*} state
         * @returns
         */
        isAdmin(state) {
            return state.flagAdmin === "Y";
        },

        isManager(state) {
            return state.flagManager === 'Y';
        },

        /**
         * @param {*} state 
         * @returns 
         */
        getUserRole(state) {
            return state.userRole;
        },
        
        /**
         * 사이트 코드를 반환한다.
         * @param {*} state 
         * @returns 
         */
        getSiteCode(state) {
            return state.siteCode;
        }
    },
    actions: {},
    modules: {},
});
