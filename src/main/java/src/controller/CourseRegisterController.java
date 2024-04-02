package src.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.Dto.CourseRegisterUserDTO;
import src.Dto.ReviewUserDTO;
import src.config.annotation.ApiPrefixController;
import src.config.dto.PagedResultDto;
import src.model.Course;
import src.model.CourseRegister;
import src.service.Course.Dto.CourseCreateDto;
import src.service.Course.Dto.CourseDto;
import src.service.CourseRegister.CourseRegisterService;
import src.service.CourseRegister.Dto.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
@RestController
@ApiPrefixController(value = "/courseRegisters")
public class CourseRegisterController {
    @Autowired
    private CourseRegisterService courseRegisterService;
    @GetMapping()
    public CompletableFuture<List<CourseRegisterDto>> findAll() {
        return courseRegisterService.getAll();
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<CourseRegisterDto> getOne(@PathVariable int id) {
        return courseRegisterService.getOne(id);
    }
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<String>> register(@RequestBody CourseRegisterCreateDto courseRegisterCreateDto) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.register(courseRegisterCreateDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }
    @PutMapping("/verify-account")
    public CompletableFuture<ResponseEntity<String>> verifyAccount(@RequestParam String email,
                                                                   @RequestParam String otp) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.verifyAccount(email, otp);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }

    @PutMapping("/regenerate-otp")
    public CompletableFuture<ResponseEntity<String>> regenerateOtp(@RequestParam String email) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.regenerateOtp(email);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }

    @PatchMapping("/{courseRegisterId}")
    public ResponseEntity<CourseRegister> updateCourseRegisterField(
            @PathVariable int courseRegisterId,
            @RequestBody Map<String, Object> fieldsToUpdate) {

            CourseRegister updatedCourseRegister = courseRegisterService.updateCourseRegister(courseRegisterId, fieldsToUpdate);
            if (updatedCourseRegister != null) {
                return ResponseEntity.ok(updatedCourseRegister);
            } else {
                return ResponseEntity.notFound().build();
            }

    }
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> deleteById(@PathVariable int id) {
        return courseRegisterService.deleteById(id);
    }
    @GetMapping("/revivewer/{userId}/{courseId}")
    public CompletableFuture<ResponseEntity<CourseRegisterUserDTO>> getCourseRegisterByUser(@PathVariable int userId, @PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            CourseRegisterUserDTO course = courseRegisterService.getCourseRegisterByUser(userId, courseId);
            if (course != null) {
                return ResponseEntity.ok(course);
            } else {
                return ResponseEntity.notFound().build();
            }
        });
    }

    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<List<RegisterCourseDTO>>> getReviewsByUserId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> {
            List<RegisterCourseDTO> register = courseRegisterService.getRegisterCourse(userId);
            return ResponseEntity.ok(register);
        });
    }
    @GetMapping("/course/{courseId}/students/count")
    public CompletableFuture<Long> countStudentsForCourse(@PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.countStudentsForCourse(courseId));
    }
    @PutMapping("/confirm-payment/{id}")
    public CompletableFuture<ResponseEntity<String>> confirmPayment(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.updateOtpSendEmail(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }
    @GetMapping("/getcoursenoactive")
    public CompletableFuture<List<UserRegisterCourse>> getNoActiveCourses() {
        return CompletableFuture.supplyAsync(courseRegisterService::getCourseRegisterNoCheck);
    }
    @PutMapping("/reject-confirm-payment/{id}")
    public CompletableFuture<ResponseEntity<String>> rejectConfirmPayment(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.TuChoiGuiMaXacNhan(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }
    @PostMapping("/register-course/{userId}/{courseId}/{otp}")
    public CompletableFuture<ResponseEntity<String>> registerCourse(@PathVariable int userId, @PathVariable int courseId, @PathVariable String otp) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.registerCourse(userId, courseId, otp);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }
    @PutMapping("/active-course/{id}")
    public CompletableFuture<ResponseEntity<String>> activeCourse(@PathVariable int id) {
        return CompletableFuture.supplyAsync(() -> {
            String result = courseRegisterService.activeCourse(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        });
    }
    @GetMapping("/total-sold-in-month/{monthYear}")
    public CompletableFuture<Integer> getTotalSoldCourseInMonth(@PathVariable String monthYear) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldCourseInMonth(monthYear));
    }
    @GetMapping("/total-price-in-month/{monthYear}")
    public CompletableFuture<Double> getTotalPriceCourseInMonth(@PathVariable String monthYear) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceCourseInMonth(monthYear));
    }
    @GetMapping("/total-sold-in-year/{year}")
    public CompletableFuture<Integer> getTotalSoldCourseInYear(@PathVariable String year) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldCourseInYear(year));
    }
    @GetMapping("/total-sold-in-day/{day}")
    public CompletableFuture<Integer> getTotalSoldCourseInDay(@PathVariable String day) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldCourseInDay(day));
    }
    @GetMapping("/total-sold")
    public CompletableFuture<Integer> getTotalSold() {
        return CompletableFuture.supplyAsync(courseRegisterService::totalCourse);
    }
    @GetMapping("/total-price")
    public CompletableFuture<Double> getTotalPrice() {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPrice());
    }

    @GetMapping("/total-price-in-day/{day}")
    public CompletableFuture<Double> getTotalPriceCourseInDay(@PathVariable String day) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceCourseInDay(day));
    }

    @GetMapping("/total-price-in-day-per-teacher/{day}/{teacherId}")
    public CompletableFuture<Double> getTotalPriceCourseInDay(@PathVariable String day, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceCourseInDayPerTeacher(day, teacherId));
    }

    @GetMapping("/total-price-in-month-per-teacher/{day}/{teacherId}")
    public CompletableFuture<Double> getTotalPriceCourseInMonth(@PathVariable String day, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceCourseInMonthPerTeacher(day, teacherId));
    }

    @GetMapping("/total-course-in-month-per-teacher/{day}/{teacherId}")
    public CompletableFuture<Double> getTotalCourseCourseInMonth(@PathVariable String day, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalCourseInMonthPerTeacher(day, teacherId));
    }

    @GetMapping("/total-course-in-day-per-teacher/{day}/{teacherId}")
    public CompletableFuture<Double> getTotalCourseCourseInDay(@PathVariable String day, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalCourseInDayPerTeacher(day, teacherId));
    }

    @GetMapping("/total-course-per-teacher/{teacherId}")
    public CompletableFuture<Double> getTotalCoursePerTeacher(@PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalCoursePerTeacher(teacherId));
    }

    @GetMapping("/total-price-per-teacher/{teacherId}")
    public CompletableFuture<Double> getTotalPricePerTeacher(@PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPricePerTeacher(teacherId));
    }

    @GetMapping("/total-sold-in-day-no-active/{day}")
    public CompletableFuture<Integer> getTotalSoldCourseInDayNoActive(@PathVariable String day) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldCourseInDayNoActive(day));
    }

    @GetMapping("/check/{userId}/{courseId}")
    public CompletableFuture<String> checkCourseRegister(@PathVariable int userId, @PathVariable int courseId) {
        return CompletableFuture.supplyAsync(() -> {
            if (courseRegisterService.isCourseRegisterValid(userId, courseId)) {
                return "true";
            } else {
                return "false";
            }
        });
    }

    @GetMapping("/total-price-in-time/{begin}/{end}")
    public CompletableFuture<Double> getTotalPriceInTime(@PathVariable String begin, @PathVariable String end) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceInTime(begin, end));
    }

    @GetMapping("/total-sold-in-time/{begin}/{end}")
    public CompletableFuture<Double> getTotalSoldInTime(@PathVariable String begin, @PathVariable String end) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldInTime(begin, end));
    }

    @GetMapping("/total-price-in-time-per-teacher/{begin}/{end}/{teacherId}")
    public CompletableFuture<Double> getTotalPriceInTimePerTeacher(@PathVariable String begin, @PathVariable String end, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalPriceInTimePerTeacher(begin, end, teacherId));
    }

    @GetMapping("/total-sold-in-time-per-teacher/{begin}/{end}/{teacherId}")
    public CompletableFuture<Double> getTotalSoldInTimePerTeacher(@PathVariable String begin, @PathVariable String end, @PathVariable int teacherId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.totalSoldInTimePerTeacher(begin, end, teacherId));
    }

    @GetMapping("/{userId}/totalRegistrations")
    public CompletableFuture<Integer> getTotalRegistrationsByUserId(@PathVariable int userId) {
        return CompletableFuture.supplyAsync(() -> courseRegisterService.getTotalRegistrationsByUserId(userId));
    }

    @PostMapping("/vnpay")
    public CompletableFuture<CourseRegisterDto> create(@RequestBody CourseRegisterCreateDto input) {
        return courseRegisterService.create(input);
    }



}
