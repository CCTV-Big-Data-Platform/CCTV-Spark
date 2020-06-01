import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class PropertyFileReader {
    private static Properties prop = new Properties();
    public static Properties readPropertyFile() throws Exception {
        if(prop.isEmpty()){
            InputStream input = PropertyFileReader.class.getClassLoader().getResourceAsStream("kafka-producer.properties");
            try {
                prop.load(input);
            } catch(FileNotFoundException e){
                throw e;
            }
            catch (IOException error) {
                throw error;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return prop;
    }
}
