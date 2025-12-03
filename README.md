# 🏴‍☠️ Pirate's Movie Treasure Chest - Spring Boot Demo Application

Ahoy matey! Welcome to the most swashbuckling movie catalog web application on the seven seas! Built with Spring Boot and featuring a treasure chest of movie search capabilities with authentic pirate language.

## ⚓ Features

- **🎬 Movie Treasure Catalog**: Browse 12 classic movie treasures with detailed information
- **🔍 Advanced Movie Search**: Hunt for specific movie treasures by name, ID, or genre
- **📋 Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **🌊 Responsive Design**: Mobile-first design that works on all devices like a ship in any weather
- **🌙 Modern Pirate UI**: Dark theme with gradient backgrounds and smooth animations
- **🗺️ REST API**: JSON treasure maps for fellow pirates to access movie data programmatically

## 🛠️ Technology Stack

- **Java 8** - The sturdy ship's hull
- **Spring Boot 2.7.18** - The reliable navigation system
- **Maven** - For managing our treasure dependencies
- **Thymeleaf** - For crafting beautiful HTML treasure maps
- **Log4j 2** - For keeping a proper ship's log
- **JUnit 5.8.2** - For testing our treasure hunting capabilities

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher (ye need a proper compass!)
- Maven 3.6+ (for managing yer treasure)

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080` - ready to set sail!

### Access the Treasure Chest

- **🏴‍☠️ Movie Treasure List**: http://localhost:8080/movies
- **🔍 Search for Treasures**: http://localhost:8080/movies?name=treasure&genre=Adventure
- **📖 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🗺️ API Treasure Map**: http://localhost:8080/movies/search?name=hero

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Movie treasure hunting service
│   │       │   ├── Movie.java                # Movie treasure model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie treasure data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── templates/
│       │   ├── movies.html                   # Main treasure chest page with search
│       │   └── movie-details.html            # Individual treasure details
│       └── static/css/
│           └── movies.css                    # Pirate-themed styling
└── test/                                     # Unit tests for treasure hunting
```

## 🗺️ API Endpoints

### 🏴‍☠️ Get All Movies (with Search)
```
GET /movies
```
Returns an HTML page displaying movies. Supports search parameters for treasure hunting!

**Query Parameters:**
- `name` (optional): Search by movie name (partial match, case-insensitive)
- `id` (optional): Search by specific movie ID
- `genre` (optional): Filter by genre (partial match, case-insensitive)

**Examples:**
```
http://localhost:8080/movies                           # All treasures
http://localhost:8080/movies?name=hero                 # Movies with "hero" in name
http://localhost:8080/movies?genre=Action              # Action movies
http://localhost:8080/movies?name=prison&genre=Drama   # Combined search
http://localhost:8080/movies?id=5                      # Specific movie by ID
```

### 🔍 Movie Search API (JSON Response)
```
GET /movies/search
```
Returns JSON treasure map with search results - perfect for fellow pirates building their own applications!

**Query Parameters:**
- `name` (optional): Search by movie name (partial match, case-insensitive)
- `id` (optional): Search by specific movie ID  
- `genre` (optional): Filter by genre (partial match, case-insensitive)

**Response Format:**
```json
{
  "success": true,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0,
      "icon": "🎬"
    }
  ],
  "totalResults": 1,
  "message": "Ahoy! Found 1 movie treasure for ye!",
  "searchCriteria": {
    "name": "prison",
    "id": "",
    "genre": ""
  }
}
```

**Examples:**
```bash
# Search by name
curl "http://localhost:8080/movies/search?name=hero"

# Search by genre
curl "http://localhost:8080/movies/search?genre=Action"

# Search by ID
curl "http://localhost:8080/movies/search?id=1"

# Combined search
curl "http://localhost:8080/movies/search?name=the&genre=Drama"
```

**Error Responses:**
```json
{
  "success": false,
  "error": "Arrr! Invalid movie ID, matey! ID must be a positive number."
}
```

### 📖 Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🔍 Search Features

### 🎯 Search Capabilities
- **Name Search**: Find movies by partial name match (case-insensitive)
- **Genre Filtering**: Filter by genre categories
- **ID Lookup**: Direct access to specific movies
- **Combined Search**: Mix and match search criteria
- **Empty Results Handling**: Friendly pirate messages when no treasures are found

### 🏴‍☠️ Pirate Language Integration
- Search results include authentic pirate language and emojis
- Error messages are delivered in proper pirate speak
- Success messages celebrate treasure discoveries
- API responses maintain the pirate theme

### 📱 User Interface
- **Search Form**: Easy-to-use form with name, ID, and genre fields
- **Genre Dropdown**: Pre-populated with available genres
- **Clear Search**: Quick way to reset and view all movies
- **Search Results**: Clear indication of search status and result count
- **Responsive Design**: Works perfectly on mobile devices

## 🧪 Testing

Run the treasure hunting tests:
```bash
mvn test
```

The test suite includes:
- **MovieServiceTest**: Comprehensive tests for search functionality
- **MoviesControllerTest**: Tests for web endpoints and API responses
- **Edge Case Testing**: Invalid parameters, empty results, error handling

## 🚨 Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Check the logs for any scurvy bugs:
```bash
tail -f logs/application.log
```

## 🤝 Contributing

This project welcomes fellow pirates! Feel free to:
- Add more movie treasures to the catalog
- Enhance the search functionality with more filters
- Improve the pirate language and themes
- Add new API endpoints for treasure management
- Enhance the responsive design for better mobile experience

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May yer code be bug-free and yer deployments smooth sailing! 🏴‍☠️*
