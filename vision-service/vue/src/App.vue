<template>
    <v-app>
        <v-app-bar app color="#1A237E" dense dark>
            <v-tabs
                background-color="#1A237E"
                center-active
                dark
                v-model="selectedMenu"
            >
                <v-tab key="s2cPage">스케치를 코드로 변환</v-tab>
            </v-tabs>
            <v-spacer></v-spacer>

            <!-- login, logout -->
            <!-- <v-icon class="mr-3"
                @click="userLogout"
            >{{iconName}}</v-icon> -->

            <!--
            <v-menu
                bottom
                left
                offset-y
            >
                <template v-slot:activator="{ on, attrs }">
                    <v-avatar
                        size="32px"
                        class="mr-3"
                        v-bind="attrs"
                        v-on="on"
                    >
                        <img
                            alt="Avatar"
                            src="@/assets/images/smile.png"
                        >
                    </v-avatar>
                </template>
                <v-list>
                    <v-list-item link key="A" @click="changePwd=true">
                        <v-list-item-title>비밀번호 변경</v-list-item-title>
                    </v-list-item>
                    <v-list-item link key="B" @click="userLogout">
                        <v-list-item-title>로그아웃</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
            -->
            <v-avatar
                size="32px"
                class="mr-0"
            >
                <img
                    alt="Avatar"
                    src="@/assets/images/icon_tm.png"
                >
            </v-avatar>
        </v-app-bar>
        <v-main>
            <v-tabs-items v-model="selectedMenu">
                <v-tab-item key="s2cPage">
                    <Sketch2Code />
                </v-tab-item>
            </v-tabs-items>
        </v-main>
        <!-- S:비밀번호 변경 -->
        <v-dialog
            v-model="changePwd"
            persistent
            hide-overlay
            transition="dialog-bottom-transition"
            max-width="400"
        >
            <v-card>
                <v-toolbar dense dark color="error">
                    <v-btn icon dark @click="changePwd = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    <v-toolbar-title>비밀번호 변경</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-items>
                        <v-btn dark text @click="changePassword"> 저장 </v-btn>
                    </v-toolbar-items>
                </v-toolbar>
                <v-card-text>
                    <v-form>
                        <v-text-field
                            v-model="oldPassword"
                            type="password"
                            label="이전 비밀번호"
                            hide-details="auto"
                            class="mt-3 ml-4 mr-4 mb-4"
                        ></v-text-field>
                        <v-text-field
                            v-model="newPassword"
                            type="password"
                            label="새 비밀번호"
                            hide-details="auto"
                            class="ml-4 mr-4 mb-4"
                        ></v-text-field>
                        <v-text-field
                            v-model="newPasswordRe"
                            type="password"
                            label="새 비밀번호 확인"
                            hide-details="auto"
                            class="ml-4 mr-4 mb-4"
                        ></v-text-field>
                    </v-form>
                </v-card-text>
            </v-card>
        </v-dialog>
        <!-- E:사용자 추가 -->
    </v-app>
</template>

<script>
import Sketch2Code from "./views/Sketch2Code.vue";
import { mapGetters, mapMutations } from "vuex";
import * as users from "./api/users";
import {errorBox, messageBox} from "./utils/toast";

/*
 * Theme designer
 * https://lobotuerto.com/vuetify-color-theme-builder/
 */
export default {
    name: "App",

    components: {
        Sketch2Code
    },

    data() {
        return {
            selectedMenu: "designMain",
            flagLogin: false,
            /**
             * 비밀번호 변경
             */
            changePwd: false,
            oldPassword: "",
            newPassword: "",
            newPasswordRe: "",
        };
    },

    computed: {
        ...mapGetters(["isAdmin", "isNone", "isLogin", "getSiteCode"]),

        iconName() {
            return this.isLogin == true ? "mdi-logout" : "mdi-key";
        }
    },

    methods: {
        ...mapMutations(['setUserRole', 'logout']),

        /**
         * 사용자 로그아웃
         */
        userLogout() {
            if(this.isLogin) {
                users.logout().then(response => {
                    if (response.data.returnCode == true) {
                        this.setUserRole("");
                        this.$router.push('/');
                    } else {
                        errorBox(response.data.returnMessage);
                    }
                })
                .catch(err => {
                    errorBox(err);
                })
                .finally(() => {
                    this.logout();
                });
            }
            else {
                this.$nextTick(() => {
                    this.$refs.login.login();
                });
            }
        },

        /**
         * 비밀번호 변경
         */
        changePassword() {
            if(this.oldPassword.length == 0) {
                messageBox("이전 비밀번호를 입력하세요.");
                return;
            }
            if(this.newPassword.length == 0) {
                messageBox("새 비밀번호를 입력하세요.");
                return;
            }
            if(this.newPasswordRe.length == 0) {
                messageBox("새 비밀번호 확인을 입력하세요.");
                return;
            }

            if(this.newPassword !== this.newPasswordRe) {
                messageBox("입력한 새 비밀번호가 다릅니다.");
                return;
            }

            let pwdData = {
                oldPassword: this.oldPassword,
                newPassword: this.newPassword,
                newPasswordRe: this.newPasswordRe,
            };
            users.changePassword(pwdData)
                .then(response => {
                    if (response.data.returnCode == true) {
                        messageBox("변경되었습니다.");
                    } else {
                        errorBox(response.data.returnMessage);
                    }
                })
                .catch(err => {
                    errorBox(err);
                })
                .finally(() => {
                    this.oldPassword = "";
                    this.newPassword = "";
                    this.newPasswordRe = "";

                    this.setUserRole("");
                    this.$router.push('/');
                });
        }
    },
};
</script>
