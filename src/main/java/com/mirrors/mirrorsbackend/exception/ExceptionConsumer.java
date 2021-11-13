package com.mirrors.mirrorsbackend.exception;

import javax.servlet.http.HttpSession;

public class ExceptionConsumer {
    private final HttpSession session;

    public ExceptionConsumer(HttpSession session) {
        this.session = session;
    }
    
    public void consume() {
        session.removeAttribute("error");
    }
}
