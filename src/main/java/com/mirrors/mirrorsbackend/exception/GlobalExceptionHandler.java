package com.mirrors.mirrorsbackend.exception;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public void handleConflict(RuntimeException error,
                               HttpServletResponse response) throws IOException {
        error.printStackTrace();
        ExceptionLogger.logException(error);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(error.getMessage());
        if (error instanceof TokenException)
            response.sendRedirect("/api/login?token_error");
    }
}
