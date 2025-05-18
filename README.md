# AI Devs 3 Spring AI

This Spring Boot application is the playground for excersises from AI DEVS 3 (aidevs.pl) course.

## Technical Stack

- Java 21
- Spring Boot
- Spring AI

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
```

2. Navigate to the project directory:
```bash
cd ai-devs-3-spring-ai
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

## Development

The project follows standard Spring Boot project structure:
```
src/
├── main/
│   ├── java/
│   │   └── altocumulus/
│   │       └── aidevs3/
│   │           └── S01e02Controller.java
│   └── resources/
│       ├── static/
│       └── templates/
```

## Testing

To run the tests:
```bash
./mvnw test
```

## S01e02Controller

The goals of this exercise are to:
- Train LLM with instructions provided in the course
- Communicate with API (https://xyz.ag3nts.org/verify)
- Read the question
- Pass the question to LLM
- Read the LLM answer
- Pass the answer back to API
- Continue until `{{FLG:...}}` is returned

### Usage
Send a GET request to:
```
http://localhost:8080/s01e02/api/flag
```

## S01e03Controller

Key learning points:
- Understanding that not all data processing requires LLM involvement
- Some tasks are better handled by traditional application logic (e.g., mathematical calculations)
- Importance of optimizing LLM usage due to context window limitations
- Hybrid approach: using both application logic and LLM where appropriate

The goals of this exercise are to:
- Read and validate JSON file with test data
- Validate mathematical equations (e.g., "22 + 84 = 106")
- Process optional test cases using LLM
- Send validated data to API
- Handle API responses
- Return flag when successful

The JSON structure includes:
```json
{
    "apikey": "your-api-key",
    "description": "task description",
    "copyright": "copyright info",
    "test-data": [
        {
            "question": "22 + 84",
            "answer": 106,
            "test": {
                "q": "question for LLM",
                "a": "LLM answer"
            }
        }
    ]
}
```

### Usage
Send a GET request to:
```
http://localhost:8080/s01e03/api/flag
```

The controller will:
1. Read input JSON from `resources/static/s01e03controller/input.json`
2. Validate mathematical equations
3. Process any test questions through LLM
4. Submit validated data to API endpoint
5. Return the flag or error message

### Implementation Highlights
- Mathematical validations performed by application logic
- LLM used only for specific test cases requiring natural language processing
- Efficient resource usage by avoiding unnecessary LLM calls
- Reduced costs and improved performance through selective LLM usage