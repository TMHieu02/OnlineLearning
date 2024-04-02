package src.service.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import src.config.dto.PagedResultDto;
import src.config.dto.Pagination;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.Course;
import src.model.Document;
import src.model.Document;
import src.model.Document;
import src.repository.DocumentRepository;

import src.service.Document.Dto.DocumentCreateDto;
import src.service.Document.Dto.DocumentDto;
import src.service.Document.Dto.DocumentDto;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;
    @Async
    public CompletableFuture<List<DocumentDto>> getAll() {
        return CompletableFuture.completedFuture(
                documentRepository.findAll().stream().map(
                        x -> toDto.map(x, DocumentDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<DocumentDto> getOne(int id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(document, DocumentDto.class));
    }
    @Async
    public CompletableFuture<DocumentDto> create(DocumentCreateDto input) {
        Document document = new Document();
        document.setFile_path(input.getFile_path());
        document.setTitle(input.getTitle());
        document.setImage(input.getImage());
        document.setCourseId(input.getCourseId());
        Document savedDocument = documentRepository.save(document);
        return CompletableFuture.completedFuture(toDto.map(savedDocument, DocumentDto.class));
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (!documentOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Document document = documentOptional.get();
            document.setIsDeleted(true);
            document.setUpdateAt(new Date(new java.util.Date().getTime()));
            documentRepository.save(document);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    public Document updateDocument(int documentId, Map<String, Object> fieldsToUpdate) {
        Optional<Document> optionalDocument = documentRepository.findById(documentId);
        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            updateDocumentFields(document, fieldsToUpdate);
            document.setUpdateAt(new Date());
            documentRepository.save(document);
            return document;
        }
        return null;
    }
    private void updateDocumentFields(Document document, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateDocumentField(document, fieldName, value);
        }
    }
    private void updateDocumentField(Document document, String fieldName, Object value) {
        switch (fieldName) {
            case "file_path":
                document.setFile_path((String) value);
                break;
            case "title":
                document.setTitle((String) value);
                break;
            case "image":
                document.setImage((String) value);
                break;
            case "courseId":
                document.setCourseId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<List<DocumentDto>> findByCourseId(int courseId) {
        return CompletableFuture.completedFuture(
                documentRepository.findByCourseId(courseId).stream().map(
                        x -> toDto.map(x, DocumentDto.class)
                ).collect(Collectors.toList()));
    }
    public int countDocumentsByCourseId(int courseId) {
        return documentRepository.countByCourseId(courseId);
    }
    

}
