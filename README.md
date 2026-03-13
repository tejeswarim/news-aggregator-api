# News Aggregator API

A RESTful API built with Spring Boot that aggregates news from multiple sources based on user preferences, featuring JWT authentication, caching, and article management.

## Features

### Core Features
- User registration and login with JWT authentication
- News preference management (categories: business, technology, sports, entertainment, health, science)
- Fetch news articles based on user preferences
- In-memory H2 database for data storage

### Optional Features Implemented
- Caching mechanism using Caffeine to reduce external API calls
- Mark articles as "read" or "favorite"
- Search news articles by keywords
- Background job to refresh cached news every hour
- Comprehensive exception handling and input validation

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **WebClient** for async HTTP requests
- **Caffeine Cache**
- **Lombok**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- NewsAPI key (get it from https://newsapi.org/)

## Setup Instructions

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd news-aggregator-api
```

### 2. Configure API Key

Edit `src/main/resources/application.properties` and replace `YOUR_API_KEY_HERE` with your actual NewsAPI key:

```properties
news.api.key=your_actual_api_key_here
```

### 3. Build the project

```bash
mvn clean install
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

#### Register a new user
```http
POST /api/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

#### Login
```http
POST /api/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe"
}
```

### Preferences Management

#### Get user preferences
```http
GET /api/preferences
Authorization: Bearer <token>
```

**Response:**
```json
["technology", "business", "sports"]
```

#### Update user preferences
```http
PUT /api/preferences
Authorization: Bearer <token>
Content-Type: application/json

{
  "preferences": ["technology", "business", "sports"]
}
```

**Available categories:** business, technology, sports, entertainment, health, science

### News Articles

#### Get news based on preferences
```http
GET /api/news
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": "uuid",
    "title": "Article Title",
    "description": "Article description",
    "url": "https://...",
    "urlToImage": "https://...",
    "publishedAt": "2024-01-01T12:00:00Z",
    "source": "Source Name",
    "author": "Author Name",
    "content": "Article content...",
    "read": false,
    "favorite": false
  }
]
```

#### Mark article as read
```http
POST /api/news/{articleId}/read
Authorization: Bearer <token>
```

#### Mark article as favorite
```http
POST /api/news/{articleId}/favorite
Authorization: Bearer <token>
```

#### Get all read articles
```http
GET /api/news/read
Authorization: Bearer <token>
```

#### Get all favorite articles
```http
GET /api/news/favorites
Authorization: Bearer <token>
```

#### Search news by keyword
```http
GET /api/news/search/{keyword}
Authorization: Bearer <token>
```

## Testing with Postman

1. **Register a user**: Send POST request to `/api/register`
2. **Copy the token** from the response
3. **Set Authorization header**: For all subsequent requests, add header:
   ```
   Authorization: Bearer <your-token>
   ```
4. **Update preferences**: PUT request to `/api/preferences`
5. **Fetch news**: GET request to `/api/news`

## Testing with cURL

### Register
```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

### Update Preferences
```bash
curl -X PUT http://localhost:8080/api/preferences \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"preferences":["technology","business"]}'
```

### Get News
```bash
curl -X GET http://localhost:8080/api/news \
  -H "Authorization: Bearer <token>"
```

## Database Console

Access H2 console at: `http://localhost:8080/h2-console`

- **JDBC URL**: `jdbc:h2:mem:newsdb`
- **Username**: `sa`
- **Password**: (leave empty)

## Project Structure

```
src/main/java/com/newsaggregator/
├── config/              # Configuration classes
│   ├── SecurityConfig.java
│   └── WebClientConfig.java
├── controller/          # REST controllers
│   ├── AuthController.java
│   ├── NewsController.java
│   └── PreferencesController.java
├── dto/                 # Data Transfer Objects
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── NewsArticle.java
│   ├── PreferencesRequest.java
│   └── RegisterRequest.java
├── exception/           # Exception handling
│   └── GlobalExceptionHandler.java
├── model/               # JPA entities
│   ├── User.java
│   └── UserArticle.java
├── repository/          # Data repositories
│   ├── UserArticleRepository.java
│   └── UserRepository.java
├── security/            # Security components
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
├── service/             # Business logic
│   ├── AuthService.java
│   ├── NewsService.java
│   ├── PreferencesService.java
│   └── UserDetailsServiceImpl.java
└── NewsAggregatorApplication.java
```

## Error Handling

The API provides comprehensive error handling:

- **400 Bad Request**: Invalid input or validation errors
- **401 Unauthorized**: Invalid credentials or missing/invalid token
- **404 Not Found**: User or resource not found
- **500 Internal Server Error**: Unexpected errors

## Caching

- News articles are cached for 1 hour to reduce API calls
- Cache is automatically refreshed every hour in the background
- Maximum cache size: 500 entries

## Security

- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours
- Stateless session management
- CSRF protection disabled for REST API

## Notes

- The free tier of NewsAPI has a limit of 100 requests/day
- Articles are fetched from NewsAPI's top-headlines and everything endpoints
- The application uses an in-memory database, so data is lost on restart

## Future Enhancements

- Persistent database (PostgreSQL/MySQL)
- Pagination for news articles
- User roles and permissions
- Email notifications for new articles
- Article recommendations based on reading history
- Social sharing features

## License

This project is created for educational purposes.
News Aggregator API
