package com.petra.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.petra.chatapp.R
import com.petra.chatapp.Utils
import com.petra.chatapp.modal.Messages

class MessageAdapter: RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()
    private val left = 0
    private val right = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == right){
            val view = inflater.inflate(R.layout.chatitemright,parent,false)
            MessageHolder(view)
        }else{
            val view = inflater.inflate(R.layout.chatitemleft,parent,false)
            MessageHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]
        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.setText(message.message)
        holder.timeOfSent.text = message.time?.substring(0,5)?: ""
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == Utils.getUiLoggedIn()) right else left

    fun setMessageList(newList: List<Messages>){
        this.listOfMessage = newList
    }
}

class MessageHolder(itemView: View):ViewHolder(itemView){
    val messageText = itemView.findViewById<TextView>(R.id.show_message)
    val timeOfSent = itemView.findViewById<TextView>(R.id.timeView)

}