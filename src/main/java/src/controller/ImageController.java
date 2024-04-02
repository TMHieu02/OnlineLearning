package src.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import src.config.annotation.ApiPrefixController;
import src.config.dto.SuccessResponseDto;
import src.config.exception.NotFoundException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@ApiPrefixController("cloud")
public class ImageController {
    @Autowired
    private Cloudinary cloudinary;
    @Async
    @PostMapping(value = "/images/upload", consumes = "multipart/form-data")
    public CompletableFuture<SuccessResponseDto<String>> uploadFileCloud(@RequestPart("file") MultipartFile file) throws IOException {
        boolean isImage = file.getContentType().startsWith("image/");
        if (isImage) {
            byte[] fileData = file.getBytes();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileData));
            int newWidth = 1920;
            int newHeight = 1080;
            boolean isLargeImage = originalImage != null && originalImage.getWidth() > newWidth && originalImage.getHeight() > newHeight;
            if (isLargeImage) {
                BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();
                ByteArrayOutputStream newImageBytes = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "jpg", newImageBytes);
                fileData = newImageBytes.toByteArray();
            }
            Map uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.emptyMap());
            String fileUrl = (String) uploadResult.get("url");
            return CompletableFuture.completedFuture(new SuccessResponseDto<String>(fileUrl));
        } else {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String fileUrl = ((String) uploadResult.get("url")).replace("http", "https");
            return CompletableFuture.completedFuture(new SuccessResponseDto<String>(fileUrl));
        }
    }
    @Async
    @PostMapping(value = "/videos/upload", consumes = "multipart/form-data")
    public CompletableFuture<SuccessResponseDto<String>> uploadVideoCloud(@RequestPart("file") MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dqptxftlv",
                "api_key", "268148952558612",
                "api_secret", "8gzmcO9n4yChRpHfXAr-8-T6ZXQ"
        ));
        // Upload video lên Cloudinary
        Map<String, Object> uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.asMap(
                "resource_type", "video"
        ));
        String videoUrl = (String) uploadResult.get("url");
        return CompletableFuture.completedFuture(new SuccessResponseDto<String>(videoUrl));
    }

    @Async
    @DeleteMapping("/images/{publicId}")
    public CompletableFuture<SuccessResponseDto<String>> deleteFileCloud(@PathVariable String publicId) {
        try {
            if (publicId == null || publicId.trim() == "")
                throw new NotFoundException("Not found publicId");
            cloudinary.api().deleteResources(List.of(publicId), ObjectUtils.emptyMap());
            return CompletableFuture.completedFuture(new SuccessResponseDto<String>("success"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

