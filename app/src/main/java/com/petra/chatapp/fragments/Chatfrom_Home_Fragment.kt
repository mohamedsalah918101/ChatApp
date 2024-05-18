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
import com.petra.chatapp.databinding.FragmentChatfromHomeBinding
import com.petra.chatapp.modal.Messages
import com.petra.chatapp.modal.Users
import com.petra.chatapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class Chatfrom_Home_Fragment : Fragment() {

    private lateinit var args: Chatfrom_Home_FragmentArgs
    private lateinit var chatfromhomeBinding: FragmentChatfromHomeBinding
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
        chatfromhomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom__home, container, false)

        return chatfromhomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = Chatfrom_Home_FragmentArgs.fromBundle(requireArguments())

        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        chattoolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = chattoolbar.findViewById(R.id.chatImageViewUser)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        tvUserName = view.findViewById(R.id.chatUserName)
        backbtn = chattoolbar.findViewById(R.id.chatBackBtn)

        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatfromHome_to_homeFragment)
        }


        Glide.with(requireContext()).load(args.recentchats.friendimage).into(circleImageView)
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(args.recentchats.friendid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val userModel = value.toObject(Users::class.java)
                    tvStatus.setText(userModel!!.status.toString())
                }
            }

        tvUserName.setText(args.recentchats.name)

        chatfromhomeBinding.viewModel = chatAppViewModel
        chatfromhomeBinding.lifecycleOwner = viewLifecycleOwner

        chatfromhomeBinding.sendBtn.setOnClickListener {
            chatAppViewModel.sendMessage(
                Utils.getUiLoggedIn(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendimage!!
            )
        }

        chatAppViewModel.getMessages(args.recentchats.friendid!!)
            .observe(viewLifecycleOwner, Observer {
                initRecyclerView(it)

            })


    }

    private fun initRecyclerView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatfromhomeBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatfromhomeBinding.messagesRecyclerView.adapter = messageAdapter

    }


}