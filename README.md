# AI Devs 3 - Spring AI

A Spring Boot application integrating with OpenAI's GPT models through Spring AI.

## Requirements

- Java 21

## Setup

1. Clone the repository
```bash
git clone https://github.com/konradmirowski/ai-devs-3-spring-ai.git
cd ai-devs-3-spring-ai
```

2. Configure API keys in `application.properties`:
```properties
spring.ai.openai.api-key=your-openai-key
c3ntrala.api.key=your-c3ntrala-key
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── altocumulus/
│   │       └── aidevs3/
│   │           ├── client/
│   │           │   └── C3ntralaClient.java
│   │           ├── config/
│   │           │   └── ApplicationConfig.java
│   │           ├── controller/
│   │           │   ├── MainController.java
│   │           │   └── ChatController.java
│   │           ├── model/
│   │           │   ├── common/
│   │           │   └── exercise/
│   │           └── service/
│   │               ├── chat/
│   │               │   └── ChatService.java
│   │               └── exercise/
│   │                   ├── S01e02Service.java
│   │                   ├── S01e03Service.java
│   │                   └── S01e05Service.java
│   └── resources/
       ├── static/
       │   └── exercise/
       └── application.properties
```

## Available Endpoints

### Chat Endpoint

Send messages to OpenAI's GPT model:

```bash
curl -X POST \
  http://localhost:8080/chat/sendMessage \
  -H 'Content-Type: text/plain' \
  -d 'Your prompt here'
```

### Main Endpoints

#### S01e02 - Robot Communication
```bash
curl -X GET http://localhost:8080/main/s01e02/flag
```

#### S01e03 - JSON Processing
```bash
curl -X GET http://localhost:8080/main/s01e03/flag
```

#### S01e05 - Text Censorship
```bash
curl -X GET http://localhost:8080/main/s01e05/flag
```

## Services

### ChatService

Core service for interacting with OpenAI's GPT models. Supports multiple model configurations and system prompts.

### Main Services

#### S01e02Service
Handles robot communication protocol implementation.

#### S01e03Service
Processes and validates JSON data structures.

#### S01e05Service
Text censorship service that replaces sensitive information with "CENZURA" placeholder:
- Full names
- City names
- Street addresses
- Age information

Example:
```text
Input: 
Podejrzany: Krzysztof Kwiatkowski. Mieszka w Szczecinie przy ul. Różanej 12. Ma 31 lat.

Output:
Podejrzany: CENZURA. Mieszka w CENZURA przy ul. CENZURA. Ma CENZURA lat.
```

## Prompts

### S01e04 - Robot Pathfinding

Located in `src/main/resources/static/s01e04prompt/prompt.yaml`

This prompt was created with Google Gemini to define a precise robot pathfinding task, specifically optimized for GPT-4o-mini's limitations:

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