package processor;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

public class FaceRecognitionMap implements MapFunction<Row, Row> {
    static String tmpPath = "./tmp/temp.json";

    @Override
    public Row call(Row row) throws Exception {
        String data = row.get(0).toString();
        String timestamp = row.get(1).toString();
        String detectedType = new String();
        //String userId;

        Boolean flag = false;
//        String json = null;
//        Gson gson = new Gson();
//
//        // convert row data to json
//        // row[0] = image data, row[1] = timestamp, row[2] = user data
//        JsonObject obj = new JsonObject();
//        obj.addProperty("data", row.get(0).toString());
//        obj.addProperty("timestamp", row.get(1).toString());
//        obj.addProperty("userId", row.get(2).toString());
//        json = gson.toJson(obj);
//
//        // write json file in tmp directory for python module
//        File tmpFile = new File(tmpPath);
//        FileWriter jsonWriter = new FileWriter(tmpPath);
//        jsonWriter.write(json);
//        jsonWriter.flush();
//        jsonWriter.close();

        // execute python module using shell command
        String cmd = "cmd /c python ./python-module/test.py";
        CommandLineExecutor commandLineExecutor = new CommandLineExecutor();
        String isDetected = commandLineExecutor.execute(cmd);

        //FileUtils.deleteQuietly(tmpFile);
       // System.out.println(row.get(0).toString());
        System.out.println(row.get(1).toString());
        // detected process
        if(isDetected.startsWith("True")) {
//            String fileName = row.get(2).toString() + "_" + System.currentTimeMillis() + ".json";
//            String path = "./detected-data/" + fileName;
//
//            // write detected json file in local directory
//            FileWriter detectedJsonWriter = new FileWriter(path);
//            detectedJsonWriter.write(json);
//            detectedJsonWriter.flush();
//            detectedJsonWriter.close();
            flag = true;
            DetectedRequestController.sendPostRequest("userId", timestamp, data, "fire");
        }
        return RowFactory.create(row.get(0), flag, row.get(1));
    }
}
