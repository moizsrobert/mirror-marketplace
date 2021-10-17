package com.mirrors.mirrorsbackend.email;

public interface EmailSender {
    void send(String to, String email);
}
