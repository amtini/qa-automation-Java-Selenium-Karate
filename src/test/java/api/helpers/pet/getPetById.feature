Feature: Get pet by ID helper

  Scenario:
    Given path 'pet', __arg.id
    When method get
    Then status 200
    And match responseHeaders['Content-Type'][0] contains 'application/json'
    And assert responseTime < 3000
