package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.messengerapp.Presenter.Presenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

/**
 * Clase [LoginActivity] de tipo AppCompatActivity, gestiona la vista del Login
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Al crear la vista se inicializan los datos necesarios para poblar la interfaz gráfica
     * y vincular los elementos de la interfaz con sus respectivos eventos
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title ="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        login_btn.setOnClickListener {
            Presenter.login(email_login.text.toString(),password_login.text.toString(),this@LoginActivity)
        }
    }

}
