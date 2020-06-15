from imageai.Detection.Custom import CustomObjectDetection, CustomVideoObjectDetection
import numpy as np
from cv2 import cv2
import os
import sys
import json

class FireDetection:
    def __init__(self):

        self.execution_path = "/home/swc/spark-2.4.5-bin-hadoop2.7/CCTV-pyspark/"

        self.detector = CustomObjectDetection()
        self.detector.setModelTypeAsYOLOv3()
        self.detector.setModelPath(detection_model_path=os.path.join(self.execution_path, "detection_model-ex-33--loss-4.97.h5"))
        self.detector.setJsonPath(configuration_json=os.path.join(self.execution_path, "detection_config.json"))
        self.detector.loadModel()

    def FireDetection(self, decoded_img):
        detections = self.detector.detectObjectsFromImage(input_image=decoded_img, input_type="array",
                                                    output_image_path=os.path.join(self.execution_path, "fire_detected.jpg"),
                                                    minimum_percentage_probability=40)

        if len(detections) == 0 : fire_broken = False
        else : fire_broken = True
        
        return fire_broken