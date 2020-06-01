package processor;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.StructType;
import schema.JsonData;

public class JsonParser {
    public static Dataset<Row> jsonParser(Dataset<Row> df){
        StructType jsonDataStructType = Encoders.bean(JsonData.class).schema();
        return  df.selectExpr("CAST(value AS STRING) as value")
                .select(functions.from_json(functions.col("value"), jsonDataStructType).alias("jsonData"))
                .select("jsonData.*");
    }
}
