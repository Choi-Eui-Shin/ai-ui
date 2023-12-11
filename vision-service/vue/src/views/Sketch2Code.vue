<template>
    <div>
        <v-container fluid>
            <v-row>
                <v-col cols="12" class="pb-0">
                    <v-btn color="primary" @click="uploadImage">이미지 분석</v-btn>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <canvas ref="drawCanvas" style="overflow-y: auto; overflow-x: auto; padding: 0; border-style: solid; border-color: antiquewhite;"></canvas>
                </v-col>
            </v-row>
        </v-container>
        <!-- S:이미지 업로드 -->
        <v-dialog
            v-model="dialog"
            hide-overlay
            transition="dialog-bottom-transition"
            max-width="500"
        >
            <v-card>
                <v-toolbar dense dark color="primary">
                    <v-btn icon dark @click="dialog = false">
                        <v-icon>mdi-close</v-icon>
                    </v-btn>
                    <v-toolbar-title>이미지 업로드</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-toolbar-items>
                        <!-- <v-btn dark text @click="uploadImage">업로드</v-btn> -->
                    </v-toolbar-items>
                </v-toolbar>
                <v-card-text>
                    <v-container fluid>
                        <v-row>
                            <v-col cols="12">
                                <v-file-input
                                    accept="image/jpeg; image/png"
                                    label="화면 스케치"
                                    v-model="sketchFile"
                                ></v-file-input>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-btn
                        outlined
                        text
                        color="teal accent-4"
                        @click="handleUpload"
                        class="mb-2"
                        :disabled="this.sketchFile === null"
                    >
                        업로드
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
import { errorBox, messageBox } from "../utils/toast";
import * as yolo from "../api/yolo";
import { fabric } from 'fabric';
import {disableControlVisible} from "../utils/fabricUtils";

/**
 * http://fabricjs.com/
 */
export default {
    name: "Sketch2Code",

    components: {
    },

    data() {
        return {
            dialog: false,
            sketchFile: null,
            /**
             * fabric canvas
             */
            drawCanvas: null,
        };
    },

    beforeDestroy () {
        if (typeof window === 'undefined') return;
        window.removeEventListener('resize', this.onResize, { passive: true });
    },

    mounted() {
        this.onResize();
        window.addEventListener('resize', this.onResize, { passive: true });

        this.drawCanvas = new fabric.Canvas(this.$refs.drawCanvas);
    },
    
    computed: {
    },
    
    methods: {
        onResize () {
            let can = this.$refs.drawCanvas;
            can.width = window.innerWidth-26;
            can.height = window.innerHeight-120;
        },

        uploadImage() {
            this.sketchFile = null;
            this.dialog = true;

            // const rect = new fabric.Rect({
            //     fill: 'red',
            //     width: 32,
            //     height: 32,
            //     borderColor: 'blue'
            // });

            // rect.setControlVisible('mtr', false);

            // this.drawCanvas.add(rect).setActiveObject(rect);
        },

        /**
         * 
         */
        handleUpload() {
            if (this.sketchFile === null) {
                messageBox("파일을 선택하세요");
                return;
            }

            this.dialog = false;

            yolo.yolov8(this.sketchFile).then((response) => {
                if (response.data.returnCode == true) {
                    this.showResult(response.data.result);
                } else {
                    errorBox(response.data.returnMessage);
                }
            })
            .catch((err) => {
                errorBox(err);
            })
            .finally(() => (this.isLoadErd = false));
        },

        /**
         * 분석 결과를 화면에 표시
         * @param {*} result 
         */
        showResult(res) {
            console.log("@.@ ==>", res);

            /**
             * 초기화
             */
            this.drawCanvas.clear();
            
            let thisCanvas = this.drawCanvas;
            
            /**
             * 추출 객체 표시
             */
            res.result.forEach(elm => {
                // let x = elm.rect.x;
                // let y = elm.rect.y;
                // let width = elm.rect[2] - elm.rect[0];
                // let height = elm.rect[3] - elm.rect[1];

                const rect = new fabric.Rect({
                    fill: 'blue',
                    opacity: 0.15,
                    left: elm.rect.x,
                    top: elm.rect.y,
                    width: elm.rect.width,
                    height: elm.rect.height,
                    borderColor: 'blue'
                });

                disableControlVisible(rect);
                // thisCanvas.add(rect);
                thisCanvas.insertAt(rect, 0);
            });

            /**
             * 이미지 출력
             */
            // ERROR
            // let srcImg = document.createElement("img");
            // srcImg.src = res.sourceImage;
            // var imgInstance = new fabric.Image(res.sourceImage, {
            //     left: 0,
            //     top: 0,
            // });
            // disableControlVisible(imgInstance);
            // this.drawCanvas.add(imgInstance);

            // OK
            // fabric.Image.fromURL(res.sourceImage, function(oImg) {
            //     disableControlVisible(oImg);
            //     thisCanvas.add(oImg);
            // });

            // 백그라운드 이미지로 출력
            thisCanvas.setBackgroundImage(res.sourceImage, thisCanvas.renderAll.bind(thisCanvas));
        }
    },
};
</script>
<style>
</style>
