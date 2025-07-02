Feature: SIM Card Activation

  Scenario: Successfully activate a SIM card
    Given the SIM card ICCID is "1255789453849037777" and email is "success@example.com"
    When I send an activation request
    Then the SIM card should be marked as activated in the system with ID 1

  Scenario: Fail to activate a SIM card
    Given the SIM card ICCID is "8944500102198304826" and email is "fail@example.com"
    When I send an activation request
    Then the SIM card should be marked as not activated in the system with ID 2
