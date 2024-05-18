package com.petra.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.petra.chatapp.R
import com.petra.chatapp.Utils
import com.petra.chatapp.adapter.MessageAdapter
import com.petra.chatapp.databinding.FragmentChatBinding
import com.petra.chatapp.modal.Messages
import com.petra.chatapp.modal.Users
import com.petra.chatapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatFragment : Fragment() {
    private lateinit var args: ChatFragmentArgs
    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chattoolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvStatus: TextView
    private lateinit var backbtn: ImageView
    private lateinit var messageAdapter: MessageAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        chatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = ChatFragmentArgs.fromBundle(requireArguments())

        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        chattoolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = chattoolbar.findViewById(R.id.chatImageViewUser)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        tvUserName = view.findViewById(R.id.chatUserName)
        backbtn = chattoolbar.findViewById(R.id.chatBackBtn)

        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }


        Glide.with(requireContext()).load(args.users.imageUrl).into(circleImageView)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(args.users.userid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val userModel = value.toObject(Users::class.java)
                    tvStatus.setText(userModel!!.status.toString())
                }
            }

        tvUserName.setText(args.users.username)

        chatBinding.viewModel = chatAppViewModel
        chatBinding.lifecycleOwner = viewLifecycleOwner

        chatBinding.sendBtn.setOnClickListener {
            chatAppViewModel.sendMessage(Utils.getUiLoggedIn(), args.users.userid!!, args.users.username!!, args.users.imageUrl!!)
        }

        chatAppViewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)

        })


    }

    private fun initRecyclerView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter

    }

}