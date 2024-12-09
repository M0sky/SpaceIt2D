package com.mosky.spaceit2d.out

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mosky.spaceit2d.util.MyInterface
import com.mosky.spaceit2d.R
import com.mosky.spaceit2d.out.ForgetPassword.Companion.setInterfaceInstance1
import com.mosky.spaceit2d.out.SignUpActivity.Companion.setInterfaceInstance2
import com.mosky.spaceit2d.`in`.MenuActivity

/**
 * Actividad principal que maneja el inicio de sesión y la navegación a otras actividades.
 *
 * Permite a los usuarios iniciar sesión, registrarse y recuperar contraseñas.
 */
class MainActivity : AppCompatActivity() , MyInterface {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    /**
     * Configura la actividad cuando se crea, incluyendo la inicialización del layout,
     * la configuración del modo nocturno y la configuración de los listeners de los botones.
     *
     * @param savedInstanceState El estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configura los márgenes para el área de contenido según las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera el estado del modo nocturno desde SharedPreferences.
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isNightMode = sharedPreferences.getBoolean("night_mode", false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

        // Inicializa las vistas
        val btnLogin : Button = findViewById(R.id.button_login)
        val txtEmailaddress : TextView = findViewById(R.id.text_emailaddress)
        val txtPassword : TextView = findViewById(R.id.text_password)
        val btnSignup : TextView = findViewById(R.id.text_signup)
        val btnForgetPassword : TextView = findViewById(R.id.text_forgetpassword)

        // Inicializa FirebaseAuth
        firebaseAuth = Firebase.auth

        // Configura el botón de inicio de sesión
        btnLogin.setOnClickListener {
            signIn(txtEmailaddress.text.toString(), txtPassword.text.toString())
        }

        // Configura el botón de registro
        btnSignup.setOnClickListener {
            setInterfaceInstance2(this@MainActivity)
            startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
        }

        // Configura el botón de recuperación de contraseña
        btnForgetPassword.setOnClickListener {
            setInterfaceInstance1(this@MainActivity)
            startActivity(Intent(this@MainActivity, ForgetPassword::class.java))
        }
    }

    /**
     * Maneja el proceso de inicio de sesión utilizando el correo electrónico y la contraseña proporcionados.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    private fun signIn(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(baseContext,"Error: Please fill out these fields",Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        if (firebaseAuth.currentUser!!.isEmailVerified) {
                            Toast.makeText(baseContext, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                        } else {
                            Toast.makeText(baseContext, "Email not verified", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "Error en la autenticación", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    /**
     * Implementa el método de la interfaz [MyInterface].
     * Este método es llamado para recibir datos de otras actividades.
     *
     * @param data Los datos recibidos de otra actividad.
     */
    override fun callBack(data: String?) {
        //Toast.makeText(baseContext, "Data received: $data", Toast.LENGTH_SHORT).show()
    }
}