package com.kgc.tiku.bluebird.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kgc.tiku.bluebird.R
import com.kgc.tiku.bluebird.entity.Ranking


class ClassRankingListItemAdapter constructor(context: Context, textViewResourceId: Int, objects: List<Ranking>) :
    ArrayAdapter<Ranking>(context, textViewResourceId, objects) {
    private var resourceId: Int = 0

    init {
        resourceId = textViewResourceId
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val ranking = getItem(position) as Ranking
        val view = LayoutInflater.from(context).inflate(resourceId, null)
        view.findViewById<TextView>(R.id.userName).text = ranking.userName
        view.findViewById<TextView>(R.id.percentage).text = ranking.percentage
        view.findViewById<TextView>(R.id.index).text = ranking.index.toString()
        view.findViewById<TextView>(R.id.answerNo).text = ("累计答题 " + ranking.answerNo)
        view.findViewById<TextView>(R.id.actualQuestionNoNow).text = ("实际答题 " + ranking.actualQuestionNoNow.toString())
        return view
    }

}