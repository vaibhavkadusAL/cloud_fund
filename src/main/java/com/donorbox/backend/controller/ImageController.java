package com.donorbox.backend.controller;

import com.donorbox.backend.service.ImageUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Image API", description = "Endpoints for image upload and retrieval")
public class ImageController {

    private final ImageUploadService imageUploadService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

     //Upload an image for causes
    @PostMapping("/causes/upload-image")
    @Operation(summary = "Upload cause image", description = "Upload an image file for a cause")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or upload failed"),
            @ApiResponse(responseCode = "413", description = "File size too large")
    })
    public ResponseEntity<Map<String, String>> uploadCauseImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("image") MultipartFile file) {
        
        return uploadImage(file, "causes");
    }

     // Upload an image for events
   
    @PostMapping("/events/upload-image")
    @Operation(summary = "Upload event image", description = "Upload an image file for an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or upload failed"),
            @ApiResponse(responseCode = "413", description = "File size too large")
    })
    public ResponseEntity<Map<String, String>> uploadEventImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("image") MultipartFile file) {
        
        return uploadImage(file, "events");
    }
     // Generic image upload method
    @PostMapping("/upload-image")
    @Operation(summary = "Upload image", description = "Upload an image file with specified category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or upload failed"),
            @ApiResponse(responseCode = "413", description = "File size too large")
    })
    public ResponseEntity<Map<String, String>> uploadGenericImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("image") MultipartFile file,
            @Parameter(description = "Category for organizing images")
            @RequestParam(value = "category", defaultValue = "general") String category) {
        
        return uploadImage(file, category);
    }

     // Serve uploaded images
    @GetMapping("/images/{category}/{filename:.+}")
    @Operation(summary = "Get uploaded image", description = "Retrieve an uploaded image file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "Image category")
            @PathVariable String category,
            @Parameter(description = "Image filename")
            @PathVariable String filename) {
        
        try {
            Path imagePath = Paths.get(uploadDir).resolve(category).resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Determine content type based on file extension
                String contentType = determineContentType(filename);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                log.warn("Image not found or not readable: {}", imagePath);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("Error creating resource for image: {}/{}", category, filename, e);
            return ResponseEntity.notFound().build();
        }
    }

     // Delete an uploaded image
    @DeleteMapping("/images/{category}/{filename:.+}")
    @Operation(summary = "Delete uploaded image", description = "Delete an uploaded image file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Image not found"),
            @ApiResponse(responseCode = "500", description = "Error deleting image")
    })
    public ResponseEntity<Map<String, String>> deleteImage(
            @Parameter(description = "Image category")
            @PathVariable String category,
            @Parameter(description = "Image filename")
            @PathVariable String filename) {
        
        String imagePath = category + "/" + filename;
        boolean deleted = imageUploadService.deleteImage(imagePath);
        
        Map<String, String> response = new HashMap<>();
        
        if (deleted) {
            response.put("message", "Image deleted successfully");
            response.put("imagePath", imagePath);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Image not found or could not be deleted");
            response.put("imagePath", imagePath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

     // Common image upload logic
    private ResponseEntity<Map<String, String>> uploadImage(MultipartFile file, String category) {
        Map<String, String> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            String relativePath = imageUploadService.uploadImage(file, category);
            String fullUrl = imageUploadService.getImageUrl(relativePath);
            
            response.put("message", "Image uploaded successfully");
            response.put("imagePath", relativePath);
            response.put("imageUrl", fullUrl);
            response.put("filename", file.getOriginalFilename());
            response.put("category", category);
            
            log.info("Image uploaded successfully: {} -> {}", file.getOriginalFilename(), relativePath);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Error uploading image: {}", e.getMessage(), e);
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    } 
     // Determine content type based on file extension
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        
        switch (extension) {
            case "jpg":
                return "image/jpg";
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }
}
