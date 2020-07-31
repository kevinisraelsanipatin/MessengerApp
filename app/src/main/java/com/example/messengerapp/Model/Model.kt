package com.example.messengerapp.Model

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

object Model {
    private val firebaseDbRef = FirebaseDatabase.getInstance().reference
    private val firebaseAuthRef = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = firebaseAuthRef.currentUser
    private var chatListListener: ValueEventListener? = null
    private var isSearching = false
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

    private fun retrieveChatList(context: Context, mUsersChatList: List<ChatList>) {
        val ref = getChild("Users")

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
                Presenter.updateChatFrag(UserAdapter(context!!, mUsers as ArrayList<Users>, true))
            }
        })
    }

    fun getUsersList(context: Context) {
        if (currentUser == null) currentUser = firebaseAuthRef.currentUser
        val firebaseUserID = currentUser!!.uid

        val refUsers = getChild("Users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val mUsers = ArrayList<Users>()
                if (!isSearching) {
                    for (snapshot in p0.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (!(user!!.getUID()).equals(firebaseUserID)) {
                            mUsers.add(user)
                        }
                    }
                    Presenter.updateSearchList(UserAdapter(context, mUsers, false))
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun searchFor(string: String, context: Context) {
        isSearching = string != ""
        if (currentUser == null) currentUser = firebaseAuthRef.currentUser
        val firebaseUserID = currentUser!!.uid

        val queryUsers = getChild("Users").orderByChild("search")
            .startAt(string)
            .endAt(string + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val mUsers = ArrayList<Users>()
                for (snapshot in p0.children) {

                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        mUsers.add(user)
                    }
                }
                try {
                    Presenter.updateSearchList(UserAdapter(context, mUsers, false))

                } catch (_: java.lang.NullPointerException) {
                    Log.e("NullPESearch", "NullPESearch")
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    fun updateStatus(status: String) {
        val ref = getChild("Users").child(currentUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
    }

    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    fun registerUser(username:String, email:String, password:String, context: Context) {
        firebaseAuthRef.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUserID = currentUser!!.uid
                    val refUsers = getChild("Users")
                        .child(firebaseUserID)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] =
                        "https://firebasestorage.googleapis.com/v0/b/chatapp-205b5.appspot.com/o/profile.png?alt=media&token=8aed6de1-33dd-4379-897f-afc7edcb83cf"
                    userHashMap["cover"] =
                        "https://firebasestorage.googleapis.com/v0/b/chatapp-205b5.appspot.com/o/cover.jpg?alt=media&token=e2b01e85-2f8a-4a10-8ebb-b8d32fa9c9f0"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent =
                                    Intent(context, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                                (context as AppCompatActivity).finish()
                            }
                        }
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