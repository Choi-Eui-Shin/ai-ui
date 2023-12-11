"""
Flask-API==3.0.post1
Flask-API==3.1
Flask-Compress==1.14
"""
from flask import request
from flask import Flask
from ultralytics import YOLO
import io
from PIL import Image
import uuid

"""
YOLOv8 모델을 이용한 이미지 예측
:return:
"""
# 자신의 모델 파일 경로에서 모델을 불러옵니다.
model = YOLO('./runs/detect/train/weights/best.pt')

app = Flask(__name__)

@app.route("/")
def alive():
    return "YOLOv8을 이용한 UI 요소 추출"


def byte2Pil(image_data):
    """
    지정된 바이트 배열을 PIL Image로 변환하여 반환
    :param image_data: 이미지 바이트 클래스 # <class 'bytes'>
    :return:
    """
    data_io = io.BytesIO(image_data)
    return Image.open(data_io)


"""
END-POINT 추가
"""
@app.route("/v1/yolov8", methods=['POST'])
def yolov8():
    # 바이트 배열을 PIL Image로 변환
    image = byte2Pil(request.data)

    # 이미지를 예측합니다.
    results = model(image)

    res_json = {}
    if len(results) > 0:
        names_dic = results[0].names
        box_obj = results[0].boxes

        xyxy_list = box_obj.xyxy.int().tolist()
        cls_list = box_obj.cls.int().tolist()
        conf_list = box_obj.conf.tolist()

        obj_list = []
        for i in range(len(cls_list)):
            elm = {
                'rect': {
                    'x': xyxy_list[i][0],
                    'y': xyxy_list[i][1],
                    'width': xyxy_list[i][2]-xyxy_list[i][0],
                    'height': xyxy_list[i][3]-xyxy_list[i][1],
                    # xyxy_list[i],
                },
                'classId': names_dic[cls_list[i]],
                'confidence': conf_list[i],
                'uid': uuid.uuid4().hex
            }
            obj_list.append(elm)

        res_json['returnCode'] = True
        res_json['result'] = obj_list
    else:
        res_json['returnCode'] = False
        res_json['returnMessage'] = "EMPTY"

    return res_json
    # , status.HTTP_200_OK, {"Content-Type": "application/json; charset=utf-8", "Access-Control-Allow-Origin": "*"}


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=9999)
