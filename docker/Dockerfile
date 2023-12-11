FROM base-ai

#RUN apt-get update
#RUN apt-get -y install libgl1-mesa-glx
#RUN apt-get -y install libglib2.0-0
#RUN apt-get -y install libxcb-xinerama0

WORKDIR /root

ADD Utils.py .
ADD captcha_model.py .
ADD MyRect.py .
ADD extractor_v4.py .
ADD captcha_service.py .
ADD bmcnn_service.py .

CMD ["python3", "bmcnn_service.py"]