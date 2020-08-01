package com.example.messengerapp.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.Presenter.Presenter

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

    /**
     * Atributos del Fragmento ChastFragment
     */
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var mUsersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!
    var loadOnAttach: Boolean = false

    /**
     * Metodo onCreareView que inicializa el ciclo de vida de un Fragmento del Activity
     */
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
        //Presenter.updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    /**
     * Metodo onAttach permite la comunicación con la funcion manageChats del Presentador
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Presenter.manageChats(context, this)
    }

    /**
     * Metodo onResume permite la comunicación con la funcion manageChats del Presentador
     */
    override fun onResume() {
        super.onResume()
        Presenter.manageChats(context!!, this)
    }

    /**
     * Metodo updateAdapter que permite la actualizacion de una parte de la vista
     */
    fun updateAdapter(userAdapter: UserAdapter) {
        this.userAdapter = userAdapter
        recycler_view_chatlist.adapter = userAdapter
    }

    /**
     * Metodo updateChatList nos permite actualizar la lista de chats en el framento
     */
    fun updateChatList(mUsersChatList: List<ChatList>) {
        this.mUsersChatList = mUsersChatList
    }
}
