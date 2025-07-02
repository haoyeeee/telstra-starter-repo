package au.com.telstra.simcardactivator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/sim")
public class SimController {

    private static final Logger logger = LoggerFactory.getLogger(SimController.class);
    private final RestTemplate restTemplate;
    private final SimActivationRecordRepository repository;

    @Autowired
    public SimController(SimActivationRecordRepository repository) {
        this.restTemplate = new RestTemplate();
        this.repository = repository;
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody SimActivationRequest request) {
        Map<String, String> payload = new HashMap<>();
        payload.put("iccid", request.getIccid());

        try {
            ResponseEntity<ActuatorResponse> response = restTemplate.postForEntity(
                    "http://localhost:8444/actuate",
                    payload,
                    ActuatorResponse.class
            );

            ActuatorResponse body = response.getBody();
            boolean success = false;

            if (body != null) {
                success = body.isSuccess();
                logger.info("SIM activation success: {}", success);
            } else {
                logger.warn("Response body from actuator was null");
            }

            SimActivationRecord simRecord = new SimActivationRecord(
                    request.getIccid(),
                    request.getCustomerEmail(),
                    success
            );
            repository.save(simRecord);

            return ResponseEntity.ok("Activation result: " + success);
        } catch (Exception e) {
            logger.error("Error contacting actuator", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error contacting actuator.");
        }
    }

    @GetMapping("/query")
    public ResponseEntity<SimActivationRecord> getSimRecord(@RequestParam Long simCardId) {
        return repository.findById(simCardId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
