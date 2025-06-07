# LearningBot

LearningBot is a personalized learning platform for programming in a chatbot-style interface, built with Spring Boot and React.js frameworks.

## Getting started (Local Setup)
This will cover a step-by-step guideline to run the app locally.

### Prerequisites
- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Node.js + npm](https://nodejs.org/en/download)
- [Maven](https://maven.apache.org/install.html)
- *PostgreSQL* (Make sure your PostgreSQL server is running and a database is created.)
- [Azure OpenAI](https://azure.microsoft.com/en-us/products/ai-services/openai-service) (You will need to create a resource and deployment for gpt-4.1-mini)

### Start Backend Server
1. Set up environment variable by going to `.env` file and configure your credentials for PostgreSQL Database and [Azure OpenAI](https://azure.microsoft.com/en-us/products/ai-services/openai-service). (The Azure OpenAI credentials can be found in *Resource Management* > *Key and Endpoints* in your resource page.)
2. Navigate to the backend directory

```bash
cd backend
```

3. Install dependencies and build the project

```bash
mvn clean install
```
4. Start the server
```bash
mvn spring-boot:run
```
The backend should now be running at `http://localhost:8080`

### Start Frontend Website
1. Navigate to the frontend directory
```bash
cd frontend
```

2. Install dependencies
```bash
npm install
```

3. Start the development server
```bash
npm start
```
The frontend should now be running at `http://localhost:3000`

## Note
During the current development, all the lesson history will be deleted after terminating the backend server. 