package eu.irrationalcharm.mediaservice.service;

import eu.irrationalcharm.enums.ErrorCode;
import eu.irrationalcharm.mediaservice.exception.BusinessException;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfilePictureService {

    private final S3Template s3Template;

    @Value("${application.bucket.name}")
    private String bucketName;

    // Locally: http://10.0.2.2:9000 (Android Emulator) or http://localhost:9000
    // Prod: https://cdn.yourdomain.com
    @Value("${application.bucket.public-url:http://localhost:9000}")
    private String publicBucketUrl;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp");

    public String uploadProfileImage(MultipartFile image, Jwt jwt) {
        String userId = jwt.getClaimAsString("internal_id");

        validateImage(image);

        try {
            byte[] originalImage = image.getBytes();

            byte[] thumbnailBytes = processImage(new ByteArrayInputStream(originalImage), 200, 200, 0.8);
            String thumbnailPath = String.format("users/%s/profile_thumb.jpg", userId);
            uploadToS3(thumbnailPath, thumbnailBytes);

            byte[] fullImageBytes = processImage(new ByteArrayInputStream(originalImage), 1080, 1080, 0.9);
            String fullImagePath = String.format("users/%s/profile.jpg", userId);
            uploadToS3(fullImagePath, fullImageBytes);

            return String.format("%s/%s/%s", publicBucketUrl, bucketName, fullImagePath);

        } catch (IOException e) {
            log.error("Failed to process image for user {}", userId, e);
            throw new BusinessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.INTERNAL_ERROR,
                    "Failed to process profile image"
            );
        }

    }


    private void validateImage(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Image cannot be empty");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, "Invalid image type. Only JPEG, PNG, WEBP allowed.");
        }
    }

    /**
     * Resizes and compresses the image.
     * @param inputStream Original image stream
     * @param width Max width
     * @param height Max height
     * @param quality Compression quality (0.0 - 1.0)
     * @return Processed image bytes
     */
    private byte[] processImage(InputStream inputStream, int width, int height, double quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(inputStream)
                .size(width, height)
                .outputFormat("jpg") // Standardize everything to JPG for consistency
                .outputQuality(quality) // Compression step
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    private void uploadToS3(String key, byte[] content) {
        // Simple upload using Spring Cloud AWS S3Template
        s3Template.upload(bucketName, key, new ByteArrayInputStream(content));
        log.info("Uploaded image to S3: {}", key);
    }
}
