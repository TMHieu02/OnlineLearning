package src.config.swagger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import src.config.dto.SuccessResponseDto;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Component
public class CustomOpenApiResponseInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            // Check if the method has the CustomOpenApiResponse annotation
            OpenApiResponse annotation = method.getAnnotation(OpenApiResponse.class);
            if (annotation != null) {
                // Get the response type and check if it is a ResponseEntity
                Type returnType = method.getGenericReturnType();
                if (returnType instanceof ParameterizedType parameterizedType) {
                    Type rawType = parameterizedType.getRawType();
                    if (rawType.equals(ResponseEntity.class)) {
                        // Get the response body and apply the SuccessResponseDto to it
                        ResponseEntity<?> responseEntity = (ResponseEntity<?>) modelAndView.getModel().get("responseEntity");
                        Object responseBody = responseEntity.getBody();
                        SuccessResponseDto<?> successResponseDto = new SuccessResponseDto<>(responseBody);
                        modelAndView.getModel().put("responseEntity", ResponseEntity.ok(successResponseDto));
                    }
                }
            }
        }
    }
}
