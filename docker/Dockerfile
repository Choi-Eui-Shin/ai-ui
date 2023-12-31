FROM base-aiui

#RUN apt-get update
#RUN apt-get -y install libgl1-mesa-glx
#RUN apt-get -y install libglib2.0-0
#RUN apt-get -y install libxcb-xinerama0

ADD requirements.txt .

RUN pip install -r requirements.txt

WORKDIR /root

ADD yolo_service.py .

CMD ["python3", "yolo_service.py"]