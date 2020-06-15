from imageai.Detection.Custom import CustomObjectDetection, CustomVideoObjectDetection
import face_recognition
import numpy as np
from cv2 import cv2
import os
import sys
import json
import base64

class FaceRecognition:
    def __init__(self):
        known_path = '/home/swc/spark-2.4.5-bin-hadoop2.7/CCTV-pyspark/images/' # directory path of know_faces
        image_format = 'jpg'

        known_list = os.listdir(known_path)
        self.known_faces = []

        # Load & encode all images from known_path
        for f in known_list : 
            if f.split('.')[-1] != image_format : continue
            known_img = face_recognition.load_image_file(known_path+f)
            known_img_encoding = face_recognition.face_encodings(known_img)[0]
            self.known_faces.append(known_img_encoding)

    def FaceRecognition(self, decoded_img):
        #encoding frame
        try :
            unknown_face_encoding = face_recognition.face_encodings(decoded_img)[0]
            # results is an array of True/False telling if the unknown face matched anyone in the known_faces array
            # 아는 얼굴이면 False, 모르는 얼굴이면 True
            results = face_recognition.compare_faces(self.known_faces, unknown_face_encoding)
            return not True in results
        except IndexError:
            # print("얼굴없음")
            return False
