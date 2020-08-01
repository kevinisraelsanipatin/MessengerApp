package com.example.messengerapp.Presenter

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.messengerapp.AdapterClasses.ChatAdapter
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Fragments.SearchFragment
import com.example.messengerapp.MessageChatActivity
import com.example.messengerapp.Model.Model
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users
import com.google.firebase.database.DatabaseReference

/**
 * Objeto [Presenter] permite la comunicacion entre la vista y el modelo
 */
object Presenter {

    /**
     * Atributos del Presentador
     */
    var chatFragRef: ChatsFragment? = null
    var searchFragment: SearchFragment? = null
    var messageChat: MessageChatActivity? = null

    /**
     * Metodo login que nos permite logearnos en la app
     * parameter [email]
     * parameter [password]
     * parameter [context]
     */
    fun login(email: String, password: String, context: Context) {
        Model.loginUser(email, password, context)
    }

    /**
     * Metodo getChild que obtiene una referencia de la base de datos
     * parameter [child]
     * parameter [dbref]
     */
    fun getChild(child: String, dbref: DatabaseReference?): DatabaseReference {
        return if (dbref == null) Model.getChild(child) else Model.getChild(child, dbref)
    }

    /**
     * Metodo manageChats permite loa gestion de los chats
     * parameter [context]
     * parameter [ref]
     */
    fun manageChats(context: Context, ref: ChatsFragment) {
        chatFragRef = ref
        Model.getChatList(context)
    }

    /**
     * Metodo updateChatFrag permite actualizar un fragmento de la aplicación
     * parameter [uadap]
     */
    fun updateChatFrag(uadap: UserAdapter) {
        chatFragRef!!.updateAdapter(uadap)
    }

    /**
     * Metodo updateChatList permite actualizar la lista de chats de un usuario en el fragmento
     * parameter [musersChatList]
     */
    fun updateChatList(mUsersChatList: List<ChatList>) {
        chatFragRef!!.updateChatList(mUsersChatList)
    }

    /**
     * Metodo getUsers permite obtener todos los usuarios registrados en la app
     * parameter [context]
     * parameter [ref]
     */
    fun getUsers(context: Context, ref: SearchFragment) {
        searchFragment = ref
        Model.getUsersList(context)
    }

    /**
     * Metodo updateSearchList permite actualizar la lista en el fragmento de Busqueda de la App
     * parameter [uadap]
     */
    fun updateSearchList(uadap: UserAdapter) {
        searchFragment!!.updateAdapter(uadap)
    }

    /**
     * Metodo searchFor permite buscar un usuario de acuerdo al ingreso de caracteres
     * parameter [string]
     * parameter [context]
     */
    fun searchFor(string: String, context: Context) {
        Model.searchFor(string, context)
    }

    /**
     * Metodo updateStatus permite actualizar el estado del usuario, es decir activo o inativo
     * parameter [status]
     */
    fun updateStatus(status: String) {
        Model.updateStatus(status)
    }

    /**
     * Metodo isLoggedIn permite identificar la autenticación del usuario en firebase
     */
    fun isLoggedIn(): Boolean {
        return Model.isLoggedIn()
    }

    /**
     * Metodo registerUser permite registrar un usuario en la app
     * parameter [username]
     * parameter [email]
     * parameter [password]
     * parameter [context]
     */
    fun registerUser(username: String, email: String, password: String, context: Context) {
        when {
            username == "" -> {
                Toast.makeText(context, "Ingrese el usuario", Toast.LENGTH_LONG).show()
            }
            email == "" -> {
                Toast.makeText(context, "Ingrese el email", Toast.LENGTH_LONG).show()
            }
            password == "" -> {
                Toast.makeText(context, "Ingrese el paswword", Toast.LENGTH_LONG).show()
            }
            else -> Model.registerUser(username, email, password, context)
        }
    }

    /**
     * Metodo retrieveChats nos permite recuperar los chats de acuerdo a ciertos parametros
     * parameter [ref]
     * parameter [senderId]
     * parameter [receiverId]
     * parameter [receiverId]
     * parameter [userIdVisit]
     * parameter [context]
     */
    fun retrieveChats(ref: MessageChatActivity, senderId: String,
                      receiverId: String?,
                      receiverImageUrl: String?,
                      userIdVisit: String,
                      context: Context) {
        messageChat = ref
        Model.retrieveMessages(senderId, receiverId, receiverImageUrl, userIdVisit, context)
    }

    /**
     * Metodo updateChats permite actualizar los chats de los usuarios
     * parameter [chatsAdapter]
     */
    fun updateChats(chatsAdapter: ChatAdapter){
        messageChat!!.updateMessages(chatsAdapter)
    }

    /**
     * Metodo removeLister permite eliminar a posibles oyentes que ya no esten iteractuando con la base de firebase
     */
    fun removeListener(){
        Model.removeListeners()
    }

    /**
     * Metodo sendMessage permite enviar un mensaje de un remitente a un receotor
     * parameter [senderId]
     * parameter [receiverId]
     * parameter [message]
     * parameter [userIdVisit]
     */
    fun sendMessage(senderId: String,
                    receiverId: String?,
                    message: String,
                    userIdVisit: String){
        Model.sendMessageToUser(senderId, receiverId, message, userIdVisit)
    }
}