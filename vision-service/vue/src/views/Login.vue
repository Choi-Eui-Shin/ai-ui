<template>
    <v-app id="sky">
        <v-main>
            <v-container fill-height fluid>
                <v-layout align-center justify-center>
                    <v-card class="elevation-4" width="420">
                        <v-card-text>
                            <v-layout justify-center align-center>
                                <v-flex shrink>
                                    <v-img
                                        class="mt-5"
                                        src="@/assets/images/smile.png"
                                        width="222"
                                        height="32"
                                    ></v-img>
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                        <v-card-text>
                            <form ref="form" @submit.prevent="login()">
                                <v-text-field
                                    v-model="userId"
                                    name="userId"
                                    type="text"
                                    placeholder="사용자 아이디"
                                    required
                                    outlined
                                    class="ml-6 mr-6"
                                    ref="userId"
                                ></v-text-field>

                                <v-text-field
                                    v-model="userPassword"
                                    name="userPassword"
                                    type="password"
                                    placeholder="비밀번호"
                                    required
                                    outlined
                                    class="ml-6 mr-6"
                                ></v-text-field>
                                <v-layout justify-center align-center>
                                    <v-flex shrink>
                                        <v-btn
                                            type="submit"
                                            class="mt-2 mb-5"
                                            color="primary"
                                            :disabled="this.userId.length == 0 || this.userPassword.length == 0"
                                            >로그인</v-btn
                                        >
                                    </v-flex>
                                </v-layout>
                            </form>
                        </v-card-text>
                    </v-card>
                </v-layout>
            </v-container>
        </v-main>
    </v-app>
</template>

<script>
import * as users from "../api/users";
import { errorBox } from "../utils/toast";
import { mapMutations } from "vuex";

export default {
    name: "loginPage",
    data() {
        return {
            userId: "",
            userPassword: "",
        };
    },

    mounted() {
        this.$nextTick(() => {
            this.$.userId.focus();
        });
    },

    methods: {
        ...mapMutations(["setUserRole"]),

        /**
         * 로그인
         */
        login() {
            let loginData = {
                userId: this.userId,
                userPassword: this.userPassword,
            };

            users.login(loginData).then((response) => {
                if (response.data.returnCode == true) {
                    this.userId = "";
                    this.userPassword = "";

                    /**
                     * store에 저장
                     */
                    this.setUserRole(response.data.result.userRole);
                    this.$router.push("/home");
                } else {
                    errorBox(response.data.returnMessage);
                }
            })
            .catch((err) => {
                errorBox(err);
            });
        },
    },
};
</script>
<style>
#sky {
    /* background: url("../assets/images/sky2.png"); */
    background-color: #002559;
    background-size: cover;
    overflow-y: hidden;
}
</style>
