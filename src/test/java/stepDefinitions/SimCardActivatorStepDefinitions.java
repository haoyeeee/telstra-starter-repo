package stepDefinitions;

import au.com.telstra.simcardactivator.SimCardActivator;
import io.cucumber.java.en.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = SimCardActivator.class, loader = SpringBootContextLoader.class)
public class SimCardActivatorStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    private String iccid;
    private String customerEmail;
    private long simId = 0;
    private final String BASE_URL = "http://localhost:8080";

    @Given("the SIM card ICCID is {string} and email is {string}")
    public void theSimCardICCIDAndEmail(String iccid, String email) {
        this.iccid = iccid;
        this.customerEmail = email;
    }

    @When("I send an activation request")
    public void iSendAnActivationRequest() {
        Map<String, String> request = new HashMap<>();
        request.put("iccid", iccid);
        request.put("customerEmail", customerEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL + "/sim/activate", entity, String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        simId++;
    }

    @Then("the SIM card should be marked as activated in the system with ID {int}")
    public void simCardShouldBeActivated(int expectedId) {
        ResponseEntity<Map> response = restTemplate.getForEntity(BASE_URL + "/sim/query?simCardId=" + expectedId, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(iccid, response.getBody().get("iccid"));
        Assertions.assertEquals(customerEmail, response.getBody().get("customerEmail"));
        Assertions.assertTrue((Boolean) response.getBody().get("active"));
    }

    @Then("the SIM card should be marked as not activated in the system with ID {int}")
    public void simCardShouldNotBeActivated(int expectedId) {
        ResponseEntity<Map> response = restTemplate.getForEntity(BASE_URL + "/sim/query?simCardId=" + expectedId, Map.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(iccid, response.getBody().get("iccid"));
        Assertions.assertEquals(customerEmail, response.getBody().get("customerEmail"));
        Assertions.assertFalse((Boolean) response.getBody().get("active"));
    }
}
