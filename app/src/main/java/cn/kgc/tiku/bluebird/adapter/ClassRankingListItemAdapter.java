package cn.kgc.tiku.bluebird.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.kgc.tiku.bluebird.R;
import cn.kgc.tiku.bluebird.entity.Ranking;
import cn.kgc.tiku.bluebird.utils.Contant;

/**
 * Created by star on 2018/8/19.
 */

public class ClassRankingListItemAdapter extends ArrayAdapter {
    private final int resourceId;

    public ClassRankingListItemAdapter(Context context, int textViewResourceId, List<Ranking> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ranking ranking = (Ranking) getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ((TextView) view.findViewById(R.id.mc)).setText(String.valueOf(ranking.getMc()));
        ((TextView) view.findViewById(R.id.xm)).setText(ranking.getXm());
        ((TextView) view.findViewById(R.id.ljdt)).setText(String.valueOf(ranking.getLjdt()));
        ((TextView) view.findViewById(R.id.sjdt)).setText(String.valueOf(ranking.getSjdt()));
        ((TextView) view.findViewById(R.id.zql)).setText(String.valueOf(ranking.getZql()+"%"));
        if(ranking.getXm().equals(Contant.userInfo.getUserName())){
            view.setBackgroundColor(Color.parseColor("#ff4081"));
        }
        return view;
    }
}
