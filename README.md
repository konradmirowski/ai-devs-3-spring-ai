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
