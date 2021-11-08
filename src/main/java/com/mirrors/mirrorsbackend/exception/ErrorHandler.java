package com.mirrors.mirrorsbackend.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {IllegalStateException.class, IllegalArgumentException.class})
    private ModelAndView handleConflict(RuntimeException exception, WebRequest request) {
        ModelAndView view;

        if (exception.getMessage().contains("Token Error:"))
            view = new ModelAndView("redirect:/api/login");
        else {
            String requestURL = ((ServletWebRequest) request).getRequest().getRequestURI();
            System.out.println(requestURL);
            String redirectURL = "redirect:" + requestURL;
            view = new ModelAndView(redirectURL);
            if (request.getParameter("token") != null)
                view.addObject("token", request.getParameterValues("token"));
        }

        view.addObject("error", exception.getMessage());
        return view;
    }
}
