package com.example.messengerapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.ChatAdapter
import com.example.messengerapp.Fragments.APIService
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: ChatAdapter? = null
    var mChatList: List<Chat>? = null
    var reference: DatabaseReference? = null
    var notify = false
    lateinit var recycler_view_chats: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)
        val toobar: Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toobar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toobar.setNavigationOnClickListener {
            val intent = Intent(this@MessageChatActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        apiService =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager
        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                username_mchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mchat)
                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


        send_message_btn.setOnClickListener {
            val message = text_message.text.toString()
            notify = true
            if (message == "") {
                Toast.makeText(
                    this@MessageChatActivity, "Ingrese un mensaje primero...",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }

        attact_image_file_btn.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender()
                            .equals(senderId)
                    ) {
                        (mChatList as ArrayList).add(chat)
                    }
                    chatsAdapter = ChatAdapter(
                        this@MessageChatActivity,
                        (mChatList as ArrayList),
                        receiverImageUrl!!
                    )
                    recycler_view_chats.adapter = chatsAdapter
                }
            }
        })
    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
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
                        .child(firebaseUser!!.uid)
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
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })

                }
            }
        val userReference = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)
                if (notify) {
                    sendNotification(receiverId, user!!.getUserName(), message)
                }
                notify = false
            }

        })
    }

    var apiService: APIService? = null
    private fun sendNotification(receiverId: String?, userName: String?, message: String) {
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val query = ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (d in p0.children) {
                    val token: Token? = d.getValue(Token::class.java)
                    val data =
                        Data(
                            firebaseUser!!.uid,
                            R.mipmap.ic_launcher,
                            "$userName: $message",
                            "New Message",
                            userIdVisit
                        )
                    val sender = Sender(data!!, token!!.getToken().toString())
                    apiService!!.sendNotification(sender).enqueue(object: Callback<Response> {
                        /**
                         * Invoked for a received HTTP response.
                         *
                         *
                         * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
                         * Call [Response.isSuccessful] to determine if the response indicates success.
                         */
                        override fun onResponse(
                            call: Call<Response>,
                            response: retrofit2.Response<Response>
                        ) {
                            if (response.code() == 200) {
                                if (response.body()!!.success !== 1) {
                                    Toast.makeText(
                                        this@MessageChatActivity,
                                        "Failed, Nothing happened",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            }
                        }

                        /**
                         * Invoked when a network exception occurred talking to the server or when an unexpected
                         * exception occurred creating the request or processing the response.
                         */
                        override fun onFailure(call: Call<Response>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data!!.data != null) {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Cargando imagen, espere por favor...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "Te envié una imagen"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener {
                            task
                            if (task.isSuccessful) {
                                //Implementación de Notificaciones usando FCM

                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)

                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(Users::class.java)
                                        if (notify) {
                                            sendNotification(
                                                userIdVisit,
                                                user!!.getUserName(),
                                                "Te envié una imagen"
                                            )
                                        }
                                        notify = false
                                    }

                                })
                            }
                        }
                    progressBar.dismiss()
                }
            }
        }
    }

    var seenListener: ValueEventListener? = null
    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference
            .child("Chats")
        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (d in p0.children) {
                    val chat = d.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender()
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

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}

private fun <T> Call<T>.enqueue(callback: Callback<Response>) {

}
