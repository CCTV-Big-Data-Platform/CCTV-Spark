import base64
import face_recognition
import numpy as np
from cv2 import cv2
import os
import sys

known_path = './images/' # directory path of know_faces
image_format = 'jpg'

# image to base64 encoding
# you have to set 'encoded_image' to frame code
#with open("obama2.jpg", "rb") as img_file:
#    encoded_image = base64.b64encode(img_file.read())

# encoded_image = command line argument
encoded_image = sys.argv[1]

#base64 to image(uint8) decoding
img64_decode = base64.decodebytes(encoded_image)
im_arr = np.frombuffer(img64_decode, dtype=np.uint8)
decoded_img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

#encoding frame
unknown_face_encoding = face_recognition.face_encodings(decoded_img)[0]

# Load & encode all images from known_path
known_list = os.listdir(known_path)
known_faces = []


for f in known_list : 
    if f.split('.')[-1] != image_format : continue
    known_img = face_recognition.load_image_file(known_path+f)
    known_img_encoding = face_recognition.face_encodings(known_img)[0]
    known_faces.append(known_img_encoding)

# results is an array of True/False telling if the unknown face matched anyone in the known_faces array
results = face_recognition.compare_faces(known_faces, unknown_face_encoding)
print(not True in results)