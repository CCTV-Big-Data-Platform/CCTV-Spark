package processor;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import scala.util.parsing.json.JSONArray;

public class FaceRecognitionMap implements MapFunction<Row, Row> {
    @Override
    public Row call(Row row) throws Exception {
        Boolean flag = false;
        String data = row.getAs("data");
        String timestamp = row.getAs("timestamp");
        String userId = row.getAs("userId");

        // detection process
        String responseString = DetectionController.sendPostRequest(data);

        // parse to json
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(responseString);

        Boolean fireFlag = element.getAsJsonObject().get("fire_broken").getAsBoolean();
        Boolean faceFlag = element.getAsJsonObject().get("unknown_person").getAsBoolean();

        // detected process
        if(fireFlag){
            flag = true;
            DetectedRequestController.sendPostRequest(userId, timestamp, "fire_broken");
        }
        if(faceFlag){
            flag = true;
            DetectedRequestController.sendPostRequest(userId, timestamp, "unknown_person");
        }

        return RowFactory.create(data, fireFlag, timestamp, faceFlag, userId);
    }
}
