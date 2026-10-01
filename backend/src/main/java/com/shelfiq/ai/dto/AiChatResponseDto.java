package com.shelfiq.ai.dto;

import java.util.List;
import java.util.Map;

public class AiChatResponseDto {
    private String reply;
    private String intent;
    private Map<String, Object> contextData;
    private List<String> suggestedFollowups;

    public AiChatResponseDto() {
    }

    public AiChatResponseDto(String reply, String intent, Map<String, Object> contextData, List<String> suggestedFollowups) {
        this.reply = reply;
        this.intent = intent;
        this.contextData = contextData;
        this.suggestedFollowups = suggestedFollowups;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public List<String> getSuggestedFollowups() {
        return suggestedFollowups;
    }

    public void setSuggestedFollowups(List<String> suggestedFollowups) {
        this.suggestedFollowups = suggestedFollowups;
    }
}
