package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messengerapp.Presenter.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser?=null

    /**
     * Al crear la actividad se ejecuta esta función para inicializar la vista y establecer
     * los eventos respectivos para los elementos de la interfaz
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        register_welcome_btn.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_welcome_btn.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Al iniciar la actividad se ejecuta esta función comprobando si ya existe un usuario
     * registrado
     */
    override fun onStart() {
        super.onStart()
        if(Presenter.isLoggedIn())
        {
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
