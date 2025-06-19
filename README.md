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
- docker

### Start Backend Server
1. Set up environment variable by going to `.env` file and configure your credentials for PostgreSQL Database and [Azure OpenAI](https://azure.microsoft.com/en-us/products/ai-services/openai-service). (The Azure OpenAI credentials can be found in *Resource Management* > *Key and Endpoints* in your resource page.)
2. Build the Docker image for C++ execution (only needed once):

```bash
docker pull gcc:12
docker build -t cpp-runner ./docker
```
3. Install dependencies and build the project
- Option A: Run from IntelliJ (Recommended): Click Run on `LearningWebApplication.java`.
- Option B: Run from terminal by following below command lines:

For macOS/Linux:

```bash
cd backend
source .env
mvn spring-boot:run
```
For window, please set environment manually:
```bash
set POSTGRES_DB_URL=your-database-url
set POSTGRES_DB_USERNAME=your-database-username
set POSTGRES_DB_PW=your-database-password
set AZURE_OPENAI_API_KEY=your-azure-openai-resource-api-key
set AZURE_OPENAI_ENDPOINT=your-azure-openai-resource-endpoint
set JWT_SECRET=your-32-character-key

cd backend
mvn spring-boot:run
```

The backend should now be running at `http://localhost:8080`

### Start Frontend Website
Install dependencies and start the server
```bash
cd frontend
npm install
npm start
```

The frontend should now be running at `http://localhost:3000`

## Note
During the current development, all the lesson history will be deleted after terminating the backend server. 