from imageai.Detection.Custom import CustomObjectDetection, CustomVideoObjectDetection
import face_recognition
import numpy as np
from cv2 import cv2
import os
import sys
import json
import base64

class Process:
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

        self.execution_path = "/home/swc/spark-2.4.5-bin-hadoop2.7/CCTV-pyspark/"

        self.detector = CustomObjectDetection()
        self.detector.setModelTypeAsYOLOv3()
        # self.detector.setModelPath(detection_model_path=os.path.join(self.execution_path, "detection_model-ex-33--loss-4.97.h5"))
        # self.detector.setJsonPath(configuration_json=os.path.join(self.execution_path, "detection_config.json"))
        self.detector.setModelPath(detection_model_path=os.path.join("/home/swc/spark-2.4.5-bin-hadoop2.7/CCTV-pyspark/", "detection_model-ex-33--loss-4.97.h5"))
        self.detector.setJsonPath(configuration_json=os.path.join("/home/swc/spark-2.4.5-bin-hadoop2.7/CCTV-pyspark/", "detection_config.json"))
        self.detector.loadModel()

    def ProcessImage(self, data):
        encoded_image = data

        #base64 to image(uint8) decoding
        img64_decode = base64.b64decode(encoded_image)
        im_arr = np.frombuffer(img64_decode, dtype=np.uint8)
        decoded_img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

        face = self.FaceRecognition(decoded_img) #True면 침입자
        fire = self.FireDetection(decoded_img) #True면 화재발생

        result= {"unknown_person" : face, "fire_broken" : fire}
        print(result)
        return result

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

    def FireDetection(self, decoded_img):
        detections = self.detector.detectObjectsFromImage(input_image=decoded_img, input_type="array",
                                                    output_image_path=os.path.join(self.execution_path, "fire_detected.jpg"),
                                                    minimum_percentage_probability=40)

        if len(detections) == 0 : fire_broken = False
        else : fire_broken = True
        
        return fire_broken
        '''
        for detection in detections:
            print(detection["name"], " : ", detection["percentage_probability"], " : ", detection["box_points"])
        '''