package src.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.*;
import src.config.annotation.ApiPrefixController;
import src.config.auth.RateLimiterInterceptor;
import src.config.middleware.GlobalApiLoggerInterceptor;

import java.time.Duration;
import java.util.concurrent.Executor;
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Value("dqptxftlv")
    private String cloudName;

    @Value("268148952558612")
    private String apiKey;

    @Value("8gzmcO9n4yChRpHfXAr-8-T6ZXQ")
    private String apiSecret;

    @Autowired
    private GlobalApiLoggerInterceptor globalApiLoggerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalApiLoggerInterceptor);
        registry.addInterceptor(rateLimiterInterceptor(rateLimiterRegistry()));

    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("api/v1", HandlerTypePredicate.forAnnotation(ApiPrefixController.class));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    //AsyncConfig
    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("MyAsyncThread-");
        executor.initialize();
        return executor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://boss-breath-production.up.railway.app/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*");
    }


    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10000)
                .limitRefreshPeriod(Duration.ofHours(1))
                .timeoutDuration(Duration.ofMillis(25))
                .build();
        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiterInterceptor rateLimiterInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        return new RateLimiterInterceptor(rateLimiterRegistry);
    }



}