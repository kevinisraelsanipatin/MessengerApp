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
 * Clase encargada de implementar los metodos de la aplicacion
 * de mensajeria instantatea
 *
 * @author Ismael Martinez - Kevin Sanipatin
 * @version 01/08/2020 v1
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
     * @param email
     * @param password
     * @param context
     */
    fun login(email: String, password: String, context: Context) {
        Model.loginUser(email, password, context)
    }

    /**
     * Metodo getChild que obtiene una referencia de la base de datos
     * @param child
     * @param dbref
     */
    fun getChild(child: String, dbref: DatabaseReference?): DatabaseReference {
        return if (dbref == null) Model.getChild(child) else Model.getChild(child, dbref)
    }

    /**
     * Metodo manageChats permite loa gestion de los chats
     * @param context
     * @param ref
     */
    fun manageChats(context: Context, ref: ChatsFragment) {
        chatFragRef = ref
        Model.getChatList(context)
    }

    /**
     * Metodo updateChatFrag permite actualizar un fragmento de la aplicación
     * @param uadap
     */
    fun updateChatFrag(uadap: UserAdapter) {
        chatFragRef!!.updateAdapter(uadap)
    }

    /**
     * Metodo updateChatList permite actualizar la lista de chats de un usuario en el fragmento
     * @param mUsersChatList
     */
    fun updateChatList(mUsersChatList: List<ChatList>) {
        chatFragRef!!.updateChatList(mUsersChatList)
    }

    /**
     * Metodo getUsers permite obtener todos los usuarios registrados en la app
     * @param context
     * @param ref
     */
    fun getUsers(context: Context, ref: SearchFragment) {
        searchFragment = ref
        Model.getUsersList(context)
    }

    /**
     * Metodo updateSearchList permite actualizar la lista en el fragmento de Busqueda de la App
     * @param uadap
     */
    fun updateSearchList(uadap: UserAdapter) {
        searchFragment!!.updateAdapter(uadap)
    }

    /**
     * Metodo searchFor permite buscar un usuario de acuerdo al ingreso de caracteres
     * @param string
     * @param context
     */
    fun searchFor(string: String, context: Context) {
        Model.searchFor(string, context)
    }

    /**
     * Metodo updateStatus permite actualizar el estado del usuario, es decir activo o inativo
     * @param status
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
     * @param username
     * @param email
     * @param password
     * @param context
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
     * @param ref
     * @param senderId
     * @param receiverId
     * @param receiverImageUrl
     * @param userIdVisit
     * @param context
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
     * @param chatsAdapter
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
     * @param senderId
     * @param receiverId
     * @param message
     * @param userIdVisit
     */
    fun sendMessage(senderId: String,
                    receiverId: String?,
                    message: String,
                    userIdVisit: String){
        Model.sendMessageToUser(senderId, receiverId, message, userIdVisit)
    }
}