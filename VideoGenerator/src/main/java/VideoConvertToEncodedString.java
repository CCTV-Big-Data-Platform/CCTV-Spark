import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.*;
import java.sql.Timestamp;

public class VideoConvertToEncodedString {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary("opencv_ffmpeg320_64");
    }
    public static void main(String[] args) throws IOException {
        String path = "./sample-video/sample.mp4";
        String tmpPath = "./tmpFrame/temp.jpg";

        Mat frame = new Mat();
        VideoCapture videoCapture = new VideoCapture();
        videoCapture.open(path);

        if(videoCapture.isOpened()){
            int video_length = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
            int frames_per_second = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
            int frame_number = (int) videoCapture.get(Videoio.CAP_PROP_POS_FRAMES);

            int cnt=0;
            String json = null;
            Gson gson = new Gson();
            while(videoCapture.read(frame)){
                cnt++;

                // img processing
                byte[] frameBuffer = new byte[(int)(frame.total()*frame.channels())];
                frame.get(0,0,frameBuffer);

                //save image file in tmpPath
                Imgcodecs.imwrite(tmpPath, frame);
                File tmpFile = new File(tmpPath);

                //testMethod(cnt, tmpFile);
                String encodedString = encodingMethod(cnt, tmpFile);

                FileUtils.deleteQuietly(tmpFile);

                // write original image file for test
                //Imgcodecs.imwrite("./img1/" + cnt +".jpg", frame);

                String timestamp = new Timestamp(System.currentTimeMillis()).toString();
                JsonObject obj = new JsonObject();
                obj.addProperty("timestamp", timestamp);
                obj.addProperty("data", encodedString);
                json = gson.toJson(obj);

                FileWriter jsonWriter = new FileWriter("./data-log/" + cnt + ".json");
                jsonWriter.write(json);
                jsonWriter.flush();
                jsonWriter.close();
            }
            frame.release();
            videoCapture.release();
        }
        else {
            System.out.println("videoCapture not opened");
        }
    }

    private static String encodingMethod(int cnt, File tmpFile) {
        try{
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(tmpFile);
            byte imageData[] = new byte[(int) tmpFile.length()];
            imageInFile.read(imageData);

            // Converting Image byte array into Base64 String
            String imageDataString = encodeImage(imageData);

            imageInFile.close();

            //System.out.println("Image Successfully Manipulated!");
            return imageDataString;
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
            return "FileNotFoundError";
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
            return "IOException";
        }
    }

    /**
     * convert each Frame image data to byte array data
     * and save each byte array data as image data for testing
     */
    private static void testMethod(int cnt, File tmpFile){
        try{
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(tmpFile);
            byte imageData[] = new byte[(int) tmpFile.length()];
            imageInFile.read(imageData);

            // Converting Image byte array into Base64 String
            String imageDataString = encodeImage(imageData);

            // Converting a Base64 String into Image byte array
            byte[] imageByteArray = decodeImage(imageDataString);

            // Write a image byte array into file system
            FileOutputStream imageOutFile = new FileOutputStream(
                    "./img2/" + cnt +".jpg");

            imageOutFile.write(imageByteArray);

            imageInFile.close();
            imageOutFile.close();

            System.out.println("Image Successfully Manipulated!");
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
    }

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(imageDataString);
    }
}
