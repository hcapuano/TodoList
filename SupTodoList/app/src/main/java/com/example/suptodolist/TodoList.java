package com.example.suptodolist;

import java.io.Serializable;
import java.util.ArrayList;

public class TodoList implements Serializable {

    private ArrayList<ListObject> lists;

    public TodoList(ArrayList<ListObject> newTodoList) {
        this.lists = newTodoList;
    }

    public ArrayList<ListObject> getTodoList() {
        return lists;
    }

    public void setTodoList(ArrayList<ListObject> newTodoList) {
        this.lists = newTodoList;
    }

    public void add(ListObject newList){
        this.lists.add(newList);
    }
}
