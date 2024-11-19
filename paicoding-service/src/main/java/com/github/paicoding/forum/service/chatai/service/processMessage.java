package com.github.paicoding.forum.service.chatai.service;

import org.springframework.stereotype.Service;

@Service
public interface processMessage {
    void processMessage(String msg);
}
