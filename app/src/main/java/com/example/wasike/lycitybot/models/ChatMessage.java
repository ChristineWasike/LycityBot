package com.example.wasike.lycitybot.models;

import java.util.Date;
import org.parceler.Parcel;

@Parcel
public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private boolean isSend;
    private String pushId;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    public ChatMessage() {}

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public boolean getSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        this.isSend = send;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
