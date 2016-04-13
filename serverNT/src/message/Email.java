/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.io.Serializable;

/**
 *
 * @author xaronai
 */
public class Email implements Serializable{
    private boolean isNew;
    private String senter, reciever, subject, mainBody;
    public Email() {
        this.isNew = false;
        this.senter = null;
        this.reciever = null;
        this.subject = null;
        this.mainBody = null;
    }
    public Email(Email mail) {
        this.isNew = true;
        this.senter = mail.senterGetter();
        this.reciever = mail.recieverGetter();
        this.subject = mail.subjectGetter();
        this.mainBody = mail.mainBodyGetter();
    }
    public Email(boolean isNew, String senter, String reciever, String subject, String mainBody) {
        this.isNew = isNew;
        this.senter = senter;
        this.reciever = reciever;
        this.subject = subject;
        this.mainBody = mainBody;
    }
    public Email(String isNew, String senter, String reciever, String subject, String mainBody) {
        this.isNew = isNew.equals("true");
        this.senter = senter;
        this.reciever = reciever;
        this.subject = subject;
        this.mainBody = mainBody;
    }
    public boolean isNewBGetter(){
        return isNew;
    }
    public String isNewSGetter(){
        if ( isNew ) {
            return "true";
        } else {
            return "false";
        }
    }
    public String senterGetter(){
        return senter;
    }
    public String recieverGetter(){
        return reciever;
    }
    public String subjectGetter(){
        return subject;
    }
    public String mainBodyGetter(){
        return mainBody;
    }
    public void isNewSetter(boolean loula){
        this.isNew=loula;
    }
    public void isNewSetter(String loula){
        this.isNew = loula.equals("true");
    }
    public void senterSetter(String senter){
        this.senter = senter;
    }
    public void recieverSetter(String reciever){
        this.reciever = reciever;
    }
    public void subjectSetter(String subject){
        this.subject = subject;
    }
    public void mainBodySetter(String mainBody){
        this.mainBody = mainBody;
    }
    public String soutAllGetter(){
        if ( isNew ) {
            return "true "+senter+" "+reciever+" "+subject+" "+mainBody;
        } else {
            return "false "+senter+" "+reciever+" "+subject+" "+mainBody;
        }
    }
    public String writeAllGetter(){
        if ( isNew ) {
            return "true\n"+senter+"\n"+reciever+"\n"+subject+"\n"+mainBody;
        } else {
            return "false\n"+senter+"\n"+reciever+"\n"+subject+"\n"+mainBody;
        }
    }
}
