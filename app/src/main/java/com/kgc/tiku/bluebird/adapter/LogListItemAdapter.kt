package com.kgc.tiku.bluebird.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kgc.tiku.bluebird.R


class LogListItemAdapter(context: Context, textViewResourceId: Int, objects: List<String>) :
    ArrayAdapter<String>(context, textViewResourceId, objects) {
    private var resourceId: Int = 0

    init {
        resourceId = textViewResourceId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId, null)
        val textView = view.findViewById<TextView>(R.id.fruit_name)
        textView.text = getItem(position)
        return textView
    }
}