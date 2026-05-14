Feature: Pet contract definitions

  Scenario:
    * def petShape =
    """
    {
      id: '#number',
      name: '#string',
      category: { id: '#number', name: '#string' },
      photoUrls: '#[] #string',
      tags: '#[] #object',
      status: '#regex (available|pending|sold)'
    }
    """
