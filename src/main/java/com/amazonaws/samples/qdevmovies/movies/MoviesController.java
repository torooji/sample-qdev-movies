package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "id", required = false) Long id,
                           @RequestParam(value = "genre", required = false) String genre) {
        logger.info("Ahoy! Fetching movie treasures with search criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> movies;
        boolean isSearching = (name != null && !name.trim().isEmpty()) || 
                             (id != null && id > 0) || 
                             (genre != null && !genre.trim().isEmpty());
        
        if (isSearching) {
            movies = movieService.searchMovies(name, id, genre);
            model.addAttribute("searchPerformed", true);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("searchResultsCount", movies.size());
        } else {
            movies = movieService.getAllMovies();
            model.addAttribute("searchPerformed", false);
        }
        
        model.addAttribute("movies", movies);
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    /**
     * REST API endpoint for movie search - returns JSON response
     * Arrr! This be the API treasure chest for other pirates to plunder our movie data!
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Ahoy! API search request received - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Arrr! Invalid movie ID, matey! ID must be a positive number.");
                errorResponse.put("success", false);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id : "",
                "genre", genre != null ? genre : ""
            ));
            
            if (searchResults.isEmpty()) {
                response.put("message", "Arrr! No movie treasures found matching yer search criteria, matey!");
            } else {
                response.put("message", String.format("Ahoy! Found %d movie treasure%s for ye!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            logger.info("Shiver me timbers! Search completed successfully! Returning {} results", searchResults.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! Error occurred during movie search: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Arrr! Something went wrong while searching for movie treasures!");
            errorResponse.put("success", false);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, 
                                 org.springframework.ui.Model model,
                                 HttpSession session,
                                 @RequestParam(value = "error", required = false) String error) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        
        // Get user's reviews from session
        String sessionKey = "movie_" + movieId + "_reviews";
        @SuppressWarnings("unchecked")
        List<Review> userReviews = session != null ? (List<Review>) session.getAttribute(sessionKey) : null;
        if (userReviews == null) {
            userReviews = new ArrayList<>();
            logger.info("No user reviews found for movie {}", movieId);
        } else {
            logger.info("Retrieved {} user reviews for movie {}", userReviews.size(), movieId);
        }
        
        // Get stored username from session
        String storedUserName = session != null ? (String) session.getAttribute("user_name") : null;
        
        // Combine mock reviews with user reviews using our optimized ReviewService
        List<Review> mockReviews = reviewService.getReviewsForMovie(movie.getId());
        List<Review> allReviews = new ArrayList<>();
        allReviews.addAll(mockReviews);
        allReviews.addAll(userReviews);
        
        // Add data to model for template
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", allReviews);
        model.addAttribute("storedUserName", storedUserName);
        model.addAttribute("error", error);
        
        return "movie-details";
    }

    @PostMapping("/movies/{id}/details")
    public String addReview(@PathVariable("id") Long movieId,
                           @RequestParam("userName") String userName,
                           @RequestParam("rating") int rating,
                           @RequestParam("comment") String comment,
                           HttpSession session) {
        logger.info("Adding review for movie ID: {}", movieId);
        
        // Use our optimized MovieService instead of static array
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            return "redirect:/movies/" + movieId + "/details?error=Movie+Not+Found";
        }
        
        // Create review request and validate using the intentional code smells
        ReviewRequest request = new ReviewRequest(userName, rating, comment);
        String validationError = ReviewValidator.validateReview(request);
        
        if (validationError != null) {
            // Redirect back with error parameter
            return "redirect:/movies/" + movieId + "/details?error=" + validationError.replace(" ", "+");
        }
        
        // Get or create avatar for this session
        String avatar = (String) session.getAttribute("user_avatar");
        if (avatar == null) {
            // Randomly select an avatar emoji
            String[] avatars = {"👨", "👩", "🧑", "👴", "👵", "🧒"};
            Random random = new Random();
            avatar = avatars[random.nextInt(avatars.length)];
            session.setAttribute("user_avatar", avatar);
        }
        
        // Store username in session for future reviews
        session.setAttribute("user_name", userName);
        
        // Create new review
        Review newReview = new Review(userName, avatar, (double) rating, comment);
        
        // Get existing user reviews from session
        String sessionKey = "movie_" + movieId + "_reviews";
        @SuppressWarnings("unchecked")
        List<Review> userReviews = (List<Review>) session.getAttribute(sessionKey);
        if (userReviews == null) {
            userReviews = new ArrayList<>();
            logger.info("Creating new review list for movie {}", movieId);
        } else {
            logger.info("Found {} existing reviews for movie {}", userReviews.size(), movieId);
        }
        
        // Add new review to the list
        userReviews.add(newReview);
        session.setAttribute(sessionKey, userReviews);
        logger.info("Added review. Total reviews for movie {}: {}", movieId, userReviews.size());
        
        // Redirect back to details page (Post-Redirect-Get pattern)
        return "redirect:/movies/" + movieId + "/details?reviewAdded=true";
    }
}