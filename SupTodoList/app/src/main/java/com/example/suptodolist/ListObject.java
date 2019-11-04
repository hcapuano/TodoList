package com.example.suptodolist;

import java.io.Serializable;

public class ListObject implements Serializable {

    private String listID;
    private String lastUpdate;
    private String userCreator;
    private String userInvited;
    private String todo;

    public ListObject(String listID, String lastUpdate, String userCreator, String userInvited, String todo) {
        this.listID = listID;
        this.lastUpdate = lastUpdate;
        this.userCreator = userCreator;
        this.userInvited = userInvited;
        this.todo = todo;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(String userCreator) {
        this.userCreator = userCreator;
    }

    public String getUserInvited() {
        return userInvited;
    }

    public void setUserInvited(String userInvited) {
        this.userInvited = userInvited;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String[] toArrayString() {
        return new String[]{
                listID,
                lastUpdate,
                userCreator,
                userInvited,
                todo
        };
    }
}
