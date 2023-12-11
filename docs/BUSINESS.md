# 개요





# 시스템 구성





# 모델

## 학습 데이터

- roboflow 데이터
- 직접 생성



- [ ] 라벨 데이터 이해, 클래스 및 좌표로 구성된 텍스트 파일에서 좌표가 실수 값인 이유..



## YOLO 데이터 포멧

- 이미지
  - JPEG 또는 PNG 형식의 파일
- 레이블
  - 이미지 파일과 동일한 이름을 사용하고, 확장자만 txt로 바꾼다.
  - 레이블의 각 줄은 다음과 같이 구성
    - class : 객체의 클래스 인스턴스
    - center_x : 객체의 중심 X 좌표
    - center_y : 객체의 중심 Y 좌표
    - width : 객체의 너비
    - height : 객체의 높이
  - 중심 좌표 및 객체의 너비, 높이는 이미지의 너비와 높이로 정규화 한 값이다.



@





## UI 구성 요소

| UI 구성 요소 | 이벤트  | 카테고리          |
| ------------ | ------- | ----------------- |
| button       | click   | web, ios, android |
| checkbox     | click   | web, ios, android |
| div          |         | web               |
| image        |         | web, ios, android |
| label        |         | web, ios, android |
| radiobutton  | click   | web, ios, android |
| select       | click   | web, ios, android |
| textbox      | changed | web, ios, android |
|              |         |                   |
|              |         |                   |
|              |         |                   |



## 중간언어 (Interlanguage)

추출된 UI 요소를 표현하는 것으로, 특정 언어에 종속되지 않고 일반화된 내용을 정의한다. 중간 언어는 최종 코드 생성을 정의한 템플릿과 결합하여 UI 생성을 위한 코드를 생성한다.

| 항목         | 타입   | 설명                            |
| ------------ | ------ | ------------------------------- |
| classId      | String | 객체의 클래스                   |
| confidence   | double | 객체의 신뢰도                   |
| depth        | int    | 객체의 계층구조                 |
| rect         | Object | 객체 경계영역                   |
| x            | int    | x 좌표                          |
| y            | int    | y 좌표                          |
| width        | int    | 폭                              |
| height       | int    | 높이                            |
| uid          | String | UUID                            |
| propertyName | String | UI 요소에 할당한 속성 이름      |
| events       | List   | 코드 생성 시 사용할 이벤트 목록 |





# TODO

- [ ] 라벨 데이터 이해, 클래스 및 좌표로 구성된 텍스트 파일에서 좌표가 실수 값인 이유..





# 참조





