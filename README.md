# 개요
YOLOv8 모델을 이용하여 손으로 그린 스케치 UI를 중간 언어 형태로 변환한다. 생성된 중간언어는 목표 시스템을 정의한 Target Template(이하 TT) 데이터와 결합하여 최종 코드를 생성한다.











# YOLO 클래스

```python
# 자신의 모델 파일 경로에서 모델을 불러옵니다.
model = YOLO('./runs/detect/train/weights/best.pt')

# 이미지를 예측합니다.
results = model('./test_image/sample.png')

# 예측 결과를 출력합니다.
boxObj = results[0].boxes

print(f'xyxy = {boxObj.xyxy.tolist()}')
print(f'cls = {boxObj.cls.tolist()}')
print(f'conf = {boxObj.conf.tolist()}')
```



## Results 객체
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
