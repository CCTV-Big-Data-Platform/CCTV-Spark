package properties;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {
    String data;
    String timestamp;
    Boolean isDetected;
}
