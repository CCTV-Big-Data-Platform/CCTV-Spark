
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import processor.FaceRecognitionMap;
import properties.DetectionResult;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;

public class VistApplication {

    public static void main(String[] args) {
        StructType detectionResultStructType = Encoders.bean(DetectionResult.class).schema();
        ExpressionEncoder<Row> encoder = RowEncoder.apply(detectionResultStructType);

        SparkSession spark = SparkSession
                .builder()
                .master("local[2]")
                .appName("localTestApp")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // load local json data
        Dataset<Row> df = spark
                .read()
                .format("kafka")
                .option("kafka.bootstrap.servers", "1.201.142.81:9092")
                .option("subscribe", "CCTV-stream")
                .option("kafka.max.request.size", "5242880")
                .load();

        Dataset<Row> df2 = df.map(new FaceRecognitionMap(),encoder);

        df2.foreach(row -> {

        });
    }
}