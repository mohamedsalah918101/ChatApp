package com.petra.chatapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.petra.chatapp.R
import com.petra.chatapp.modal.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter: RecyclerView.Adapter<RecentChatHolder>() {
    private var listofchats = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null
    private var recentModel = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist,parent,false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listofchats.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {
        val recentchatlist = listofchats[position]
        recentModel = recentchatlist
        holder.userName.setText(recentchatlist.name)
        val themessage = recentchatlist.message!!.split("").take(6).joinToString("")
        val makelastmessage = "${recentchatlist.person}: $themessage"
        holder.lastMessage.setText(makelastmessage)

        Glide.with(holder.itemView.context).load(recentchatlist.friendimage).into(holder.imageView)
        holder.timeView.setText(recentchatlist.time!!.substring(0,5))

        holder.itemView.setOnClickListener{
            listener?.getOnRecentChatClicked(position,recentchatlist)
        }
    }
    fun setOnRecentChatListener(listener: onRecentChatClicked){
        this.listener = listener
    }
    fun setOnRecentList(list: List<RecentChats>){
        this.listofchats = list
    }
}
class RecentChatHolder(itemview: View) : ViewHolder(itemview){
    val imageView : CircleImageView = itemview.findViewById(R.id.recentChatImageView)
    val userName:TextView = itemview.findViewById(R.id.recentChatTextName)
    val lastMessage:TextView = itemview.findViewById(R.id.recentChatTextLastMessage)
    val timeView:TextView = itemview.findViewById(R.id.recentChatTextTime)
}
interface onRecentChatClicked{
    fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats)
}