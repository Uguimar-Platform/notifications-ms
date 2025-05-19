package com.uguimar.notificationsms.domain.model;

public class Email {
    private String to;
    private String subject;
    private String code;

    public Email(String to, String subject, String code) {
        this.to = to;
        this.subject = subject;
        this.code = code;
    }

    public Email(){
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
