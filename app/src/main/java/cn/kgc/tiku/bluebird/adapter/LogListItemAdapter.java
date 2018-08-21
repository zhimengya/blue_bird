package cn.kgc.tiku.bluebird.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.entity.LogListItem;


public class LogListItemAdapter extends ArrayAdapter {
    private final int resourceId;

    public LogListItemAdapter(Context context, int textViewResourceId, List<LogListItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogListItem item = (LogListItem) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView textView = (TextView) view.findViewById(R.id.fruit_name);//获取该布局内的文本视图
        textView.setText(item.getMessage());//为文本视图设置文本内容
        textView.setTextColor(item.getColor());
        textView.setTag(item);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogListItem item = (LogListItem) v.getTag();
                new AlertDialog.Builder(LogListItemAdapter.this.getContext())
                        .setTitle(item.getLevel())
                        .setMessage(item.getMessage())
                        .setPositiveButton("确定", null)
                        .create().show();
            }
        });
        return view;
    }
}
