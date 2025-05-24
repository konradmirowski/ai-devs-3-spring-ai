# AI Devs 3 - Spring AI

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

## ChatController

A simple REST controller that provides an interface to Spring AI's ChatClient SDK.

### Features
- Direct access to LLM through Spring AI's ChatClient
- Stateless message handling
- Simple POST endpoint for sending prompts

### Usage
Send a POST request to:
```bash
curl -X POST \
  http://localhost:8080/chat/sendMessage \
  -H 'Content-Type: text/plain' \
  -d 'Your prompt here'
```

### Technical Details
- Uses Spring AI's ChatClient.Builder for LLM integration
- Forwards raw prompts without additional processing
- Returns LLM responses as plain text
- No conversation history management (stateless)

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

## S01e05Service - Text Censorship Service

This service is responsible for censoring personal information in text using OpenAI's GPT model. It follows specific censorship rules to protect sensitive data.

### Features:
- Censors personal information including:
  - Full names
  - City names
  - Street addresses (with special handling for "ul." prefix)
  - Age information
- Uses the word "CENZURA" for all censored content
- Preserves text structure while applying censorship

### Example:
Input:
```
Podejrzany: Krzysztof Kwiatkowski. Mieszka w Szczecinie przy ul. Różanej 12. Ma 31 lat.
```

Output:
```
Podejrzany: CENZURA. Mieszka w CENZURA przy ul. CENZURA. Ma CENZURA lat.
```
### Usage
Send a GET request to:
```
http://localhost:8080/s01e03/api/flag
```


## Prompts

### S01e04 - Robot Pathfinding
Located in `src/main/resources/static/s01e04prompt/prompt.yaml`

This prompt was created with Google Gemini to define a precise robot pathfinding task, specifically optimized for GPT-4-mini's limitations:

- **Model Constraints**:
  - Uses GPT-4-mini which has limited capabilities compared to larger models
  - Requires more explicit instructions and constraints
  - Less reliable at complex reasoning tasks
  - Needs structured output format to ensure consistent responses
  - Cannot guarantee 100% deterministic pathfinding in a single turn
  - May require multiple attempts or fallback strategies

**Key Features**:
- Structured YAML format optimized for limited model capabilities
- Simple, clear movement rules
- Explicit output requirements in JSON format
- Step-by-step instructions to guide limited reasoning
- Built-in validation rules for response consistency

The prompt demonstrates how to:
- Structure AI instructions for less capable models
- Define clear input/output formats for reliable responses
- Include explicit validation requirements
- Break down complex tasks into manageable steps
- Enforce specific response formats to prevent hallucination
- Acknowledge and work around model limitations
- Plan for potential inconsistencies in responses
- Implement fallback strategies for unreliable outputs
