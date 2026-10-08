package com.sahana.sahanamart.dto;

public class ChatResponseDTO {
    private String reply;
    private String provider;
    private boolean cached;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String reply, String provider, boolean cached) {
        this.reply = reply;
        this.provider = provider;
        this.cached = cached;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
