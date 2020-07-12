package com.example.callblocker.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.callblocker.R
import com.example.callblocker.model.BlockedNumber

class BlackListAdapter (private val blackList: List<BlockedNumber>?,
                        private val onDelete: (blockedNumber: BlockedNumber) -> Unit
) : RecyclerView.Adapter<BlackListAdapter.ViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, index: Int): ViewHolder {
        val rootView = LayoutInflater.from(viewGroup.context).inflate(R.layout.blacklist_row, viewGroup, false)
        return ViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return blackList?.size!!
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, index: Int) {
        val blockedNumber = blackList?.get(index)
        if(blockedNumber?.name == null) {
            viewHolder.name.text = blockedNumber?.number
            viewHolder.number.visibility = View.GONE
        } else {
            viewHolder.name.text = blockedNumber?.name
            viewHolder.number.text = blockedNumber?.number
            viewHolder.number.visibility = View.VISIBLE
        }
        blockedNumber?.let { viewHolder.deleteButton.setOnClickListener { onDelete(blockedNumber) } }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val name: TextView = itemView.findViewById(R.id.tv_name) as TextView
        val number: TextView = itemView.findViewById(R.id.tv_number) as TextView
        val deleteButton: ImageButton = itemView.findViewById(R.id.btn_delete) as ImageButton
    }

}
