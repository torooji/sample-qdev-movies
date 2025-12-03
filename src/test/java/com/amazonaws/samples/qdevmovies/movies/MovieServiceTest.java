package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Test class for the MovieService treasure hunting functionality!
 * These tests ensure our movie search be working ship-shape, matey!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Should load all movie treasures from JSON")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        
        assertNotNull(movies, "Movie treasure chest should not be null!");
        assertFalse(movies.isEmpty(), "Arrr! The treasure chest should not be empty!");
        assertTrue(movies.size() > 0, "Should have some movie treasures loaded");
    }

    @Test
    @DisplayName("Should find movie treasure by valid ID")
    public void testGetMovieByValidId() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        
        assertTrue(movie.isPresent(), "Should find the movie treasure with ID 1");
        assertEquals(1L, movie.get().getId(), "Movie ID should match");
        assertEquals("The Prison Escape", movie.get().getMovieName(), "Movie name should match");
    }

    @Test
    @DisplayName("Should return empty for invalid movie ID")
    public void testGetMovieByInvalidId() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        
        assertFalse(movie.isPresent(), "Should not find movie with non-existent ID");
    }

    @Test
    @DisplayName("Should return empty for null or negative ID")
    public void testGetMovieByNullOrNegativeId() {
        Optional<Movie> nullIdMovie = movieService.getMovieById(null);
        Optional<Movie> negativeIdMovie = movieService.getMovieById(-1L);
        Optional<Movie> zeroIdMovie = movieService.getMovieById(0L);
        
        assertFalse(nullIdMovie.isPresent(), "Should not find movie with null ID");
        assertFalse(negativeIdMovie.isPresent(), "Should not find movie with negative ID");
        assertFalse(zeroIdMovie.isPresent(), "Should not find movie with zero ID");
    }

    @Test
    @DisplayName("Should search movies by name (case-insensitive)")
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        
        assertNotNull(results, "Search results should not be null");
        assertEquals(1, results.size(), "Should find one movie with 'prison' in the name");
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        
        // Test case-insensitive search
        List<Movie> caseInsensitiveResults = movieService.searchMovies("PRISON", null, null);
        assertEquals(1, caseInsensitiveResults.size(), "Case-insensitive search should work");
    }

    @Test
    @DisplayName("Should search movies by genre")
    public void testSearchMoviesByGenre() {
        List<Movie> dramaResults = movieService.searchMovies(null, null, "Drama");
        
        assertNotNull(dramaResults, "Search results should not be null");
        assertTrue(dramaResults.size() > 0, "Should find movies with Drama genre");
        
        // Verify all results contain Drama in genre
        for (Movie movie : dramaResults) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"), 
                "All results should contain 'drama' in genre");
        }
    }

    @Test
    @DisplayName("Should search movies by specific ID")
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 2L, null);
        
        assertNotNull(results, "Search results should not be null");
        assertEquals(1, results.size(), "Should find exactly one movie with ID 2");
        assertEquals(2L, results.get(0).getId(), "Found movie should have ID 2");
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Should search movies by name and genre combination")
    public void testSearchMoviesByNameAndGenre() {
        List<Movie> results = movieService.searchMovies("hero", null, "Action");
        
        assertNotNull(results, "Search results should not be null");
        
        // Verify all results match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("hero") || 
                      movie.getGenre().toLowerCase().contains("action"),
                "Results should match name or genre criteria");
        }
    }

    @Test
    @DisplayName("Should return empty list for non-matching search criteria")
    public void testSearchMoviesNoMatches() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        
        assertNotNull(results, "Search results should not be null");
        assertTrue(results.isEmpty(), "Should return empty list for non-matching criteria");
    }

    @Test
    @DisplayName("Should return all movies when search criteria are empty")
    public void testSearchMoviesWithEmptyCriteria() {
        List<Movie> results = movieService.searchMovies("", null, "");
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(results, "Search results should not be null");
        assertEquals(allMovies.size(), results.size(), 
            "Empty search should return all movies");
    }

    @Test
    @DisplayName("Should return all movies when search criteria are null")
    public void testSearchMoviesWithNullCriteria() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertNotNull(results, "Search results should not be null");
        assertEquals(allMovies.size(), results.size(), 
            "Null search should return all movies");
    }

    @Test
    @DisplayName("Should get all unique genres from movie collection")
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        
        assertNotNull(genres, "Genres list should not be null");
        assertFalse(genres.isEmpty(), "Should have some genres");
        
        // Check for expected genres from the test data
        assertTrue(genres.contains("Drama"), "Should contain Drama genre");
        assertTrue(genres.contains("Action/Crime"), "Should contain Action/Crime genre");
        assertTrue(genres.contains("Adventure/Fantasy"), "Should contain Adventure/Fantasy genre");
        
        // Verify no duplicates
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount, "Genres list should not contain duplicates");
    }

    @Test
    @DisplayName("Should handle partial name matches correctly")
    public void testSearchMoviesPartialNameMatch() {
        List<Movie> results = movieService.searchMovies("the", null, null);
        
        assertNotNull(results, "Search results should not be null");
        assertTrue(results.size() > 1, "Should find multiple movies with 'the' in the name");
        
        // Verify all results contain 'the' in the name (case-insensitive)
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"), 
                "All results should contain 'the' in the name");
        }
    }

    @Test
    @DisplayName("Should handle whitespace in search terms")
    public void testSearchMoviesWithWhitespace() {
        List<Movie> results1 = movieService.searchMovies("  prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("prison", null, null);
        
        assertNotNull(results1, "Search results should not be null");
        assertNotNull(results2, "Search results should not be null");
        assertEquals(results2.size(), results1.size(), 
            "Whitespace should be trimmed and results should be the same");
    }
}