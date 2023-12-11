<template>
    <div>
        <v-dialog
            v-model="dialog"
            hide-overlay
            transition="dialog-bottom-transition"
            max-width="600"
        >
            <v-card>
                <v-toolbar dense dark color="primary">
                    <v-btn icon dark @click="dialog = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    <v-toolbar-title>이미지 업로드</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-items>
                        <v-btn dark text @click="uploadImage">업로드</v-btn>
                    </v-toolbar-items>
                </v-toolbar>
                <v-card-text>
                    <v-container fluid>
                        <v-row>
                            <v-col cols="12">
                                <v-card class="mx-auto pa-0">
                                    <v-card-text>
                                        <v-file-input
                                            accept="multipart/mixed"
                                            label="화면 스케치"
                                            v-model="sketchFile"
                                        ></v-file-input>
                                    </v-card-text>
                                    <v-card-actions>
                                        <v-btn
                                            outlined
                                            text
                                            color="teal accent-4"
                                            @click="handleUpload"
                                        >
                                            업로드
                                        </v-btn>
                                    </v-card-actions>
                                </v-card>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            dialog: false,
            sketchFile: null,
        };
    },

    computed: {
    },

    methods: {
        /**
         * 
         */
        showUpload() {
            this.sketchFile = null;
            this.dialog = true;
        },

        /**
         * 
         */
         handleUpload() {
            this.$emit("uploadEvent", this.sketchFile);
            this.dialog = false;
        },
    },
};
</script>
