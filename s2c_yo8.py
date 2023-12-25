"""
# 모듈 설치
pip install ultralytics==8.0.223
pip install PyYAML==6.0.1
pip install torch==2.1.0 torchvision==0.16.0 torchaudio==2.1.0 --index-url https://download.pytorch.org/whl/cu118

# 오류 해결
 - YOLO v8 GPU 사용 불가 문제 해결
 - https://blog.deeplink.kr/?p=1893
"""
from ultralytics import YOLO,checks
from PIL import Image

def training():
    """
    커스텀 이미지로 YOLOv8 모델 학습
    :return:
    """
    # 실행 환경 체크
    checks()

    # 베이스 모델 로드
    model = YOLO('yolov8n.pt')

    # 학습
    model.train(data='F:/OUTPUT/data.yaml',
                epochs=100,
                patience=30,
                batch=64,
                imgsz=640,
                device=[0])

    # model.train(data='C:/WorkSpace/0.AI/ai-ui/data.yaml',
    #             epochs=50,
    #             patience=30,
    #             batch=32,
    #             imgsz=640,
    #             device=[0])
    #
    print(model.names)


def prediction():
    """
    YOLOv8 모델을 이용한 이미지 예측
    :return:
    """
    # 자신의 모델 파일 경로에서 모델을 불러옵니다.
    model = YOLO('./runs/detect/train/weights/best.pt')

    """
    # 이미지를 예측합니다.
    results = model.predict(source='./test_image', save=True)
    # 예측 결과를 출력합니다.
    print(results)
    """

    # 이미지를 예측합니다.
    results = model('./test_image/sample.png')
    """
    # Results 객체 
    - orig_img: 원본 이미지를 나타내는 numpy 배열입니다.
    - orig_shape: 원본 이미지의 크기를 나타내는 튜플입니다.
    - boxes: 감지된 객체의 경계 상자를 나타내는 Boxes 객체입니다.
      - cls: 각 객체의 클래스
      - conf: 각 객체의 클래스 신뢰도
      - xyxy: 배열의 각 요소는 검출된 각 객체의 경계를 나타낸다. (left, top, right, bottom)
    - masks: 감지된 객체의 마스크를 나타내는 Masks 객체입니다.
    - probs: 분류 작업에 대한 각 클래스의 확률을 나타내는 Probs 객체입니다.
    - keypoints: 감지된 객체의 키포인트를 나타내는 Keypoints 객체입니다.
    - speed: 전처리, 추론, 후처리 속도(밀리초 단위)를 나타내는 딕셔너리입니다.
    - names: 클래스 이름을 나타내는 딕셔너리입니다.
    - path: 이미지 파일의 경로를 나타내는 문자열입니다.
    - _keys: 비어 있지 않은 속성의 이름을 나타내는 튜플입니다.
    """


    # 예측 결과를 출력합니다.
    names_dic = results[0].names
    box_obj = results[0].boxes

    xyxy_list = box_obj.xyxy.int().tolist()
    cls_list = box_obj.cls.int().tolist()
    conf_list = box_obj.conf.tolist()

    obj_list = []
    for i in range(len(cls_list)):
        elm = {
            'rect': xyxy_list[i],
            'class': names_dic[cls_list[i]],
            'confidence': conf_list[i]
        }

        obj_list.append(elm)

    # print(f'xyxy = {xyxy_list}')
    # print(f'cls = {cls_list}')
    # print(f'conf = {conf_list}')
    print(f'detect = {obj_list}')


if __name__ == '__main__':
    training()
    # prediction()

