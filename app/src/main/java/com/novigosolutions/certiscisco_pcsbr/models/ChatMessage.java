package com.novigosolutions.certiscisco_pcsbr.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "ChatMessage")
public class ChatMessage extends Model {

    @Column(name = "MessageId")
    public int MessageId;

    @Column(name = "Message")
    public String Message;

    @Column(name = "CreatedDt")
    public String CreatedDt;

    @Column(name = "IsFromDevice")
    public boolean IsFromDevice;

    @Column(name = "IsRead")
    public boolean IsRead;

    public ChatMessage() {

    }

    public ChatMessage(int MessageId, String message, String createdDt, boolean isFromDevice) {
        this.MessageId = MessageId;
        Message = message;
        CreatedDt = createdDt;
        IsFromDevice = isFromDevice;
    }

    public static int getLastMessageId() {
        List<ChatMessage> chatMessages = getAllMessage();
        if (chatMessages.size() > 0) return chatMessages.get(chatMessages.size() - 1).MessageId;
        else return 0;
    }

    public static String getLastMessage() {
        List<ChatMessage> chatMessages = getAllMessage();
        if (chatMessages.size() > 0) return chatMessages.get(chatMessages.size() - 1).Message;
        else return "";
    }

    public static boolean isExist(int MessageId) {
        return null != new Select().from(ChatMessage.class)
                .where("MessageId=?", MessageId)
                .executeSingle();
    }


    public static List<ChatMessage> getAllMessage() {
        List<ChatMessage> messages = new Select().from(ChatMessage.class)
                .where("MessageId > -1")
                .orderBy("MessageId asc")
                .execute();

        messages.addAll(getOfflineMessages());
        return messages;
    }

    public static List<ChatMessage> getOfflineMessages() {
        return new Select().from(ChatMessage.class)
                .where("MessageId < 0")
                .orderBy("MessageId asc")
                .execute();
    }

    public static void deleteOfflineMessages() {
        new Delete().from(ChatMessage.class)
                .where("MessageId < 0")
                .execute();
    }

    public static void setRead(int MessageId) {
        new Update(ChatMessage.class)
                .set("IsRead=?", 1)
                .where("MessageId=?", MessageId)
                .execute();
    }

    public static List<ChatMessage> getUnreadMessages() {
        return new Select().from(ChatMessage.class)
                .where("IsRead=? AND IsFromDevice=?", 0, 0)
                .execute();
    }

    public static void remove() {
        new Delete().from(ChatMessage.class)
                .execute();
    }
}