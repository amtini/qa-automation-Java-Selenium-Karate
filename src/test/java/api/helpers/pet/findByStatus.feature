Feature: Find pets by status helper

  Scenario:
    Given path 'pet', 'findByStatus'
    And param status = __arg.status
    When method get
    Then status 200
    And match responseHeaders['Content-Type'][0] contains 'application/json'
    And assert responseTime < 3000
