package com.interviewiq.messaging.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InboundEmailWebhookRequest {
    
    @JsonProperty("from")
    private String from;
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("subject")
    private String subject;
    
    @JsonProperty("body-plain")
    private String bodyPlain;
    
    @JsonProperty("message-id")
    private String messageId;
}
