<template>
    <div>
        <v-container fluid>
            <v-row>
                <v-col cols="12" class="pb-0">
                    <v-btn color="primary" @click="uploadImage">이미지 분석</v-btn>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="9">
                    <canvas ref="drawCanvas" style="overflow-y: auto; overflow-x: auto; padding: 0; border-style: solid; border-color: antiquewhite;"></canvas>
                </v-col>
                <v-col cols="3">
                    <v-row>
                        <v-col cols="12">
                            <v-text-field
                                label="클래스"
                                hide-details="auto"
                                outlined
                                readonly
                                v-model="className"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="12">
                            <v-text-field
                                label="프로퍼티 이름"
                                hide-details="auto"
                                outlined
                                v-model="propertyName"
                                @input="propertyChanged"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col cols="12">
                            <v-select
                                v-model="selectedEvents"
                                :items="events"
                                chips
                                label="이벤트"
                                multiple
                                outlined
                                @input="eventChanged"
                            ></v-select>
                        </v-col>
                    </v-row>
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
            /**
             * 이미지 분석 결과
             */
            uiElements: [],
            /**
             * 이벤트 목록
             */
            events: ['click', 'changed'],
            /**
             * 클래스 이름
             */
            className: '',
            /**
             * 프로퍼티 이름
             */
            propertyName: '',
            /**
             * 선택 이벤트
             */
            selectedEvents: [],
            /**
             * 선택된 UI 요소
             */
            selectedUiObject: null,

        };
    },

    beforeDestroy () {
        if (typeof window === 'undefined') return;
        window.removeEventListener('resize', this.onResize, { passive: true });
    },

    mounted() {
        this.onResize();
        window.addEventListener('resize', this.onResize, { passive: true });

        /**
         * fabric 캔버스
         */
        let canvas = new fabric.Canvas(this.$refs.drawCanvas);
        this.drawCanvas = canvas;
        let _ckHandler = this.clickHandler;

        // 이벤트 등록
        canvas.on('mouse:down', function(opt) {
            var evt = opt.e;
            if (evt.altKey === true) {
                this.isDragging = true;
                this.selection = false;
                this.lastPosX = evt.clientX;
                this.lastPosY = evt.clientY;
            }
            else {
                _ckHandler(opt);
            }
        });
        canvas.on('mouse:move', function(opt) {
        if (this.isDragging) {
            var e = opt.e;
            var vpt = this.viewportTransform;
            vpt[4] += e.clientX - this.lastPosX;
            vpt[5] += e.clientY - this.lastPosY;
            this.requestRenderAll();
            this.lastPosX = e.clientX;
            this.lastPosY = e.clientY;
        }
        });
        canvas.on('mouse:up', function() {
            // on mouse up we want to recalculate new interaction
            // for all objects, so we call setViewportTransform
            this.setViewportTransform(this.viewportTransform);
            this.isDragging = false;
            this.selection = true;
        });
        canvas.on('mouse:wheel', function(opt) {
            let delta = opt.e.deltaY;
            let zoom = canvas.getZoom();
            zoom *= 0.999 ** delta;
            if (zoom > 20) zoom = 20;
            if (zoom < 0.01) zoom = 0.01;
            canvas.zoomToPoint({ x: opt.e.offsetX, y: opt.e.offsetY }, zoom);
            opt.e.preventDefault();
            opt.e.stopPropagation();
        });
    

    },
    
    computed: {
    },
    
    methods: {
        onResize () {
            let can = this.$refs.drawCanvas;
            let w = window.innerWidth-26;
            can.width = w * (3/4);
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
         * 이벤트 선택 변경
         * @param {*} item 
         */
        eventChanged(item) {
            if(item == null)
                return;

            if(this.selectedUiObject !== null) {
                this.selectedUiObject['events'] = item;
            }
        },

        /**
         * 프로퍼티 변경
         * @param {*} newName 
         */
        propertyChanged(newName) {
            if(this.selectedUiObject !== null) {
                this.selectedUiObject['propertyName'] = newName;
            }
        },
        
        /**
         * UI
         * @param {*} options 
         */
        clickHandler(options) {
            if(options.target) {
                let pos = this.uiElements.findIndex(m => m.uid === options.target.uid);
                if(pos !== -1) {
                    let obj = this.uiElements[pos];
                    this.className = obj.classId;
                    this.propertyName = obj.propertyName;
                    this.selectedEvents = obj.events;

                    this.selectedUiObject = obj;
                }
                else {
                    this.selectedUiObject = null;

                    this.className = '';
                    this.propertyName = '';
                    this.selectedEvents = [];
                }
            }
            else {
                this.selectedUiObject = null;

                this.className = '';
                this.propertyName = '';
                this.selectedEvents = [];
            }
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
            this.uiElements = [];
            
            let thisCanvas = this.drawCanvas;
            
            /**
             * 추출 객체 표시
             */
            res.result.forEach(elm => {
                elm['propertyName'] = '';
                elm['events'] = [];
                this.uiElements.push(elm);

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

                // 클릭 이벤트에서 사용할 객체 키
                rect.set('uid', elm.uid);

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
