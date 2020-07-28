package com.example.messengerapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasses.ChatList
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.Notifications.FirebaseInstance
import com.example.messengerapp.Notifications.Token

import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var mUsersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chats, container, false)
        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context!!)
        mUsersChatList = ArrayList()
        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsersChatList as ArrayList).clear()
                for (dataSnapshot in p0.children) {
                    val chatlist = dataSnapshot.getValue(ChatList::class.java)
                    (mUsersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

        })
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    private fun retrieveChatList() {
        mUsers = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in p0.children) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in mUsersChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId())) {
                            (mUsers as ArrayList<Users>).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers as ArrayList<Users>, true)
                recycler_view_chatlist.adapter = userAdapter
            }

        })
    }
}
