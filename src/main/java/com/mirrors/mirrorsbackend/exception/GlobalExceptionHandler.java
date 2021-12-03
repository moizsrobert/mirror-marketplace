package com.mirrors.mirrorsbackend.exception;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MarketplaceException.class)
    public void handleConflict(MarketplaceException exception,
                               HttpServletResponse response) throws IOException {
        exception.printStackTrace();
        MarketplaceExceptionLogger.getInstance().logException(exception);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(exception.getMessage());
        if (exception instanceof TokenException)
            response.sendRedirect("/api/login?token_error");
    }
}
