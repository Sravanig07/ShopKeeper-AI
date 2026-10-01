package com.shelfiq.ai.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

public class AiChatRequestDto {

    @NotBlank(message = "Query cannot be blank")
    private String message;

    public AiChatRequestDto() {
    }

    public AiChatRequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
