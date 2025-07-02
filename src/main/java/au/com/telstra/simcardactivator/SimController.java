package au.com.telstra.simcardactivator;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sim")
public class SimController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody SimActivationRequest request) {
        // 创建 actuator 所需的 payload
        Map<String, String> payload = new HashMap<>();
        payload.put("iccid", request.getIccid());

        try {
            ResponseEntity<ActuatorResponse> response = restTemplate.postForEntity(
                    "http://localhost:8444/actuate",
                    payload,
                    ActuatorResponse.class
            );

            boolean success = response.getBody() != null && response.getBody().isSuccess();
            System.out.println("SIM activation success: " + success);

            return ResponseEntity.ok("Activation result: " + success);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error contacting actuator.");
        }
    }
}
