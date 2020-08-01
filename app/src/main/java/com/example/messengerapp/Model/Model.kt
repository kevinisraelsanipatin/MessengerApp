package com.example.messengerapp.Model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.AdapterClasses.ChatAdapter
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.MainActivity
import com.example.messengerapp.Model.ModelClasses.Chat
import com.example.messengerapp.Model.ModelClasses.ChatList
import com.example.messengerapp.Model.ModelClasses.Users

import com.example.messengerapp.Presenter.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

/**
 * Clase Model donde se instancian ciertos valores para la comunicación con Firebase
 */
object Model {

    /**
     * Atributos de la clase Model
     */
    private val firebaseDbRef = FirebaseDatabase.getInstance().reference
    private val firebaseAuthRef = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = firebaseAuthRef.currentUser
    private var chatListListener: ValueEventListener? = null
    private var isSearching = false
    private var seenListener: ValueEventListener? = null
    private var reference: DatabaseReference? = null

    /**
     * Metodo loginUser
     * parameter [email] corresponde al correo o email del usuario
     * parameter [password] corresponde a la contraseña del usuario
     * parameter [context] corresponde al ambiente de trabajo o vista en la cual se esta ejecutando la accion
     */
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

    /**
     * Metodo getChild obtiene la referencia en la BD de Firebase segun el parametro child
     * parameter [child] corresponde al nombre del Objeto de la Base de Datos
     */
    fun getChild(child: String): DatabaseReference {
        return firebaseDbRef.child(child)
    }

    /**
     * Metodo getChild obtiene la referencia en la BD de Firebase segun el parametro child y ref
     * parameter [child] corresponde al nombre del Objeto de la Base de Datos
     * parameter [ref] corresponde a una referencia especifica en la base de datos
     */
    fun getChild(child: String, ref: DatabaseReference): DatabaseReference {
        return ref.child(child)
    }

    /**
     * Metodo updateToken usado para actualizar el token
     */
    fun updateToken(token: String?) {
    }

    /**
     * Metodo getChatList utilizado para obtener la lista de chasts
     * parameter [context] correspondiente al identificador del entorno en la app
     */
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

    /**
     * Metodo retrieveChatList usado para recuperar la lista de chats en base a una lista de chats de los usuarios
     * parameter [context] correspondiente al identificador del entorno en la app
     * parameter [mUsersChatList] correspondiente a la lista de Chats de cada Usuario
     */
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

    /**
     * Metodo getUsersList usado ara obtener la lista de usuarios
     * parameter [context] correspondiente al identificador del entorno en la app
     */
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

    /**
     * Metodo searchFor utilizado para buscar usuarios por el ID
     * parameter [string] corresponde al nombre usuario
     * parameter [context] correspondiente al identificador del entorno en la app
     */
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

    /**
     * Metodo updateStatus utilizado para actualizar el estado del Usuario
     * parameter [status] corresponde al estado del usuario true o false
     */
    fun updateStatus(status: String) {
        if (currentUser == null) currentUser = firebaseAuthRef.currentUser
        val ref = getChild("Users").child(currentUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        ref.updateChildren(hashMap)
    }

    /**
     * Metodo isLoggedIn que retorna un booleano de acuerdo a la autenticacion en Firebase
     */
    fun isLoggedIn(): Boolean {
        return firebaseAuthRef.currentUser != null
    }

    /**
     * Metodo registerUser nos permite registrar un nuevo usuario en la Base de Datos de Firebase
     * parameter [username] nombre de Usuario ingresado por pantalla
     * parameter [email] email correspondiente al usuario
     * parameter [password] contraseña del usuario
     * parameter [context] correspondiente al identificador del entorno en la app
     */
    fun registerUser(username: String, email: String, password: String, context: Context) {
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

    /**
     * Metodo retrieveMessages nos permite recuperar los mensajes de acuerdo a varios parametros
     * parameter [senderId] corresponde al id del remitente
     * parameter [receiverId] corresponde al id del receptor
     * parameter [receiverImageUrl] corresponde a la url de la imagen del receptor
     * parameter [userIdVisit] corresponde al id de la visita del usuario
     * parameter [context] correspondiente al identificador del entorno en la app
     */
    fun retrieveMessages(
        senderId: String,
        receiverId: String?,
        receiverImageUrl: String?,
        userIdVisit: String,
        context: Context
    ) {

        val reference = getChild("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var mChatList = ArrayList<Chat>()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender()
                            .equals(senderId)
                    ) {
                        mChatList.add(chat)
                    }
                    Presenter.updateChats(
                        ChatAdapter(
                            context,
                            (mChatList as java.util.ArrayList),
                            receiverImageUrl!!
                        )
                    )
                }
            }
        })
        seenMessage(userIdVisit)
    }

    /**
     * Metodo sendMessage nos permite enviar un mensaje desde un remitende hasta un receptor
     * parameter [senderId] corresponde al id del remitende
     * parameter [receiverId] corresponde al id del receptor
     * parameter [message] corresponde al mensaje como tal
     * parameter [userIdVisit] corresponde al id de la visita del usuario
     */
    fun sendMessageToUser(
        senderId: String,
        receiverId: String?,
        message: String,
        userIdVisit: String
    ) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseAuthRef.currentUser!!.uid)
                        .child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseAuthRef.currentUser!!.uid)
                            chatsListReceiverRef.child("id")
                                .setValue(firebaseAuthRef.currentUser!!.uid)
                        }
                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })

                }
            }
    }

    /**
     * Metodo seenMessage usado para marcar como leido el mensaje "visto"
     * parameter [userId] corresponde al id del usuario destinatario
     */
    private fun seenMessage(userId: String) {
        reference = FirebaseDatabase.getInstance().reference
            .child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                for (d in p0.children) {
                    val chat = d.getValue(Chat::class.java)
                    if (chat!!.getReceiver()
                            .equals(firebaseAuthRef.currentUser!!.uid) && chat!!.getSender()
                            .equals(userId)
                    ) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        d.ref.updateChildren(hashMap)
                    }
                }
            }

        })
    }

    /**
     * Metodo removeListeners usado para eliminar posibles oyentes
     */
    fun removeListeners() {
        reference!!.removeEventListener(seenListener!!)
    }
}

