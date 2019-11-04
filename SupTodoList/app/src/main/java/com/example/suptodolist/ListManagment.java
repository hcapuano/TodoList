package com.example.suptodolist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ListManagment extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<String>();
    public int itemToDelete;
    private Context context;

    ListManagment(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View viewTodoList = convertView;
        if (viewTodoList == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewTodoList = inflater.inflate(R.layout.activity_listview, null);
        }

        TextView listItemText = viewTodoList.findViewById(R.id.label);
        listItemText.setText(list.get(position));

        Button deleteBtn = viewTodoList.findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                itemToDelete = position;
                list.remove(position);
                notifyDataSetChanged();
                //TODO GET id with list Index to delete, then UPDATE REQUEST API + Update BDD
            }
        });

        if(viewTodoList.getContext().getClass().getSimpleName().equals("ListActivity")){
            deleteBtn.setVisibility(View.INVISIBLE);
        }

        return viewTodoList;
    }
}
