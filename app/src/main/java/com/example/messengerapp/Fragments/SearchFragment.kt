package com.example.messengerapp.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.Presenter.Presenter
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    /**
     * Atibutos del fragmento Searchfragment
     */
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    /**
     * Metodo onCreareView que inicializa el ciclo de vida de un Fragmento del Activity
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view?.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = userAdapter
        searchEditText = view.findViewById(R.id.searchUserET)
        mUsers = ArrayList()
        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    Presenter.searchFor(cs.toString().toLowerCase(), context!!)
                } catch (_: NullPointerException) {
                    Log.e("NullPointSearchFragment", "Error $context:context ")
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    /**
     * Metodo onAttach permite la comunicación con el presentador llamando al metodo getUsers
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        Presenter.getUsers(context, this)
    }

    /**
     * Metodo updateAdapter permite la actualización del fragmento con los usuarios registrdos en la App
     */
    fun updateAdapter(userAdapter: UserAdapter) {
        this.userAdapter = userAdapter
        recyclerView!!.adapter = userAdapter
    }

}
