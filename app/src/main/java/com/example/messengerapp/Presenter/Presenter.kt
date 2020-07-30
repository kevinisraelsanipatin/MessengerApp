package com.example.messengerapp.Presenter

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Model.Model
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.google.firebase.database.DatabaseReference

object Presenter {
    var chatFragRef: ChatsFragment? = null
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
        if (chatFragRef == null) chatFragRef = ref
        Model.getChatList(context)
    }

    fun updateFrag(uadap: UserAdapter) {
        chatFragRef!!.updateAdapter(uadap)
    }

    fun updateChatList(mUsersChatList: List<ChatList>) {
        chatFragRef!!.updateChatList(mUsersChatList)
    }
}