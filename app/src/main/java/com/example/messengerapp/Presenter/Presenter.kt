package com.example.messengerapp.Presenter

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Fragments.SearchFragment
import com.example.messengerapp.Model.Model
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.google.firebase.database.DatabaseReference

object Presenter {
    var chatFragRef: ChatsFragment? = null
    var searchFragment: SearchFragment? = null
    fun login(email: String, password: String, context: Context) {
        Model.loginUser(email, password, context)
    }

    fun getChild(child: String, dbref: DatabaseReference?): DatabaseReference {
        return if (dbref == null) Model.getChild(child) else Model.getChild(child, dbref)
    }

    fun updateToken(token: String?) {
        Model.updateToken(token)
    }

    fun manageChats(context: Context, ref: ChatsFragment) {
        chatFragRef = ref
        Model.getChatList(context)
    }

    fun updateChatFrag(uadap: UserAdapter) {
        chatFragRef!!.updateAdapter(uadap)
    }

    fun updateChatList(mUsersChatList: List<ChatList>) {
        chatFragRef!!.updateChatList(mUsersChatList)
    }

    fun getUsers(context: Context, ref: SearchFragment) {
        searchFragment = ref
        Model.getUsersList(context)
    }

    fun updateSearchList(uadap: UserAdapter) {
        searchFragment!!.updateAdapter(uadap)
    }

    fun searchFor(string: String, context: Context) {
        Model.searchFor(string, context)
    }

    fun updateStatus(status: String) {
        Model.updateStatus(status)
    }

    fun isLoggedIn(): Boolean {
        return Model.isLoggedIn()
    }

    fun registerUser(username: String, email: String, password: String, context: Context) {
        when {
            username == "" -> {
                Toast.makeText(context,"Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            email == "" -> {
                Toast.makeText(context,"Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            password == "" -> {
                Toast.makeText(context,"Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            else -> Model.registerUser(username, email, password, context)
        }
    }
}