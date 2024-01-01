<template>
    <v-container fluid>
        <v-row>
            <v-col cols="12" class="d-flex flex-row">
                <v-btn color="primary" small class="ml-3 mt-2 mr-2" @click="uploadImage">이미지 분석</v-btn>
                <v-btn color="error" small class="mt-2 mr-2" @click="generateCode">소스코드 생성</v-btn>
                <v-select
                    :items="targetLanguage"
                    item-text="text"
                    item-value="value"
                    v-model="selectedTarget"
                    label="코드 템플릿"
                    solo
                    dense hide-details="auto"
                    class="select"
                ></v-select>
            </v-col>
        </v-row>
        <v-row class="mt-0">
            <v-col cols="9">
                <v-container fluid>
                    <v-row>
                        <v-col cols="12">
                            <canvas ref="drawCanvas" style="overflow-y: auto; overflow-x: auto; padding: 0; border-style: solid; border-color: rgb(241, 239, 236);"></canvas>
                        </v-col>
                    </v-row>
                </v-container>
            </v-col>
            <v-col cols="3">
                <v-container fluid class="ml-2 mr-2">
                    <v-row>
                        <v-col>
                            <div style="overflow-y: auto; height: 300px; padding: 0; border-style: solid; border-color: rgb(241, 239, 236);">
                                <v-list dense>
                                    <v-list-item-group
                                        color="primary"
                                        v-model="selectedElement"
                                    >
                                        <v-list-item
                                            v-for="(item) in uiElements"
                                            :key="item.uid"
                                            :id="'list-item-' + item.uid"
                                            @click="selectElement(item)"
                                        >
                                            <!-- <v-list-item-icon>
                                                <v-icon v-text="item.icon"></v-icon>
                                            </v-list-item-icon> -->
                                            <v-list-item-content>
                                                <v-list-item-title v-text="'(' + item.number + ') ' + item.classId"></v-list-item-title>
                                            </v-list-item-content>
                                            <v-list-item-icon>
                                                <v-icon color="error" @click="deleteElement(item)">mdi-delete-forever</v-icon>
                                            </v-list-item-icon>
                                        </v-list-item>
                                    </v-list-item-group>
                                </v-list>
                            </div>
                        </v-col>
                    </v-row>
                    <v-row class="mt-2">
                        <v-col cols="12">
                            <v-text-field
                                label="클래스"
                                hide-details="auto"
                                outlined dense
                                readonly
                                v-model="className"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row class="mt-0">
                        <v-col cols="12">
                            <v-text-field
                                label="객체 경계"
                                hide-details="auto"
                                outlined dense
                                readonly
                                v-model="objRect"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row class="mt-0">
                        <v-col cols="12">
                            <v-text-field
                                label="프로퍼티 이름"
                                hide-details="auto"
                                outlined dense
                                v-model="propertyName"
                                @input="propertyChanged"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row class="mt-0">
                        <v-col cols="12">
                            <v-text-field
                                label="라벨"
                                hide-details="auto"
                                outlined dense
                                v-model="labelText"
                                @input="labelChanged"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                    <v-row class="mt-0">
                        <v-col cols="12">
                            <v-select
                                v-model="selectedEvents"
                                :items="events"
                                chips
                                label="이벤트"
                                hide-details="auto"
                                multiple
                                outlined dense
                                @input="eventChanged"
                            ></v-select>
                        </v-col>
                    </v-row>
                    <v-row class="mt-0">
                        <v-col cols="12">
                            <v-text-field
                                label="이벤트 핸들러 이름"
                                hide-details="auto"
                                outlined dense
                                v-model="handlerName"
                                @input="handlerChanged"
                            ></v-text-field>
                        </v-col>
                    </v-row>
                </v-container>
            </v-col>
        </v-row>
        <!-- S:이미지 업로드 -->
        <v-dialog
            v-model="dialog"
            hide-overlay
            transition="dialog-bottom-transition"
            max-width="500"
        >
            <v-card>
                <v-toolbar dense dark color="#1A237E">
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
        <!-- S:SourceViewDialog 업로드 -->
        <SourceViewDialog
            ref="srcViewer"
            v-on:download="download" />
        <!-- E:SourceViewDialog 업로드 -->
    </v-container>
</template>

<script>
import { errorBox, messageBox } from "../utils/toast";
import * as yolo from "../api/yolo";
import * as main from "../api/main";
import { fabric } from 'fabric';
import {disableControlVisible} from "../utils/fabricUtils";
import SourceViewDialog from "../components/SourceViewDialog.vue";
import { mapMutations } from 'vuex'
import FileDownload from "js-file-download";

/**
 * http://fabricjs.com/
 */
export default {
    name: "Sketch2Code",

    components: {
        SourceViewDialog
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
            objRect: '',
            /**
             * 클래스 이름
             */
            className: '',
            /**
             * 프로퍼티 이름
             */
            propertyName: '',
            /**
             * text or label 텍스트
             */
            labelText: '',
            /**
             * 선택 이벤트
             */
            selectedEvents: [],
            /**
             * 이벤트 핸들러 이름
             */
            handlerName: '',
            /**
             * 선택된 UI 요소
             */
             selectedUiObject: null,
             selectedElement: null,
            /**
             * 생성언어
             */
            targetLanguage: [
            ],
            selectedTarget: '',

        };
    },

    beforeUnmount () {
        if (typeof window === 'undefined') return;
        window.removeEventListener('resize', this.onResize, { passive: true });
    },

    mounted() {
        let can = this.$refs.drawCanvas;
        let w = window.innerWidth;
        can.width = w * (3/4);
        can.height = window.innerHeight-140;

        window.addEventListener('resize', this.onResize, { passive: true });
       
        // this.onResize();

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
            // on mouse up we want to recalculate new interaction for all objects, so we call setViewportTransform
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

        /**
         * 템플릿 목록 조회
         */
        main.getTemplate().then((response) => {
            if (response.data.returnCode == true) {
                this.targetLanguage = [];
                response.data.result.forEach(m => {
                    this.targetLanguage.push({
                        'text': m.ruleTitle,
                        'value': m.uuid
                    });
                });
            } else {
                errorBox(response.data.returnMessage);
            }
        })
        .catch((err) => {
            errorBox(err);
        });
    },
    
    computed: {
    },
    
    methods: {
        ...mapMutations([
            'setSelectedTemplate', 'setSelectedMenu', 'setGenerateCode'
        ]),

        onResize () {
            // let can = this.$refs.drawCanvas;
            let w = window.innerWidth;
            // can.width = w * (3/4);
            // can.height = window.innerHeight-120;
            
            this.drawCanvas.setHeight(window.innerHeight-140);
            this.drawCanvas.setWidth(w * (3/4));
            this.drawCanvas.renderAll();
        },

        uploadImage() {
            this.sketchFile = null;
            this.dialog = true;
        },

        /**
         * UI 요소 삭제
         * @param {*} item 
         */
        deleteElement(item) {
            let pos = this.uiElements.findIndex(m => m.uid === item.uid);
            if(pos !== -1) {
                this.uiElements.splice(pos, 1);
                this.drawCanvas.remove(item.object);
                this.drawCanvas.renderAll();
            }
        },

        /**
         * 이미지를 분석한다.
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
            });
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
         * 이벤트 핸들러 변경
         * @param {*} newName 
         */
         handlerChanged(newName) {
            if(this.selectedUiObject !== null) {
                this.selectedUiObject['handlerName'] = newName;
            }
        },

        /**
         * LABEL 변경
         * @param {*} newName 
         */
         labelChanged(newName) {
            if(this.selectedUiObject !== null) {
                this.selectedUiObject['labelText'] = newName;
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
         * UI 요소 클릭
         * @param {*} options 
         */
        clickHandler(options, flagOther) {
            if(options.target) {
                let pos = this.uiElements.findIndex(m => m.uid === options.target.uid);
                if(pos !== -1) {
                    let obj = this.uiElements[pos];
                    this.className = obj.classId;
                    this.propertyName = obj.propertyName;
                    this.labelText = obj.labelText;
                    this.selectedEvents = obj.events;
                    let centerX = obj.rect.x + (obj.rect.width/2);
                    let centerY = obj.rect.y + (obj.rect.height/2);
                    this.objRect = centerX.toFixed() + "," + centerY.toFixed();
                    this.handlerName = obj.handlerName;

                    this.selectedUiObject = obj;
                    // 리스트 선택
                    if(flagOther === undefined) {
                        this.selectedElement = pos;
                        document.getElementById("list-item-" + obj.uid).scrollIntoView()
                    }
                }
                else {
                    this.selectedUiObject = null;
                    this.objRect = '';
                    this.className = '';
                    this.propertyName = '';
                    this.labelText = '';
                    this.selectedEvents = [];
                    this.handlerName = '';
                }
            }
            else {
                this.selectedUiObject = null;
                this.objRect = '';
                this.className = '';
                this.propertyName = '';
                this.labelText = '';
                this.selectedEvents = [];
                this.handlerName = '';
            }
        },

        /**
         * 리스트의 아이템 클릭
         * @param {*} item 
         */
        selectElement(item) {
            this.clickHandler({'target': item}, true);
            this.drawCanvas.setActiveObject(item.object);
            this.drawCanvas.renderAll();
        },

        /**
         * 분석 결과를 화면에 표시
         * @param {*} result 
         */
        showResult(res) {
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
                elm['handlerName'] = '';
                this.uiElements.push(elm);

                let text = new fabric.Text(elm['number'].toString(), 
                                {
                                    left: elm.rect.x,
                                    top: elm.rect.y,
                                    fontSize: 18,
                                    // textBackgroundColor: 'rgb(0,200,0)',
                                    textBackgroundColor: 'rgb(255, 255, 0)',
                                });
                disableControlVisible(text);
                // thisCanvas.add(text);

                let rect = new fabric.Rect({
                    fill: 'blue',
                    opacity: 0.15,
                    left: elm.rect.x,
                    top: elm.rect.y,
                    width: elm.rect.width,
                    height: elm.rect.height,
                    // borderColor: 'yellow'
                });
                disableControlVisible(rect);
                
                let group = new fabric.Group([text, rect], {
                    left: elm.rect.x,
                    top: elm.rect.y,
                    borderColor: 'yellow'
                });

                // 클릭 이벤트에서 사용할 객체 키
                group.set('uid', elm.uid);
                elm['object'] = group;

                disableControlVisible(group);
                // thisCanvas.add(rect);
                // thisCanvas.insertAt(rect, 0);
                thisCanvas.insertAt(group, this.uiElements.length);
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
        },

        /**
         * 지정된 옵션으로 소스코드를 생성한다.
         */
        generateCode() {
            if(this.selectedTarget.length == 0) {
                messageBox("생성할 코드 템플릿을 선택하세요");
                return;
            }

            if(this.uiElements.length == 0) {
                messageBox("데이터가 없습니다.");
                return;
            }

            let payload = {
                'targetTemplateUuid': this.selectedTarget,
                'uiObjects': this.uiElements
            };

            main.generate(payload).then((response) => {
                if (response.data.returnCode == true) {
                    // 소스코드 설정
                    // let fm = prettier.format(response.data.result.sourceCode, 
                    //             { semi: false, parser: "babel" });

                    // this.setGenerateCode(fm);

                    this.$refs.srcViewer.showSourceCode(response.data.result.sourceCode);
                    // const DialogClass = Vue.extend(SourceViewDialog);
                    // const buttonInstance = new DialogClass();
                    // buttonInstance.$mount();
                    // console.log(buttonInstance);
                    // // document.appendChild(buttonInstance.$el);
                    // buttonInstance.showSourceCode(response.data.result.sourceCode);


                    // this.setGenerateCode(response.data.result.sourceCode);

                    // 화면이동
                    // this.$emit('gotoScreen', 1);
                    // this.setSelectedMenu(1);
                } else {
                    errorBox(response.data.returnMessage);
                }
            })
            .catch((err) => {
                errorBox(err);
            });
        },

        /**
         * UI, 백앤드 소스코드 다운로드
         */
        download(downloadType) {
            let payload = {
                'targetTemplateUuid': this.selectedTarget,
                'uiObjects': this.uiElements
            };
            main.download(downloadType, payload).then((response) => {
                // 파일이름 추출
                let filename = response.headers['content-disposition'];
                filename = filename.replace('attachment; filename="', '');
                filename = filename.replace('"', '');


                // 파일 다운로드
                FileDownload(new Blob([response.data]), decodeURI(filename));
            });
        },
    },
};
</script>
<style>
.select {
    max-width: 200px;
    width: 200px;
}
.v-chip.v-size--default {
    height: 24px;
}
</style>
