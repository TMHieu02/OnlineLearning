package src.service.Course;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.config.dto.PagedResultDto;
import src.config.dto.Pagination;
import src.config.exception.NotFoundException;
import src.config.utils.ApiQuery;
import src.model.Category;
import src.model.Course;
import src.model.Rating;
import src.repository.CourseRegisterRepository;
import src.repository.CourseRepository;
import src.repository.RatingRepository;
import src.service.Course.Dto.*;
import src.service.Course.Dto.CourseDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseRegisterRepository courseRegisterRepository;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ModelMapper toDto;
    @PersistenceContext
    EntityManager em;

    @Async
    public CompletableFuture<List<CourseDto>> getAll() {
        return CompletableFuture.completedFuture(
                courseRepository.findAll().stream().map(
                        x -> toDto.map(x, CourseDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CourseDto> getOne(int id) {
        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(course, CourseDto.class));
    }
    @Async
    public CompletableFuture<CourseDto> create(CourseCreateDto input) {
        Course course = new Course();
        course.setTitle(input.getTitle());
        course.setPrice(input.getPrice());
        course.setPromotional_price(input.getPromotional_price());
        course.setSold(input.getSold());
        course.setDescription(input.getDescription());
        course.setActive(input.getActive());
        course.setRating(input.getRating());
        course.setImage(input.getImage());
        course.setCategoryId(input.getCategoryId());
        course.setUserId(input.getUserId());
        Course savedCourse = courseRepository.save(course);
        return CompletableFuture.completedFuture(toDto.map(savedCourse, CourseDto.class));
    }
    public Course updateCourse(int courseId, Map<String, Object> fieldsToUpdate) {
        Optional<Course> optionalCourse = courseRepository.findById(courseId);

        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            updateCourseFields(course, fieldsToUpdate);
            course.setUpdateAt(new Date());
            courseRepository.save(course);
            return course;
        }
        return null;
    }
    private void updateCourseFields(Course course, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateCourseField(course, fieldName, value);
        }
    }
    private void updateCourseField(Course course, String fieldName, Object value) {
        switch (fieldName) {
            case "title":
                course.setTitle((String) value);
                break;
            case "price":
                course.setPrice((int) value);
                break;
            case "promotional_price":
                course.setPromotional_price((int) value);
                break;
            case "sold":
                course.setSold((int) value);
                break;
            case "description":
                course.setDescription((String) value);
                break;
            case "active":
                course.setActive((Boolean) value);
                break;
            case "rating":
                course.setRating((int) value);
                break;
            case "image":
                course.setImage((String) value);
                break;
            case "categoryId":
                course.setCategoryId((int) value);
                break;
            case "userId":
                course.setUserId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (!courseOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Course course = courseOptional.get();
            course.setIsDeleted(true);
            course.setUpdateAt(new Date(new java.util.Date().getTime()));
            courseRepository.save(course);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    @Async
    public CompletableFuture<PagedResultDto<CourseDto>> findAllPagination(HttpServletRequest request, Integer limit, Integer skip) {
        long total = courseRepository.count();
        Pagination pagination = Pagination.create(total, skip, limit);
        ApiQuery<Course> features = new ApiQuery<>(request, em, Course.class, pagination);
        return CompletableFuture.completedFuture(PagedResultDto.create(pagination,
                features.filter().orderBy().paginate().exec().stream().map(x -> toDto.map(x, CourseDto.class)).toList()));
    }

    public double calculateCourseRating(int courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return -1;
        }
        Iterable<Rating> ratings = ratingRepository.findByCourseId(courseId);

        int totalRatings = 0;
        double totalRatingValue = 0.0;

        for (Rating rating : ratings) {
            totalRatings++;
            totalRatingValue += rating.getRating();
        }
        if (totalRatings == 0) {
            return 0;
        }
        double averageRating = totalRatingValue / totalRatings;
        return averageRating;
    }
    @Async
    public CompletableFuture<List<CourseDto>> getTopNew() {
        List<Course> newestCourses = courseRepository.findAll()
                .stream()
                .filter(course -> course.getActive() || !course.getIsDeleted())
                .sorted(Comparator.comparing(Course::getCreateAt).reversed())
                .limit(6)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(newestCourses.stream()
                .map(course -> toDto.map(course, CourseDto.class))
                .collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<List<CourseDto>> getTopMost() {
        List<Course> courses = courseRepository.findAll();
        Map<Course, Long> registrationsCountMap = courses.stream()
                .filter(course -> course.getActive() || !course.getIsDeleted())
                .collect(Collectors.toMap(
                        course -> course,
                        course -> courseRegisterRepository.countByCourseIdAndIsActive(course.getId(), true)
                ));
        List<Course> top4Courses = registrationsCountMap.entrySet().stream()
                .sorted(Map.Entry.<Course, Long>comparingByValue().reversed())
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(top4Courses.stream()
                .map(course -> toDto.map(course, CourseDto.class))
                .collect(Collectors.toList()));
    }

    public List<CourseInfoDTO> searchByTitle(String title, int pageNumber, int pageSize, String sort) {
        title = StringUtils.stripAccents(title.toLowerCase()); // Loại bỏ dấu và chuyển thành chữ thường
        String[] keywords = title.split("\\s+");

        StringBuilder queryStringBuilder = new StringBuilder(
                "SELECT c FROM Course c WHERE c.active = true AND (c.isDeleted is null OR c.isDeleted = false) AND ("
        );

        for (int i = 0; i < keywords.length; i++) {
            queryStringBuilder.append("(LOWER(TRANSLATE(c.title, 'áàảãạâấầẩẫậăắằẳẵặéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ', 'aaaaaaaaaaaaaaaaaeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyd')) LIKE :keyword").append(i);
            queryStringBuilder.append(" OR ");
            queryStringBuilder.append("LOWER(TRANSLATE(c.userByUserId.fullname, 'áàảãạâấầẩẫậăắằẳẵặéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ', 'aaaaaaaaaaaaaaaaaeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyd')) LIKE :keyword").append(i + keywords.length);
            queryStringBuilder.append(")");
            if (i < keywords.length - 1) {
                queryStringBuilder.append(" AND ");
            }
        }
        queryStringBuilder.append(")");
        Query query = em.createQuery(queryStringBuilder.toString());
        for (int i = 0; i < keywords.length; i++) {
            query.setParameter("keyword" + i, "%" + keywords[i] + "%");
            query.setParameter("keyword" + (i + keywords.length), "%" + keywords[i] + "%");
        }
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<Course> courses = query.getResultList();
        if (sort != null && !sort.isEmpty()) {
            if (sort.equals("learn-most")) {
                courses.sort(Comparator.comparing(Course::getSold).reversed());
            } else if (sort.equals("rating")) {
                courses.sort(Comparator.comparing(Course::getRating).reversed());
            } else if (sort.equals("new")) {
                courses.sort(Comparator.comparing(Course::getCreateAt).reversed());
            } else if (sort.equals("price-high")) {
                courses.sort(Comparator.comparing(Course::getPromotional_price).reversed());
            } else if (sort.equals("price-low")) {
                courses.sort(Comparator.comparing(Course::getPromotional_price));
            }
        }
        List<CourseInfoDTO> courseInfoDTOs = new ArrayList<>();
        for (Course course : courses) {
            CourseInfoDTO courseInfoDTO = convertCourseToCourseInfoDTO(course);
            courseInfoDTOs.add(courseInfoDTO);
        }

        return courseInfoDTOs;
    }


    public List<CourseInfoDTO> getCoursesByCategoryId(int categoryId) {
        List<Course> newCourses = courseRepository.findByCategoryId(categoryId);
        List<CourseInfoDTO> courses = new ArrayList<>();
        for (Course course : newCourses) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
            courses.add(ur1);
        }
        return courses;
    }
    public List<CourseInfoDTO> findByUserId(int userId) {
        List<Course> newCourses = courseRepository.findByUserId(userId);
        List<CourseInfoDTO> courses = new ArrayList<>();
        for (Course course : newCourses) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
            courses.add(ur1);
        }
        return courses;
    }

    public CourseInfoDTO convertCourseToCourseInfoDTO (Course course){
        CourseInfoDTO ur1 = new CourseInfoDTO();
        ur1.setCourse_id(course.getId());
        ur1.setTitle(course.getTitle());
        ur1.setCategory_id(course.getCategoryId());
        ur1.setCategory_name(course.getCategoryByCategoryId().getName());
        ur1.setUser_id(course.getUserId());
        ur1.setUser_name(course.getUserByUserId().getFullname());
        ur1.setPrice(course.getPrice());
        ur1.setPromotional_price(course.getPromotional_price());
        ur1.setSold(course.getSold());
        ur1.setDescription(course.getDescription());
        ur1.setImage(course.getImage());
        ur1.setActive(course.getActive());
        ur1.setCreated_at(course.getCreateAt());
        ur1.setUpdate_at(course.getUpdateAt());
        ur1.setRating(course.getRating());
        ur1.setCreateAt(course.getCreateAt());
        ur1.setUpdateAt(course.getUpdateAt());
        return ur1;
    }
    public List<CourseInfoDTO> getCourseAndRelateInfo(int course_id) {
        List<Course> newcourse = courseRepository.findAll()
                .stream()
                .filter(course -> (course.getActive()))
                .collect(Collectors.toList());
        List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
        for (Course course : newcourse) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);;
            courseRelateInfo.add(ur1);
        }
        return courseRelateInfo;
    }
    @Async
    public CompletableFuture<List<CourseInfoDTO>> get4CourseNewAndRelateInfo() {
        CompletableFuture<List<CourseInfoDTO>> future = CompletableFuture.supplyAsync(() -> {
            List<Course> newCourses = courseRepository.findAll()
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getCreateAt).reversed())
                    .limit(4)
                    .collect(Collectors.toList());
            List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
            for (Course course : newCourses) {
                CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
                courseRelateInfo.add(ur1);
            }
            return courseRelateInfo;
        });
        return future;
    }
    @Async
    public CompletableFuture<List<CourseInfoDTO>> get4CourseRatingAndRelateInfo() {
        CompletableFuture<List<CourseInfoDTO>> future = CompletableFuture.supplyAsync(() -> {
            List<Course> newCourses = courseRepository.findAll()
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getRating).reversed())
                    .limit(4)
                    .collect(Collectors.toList());
            List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
            for (Course course : newCourses) {
                CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
                courseRelateInfo.add(ur1);
            }
            return courseRelateInfo;
        });
        return future;
    }
    @Async
    public CompletableFuture<List<CourseInfoDTO>> get4CourseSoldAndRelateInfo() {
        CompletableFuture<List<CourseInfoDTO>> future = CompletableFuture.supplyAsync(() -> {
            List<Course> newCourses = courseRepository.findAll()
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getSold).reversed())
                    .limit(4)
                    .collect(Collectors.toList());
            List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
            for (Course course : newCourses) {
                CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
                courseRelateInfo.add(ur1);
            }
            return courseRelateInfo;
        });
        return future;
    }
    public List<CourseInfoDTO> findCourseSoldAndRelateInfoByTitle(String title) {
        List<Course> newCourses = courseRepository.searchByTitle(title);
        List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
        for (Course course : newCourses) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);;
            courseRelateInfo.add(ur1);
        }
        return courseRelateInfo;
    }
    public List<CourseInfoDTO> sortCourseInCategory(int categoryId, String sort, int page, int pageSize) {
        List<Course> listCourseResult = new ArrayList<>();
        if(sort.equals("learn-most")){
            listCourseResult = courseRepository.findByCategoryId(categoryId)
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getSold).reversed())
                    .collect(Collectors.toList());
        } else if (sort.equals("rating")) {
            listCourseResult = courseRepository.findByCategoryId(categoryId)
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getRating).reversed())
                    .collect(Collectors.toList());
        } else if (sort.equals(("new"))) {
            listCourseResult = courseRepository.findByCategoryId(categoryId)
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getCreateAt).reversed())
                    .collect(Collectors.toList());
        } else if (sort.equals("price-high")) {
            listCourseResult = courseRepository.findByCategoryId(categoryId)
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getPromotional_price).reversed())
                    .collect(Collectors.toList());
        } else if (sort.equals("price-low")) {
            listCourseResult = courseRepository.findByCategoryId(categoryId)
                    .stream()
                    .filter(Course::getActive)
                    .sorted(Comparator.comparing(Course::getPromotional_price))
                    .collect(Collectors.toList());
        } else {
            listCourseResult = courseRepository.findByCategoryId(categoryId);
        }

        // Phân trang
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listCourseResult.size());
        listCourseResult = listCourseResult.subList(startIndex, endIndex);

        List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();
        for (Course course : listCourseResult) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);
            courseRelateInfo.add(ur1);
        }
        return courseRelateInfo;
    }

    public List<CourseInfoDTO> getCoursesAndRelateInfo() {
        List<Course> newCourses = courseRepository.findAll()
                .stream()
                .collect(Collectors.toList());
        List<CourseInfoDTO> courseRelateInfo = new ArrayList<>();

        for (Course course : newCourses) {
            CourseInfoDTO ur1 = convertCourseToCourseInfoDTO(course);;
            courseRelateInfo.add(ur1);
        }
        return courseRelateInfo;
    }
    public String lockCourse(int id) {
        Optional<Course> courseOptinal = courseRepository.findById(id);
        if (courseOptinal.isPresent()) {
            Course u1 = courseOptinal.get();
            u1.setActive(false);
            courseRepository.save(u1);
        }
        return "Course will be lock";
    }
    public String unLockCourse(int id) {
        Optional<Course> courseOptinal = courseRepository.findById(id);
        if (courseOptinal.isPresent()) {
            Course u1 = courseOptinal.get();
            u1.setActive(true);
            courseRepository.save(u1);
        }
        return "Course will be unlock";
    }
    public int countCoursesByUserId(int userId) {
        return courseRepository.countByUserId(userId);
    }
    public void updateCourseRatingByCourseId(int courseId) {
        Double averageRating = ratingRepository.calculateAverageRatingByCourseId(courseId);
        int roundedRating = averageRating != null ? (int) Math.ceil(averageRating) : 0;
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setRating(roundedRating);
            courseRepository.save(course);
        }
    }
    public boolean isCourseInUser(int courseId, int userId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        return course != null && course.getUserId() == userId;
    }
    public Page<CourseInfoDTO> findAllCourses(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Course> courses = courseRepository.findAll(pageable);
        return courses.map(course -> convertCourseToCourseInfoDTO(course));
    }
    public Page<CourseInfoDTO> getCoursesByCategoryId(int categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseRepository.findByCategoryId(categoryId, pageable);
        return courses.map(course -> convertCourseToCourseInfoDTO(course));
    }
}
