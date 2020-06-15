import face
import fire

class Process:
    def __init__(self):
        self.fire_module = fire.FireDetection()
        self.face_module = face.FaceRecognition()
        
    def ProcessImage(self, data):
        encoded_image = data

        #base64 to image(uint8) decoding
        img64_decode = base64.b64decode(encoded_image)
        im_arr = np.frombuffer(img64_decode, dtype=np.uint8)
        decoded_img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

        face = self.face_module.FaceRecognition(decoded_img) #True면 침입자
        fire = self.fire_module.FireDetection(decoded_img) #True면 화재발생

        result= {"unknown_person" : face, "fire_broken" : fire}
        print(result)
        return result
