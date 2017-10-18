package com.example.wasike.lycitybot.models;

import java.util.Date;
import org.parceler.Parcel;

@Parcel
public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private boolean isSend;

    public ChatMessage(String messageText, String messageUser, boolean isSend) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.isSend = isSend;
        messageTime = new Date().getTime();
    }

    public ChatMessage() {}

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
