package com.mirrors.mirrorsbackend.util;

public interface EmailSender {
    void send(String to, String subject, String email);
}
