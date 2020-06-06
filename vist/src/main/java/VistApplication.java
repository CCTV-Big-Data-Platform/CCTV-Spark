import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import processor.FaceRecognitionMap;
import processor.ReadStreamJsonParser;
import schema.DetectionResult;

public class VistApplication {

    public static void main(String[] args) throws StreamingQueryException {
        StructType detectionResultStructType = Encoders.bean(DetectionResult.class).schema();
        ExpressionEncoder<Row> encoder = RowEncoder.apply(detectionResultStructType);

        SparkSession spark = SparkSession
                .builder()
                .master("spark://127.0.0.1:9000")
                .appName("CCTV-stream-App")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // load local json data
        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "1.201.142.81:9092")
                .option("subscribe", "test4")
                .option("fetch.max.bytes", "20971760")
                .option("startingOffsets", "latest")
                .load();

        Dataset<Row> resultDf = ReadStreamJsonParser.jsonParser(df)
                .map(new FaceRecognitionMap(), encoder);
        Dataset<Row> detectedDf = resultDf.filter("fireDetected=true OR unknownDetected=true");
        resultDf
                .writeStream()
                //.trigger(Trigger.ProcessingTime(3500))
                .foreachBatch((batchDf, batchId) -> {
                    batchDf.show();
                })
                .start()
                .awaitTermination();
    }
}