package com.petra.chatapp.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.petra.chatapp.R
import com.petra.chatapp.activities.Sign_In
import com.petra.chatapp.adapter.OnUserClickListener
import com.petra.chatapp.adapter.RecentChatAdapter
import com.petra.chatapp.adapter.UserAdapter
import com.petra.chatapp.adapter.onRecentChatClicked
import com.petra.chatapp.databinding.FragmentHomeBinding
import com.petra.chatapp.modal.RecentChats
import com.petra.chatapp.modal.Users
import com.petra.chatapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class HomeFragment : Fragment(), OnUserClickListener, onRecentChatClicked {
    lateinit var recyclerViewUsers: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homebinding: FragmentHomeBinding
    lateinit var fbaseAuth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentchatadapter: RecentChatAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homebinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homebinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        fbaseAuth = FirebaseAuth.getInstance()
        toolbar = view.findViewById(R.id.toolbarMain)
        circleImageView = toolbar.findViewById(R.id.tlImage)
        homebinding.lifecycleOwner = viewLifecycleOwner

        userAdapter = UserAdapter()
        recyclerViewUsers = view.findViewById(R.id.rvUsers)
        val layoutManagerUsers = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUsers.layoutManager = layoutManagerUsers
        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {
            userAdapter.setUserList(it)

            recyclerViewUsers.adapter = userAdapter
        })
        userAdapter.setOnUserClickListener(this)
        homebinding.logOut.setOnClickListener{
            fbaseAuth.signOut()
            startActivity(Intent(requireContext(), Sign_In::class.java))
        }
        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(circleImageView)
        })

        recentchatadapter = RecentChatAdapter()
        userViewModel.getRecentChat().observe(viewLifecycleOwner, Observer {
            homebinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)
            recentchatadapter.setOnRecentList(it)
            homebinding.rvRecentChats.adapter = recentchatadapter
        })
        recentchatadapter.setOnRecentChatListener(this)

        circleImageView.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
    }

    override fun onUserSelected(position: Int, users: Users) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)

        Log.e("HomeFragment", "ClickedOn${users.username}")

    }

    override fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatfromHome(recentchatlist)
        view?.findNavController()?.navigate(action)
    }


}