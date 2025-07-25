package com.donorbox.backend.controller;

import com.donorbox.backend.entity.*;
import com.donorbox.backend.service.*;
import com.donorbox.backend.dto.BlogRequest;
import com.donorbox.backend.dto.BlogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

//blog controller

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Admin endpoints for managing content")
@SecurityRequirement(name = "basicAuth")
public class AdminController {

    private final CauseService causeService;
    private final EventService eventService;
    private final VolunteerService volunteerService;
    private final ImageUploadService imageUploadService;
    private final BlogService blogService;

    // Admin Causes Management
    @GetMapping("/causes")
    @Operation(summary = "Admin - Get all causes", description = "Retrieve all causes for admin management")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved causes", 
                 content = @Content(mediaType = "application/json", 
                                  array = @ArraySchema(schema = @Schema(implementation = Cause.class))))
    public ResponseEntity<List<Cause>> getAllCauses() {
        List<Cause> causes = causeService.getAllCauses();
        return ResponseEntity.ok(causes);
    }

    @GetMapping("/causes/{id}")
    @Operation(summary = "Admin - Get cause by ID", description = "Retrieve specific cause for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved cause",
                        content = @Content(mediaType = "application/json", 
                                         schema = @Schema(implementation = Cause.class))),
            @ApiResponse(responseCode = "404", description = "Cause not found")
    })
    public ResponseEntity<Cause> getCauseById(
            @Parameter(description = "ID of the cause to retrieve")
            @PathVariable Long id) {
        Cause cause = causeService.getCauseById(id);
        return ResponseEntity.ok(cause);
    }

@PostMapping("/causes")
@Operation(summary = "Admin - Create cause", description = "Create a new cause")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cause created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
})
public ResponseEntity<Cause> createCause(@Valid @RequestBody Cause cause) {
    if (cause.getTitle() == null || cause.getDescription() == null || cause.getTargetAmount() == null) {
        return ResponseEntity.badRequest().build();
    }
    Cause createdCause = causeService.createCause(cause);
    return new ResponseEntity<>(createdCause, HttpStatus.CREATED);
}

    @PutMapping("/causes/{id}")
    @Operation(summary = "Admin - Update cause", description = "Update an existing cause")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cause updated successfully",
                        content = @Content(mediaType = "application/json", 
                                         schema = @Schema(implementation = Cause.class))),
            @ApiResponse(responseCode = "404", description = "Cause not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Cause> updateCause(
            @Parameter(description = "ID of the cause to update")
            @PathVariable Long id,
            @Valid @RequestBody Cause cause) {
        Cause updatedCause = causeService.updateCause(id, cause);
        return ResponseEntity.ok(updatedCause);
    }

    @DeleteMapping("/causes/{id}")
    @Operation(summary = "Admin - Delete cause", description = "Delete a cause")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cause deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cause not found")
    })
    public ResponseEntity<Void> deleteCause(
            @Parameter(description = "ID of the cause to delete")
            @PathVariable Long id) {
        causeService.deleteCause(id);
        return ResponseEntity.noContent().build();
    }

    // Admin Events Management
    @GetMapping("/events")
    @Operation(summary = "Admin - Get all events", description = "Retrieve all events for admin management")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved events")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Admin - Get event by ID", description = "Retrieve specific event for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved event"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID of the event to retrieve")
            @PathVariable Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/events")
    @Operation(summary = "Admin - Create event", description = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event createdEvent = eventService.createEvent(event);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Admin - Update event", description = "Update an existing event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID of the event to update")
            @PathVariable Long id,
            @Valid @RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Admin - Delete event", description = "Delete an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID of the event to delete")
            @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    
    // Create cause with image upload (multipart form)
     
    @PostMapping(value = "/causes/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Admin - Create cause with image", description = "Create a new cause with image upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cause created successfully with image"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or image upload failed")
    })
    public ResponseEntity<Cause> createCauseWithImage(
            @Parameter(description = "Cause title") @RequestParam("title") String title,
            @Parameter(description = "Cause description") @RequestParam("description") String description,
            @Parameter(description = "Short description") @RequestParam(value = "shortDescription", required = false) String shortDescription,
            @Parameter(description = "Target amount") @RequestParam("targetAmount") String targetAmount,
            @Parameter(description = "Category") @RequestParam(value = "category", required = false) String category,
            @Parameter(description = "Location") @RequestParam(value = "location", required = false) String location,
            @Parameter(description = "Image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Create cause object
            Cause cause = Cause.builder()
                    .title(title)
                    .description(description)
                    .shortDescription(shortDescription)
                    .targetAmount(new java.math.BigDecimal(targetAmount))
                    .category(category)
                    .location(location)
                    .build();
            
            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                String imagePath = imageUploadService.uploadImage(image, "causes");
                cause.setImageUrl(imagePath); // Store relative path, not full URL
            }
            
            Cause createdCause = causeService.createCause(cause);
            return new ResponseEntity<>(createdCause, HttpStatus.CREATED);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

     // Update cause with image upload (multipart form)
    @PutMapping(value = "/causes/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Admin - Update cause with image", description = "Update an existing cause with optional image upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cause updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cause not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Cause> updateCauseWithImage(
            @Parameter(description = "ID of the cause to update") @PathVariable Long id,
            @Parameter(description = "Cause title") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "Cause description") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Short description") @RequestParam(value = "shortDescription", required = false) String shortDescription,
            @Parameter(description = "Target amount") @RequestParam(value = "targetAmount", required = false) String targetAmount,
            @Parameter(description = "Category") @RequestParam(value = "category", required = false) String category,
            @Parameter(description = "Location") @RequestParam(value = "location", required = false) String location,
            @Parameter(description = "Image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Get existing cause
            Cause existingCause = causeService.getCauseById(id);
            
            // Update fields if provided
            if (title != null) existingCause.setTitle(title);
            if (description != null) existingCause.setDescription(description);
            if (shortDescription != null) existingCause.setShortDescription(shortDescription);
            if (targetAmount != null) existingCause.setTargetAmount(new java.math.BigDecimal(targetAmount));
            if (category != null) existingCause.setCategory(category);
            if (location != null) existingCause.setLocation(location);
            
            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (existingCause.getImageUrl() != null) {
                    imageUploadService.deleteImage(existingCause.getImageUrl());
                }
                
                String imagePath = imageUploadService.uploadImage(image, "causes");
                existingCause.setImageUrl(imagePath);
            }
            
            Cause updatedCause = causeService.updateCause(id, existingCause);
            return ResponseEntity.ok(updatedCause);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

     // Create event with image upload (multipart form)
    
    @PostMapping(value = "/events/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Admin - Create event with image", description = "Create a new event with image upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully with image"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or image upload failed")
    })
    public ResponseEntity<Event> createEventWithImage(
            @Parameter(description = "Event title") @RequestParam("title") String title,
            @Parameter(description = "Event description") @RequestParam("description") String description,
            @Parameter(description = "Short description") @RequestParam(value = "shortDescription", required = false) String shortDescription,
            @Parameter(description = "Event date (ISO format)") @RequestParam("eventDate") String eventDate,
            @Parameter(description = "Location") @RequestParam(value = "location", required = false) String location,
            @Parameter(description = "Max participants") @RequestParam(value = "maxParticipants", required = false) String maxParticipants,
            @Parameter(description = "Current participants") @RequestParam(value = "currentParticipants", required = false) String currentParticipants,
            @Parameter(description = "Image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Create event object
            Event event = Event.builder()
                    .title(title)
                    .description(description)
                    .shortDescription(shortDescription)
                    .eventDate(java.time.LocalDateTime.parse(eventDate))
                    .location(location)
                    .build();
            
            if (maxParticipants != null) {
                event.setMaxParticipants(Integer.parseInt(maxParticipants));
            }
            
            // Set current participants (default to 0 if not provided)
            if (currentParticipants != null && !currentParticipants.trim().isEmpty()) {
                event.setCurrentParticipants(Integer.parseInt(currentParticipants));
            } else {
                event.setCurrentParticipants(0);
            }
            
            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                String imagePath = imageUploadService.uploadImage(image, "events");
                event.setImageUrl(imagePath); // Store relative path, not full URL
            }
            
            Event createdEvent = eventService.createEvent(event);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

     // Update event with image upload (multipart form)
    @PutMapping(value = "/events/{id}/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Admin - Update event with image", description = "Update an existing event with optional image upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<Event> updateEventWithImage(
            @Parameter(description = "ID of the event to update") @PathVariable Long id,
            @Parameter(description = "Event title") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "Event description") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Short description") @RequestParam(value = "shortDescription", required = false) String shortDescription,
            @Parameter(description = "Event date (ISO format)") @RequestParam(value = "eventDate", required = false) String eventDate,
            @Parameter(description = "Location") @RequestParam(value = "location", required = false) String location,
            @Parameter(description = "Max participants") @RequestParam(value = "maxParticipants", required = false) String maxParticipants,
            @Parameter(description = "Image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Get existing event
            Event existingEvent = eventService.getEventById(id);
            
            // Update fields if provided
            if (title != null) existingEvent.setTitle(title);
            if (description != null) existingEvent.setDescription(description);
            if (shortDescription != null) existingEvent.setShortDescription(shortDescription);
            if (eventDate != null) existingEvent.setEventDate(java.time.LocalDateTime.parse(eventDate));
            if (location != null) existingEvent.setLocation(location);
            if (maxParticipants != null) existingEvent.setMaxParticipants(Integer.parseInt(maxParticipants));
            
            // Handle image upload if provided
            if (image != null && !image.isEmpty()) {
                // Delete old image if exists
                if (existingEvent.getImageUrl() != null) {
                    imageUploadService.deleteImage(existingEvent.getImageUrl());
                }
                
                String imagePath = imageUploadService.uploadImage(image, "events");
                existingEvent.setImageUrl(imagePath);
            }
            
            Event updatedEvent = eventService.updateEvent(id, existingEvent);
            return ResponseEntity.ok(updatedEvent);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin Volunteers Management
    @GetMapping("/volunteers")
    @Operation(summary = "Admin - Get all volunteers", description = "Retrieve all volunteer registrations")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved volunteers")
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        List<Volunteer> volunteers = volunteerService.getAllVolunteers();
        return ResponseEntity.ok(volunteers);
    }

    // ====================================
    // Admin Blog Management
    // ====================================

    @PostMapping("/blogs")
    @Operation(summary = "Admin - Create blog", description = "Create a new blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid blog data"),
            @ApiResponse(responseCode = "409", description = "Blog with slug already exists")
    })
    public ResponseEntity<BlogResponse> createBlog(@Valid @RequestBody BlogRequest request) {
        try {
            // Auto-calculate reading time if not provided
            if (request.getReadingTime() == null) {
                request.setReadingTime(blogService.calculateReadingTime(request.getContent()));
            }

            Blog blog = blogService.createBlog(request);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to create blog: " + e.getMessage());
        }
    }

    @GetMapping("/blogs")
    @Operation(summary = "Admin - Get all blogs", description = "Retrieve all blog posts for admin management")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved blogs")
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        List<Blog> blogs = blogService.getAllBlogs();
        List<BlogResponse> responses = blogs.stream()
                .map(BlogResponse::summaryFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/blogs/paginated")
    @Operation(summary = "Admin - Get paginated blogs", description = "Retrieve blogs with pagination")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated blogs")
    public ResponseEntity<Page<BlogResponse>> getBlogsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Page<Blog> blogPage = blogService.getBlogsPaginated(page, size, sortBy, sortDir);
        Page<BlogResponse> responsePage = blogPage.map(BlogResponse::summaryFromEntity);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/blogs/{id}")
    @Operation(summary = "Admin - Get blog by ID", description = "Retrieve a specific blog post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved blog"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    public ResponseEntity<BlogResponse> getBlogById(
            @Parameter(description = "Blog ID") @PathVariable Long id) {
        try {
            Blog blog = blogService.getBlogById(id);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/blogs/{id}")
    @Operation(summary = "Admin - Update blog", description = "Update an existing blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog updated successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found"),
            @ApiResponse(responseCode = "400", description = "Invalid blog data")
    })
    public ResponseEntity<BlogResponse> updateBlog(
            @Parameter(description = "Blog ID") @PathVariable Long id,
            @Valid @RequestBody BlogRequest request) {
        try {
            // Auto-calculate reading time if not provided
            if (request.getReadingTime() == null) {
                request.setReadingTime(blogService.calculateReadingTime(request.getContent()));
            }

            Blog blog = blogService.updateBlog(id, request);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/blogs/{id}")
    @Operation(summary = "Admin - Delete blog", description = "Delete a blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Blog deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    public ResponseEntity<Void> deleteBlog(
            @Parameter(description = "Blog ID") @PathVariable Long id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/blogs/{id}/publish")
    @Operation(summary = "Admin - Publish blog", description = "Publish a draft blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog published successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    public ResponseEntity<BlogResponse> publishBlog(
            @Parameter(description = "Blog ID") @PathVariable Long id) {
        try {
            Blog blog = blogService.publishBlog(id);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/blogs/{id}/unpublish")
    @Operation(summary = "Admin - Unpublish blog", description = "Unpublish a published blog post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blog unpublished successfully"),
            @ApiResponse(responseCode = "404", description = "Blog not found")
    })
    public ResponseEntity<BlogResponse> unpublishBlog(
            @Parameter(description = "Blog ID") @PathVariable Long id) {
        try {
            Blog blog = blogService.unpublishBlog(id);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/blogs/by-status/{status}")
    @Operation(summary = "Admin - Get blogs by status", description = "Retrieve blogs filtered by status")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved blogs by status")
    public ResponseEntity<List<BlogResponse>> getBlogsByStatus(
            @Parameter(description = "Blog status") @PathVariable Blog.BlogStatus status) {
        List<Blog> blogs = blogService.getBlogsByStatus(status);
        List<BlogResponse> responses = blogs.stream()
                .map(BlogResponse::summaryFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/blogs/generate-slug")
    @Operation(summary = "Admin - Generate slug from title", description = "Generate a URL-friendly slug from blog title")
    @ApiResponse(responseCode = "200", description = "Slug generated successfully")
    public ResponseEntity<Map<String, String>> generateSlug(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        if (title == null || title.trim().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Title is required");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        String slug = blogService.generateSlug(title);
        Map<String, String> response = new HashMap<>();
        response.put("slug", slug);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/blogs/calculate-reading-time")
    @Operation(summary = "Admin - Calculate reading time", description = "Calculate estimated reading time for blog content")
    @ApiResponse(responseCode = "200", description = "Reading time calculated successfully")
    public ResponseEntity<Map<String, Integer>> calculateReadingTime(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            Map<String, Integer> errorResponse = new HashMap<>();
            errorResponse.put("error", 1);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        int readingTime = blogService.calculateReadingTime(content);
        Map<String, Integer> response = new HashMap<>();
        response.put("readingTime", readingTime);
        return ResponseEntity.ok(response);
    }

    // Blog with image upload
    @PostMapping(value = "/blogs/with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Admin - Create blog with image", description = "Create a new blog post with featured image upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Blog created successfully with image"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or image upload failed")
    })
    public ResponseEntity<BlogResponse> createBlogWithImage(
            @Parameter(description = "Blog title") @RequestParam("title") String title,
            @Parameter(description = "Blog subtitle") @RequestParam(value = "subtitle", required = false) String subtitle,
            @Parameter(description = "Blog slug") @RequestParam("slug") String slug,
            @Parameter(description = "Blog content") @RequestParam("content") String content,
            @Parameter(description = "Blog excerpt") @RequestParam(value = "excerpt", required = false) String excerpt,
            @Parameter(description = "Author name") @RequestParam("author") String author,
            @Parameter(description = "Author email") @RequestParam(value = "authorEmail", required = false) String authorEmail,
            @Parameter(description = "Blog status") @RequestParam("status") String status,
            @Parameter(description = "Tags (comma-separated)") @RequestParam(value = "tags", required = false) String tags,
            @Parameter(description = "Meta title") @RequestParam(value = "metaTitle", required = false) String metaTitle,
            @Parameter(description = "Meta description") @RequestParam(value = "metaDescription", required = false) String metaDescription,
            @Parameter(description = "Is featured") @RequestParam(value = "isFeatured", defaultValue = "false") Boolean isFeatured,
            @Parameter(description = "Allow comments") @RequestParam(value = "allowComments", defaultValue = "true") Boolean allowComments,
            @Parameter(description = "Featured image file") @RequestParam(value = "image", required = false) MultipartFile image) {
        
        try {
            // Handle image upload if provided
            String featuredImage = null;
            if (image != null && !image.isEmpty()) {
                featuredImage = imageUploadService.uploadImage(image, "blogs");
            }
            
            // Create blog request
            BlogRequest request = BlogRequest.builder()
                    .title(title)
                    .subtitle(subtitle)
                    .slug(slug)
                    .content(content)
                    .excerpt(excerpt)
                    .featuredImage(featuredImage)
                    .author(author)
                    .authorEmail(authorEmail)
                    .status(Blog.BlogStatus.valueOf(status.toUpperCase()))
                    .readingTime(blogService.calculateReadingTime(content))
                    .tags(tags)
                    .metaTitle(metaTitle)
                    .metaDescription(metaDescription)
                    .isFeatured(isFeatured)
                    .allowComments(allowComments)
                    .build();
            
            Blog blog = blogService.createBlog(request);
            BlogResponse response = BlogResponse.fromEntity(blog);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
