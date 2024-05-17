package com.triadss.doctrack2.dto;
import com.google.firebase.Timestamp;

public class NotificationDTO {
    private String content, reciver, title;
    private Timestamp dateSent;
    public String getContent(){return content;}
    public void setContent(String content){ this.content = content;}
    public Timestamp getDateSent() {
        return dateSent;
    }
    public void setDateSent(Timestamp dataSent) {this.dateSent = dataSent;}
    public String getReciver(){return reciver;}
    public void setReciver(String reciver){ this.reciver = reciver;}
    public String getTitle(){return title;}
    public void setTitle(String tile){ this.title = tile;}
}
