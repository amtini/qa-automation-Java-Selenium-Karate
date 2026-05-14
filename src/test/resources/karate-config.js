function fn() {
    var env = karate.env || 'dev';
    karate.log('karate.env =', env);

    var config = {
        baseUrl: 'https://petstore.swagger.io/v2',

        // Factory for building a pet body. Used by both POST and PUT so the
        // structure of a pet lives in one place. Vary id, name and status as
        // needed for each operation.
        defaultPet: function(petId, name, status) {
            return {
                id: petId,
                name: name,
                category: { id: 1, name: 'dogs' },
                photoUrls: ['http://example.com/firulais.jpg'],
                tags: [{ id: 1, name: 'good-boy' }],
                status: status
            };
        }
    };

    if (env === 'staging') {
        config.baseUrl = 'https://staging.petstore.example.com/v2';
    }
    if (env === 'mock') {
        config.baseUrl = 'http://localhost:8080/v2';
    }
    if (env === 'prod') {
        config.baseUrl = 'https://api.petstore.com/v2';
    }

    karate.configure('url', config.baseUrl);

    return config;
}
