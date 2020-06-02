
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import processor.FaceRecognitionMap;
import processor.ReadStreamJsonParser;
import schema.DetectionResult;

public class VistApplication {

    public static void main(String[] args) {
        StructType detectionResultStructType = Encoders.bean(DetectionResult.class).schema();
        ExpressionEncoder<Row> encoder = RowEncoder.apply(detectionResultStructType);

        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("CCTV-stream-App")
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
        Dataset<Row> detectedDf = resultDf.filter("fireDetected=true OR unknownDetected=true");
        detectedDf.show();
    }
}