<template>
    <v-container fluid>
        <v-row dense>
            <v-col cols="12">
                <v-data-table
                    :headers="headers"
                    :items="userList"
                    sort-by="username"
                    class="elevation-1"
                >
                    <template v-slot:no-data>
                        <v-btn color="primary" small @click="addUser">
                            사용자 등록
                        </v-btn>
                    </template>
                    <template v-slot:item.actions="{ item }">
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on }">
                                <v-icon v-on="on" small color="primary" class="mr-2" @click="addUser">
                                    mdi-account-plus
                                </v-icon>
                            </template>
                            <span>사용자 등록</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on }">
                                <v-icon v-on="on" color="error" small class="mr-2" @click="confirmDelete(item)">
                                    mdi-account-remove
                                </v-icon>
                            </template>
                            <span>사용자 삭제</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on }">
                                <v-icon v-on="on" small color="primary" class="mr-2" @click="editUser(item)">
                                    mdi-pencil
                                </v-icon>
                            </template>
                            <span>사용자 수정</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <template v-slot:activator="{ on }">
                                <v-icon v-on="on" small color="primary" @click="resetPassword(item)">
                                    mdi-key-remove
                                </v-icon>
                            </template>
                            <span>비밀번호 초기화</span>
                        </v-tooltip>
                    </template>
                </v-data-table>
            </v-col>
        </v-row>

        <!-- S:사용자 추가 -->
        <v-dialog
            v-model="dialogUser"
            persistent
            hide-overlay
            transition="dialog-bottom-transition"
            max-width="500"
        >
            <v-card>
                <v-toolbar dense dark color="primary">
                    <v-btn icon dark @click="dialogUser = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    <v-toolbar-title>{{ dialogTitle }}</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-items>
                        <v-btn dark text @click="saveUser"> 저장 </v-btn>
                    </v-toolbar-items>
                </v-toolbar>
                <v-card-text>
                    <v-form>
                        <v-text-field
                            v-model="editedItem.userId"
                            label="사용자 아이디"
                            hide-details="auto"
                            class="mt-3 ml-4 mr-4 mb-4"
                            :disabled="resetPwd == true || isNew == false"
                        ></v-text-field>
                        <v-text-field
                            v-model="editedItem.userPassword"
                            type="password"
                            label="비밀번호"
                            hide-details="auto"
                            class="ml-4 mr-4 mb-4"
                            :disabled="isNew == false && resetPwd == false"
                        ></v-text-field>
                        <v-text-field
                            v-model="editedItem.userName"
                            label="사용자 이름"
                            hide-details="auto"
                            class="ml-4 mr-4 mb-4"
                            :disabled="resetPwd == true"
                        ></v-text-field>
                        <v-select
                            v-model="editedItem.userRole"
                            :items="roles"
                            label="권한"
                            hide-details="auto"
                            class="mb-6 ml-4 mr-3"
                            :disabled="resetPwd == true"
                        ></v-select>
                    </v-form>
                </v-card-text>
            </v-card>
        </v-dialog>
        <!-- E:사용자 추가 -->

        <!-- S:삭제 확인 -->
        <v-dialog v-model="dialogDelete" max-width="350px">
            <v-card>
                <v-card-title class="text-h6"
                    >선택된 사용자를 삭제할까요?</v-card-title
                >
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" text @click="cancelDelete"
                        >취소</v-btn
                    >
                    <v-btn color="red darken-1" text @click="deleteUser"
                        >확인</v-btn
                    >
                    <v-spacer></v-spacer>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- E:삭제 확인 -->
    </v-container>
</template>

<script>
import * as users from "../api/users";
import { errorBox, messageBox } from "../utils/toast";

export default {
    data: () => ({
        /**
         * 편집대상 클래스
         */
        targetObject: null,
        isNew: true,
        resetPwd: false,

        /**
         * 추가 다이얼로그
         */
        dialogUser: false,

        /**
         * 삭제 다이얼로그
         */
        dialogDelete: false,

        dialogAdd: false,

        headers: [
            {
                text: "사용자 아이디",
                align: "start",
                sortable: false,
                value: "userId",
            },
            {
                text: "사용자 이름",
                value: "userName",
                sortable: false,
            },
            {
                text: "비밀번호",
                value: "userPassword",
                sortable: false,
            },
            {
                text: "권한",
                value: "userRole",
                sortable: false,
            },
            {
                text: "Actions",
                value: "actions",
                sortable: false,
                width: "20%",
            },
        ],

        /**
         * 사용자 목록
         */
        userList: [],

        editedItem: {
            userId: "",
            userName: "",
            userPassword: "",
            userRole: "",
        },

        roles: ["USER", "MANAGER", "ADMIN"],
    }),

    mounted() {
        this.loadUsers();
    },

    computed: {
        dialogTitle() {
            return this.isNew === true ? "사용자 추가" : "사용자 수정";
        },
    },

    methods: {
        /**
         * 사용자 목록을 조회한다.
         */
        loadUsers() {
            users.getUserList()
                .then((response) => {
                    if (response.data.returnCode == true) {
                        // console.log("@.@ ", response.data.result);
                        this.userList = response.data.result;
                    } else {
                        messageBox(response.data.returnMessage);
                    }
                })
                .catch((err) => {
                    errorBox(err);
                });
        },

        /**
         * 사용자 추가
         */
        addUser() {
            this.isNew = true;
            this.resetPwd = false;
            this.editedItem.userId = "";
            this.editedItem.userName = "";
            this.editedItem.userPassword = "";
            this.editedItem.userRole = "USER";

            this.dialogUser = true;
        },

        /**
         * "저장" 버튼 클릭
         */
        saveUser()
        {
            if (this.editedItem.userId.length == 0) {
                messageBox("사용자 아이디를 입력하세요");
                return;
            }
            if (this.editedItem.userName.length == 0) {
                messageBox("사용자 이름을 입력하세요");
                return;
            }
            if (this.editedItem.userPassword.length == 0) {
                messageBox("비밀번호를 입력하세요");
                return;
            }
            if (this.editedItem.userRole.length == 0) {
                messageBox("권한 입력하세요");
                return;
            }

            if (this.isNew) {
                /**
                 * 등록한 클래스와 동일한 이름이 있는지 검사한다.
                 */
                let ix = this.userList.findIndex(
                    (m) => m.userId === this.editedItem.userId
                );
                if (ix != -1) {
                    messageBox("'" + this.editedItem.userId + "' 사용자는 이미 존재합니다.");
                    return;
                }
                
                users.addUser(this.editedItem)
                    .then((response) => {
                        if (response.data.returnCode == true) {
                            this.userList.push(response.data.result);
                        } else {
                            messageBox(response.data.returnMessage);
                        }
                        this.dialogUser = false;
                    })
                .catch((err) => {
                    errorBox(err);
                });
            } else {
                if(this.resetPwd) {
                    /*
                     * 비밀번호 초기화
                     */
                    users.resetPassword(this.editedItem)
                        .then((response) => {
                            if (response.data.returnCode == true) {
                                let ix = this.userList.findIndex((m) => m.userId === this.editedItem.userId);
                                if (ix != -1) {
                                    this.userList[ix].userPassword = response.data.result.userPassword;
                                }
                                messageBox("비밀번호가 변경 되었습니다");
                            } else {
                                messageBox(response.data.returnMessage);
                            }
                            this.dialogUser = false;
                        })
                    .catch((err) => {
                        errorBox(err);
                    });
                }
                else {
                    users.updateUser(this.editedItem)
                        .then((response) => {
                            if (response.data.returnCode == true) {
                                let ix = this.userList.findIndex((m) => m.userId === this.editedItem.userId);
                                if (ix != -1) {
                                    this.userList[ix].userName = this.editedItem.userName;
                                    this.userList[ix].userRole = this.editedItem.userRole;
                                }

                                messageBox("변경 되었습니다");
                            } else {
                                messageBox(response.data.returnMessage);
                            }
                            this.dialogUser = false;
                        })
                    .catch((err) => {
                        errorBox(err);
                    });
                }
            }            
        },

        /**
         * 
         */
        resetPassword(target) {
            this.targetObject = target;

            this.editedItem.userId = target.userId;
            this.editedItem.userName = target.userName;
            this.editedItem.userPassword = "";
            this.editedItem.userRole = target.userRole;

            this.isNew = false;
            this.resetPwd = true;
            this.dialogUser = true;
        },


        /**
         * 테이블에서 편집 아이콘 클릭
         */
        editUser(target) {
            this.targetObject = target;

            this.editedItem.userId = target.userId;
            this.editedItem.userName = target.userName;
            this.editedItem.userPassword = target.userPassword;
            this.editedItem.userRole = target.userRole;

            this.isNew = false;
            this.resetPwd = false;
            this.dialogUser = true;
        },

        /**
         * 삭제 확인
         */
        confirmDelete(target) {
            this.targetObject = target;
            this.dialogDelete = true;
        },

        /**
         * 삭제 다이얼로그에서 "취소" 클릭
         */
        cancelDelete() {
            this.dialogDelete = false;
            this.$nextTick(() => {
                this.targetObject = null;
            });
        },

        /**
         * 삭제 다이얼로그에서 "삭제" 클릭
         */
        deleteUser() {
            this.dialogDelete = false;
            if (this.targetObject == null) {
                return;
            }
            let row = this.targetObject;

            /**
             * 삭제
             */
            users.deleteUser(this.targetObject.userId, this.targetObject.userPassword)
                .then((response) => {
                    //console.log("@.@ deleteClass => ", response);
                    if (response.data.returnCode == true) {
                        let index = this.userList.indexOf(row);
                        if (index != -1) this.userList.splice(index, 1);
                        this.targetObject = null;
                    } else {
                        messageBox(response.data.returnMessage);
                    }
                })
                .catch((err) => {
                    errorBox(err);
                });

            this.cancelDelete();
        },

        closeAddDialog() {
            this.dialogAdd = false;
            this.targetObject = null;
        },
    },
};
</script>
