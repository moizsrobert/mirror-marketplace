package com.mirrors.mirrorsbackend.exception;

import javax.servlet.http.HttpSession;

public class SessionExceptionConsumer {
    private final HttpSession session;

    public SessionExceptionConsumer(HttpSession session) {
        this.session = session;
    }
    
    public void consume() {
        session.removeAttribute("error");
    }
}
