<template>
    <div>
        <v-dialog
            v-model="dialog"
            hide-overlay
            transition="dialog-bottom-transition"
            fullscreen persistent
            overlay-color="success"
        >
            <v-card>
                <v-toolbar dense dark color="primary">
                    <v-btn icon dark @click="dialog = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    <v-toolbar-title>UI 소스 코드</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-items>
                        <v-btn dark text @click="download">다운로드</v-btn>
                    </v-toolbar-items>
                </v-toolbar>
                <v-card-text>
                    <v-container fluid>
                        <v-row>
                            <v-col cols="12">
                                <div ref="codePanel">
                                    <VueCodeHighlight style="overflow:auto; height:600px;" language="javascript">
{{ sourceCode }}
                                    </VueCodeHighlight>
                                </div>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
/**
 * https://github.com/elisiondesign/vue-code-highlight
 */
import { component as VueCodeHighlight } from 'vue-code-highlight';
import "vue-code-highlight/themes/duotone-sea.css";
//import "vue-code-highlight/themes/window.css";
import * as yolo from "../api/yolo";
import FileDownload from "js-file-download";

export default {
    data() {
        return {
            dialog: false,
            sourceCode: ''
        };
    },

    components: {
        VueCodeHighlight
    },

    computed: {
    },

    methods: {
        /**
         * 다이얼로그 출력
         */
        showSourceCode(sourceCode) {
            this.dialog = true;
            this.$nextTick(() => {
                this.sourceCode = sourceCode;
            });
        },

        /**
         * 코드 다운로드
         */
        download() {
            let payload = {
                'option': '',
                'code': this.sourceCode
            };

            yolo.download(payload).then((response) => {
                // 파일이름 추출
                let filename = response.headers['content-disposition'];
                filename = filename.replace('attachment; filename="', '');
                filename = filename.replace('"', '');


                // 파일 다운로드
                FileDownload(new Blob([response.data]), decodeURI(filename));
            });
        }
    },
};
</script>
