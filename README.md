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