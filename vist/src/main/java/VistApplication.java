
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import processor.FaceRecognitionMap;
import processor.ReadStreamJsonParser;
import schema.DetectionResult;

import java.io.*;

public class VistApplication {

    public static void main(String[] args) throws IOException {
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
                .option("subscribe", "test2")
                .option("fetch.max.bytes", "20971760")
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> resultDf = ReadStreamJsonParser.jsonParser(df)
                .map(new FaceRecognitionMap(), encoder);
        resultDf.show();
        Dataset<Row> detectedDf = resultDf.filter(resultDf.col("faceDetection").equalTo(true));
        detectedDf.show();
    }
}