import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import java.util.Properties;

public class VideoStreamProducer {
    public static void main(String[] args) throws Exception{
                //set producer properties
        Properties prop = PropertyFileReader.readPropertyFile();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", prop.getProperty("kafka.bootstrap.servers"));
        properties.put("acks", prop.getProperty("kafka.acks"));
        properties.put("retries",prop.getProperty("kafka.retries"));
        properties.put("batch.size", prop.getProperty("kafka.batch.size"));
        properties.put("linger.ms", prop.getProperty("kafka.linger.ms"));
        properties.put("compression.type", prop.getProperty("kafka.compression.type"));
        properties.put("max.request.size", prop.getProperty("kafka.max.request.size"));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        // create kafka producer
        Producer<String, String> producer = new KafkaProducer<String, String>(properties);

        VideoDataConverter videoDataConverter = new VideoDataConverter();
        videoDataConverter.videoConvertToEncodedString(prop.getProperty("camera.url"), producer, prop.getProperty("kafka.topic"));
    }
}
