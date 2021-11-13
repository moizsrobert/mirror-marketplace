package com.mirrors.mirrorsbackend.utils;

public interface EmailSender {
    void send(String to, String subject, String email);
}
