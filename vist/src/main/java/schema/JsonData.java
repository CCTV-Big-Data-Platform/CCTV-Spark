package schema;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JsonData {
    String data;
    String userId;
    String timestamp;
}
