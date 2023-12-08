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
    model.train(data='C:/WorkSpace/0.AI/ai-ui/data.yaml',
                epochs=50,
                patience=30,
                batch=32,
                imgsz=640,
                device=[0])

    print(model.names)


def prediction():
    """
    YOLOv8 모델을 이용한 이미지 예측
    :return:
    """
    # 자신의 모델 파일 경로에서 모델을 불러옵니다.
    model = YOLO('./runs/detect/train/weights/best.pt')
    # 이미지를 예측합니다.
    results = model.predict(source='./test_image', save=True)

    # 예측 결과를 출력합니다.
    print(results)


if __name__ == '__main__':
    # training()
    prediction()

