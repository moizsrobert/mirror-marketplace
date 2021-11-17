package com.mirrors.mirrorsbackend.exception;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    private void handleConflict(RuntimeException error,
                                HttpSession session,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        error.printStackTrace();
        ExceptionLogger.logException(error);
        session.setAttribute("error", error);
        session.setAttribute("exceptionConsumer", new SessionExceptionConsumer(session));
        String landing;
        if (request.getParameter("token") != null) {
            String parameter = "?token=" + request.getParameterValues("token")[0];
            landing = ((error instanceof TokenException) ? "/api/login" : request.getRequestURI() + parameter);
        } else landing = ((error instanceof TokenException) ? "/api/login" : request.getRequestURI());
        response.sendRedirect(landing);
    }
}
