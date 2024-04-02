package src.service.CourseRegister;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.Dto.CourseRegisterUserDTO;
import src.config.exception.NotFoundException;
import src.config.gmail.EmailUtil;
import src.config.gmail.OtpUtil;

import src.model.*;
import src.repository.CourseRegisterRepository;
import src.service.CourseRegister.Dto.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class CourseRegisterService {
    @Autowired
    private CourseRegisterRepository courseRegisterRepository;
    @Autowired
    private ModelMapper toDto;
    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private MessageSource messageSource;
    @Async
    public CompletableFuture<List<CourseRegisterDto>> getAll() {
        return CompletableFuture.completedFuture(
                courseRegisterRepository.findAll().stream().map(
                        x -> toDto.map(x, CourseRegisterDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<CourseRegisterDto> getOne(int id) {
        CourseRegister courseRegister = courseRegisterRepository.findById(id).orElse(null);
        if (courseRegister == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(courseRegister, CourseRegisterDto.class));
    }
    public String register(CourseRegisterCreateDto courseRegisterDto) {
        try {
            Optional<CourseRegister> existingRegistration = courseRegisterRepository.findByUserIdAndCourseId(
                    courseRegisterDto.getUserId(), courseRegisterDto.getCourseId());
            if (existingRegistration.isPresent()) {
                return "User is already registered for this course";
            }
            String otp = otpUtil.generateOtp();
            emailUtil.sendOtpEmail(courseRegisterDto.getEmail(), otp);
            CourseRegister courseRegister = new CourseRegister();
            courseRegister.setCourseId(courseRegisterDto.getCourseId());
            courseRegister.setUserId(courseRegisterDto.getUserId());
            courseRegister.setOtp(otp);
            courseRegister.setOtpGeneratedTime(LocalDateTime.now());
            courseRegisterRepository.save(courseRegister);
            return "User registration successful";
        } catch (Exception e) {
            e.printStackTrace();
            return "User registration failed";
        }
    }
    public CourseRegister updateCourseRegister(int courseRegisterId, Map<String, Object> fieldsToUpdate) {
        Optional<CourseRegister> optionalCourseRegister = courseRegisterRepository.findById(courseRegisterId);

        if (optionalCourseRegister.isPresent()) {
            CourseRegister courseRegister = optionalCourseRegister.get();
            updateCourseRegisterFields(courseRegister, fieldsToUpdate);
            courseRegister.setUpdateAt(new Date());
            courseRegisterRepository.save(courseRegister);
            return courseRegister;
        }
        return null;
    }
    private void updateCourseRegisterFields(CourseRegister courseRegister, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateCourseRegisterField(courseRegister, fieldName, value);
        }
    }
    private void updateCourseRegisterField(CourseRegister courseRegister, String fieldName, Object value) {
        switch (fieldName) {
            case "otp":
                courseRegister.setOtp((String) value);
                break;
            case "courseId":
                courseRegister.setCourseId((int) value);
                break;
            case "userId":
                courseRegister.setUserId((int) value);
                break;
            default:
                break;
        }
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<CourseRegister> courseRegisterOptional = courseRegisterRepository.findById(id);
        if (!courseRegisterOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            CourseRegister courseRegister = courseRegisterOptional.get();
            courseRegister.setIsDeleted(true);
            courseRegister.setUpdateAt(new Date(new java.util.Date().getTime()));
            courseRegisterRepository.save(courseRegister);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    public String verifyAccount(String email, String otp) {
        CourseRegister courseRegister = null;
        if (courseRegister.getOtp().equals(otp) && Duration.between(courseRegister.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (1 * 60)) {
            courseRegister.setIsActive(true);
            courseRegisterRepository.save(courseRegister);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }
    public String regenerateOtp(String email) {
        CourseRegister courseRegister = null;
        String otp = otpUtil.generateOtp();
        emailUtil.sendOtpEmail(email, otp);
        courseRegister.setOtp(otp);
        courseRegister.setOtpGeneratedTime(LocalDateTime.now());
        courseRegisterRepository.save(courseRegister);
        return "Email sent... please verify account within 1 minute";
    }
    public CourseRegisterUserDTO getCourseRegisterByUser(int user, int course) {
        Optional<CourseRegister> optional = courseRegisterRepository.findByUserIdAndCourseId(user, course);
        if (optional.isPresent()) {
            CourseRegister courseRegister = optional.get();
            CourseRegisterUserDTO dto = new CourseRegisterUserDTO();
            dto.setUserId(courseRegister.getUserByUserId().getId());
            dto.setCourseId(courseRegister.getCourseByCourseId().getId());
            dto.setUsercourseId(courseRegister.getCourseByCourseId().getUserId());
            dto.setActive(courseRegister.getCourseByCourseId().getActive());
            dto.setIsActive(courseRegister.getIsActive());
            return dto;
        }
        return null;
    }
    public List<RegisterCourseDTO> getRegisterCourse(int userId) {
        List<RegisterCourseDTO> result = new ArrayList<>();
        List<CourseRegister> courseRegisters = courseRegisterRepository.findByUserId(userId);
        for (CourseRegister courseRegister : courseRegisters) {
            RegisterCourseDTO dto = new RegisterCourseDTO();
            dto.setCourseId(courseRegister.getCourseId());
            dto.setUserId(courseRegister.getUserId());
            dto.setTitle(courseRegister.getCourseByCourseId().getTitle());
            dto.setImage(courseRegister.getCourseByCourseId().getImage());
            dto.setName(courseRegister.getCourseByCourseId().getUserByUserId().getFullname());
            dto.setActive(courseRegister.getCourseByCourseId().getActive());
            dto.setIsActive(courseRegister.getIsActive());
            dto.setDeleted(courseRegister.getCourseByCourseId().getIsDeleted());
            dto.setIsDeleted(courseRegister.getIsDeleted());
            result.add(dto);
        }
        return result;
    }
    public Long countStudentsForCourse(int courseId) {
        return courseRegisterRepository.countDistinctUsersByCourseId(courseId);
    }
    public String updateOtpSendEmail(int id) {
        Optional<CourseRegister> courseRegister = courseRegisterRepository.findById(id);
        String otp = otpUtil.generateOtp();
        emailUtil.guiMakichHoatKhoahoc(courseRegister.get().getUserByUserId().getEmail(), otp, courseRegister.get().getCourseByCourseId().getTitle());
        if (courseRegister.isPresent()) {
            CourseRegister cr1 = courseRegister.get();
            cr1.setOtp(otp);
            courseRegisterRepository.save(cr1);
        }
        return "Email sent... please verify account within 1 minute";
    }
    public List<UserRegisterCourse> getCourseRegisterNoCheck() {
        List<CourseRegister> newestCourses = courseRegisterRepository.findAll()
                .stream()
                .filter(courseRegister ->  courseRegister.getIsDeleted() == null )
                .collect(Collectors.toList());
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        for (CourseRegister courseRegister : newestCourses) {
            UserRegisterCourse ur1 = new UserRegisterCourse();
            ur1.setEmail(courseRegister.getUserByUserId().getEmail());
            ur1.setUserId(courseRegister.getUserByUserId().getId());
            ur1.setFullname(courseRegister.getUserByUserId().getFullname());
            ur1.setCourseId(courseRegister.getCourseByCourseId().getId());
            ur1.setCourse_name(courseRegister.getCourseByCourseId().getTitle());
            ur1.setRegister_course_id(courseRegister.getId());
            ur1.setIsActive(courseRegister.getIsActive());
            ur1.setOtp(courseRegister.getOtp());
            ur1.setUpdateAt(courseRegister.getUpdateAt());
            ur1.setCreateAt(courseRegister.getCreateAt());
            ur1.setPhone(courseRegister.getUserByUserId().getPhone());
            ur1.setPrice(courseRegister.getCourseByCourseId().getPromotional_price());
            userRegisterCourses.add(ur1);
        }
        return userRegisterCourses;
    }
    public String TuChoiGuiMaXacNhan(int id) {
        Optional<CourseRegister> courseRegister = courseRegisterRepository.findById(id);
        if (courseRegister.isPresent()) {
            CourseRegister cr1 = courseRegister.get();
            cr1.setIsDeleted(true);
            courseRegisterRepository.save(cr1);
        }
        return "Course_Register will be detele";
    }
    public String registerCourse(int user_id, int course_id, String otp) {
        try{
            CompletableFuture<CourseRegister> future = new CompletableFuture<>();
            CourseRegister newCr = new CourseRegister();
            newCr.setCourseId(course_id);
            newCr.setUserId(user_id);
            newCr.setIsActive(false);
            newCr.setOtp(otp);
            CourseRegister savedVideo = courseRegisterRepository.save(newCr);
            future.complete(savedVideo);
            return "User registration successful";
        } catch (Exception e) {
            e.printStackTrace();
            return "User registration failed";
        }
    }
    public String activeCourse(int id) {
        Optional<CourseRegister> courseRegister = courseRegisterRepository.findById(id);
        CourseRegister cr1 = courseRegister.get();
        if (courseRegister.isPresent()) {
                cr1.setIsActive(true);
                courseRegisterRepository.save(cr1);
            }else{
            return "Course_Register is no actived";
        }
        return "Course_Register is actived";
    }
    public int totalSoldCourseInMonth(String monthYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfMonth = LocalDate.parse("01-" + monthYear, formatter).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        return courseRegisters.size();
    }
    public int totalCourse() {
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourseAll(
                true,
                false
        );
        return courseRegisters.size();
    }
    public double totalCoursePerTeacher(int teacherId) {
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourseAll(
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        int count = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                count ++;
            }
        }
        return count;
    }
    public double totalPrice() {
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourseAll(
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
        }
        return totalPrice;
    }
    public double totalPricePerTeacher(int teacherId) {
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourseAll(
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
            }
        }
        return totalPrice*0.75;
    }
    public double totalPriceCourseInMonth(String monthYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfMonth = LocalDate.parse("01-" + monthYear, formatter).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());

        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
        }
        return totalPrice;
    }
    //totalPriceInTime
    public double totalPriceInTime(String dateBegin, String dateFinish) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime dateStart = LocalDate.parse(dateBegin, formatter).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.parse(dateFinish, formatter).atStartOfDay();

        Date startDate = Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant());

        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
        }
        return totalPrice;
    }
    public double totalSoldInTime(String dateBegin, String dateFinish) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime dateStart = LocalDate.parse(dateBegin, formatter).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.parse(dateFinish, formatter).atStartOfDay();

        Date startDate = Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant());

        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        return courseRegisters.size();
    }
    public double totalPriceInTimePerTeacher(String dateBegin, String dateFinish,int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime dateStart = LocalDate.parse(dateBegin, formatter).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.parse(dateFinish, formatter).atStartOfDay();
        Date startDate = Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
            }
        }
        return totalPrice*0.75;
    }
    public double totalSoldInTimePerTeacher(String dateBegin, String dateFinish,int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime dateStart = LocalDate.parse(dateBegin, formatter).atStartOfDay();
        LocalDateTime dateEnd = LocalDate.parse(dateFinish, formatter).atStartOfDay();

        Date startDate = Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant());

        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        int count = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                count++;
            }
        }
        return count;
    }
    public double totalPriceCourseInDay(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfDay = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
        }
        return totalPrice;
    }

    public double totalPriceCourseInDayPerTeacher(String date, int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfDay = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
            }
        }
        return totalPrice*0.75;
    }

    public double totalCourseInDayPerTeacher(String date, int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfDay = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        int count = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                count++;
            }
        }
        return count;
    }
    public double totalPriceCourseInMonthPerTeacher(String monthYear, int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfMonth = LocalDate.parse("01-" + monthYear, formatter).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        double totalPrice = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                totalPrice += courseRegister.getCourseByCourseId().getPromotional_price();
            }
        }
        return totalPrice*0.75;
    }
    public double totalCourseInMonthPerTeacher(String monthYear, int teacherId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfMonth = LocalDate.parse("01-" + monthYear, formatter).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        List<UserRegisterCourse> userRegisterCourses = new ArrayList<>();
        int count = 0;
        for (CourseRegister courseRegister : courseRegisters) {
            int teacher_id = courseRegister.getCourseByCourseId().getUserId();
            if(teacher_id==teacherId){
                count++;
            }
        }
        return count;
    }
    public int totalSoldCourseInYear(String year) {
        LocalDateTime startOfYear = LocalDate.parse(year + "-01-01").atStartOfDay();
        LocalDateTime endOfYear = startOfYear.plusYears(1).minusNanos(1);
        Date startDate = Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfYear.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        return courseRegisters.size();
    }
    public int totalSoldCourseInDay(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfDay = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                true,
                false
        );
        return courseRegisters.size();
    }
    public int totalSoldCourseInDayNoActive(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startOfDay = LocalDate.parse(date, formatter).atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
        List<CourseRegister> courseRegisters = courseRegisterRepository.findTotalSoldCourse(
                startDate,
                endDate,
                false,
                false
        );
        return courseRegisters.size();
    }
    public boolean isCourseRegisterValid(int userId, int courseId) {
        Optional<CourseRegister> optionalCourseRegister = courseRegisterRepository.findByUserIdAndCourseIdAndIsActiveAndIsDeletedNot(userId, courseId);
        return optionalCourseRegister.isPresent();
    }

    public int getTotalRegistrationsByUserId(int userId) {
        return courseRegisterRepository.countTotalRegistrationsByUserId(userId);
    }

    @Async
    public CompletableFuture<CourseRegisterDto> create(CourseRegisterCreateDto input) {
        Optional<CourseRegister> existingCourseRegister = courseRegisterRepository.findByUserIdAndCourseIdAndIsActiveAndIsDeletedNot(
                input.getUserId(),
                input.getCourseId()
        );
        if (!existingCourseRegister.isPresent()) {
            CourseRegister course = new CourseRegister();
            course.setCourseId(input.getCourseId());
            course.setIsActive(input.getIsActive());
            course.setOtp(input.getOtp());
            course.setUserId(input.getUserId());
            CourseRegister savedCourse = courseRegisterRepository.save(course);
            return CompletableFuture.completedFuture(toDto.map(savedCourse, CourseRegisterDto.class));
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }


}

