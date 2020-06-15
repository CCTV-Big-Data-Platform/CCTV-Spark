import findspark
findspark.init()
from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StringType
from pyspark.sql.functions import from_json
import logging
import requests
import process

def process_row(row):
    # video processing
    # result is json type data including detection type as key and whether detected as value
    result = p.ProcessImage(row.data)

    for detection_type in result:
        if(result[detection_type]==True):
            # if detected is true, call notification api
            detected_api_param = {'data': row.data, 'userId':row.userId, 'timestamp':row.timestamp, 'detectionType':detection_type}
            logger.warning("### notification api request")
            r = requests.post("http://victoria.khunet.net:5900/notificate", data=detected_api_param)

def run_spark(spark):
    # define Struct type for json data reading from kafka
    json_data_structType = StructType().add("data", StringType()).add("userId", StringType()).add("timestamp", StringType())

    # read stream data from kafka
    # topic name: test4
    # data max size: 20971760
    logger.warning("kafka read")
    df = spark.readStream \
              .format("kafka") \
              .option("kafka.bootstrap.servers", "1.201.142.81:9092") \
              .option("subscribe", "test4") \
              .option("fetch.max.bytes", "20971760") \
              .option("startingOffsets", "latest") \
              .load() \
              .selectExpr("CAST(value AS STRING) as value") \
    
    # parse data as json data
    json_parsed_df = df.select(from_json(df.value, json_data_structType) \
                    .alias("jsonData")) \
                    .select("jsonData.*")

    # apply video process for each frame data
    # video process: fire detection & unknown face recognition -> pluggable
    query = json_parsed_df.writeStream \
                .foreach(process_row) \
                .start()
    query.awaitTermination()

if __name__ == "__main__":
    logger = logging.getLogger(__name__)

    # Spark Session
    spark = SparkSession \
                .builder \
                .appName("CCTV-pyspark") \
                .getOrCreate()

    # set Log Level as Warn
    spark.sparkContext.setLogLevel("WARN")
    
    logger.warning("Spark started")

    # declare video process class
    global p
    p = process.Process()

    #run spark
    run_spark(spark)

    