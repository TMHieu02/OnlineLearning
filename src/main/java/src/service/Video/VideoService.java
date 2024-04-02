package src.service.Video;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.exception.NotFoundException;
import src.model.Video;
import src.repository.VideoRepository;
import src.service.Video.Dto.VideoCreateDto;
import src.service.Video.Dto.VideoDto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ModelMapper toDto;
    @Async
    public CompletableFuture<List<VideoDto>> getAll() {
        return CompletableFuture.completedFuture(
                videoRepository.findAll().stream().map(
                        x -> toDto.map(x, VideoDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<VideoDto> getOne(int id) {
        Video video = videoRepository.findById(id).orElse(null);
        if (video == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(video, VideoDto.class));
    }
    public CompletableFuture<Video> createVideo(VideoCreateDto videoCreateDto) {
        CompletableFuture<Video> future = new CompletableFuture<>();
        Video newVideo = new Video();
        newVideo.setVideo_filepath(videoCreateDto.getVideo_filepath());
        newVideo.setDescription(videoCreateDto.getDescription());
        newVideo.setTitle(videoCreateDto.getTitle());
        newVideo.setImage(videoCreateDto.getImage());
        newVideo.setCourseId(videoCreateDto.getCourseId());
        Video savedVideo = videoRepository.save(newVideo);
        future.complete(savedVideo);
        return future;
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (!videoOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Video video = videoOptional.get();
            video.setIsDeleted(true);
            video.setUpdateAt(new Date(new java.util.Date().getTime()));
            videoRepository.save(video);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    public Video updateVideo(int videoId, Map<String, Object> fieldsToUpdate) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Video video = optionalVideo.get();
            updateVideoFields(video, fieldsToUpdate);
            video.setUpdateAt(new Date());
            videoRepository.save(video);
            return video;
        }
        return null;
    }
    private void updateVideoFields(Video video, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateVideoField(video, fieldName, value);
        }
    }
    private void updateVideoField(Video video, String fieldName, Object value) {
        switch (fieldName) {
            case "video_filepath":
                video.setVideo_filepath((String) value);
                break;
            case "title":
                video.setTitle((String) value);
                break;
            case "description":
                video.setDescription((String) value);
                break;
            case "image":
                video.setImage((String) value);
                break;
            case "isDeleted":
                video.setIsDeleted((Boolean) value);
                break;
            case "courseId":
                video.setCourseId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<List<VideoDto>> findByCourseId(int courseId) {
        return CompletableFuture.completedFuture(
                videoRepository.findByCourseId(courseId).stream().map(
                        x -> toDto.map(x, VideoDto.class)
                ).collect(Collectors.toList()));
    }
    public int countVideosByCourseId(int courseId) {
        return videoRepository.countByCourseId(courseId);
    }
    public boolean isVideoInCourse(int videoId, int courseId) {
        Video video = videoRepository.findById(videoId).orElse(null);
        return video != null && video.getCourseId() == courseId;
    }
}

