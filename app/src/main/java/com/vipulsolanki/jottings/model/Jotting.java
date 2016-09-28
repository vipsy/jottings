package com.vipulsolanki.jottings.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Jotting extends RealmObject {

    public Jotting() {

    }

    public static Jotting create(Realm realm) {
        return realm.createObject(Jotting.class);
    }

    public static RealmResults<Jotting> findAll(Realm realm) {
        return realm.where(Jotting.class).findAll().sort("dateCreated");
    }

    public static Jotting find(Realm realm, long id) {
        return realm.where(Jotting.class).equalTo("id", id).findFirst();
    }
    public static RealmResults<Jotting> findPending(Realm realm) {
        return realm.where(Jotting.class).equalTo("isFinished", false).findAll();
    }

    public static RealmResults<Jotting> findFinished(Realm realm) {
        return realm.where(Jotting.class).equalTo("isFinished", true).findAll();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Jotting{" +
                "header='" + header + '\'' +
                ", body='" + body + '\'' +
                ", dateCreated=" + dateCreated +
                ", isFinished=" + isFinished +
                '}';
    }

    private String header;
    private String body;
    private Date dateCreated;
    private boolean isFinished;

    @PrimaryKey
    private long id;

}
