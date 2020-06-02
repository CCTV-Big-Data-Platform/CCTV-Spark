package schema;

import lombok.*;
import org.apache.arrow.flatbuf.Bool;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {
    String data;
    String timestamp;
    String userId;
    Boolean fireDetected;
    Boolean unknownDetected;
}
