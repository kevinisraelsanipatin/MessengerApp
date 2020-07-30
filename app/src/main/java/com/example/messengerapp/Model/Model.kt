package com.example.messengerapp.Model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.MainActivity
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.Notifications.Token
import com.example.messengerapp.Presenter.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

object Model {
    private val firebaseDbRef = FirebaseDatabase.getInstance().reference
    private val firebaseAuthRef = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = firebaseAuthRef.currentUser
    private var chatListListener: ValueEventListener? = null
    fun loginUser(email: String, password: String, context: Context) {

        when {
            email == "" -> {
                Toast.makeText(context, "Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            password == "" -> {
                Toast.makeText(context, "Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            else -> {
                firebaseAuthRef.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(context, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            (context as AppCompatActivity).finish()
                        } else {
                            Toast.makeText(
                                context,
                                "Mensaje de Error: " + task.exception!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    fun getChild(child: String): DatabaseReference {
        return firebaseDbRef.child(child)
    }

    fun getChild(child: String, ref: DatabaseReference): DatabaseReference {
        return ref.child(child)
    }

    fun updateToken(token: String?) {
        val token1 = Token(token!!)
        firebaseDbRef.child("Tokens").child(currentUser!!.uid).setValue(token1)
    }

    fun getChatList(context: Context) {
        Log.d("Model user", currentUser.toString())
        if (currentUser == null) currentUser = firebaseAuthRef.currentUser

        getChild("ChatList").child(currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var mUsersChatList = ArrayList<ChatList>()
                for (dataSnapshot in p0.children) {
                    val chatlist = dataSnapshot.getValue(ChatList::class.java)
                    mUsersChatList.add(chatlist!!)
                }
                Presenter.updateChatList(mUsersChatList)
                retrieveChatList(context, mUsersChatList)
            }

        })
    }

    private fun retrieveChatList(
        context: Context,
        mUsersChatList: List<ChatList>
    ) {
        val ref = firebaseDbRef.child("Users")
        //FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var mUsers = ArrayList<Users>()
                for (dataSnapshot in p0.children) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in mUsersChatList!!) {
                        if (user!!.getUID().equals(eachChatList.getId())) {
                            (mUsers as ArrayList<Users>).add(user!!)
                        }
                    }
                }
                Log.d("context", context.toString())
                Log.d("users", mUsers.toString())
                Presenter.updateFrag(UserAdapter(context!!, mUsers as ArrayList<Users>, true))
            }
        })
    }
}