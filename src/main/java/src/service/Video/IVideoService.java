package src.service.Video;

import src.service.IService;
import src.service.Video.Dto.VideoCreateDto;
import src.service.Video.Dto.VideoDto;
import src.service.Video.Dto.VideoUpdateDto;

public interface IVideoService extends IService<VideoDto, VideoCreateDto, VideoUpdateDto> {
}
