package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Test class for the MoviesController treasure hunting endpoints!
 * These tests ensure our movie search API be working like a well-oiled ship, matey!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with search functionality
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Hero", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Gold", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId() == id).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                if (id != null && id > 0) {
                    return getMovieById(id).map(Arrays::asList).orElse(new ArrayList<>());
                }
                
                return testMovies.stream()
                    .filter(movie -> name == null || name.trim().isEmpty() || 
                            movie.getMovieName().toLowerCase().contains(name.toLowerCase().trim()))
                    .filter(movie -> genre == null || genre.trim().isEmpty() || 
                            movie.getGenre().toLowerCase().contains(genre.toLowerCase().trim()))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Comedy", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Should return movies page without search parameters")
    public void testGetMoviesWithoutSearch() {
        String result = moviesController.getMovies(model, null, null, null);
        
        assertNotNull(result, "Result should not be null");
        assertEquals("movies", result, "Should return movies template");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies, "Movies should be in model");
        assertEquals(3, movies.size(), "Should return all movies when no search criteria");
        assertFalse((Boolean) model.getAttribute("searchPerformed"), "Search should not be marked as performed");
    }

    @Test
    @DisplayName("Should search movies by name")
    public void testGetMoviesWithNameSearch() {
        String result = moviesController.getMovies(model, "Action", null, null);
        
        assertEquals("movies", result, "Should return movies template");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies, "Movies should be in model");
        assertEquals(1, movies.size(), "Should find one movie with 'Action' in name");
        assertEquals("Action Hero", movies.get(0).getMovieName(), "Should find the Action Hero movie");
        assertTrue((Boolean) model.getAttribute("searchPerformed"), "Search should be marked as performed");
        assertEquals("Action", model.getAttribute("searchName"), "Search name should be preserved");
    }

    @Test
    @DisplayName("Should return API search results successfully")
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", null, null);
        
        assertNotNull(response, "Response should not be null");
        assertEquals(200, response.getStatusCodeValue(), "Should return 200 OK");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertTrue((Boolean) body.get("success"), "Response should indicate success");
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertNotNull(movies, "Movies should be in response");
        assertEquals(1, movies.size(), "Should find one test movie");
        assertEquals(1, body.get("totalResults"), "Total results should match");
    }

    @Test
    @DisplayName("Should return error for invalid ID in API search")
    public void testSearchMoviesApiInvalidId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, -1L, null);
        
        assertNotNull(response, "Response should not be null");
        assertEquals(400, response.getStatusCodeValue(), "Should return 400 Bad Request for invalid ID");
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertFalse((Boolean) body.get("success"), "Response should indicate failure");
        
        String error = (String) body.get("error");
        assertTrue(error.contains("Invalid movie ID"), "Should contain error message about invalid ID");
        assertTrue(error.contains("Arrr!"), "Should contain pirate language");
    }

    @Test
    @DisplayName("Should return movie details for valid ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        
        assertNotNull(result, "Result should not be null");
        assertEquals("movie-details", result, "Should return movie-details template");
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie, "Movie should be in model");
        assertEquals(1L, movie.getId(), "Should have correct movie ID");
    }

    @Test
    @DisplayName("Should return error page for non-existent movie ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        
        assertNotNull(result, "Result should not be null");
        assertEquals("error", result, "Should return error template");
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertEquals("Movie Not Found", title, "Should have correct error title");
        assertTrue(message.contains("999"), "Error message should contain the movie ID");
    }
}
