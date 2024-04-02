package src.service.Document;

import src.service.IService;
import src.service.Document.Dto.DocumentCreateDto;
import src.service.Document.Dto.DocumentDto;
import src.service.Document.Dto.DocumentUpdateDto;

public interface IDocumentService extends IService<DocumentDto, DocumentCreateDto, DocumentUpdateDto> {
}
