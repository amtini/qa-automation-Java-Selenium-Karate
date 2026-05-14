Feature: Update pet helper

  Scenario:
    Given path 'pet'
    And request __arg
    When method put
    Then status 200
    And match responseHeaders['Content-Type'][0] contains 'application/json'
    And assert responseTime < 3000
