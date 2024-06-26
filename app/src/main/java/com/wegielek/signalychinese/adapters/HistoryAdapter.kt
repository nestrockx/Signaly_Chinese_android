package com.wegielek.signalychinese.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wegielek.signalychinese.R
import com.wegielek.signalychinese.database.History
import com.wegielek.signalychinese.interfaces.HistoryRecyclerViewListener
import com.wegielek.signalychinese.utils.Utils.Companion.historyToDictionary

class HistoryAdapter(
    private val historyRecyclerViewListener: HistoryRecyclerViewListener,
    private val context: Context
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private val dataList: MutableList<History> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataList: List<History>?) {
        this.dataList.clear()
        this.dataList.addAll(dataList!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_search_result_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = dataList[position]
        val list =
            listOf(*history.translation.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
        val translation = StringBuilder()
        for (i in list.indices) {
            if (i == 0) {
                translation.append(1).append(".\u00A0").append(list[i].trim { it <= ' ' })
                    .append(" ")
            } else if (i != list.size - 1) {
                translation.append(i + 1).append(".\u00A0").append(list[i].trim { it <= ' ' })
                    .append(" ")
            } else {
                translation.append(i + 1).append(".\u00A0").append(list[i].trim { it <= ' ' })
                    .append("")
            }
        }
        if (history.traditionalSign == history.simplifiedSign) {
            holder.charactersTv.text = history.simplifiedSign
        } else {
            holder.charactersTv.text = context.getString(
                R.string.result_text_placeholder_1,
                history.traditionalSign,
                history.simplifiedSign
            )
        }
        holder.pronunciationTv.text = history.pronunciation
        holder.translationTv.text = translation
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val charactersTv: TextView = itemView.findViewById(R.id.labelTv)
        val pronunciationTv: TextView = itemView.findViewById(R.id.pronunciationTv)
        val translationTv: TextView = itemView.findViewById(R.id.translationTv)

        init {
            itemView.setOnClickListener {
                historyRecyclerViewListener.onHistoryClicked(
                    historyToDictionary(
                        dataList[adapterPosition]
                    )
                )
            }
            itemView.setOnLongClickListener {
                historyRecyclerViewListener.onLongHistoryClicked(dataList[adapterPosition].time)
                true
            }
        }
    }
}