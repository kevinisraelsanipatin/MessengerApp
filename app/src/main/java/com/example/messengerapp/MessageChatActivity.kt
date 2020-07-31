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
import com.example.messengerapp.Model.ModelClasses.Chat
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.Presenter.Presenter
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
        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager
        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)

                username_mchat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mchat)
                Presenter.retrieveChats(
                    this@MessageChatActivity,
                    firebaseUser!!.uid,
                    userIdVisit,
                    user.getProfile(),
                    userIdVisit,
                    applicationContext
                )
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
                Presenter.sendMessage(firebaseUser!!.uid, userIdVisit, message, userIdVisit)
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

    }

    fun updateMessages(chatsAdapter: ChatAdapter) {
        this.chatsAdapter = chatsAdapter
        recycler_view_chats.adapter = chatsAdapter
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

    override fun onPause() {
        super.onPause()
        Presenter.removeListener()
    }
}
