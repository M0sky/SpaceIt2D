package com.mosky.spaceit2d.out

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.util.MyInterface

/**
 * Actividad para la recuperación de contraseña.
 *
 * Permite a los usuarios enviar un correo electrónico para restablecer su contraseña.
 */
class ForgetPassword : AppCompatActivity() {

    companion object {
        private var myInterface1: MyInterface? = null

        /**
         * Configura la instancia de la interfaz para la actividad.
         *
         * @param context El contexto de la actividad, que debe implementar [MyInterface].
         */
        fun setInterfaceInstance1(context: Context?) {
            myInterface1 = context as MyInterface?
        }
    }

    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * Configura la actividad cuando se crea, incluyendo la inicialización del layout y la configuración de vistas.
     *
     * @param savedInstanceState El estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)

        // Configura los márgenes para el área de contenido según las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa las vistas
        val txtEmailRecover : TextView = findViewById(R.id.text_emailrecover)
        val btnSendEmail : Button = findViewById(R.id.button_sendemail)
        val btnGoLogin : TextView = findViewById(R.id.text_forgetlogin)

        // Inicializa FirebaseAuth
        firebaseAuth = Firebase.auth

        // Configura la barra de acción
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "SpaceIt2d"

        // Configura el botón para enviar el correo electrónico de recuperación
        btnSendEmail.setOnClickListener {
            val aux = txtEmailRecover.text.toString()
            if (aux.isStringNotEmpty() && aux.isEmailValid()) {
                recover(txtEmailRecover.text.toString())
                finish()
                myInterface1!!.callBack("Vuelta a MainActivity desde ForgetPassword")
            } else {
                Toast.makeText(baseContext, "Invalid email", Toast.LENGTH_SHORT).show()
                txtEmailRecover.requestFocus()
            }
        }

        // Configura el botón para volver al login
        btnGoLogin.setOnClickListener { _: View? ->
            finish()
            myInterface1!!.callBack("Vuelta a MainActivity desde ForgetPassword")
        }
    }

    /**
     * Envía un correo electrónico para restablecer la contraseña del usuario.
     *
     * @param email La dirección de correo electrónico del usuario.
     */
    private fun recover(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Mail sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Error : Unsuccessful task" + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Maneja la acción de retroceso en la barra de navegación.
     *
     * Finaliza la actividad y llama al método `callBack` en la interfaz configurada.
     *
     * @return `true` si la acción de retroceso se manejó correctamente.
     */
    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterface1!!.callBack("Vuelta a MainActivity desde ForgetPassword")
        return true
    }

    /**
     * Verifica si una cadena es un correo electrónico válido.
     *
     * @return `true` si el correo electrónico es válido, `false` en caso contrario.
     */
    private fun String.isEmailValid(): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return emailRegex.toRegex().matches(this)
    }

    /**
     * Verifica si una cadena no es nula ni vacía.
     *
     * @return `true` si la cadena no es nula ni vacía, `false` en caso contrario.
     */
    private fun String?.isStringNotEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }
}