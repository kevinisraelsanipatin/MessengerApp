package com.example.messengerapp.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.MessageChatActivity
import com.example.messengerapp.Model.ModelClasses.Chat
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Clase [UserAdapter] es un adaptador para el usuario dentro de la aplicacion
 */
class UserAdapter(
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    /**
     * Atributos del adaptador del Usuario
     */
    private val mContext: Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean

    /**
     * Se inicializa los atributos del adaptador
     */
    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    /**
     * Clase interna que permite tomar los atributos de cada usuario y mostrar en la app
     * @param viewGroup
     * @param viewType
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    /**
     * Metodo getItemCount permite obtener la dimension de la lista de Usuarios
     */
    override fun getItemCount(): Int {
        return mUsers.size
    }

    /**
     * Metodo onBindViewHolder permite enlazar a un usuario con el chat correspondiente al mismo
     * @param holder
     * @param i
     */
    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        val user: Users = mUsers[i]
        holder.userNameTxt.text = user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile)
            .into(holder.profileImageView)
        if (isChatCheck) {
            holder.onlineImageView.visibility =
                if (user.getStatus() == "online") View.VISIBLE else View.GONE
            holder.ofllineImageView.visibility =
                if (user.getStatus() == "offline") View.VISIBLE else View.GONE
            retrieveLastMessage(user.getUID(), holder.lasMessageTxt)
        } else {
            holder.lasMessageTxt.visibility = View.GONE
            holder.onlineImageView.visibility = View.GONE
            holder.ofllineImageView.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Enviar Mensaje",
                "Ver perfil"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("¿Qué deseas?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0) {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id", user.getUID())
                    mContext.startActivity(intent)
                }
                if (position == 1) {

                }
            })
            builder.show()
        }
    }

    /**
     * Metodo retrieveLastMessage permite recuperar el ultimo mensaje enviado
     * @param chatUserId
     * @param lasMessageTxt
     */
    private fun retrieveLastMessage(chatUserId: String?, lasMessageTxt: TextView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        var lastMsg: String = ""
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                for (d in p0.children) {
                    val chat = d.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null)
                        if (chat.getReceiver() == firebaseUser!!.uid && chat.getSender() == chatUserId || chat.getReceiver() == chatUserId && chat.getSender() == firebaseUser!!.uid)
                            lastMsg = chat.getMessage()!!
                }
                when (lastMsg) {
                    "" -> lasMessageTxt.text = ""
                    "Te envié una imagen" -> lasMessageTxt.text = "Imagen"
                    else -> lasMessageTxt.text = lastMsg
                }
                lastMsg = ""

            }

        })
    }

    /**
     * Clase ViewHolder permite inicalizar cada item de la vista con los atributos del usuario correspondiente
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var ofllineImageView: CircleImageView
        var lasMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            ofllineImageView = itemView.findViewById(R.id.image_offline)
            lasMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }


}