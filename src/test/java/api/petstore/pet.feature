Feature: PetStore /pet endpoints CRUD flow

  Background:
    * def contracts = call read('classpath:api/helpers/pet/petContract.feature')
    * def petShape = contracts.petShape
    * def petId = 1000000 + Math.floor(Math.random() * 1000000)
    * def originalName = 'Firulais'
    * def updatedName = 'Firulais (sold)'
    * def basePet = defaultPet(petId, originalName, 'available')

  Scenario: Add a pet, find it by id, update its name and status, find by status

    # 1. POST /pet -- add a new pet to the store
    * def created = call read('classpath:api/helpers/pet/createPet.feature') basePet
    And match created.response contains petShape
    And match created.response.id == petId
    And match created.response.name == originalName
    And match created.response.status == 'available'

    # 2. GET /pet/{id} -- consult the newly created pet by ID
    * def found = call read('classpath:api/helpers/pet/getPetById.feature') { id: '#(petId)' }
    And match found.response contains petShape
    And match found.response.id == petId
    And match found.response.name == originalName
    And match found.response.status == 'available'

    # 3. PUT /pet -- update name and set status to "sold"
    * def soldPet = defaultPet(petId, updatedName, 'sold')
    * def updated = call read('classpath:api/helpers/pet/updatePet.feature') soldPet
    And match updated.response contains petShape
    And match updated.response.id == petId
    And match updated.response.name == updatedName
    And match updated.response.status == 'sold'

    # 4. GET /pet/findByStatus -- find pets with status "sold" and verify our pet is there
    * def search = call read('classpath:api/helpers/pet/findByStatus.feature') { status: 'sold' }
    And match search.response == '#[_ > 0]'
    # NOTE: we don't validate the contract on every pet in the array because
    # PetStore is a public shared sandbox with dirty data from other users
    # (some pets missing 'category', some with garbage 'name', etc.).
    # We can only validate the contract on the pet we created ourselves.
    * def myPet = karate.filter(search.response, function(p) { return p.id == petId })
    And match myPet[0] contains petShape
    And match myPet[0].name == updatedName
    And match myPet[0].status == 'sold'
