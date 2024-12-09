package com.mosky.spaceit2d.out

import android.content.Context
import android.os.Bundle
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
 * Actividad para el registro de nuevos usuarios.
 *
 * Permite a los usuarios crear una cuenta ingresando un correo electrónico y una contraseña.
 * También verifica que la contraseña y el correo electrónico cumplan con los requisitos específicos.
 */
class SignUpActivity : AppCompatActivity() {

    companion object {
        private var myInterface2: MyInterface? = null

        /**
         * Establece la instancia de la interfaz [MyInterface] para la actividad.
         *
         * @param context El contexto que implementa la interfaz [MyInterface].
         */
        fun setInterfaceInstance2(context: Context?) {
            myInterface2 = context as MyInterface?
        }
    }

    private lateinit var firebaseAuth: FirebaseAuth

    /**
     * Configura la actividad cuando se crea, incluyendo la inicialización del layout,
     * la configuración de los listeners de los botones y la validación de datos de entrada.
     *
     * @param savedInstanceState El estado guardado de la actividad, si lo hay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Configura los márgenes para el área de contenido según las barras del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa las vistas
        val btnSignup : Button = findViewById(R.id.button_signup)
        val txtEmailsignup : TextView = findViewById(R.id.text_emailsignup)
        val txtPasswordsignup1 : TextView = findViewById(R.id.text_passwordsignup1)
        val txtPasswordsignup2 : TextView = findViewById(R.id.text_passwordsignup2)

        // Inicializa FirebaseAuth
        firebaseAuth = Firebase.auth

        // Configura el ActionBar
        val toggle = supportActionBar
        toggle!!.setDisplayHomeAsUpEnabled(true)
        toggle.title = "SpaceIt2d"

        // Configura el botón de registro
        btnSignup.setOnClickListener {
            val aux1 = txtEmailsignup.text.toString()
            val aux2 = txtPasswordsignup1.text.toString()
            val aux3 = txtPasswordsignup2.text.toString()

            if (aux1.isStringNotEmpty() && aux2.isStringNotEmpty() && aux3.isStringNotEmpty()) {
                if (aux1.isEmailValid()) {
                    if (aux2.isPasswordValid() && aux3.isPasswordValid() && aux2 == aux3) {
                        signUp(txtEmailsignup.text.toString(), txtPasswordsignup1.text.toString())
                        //myInterface2!!.callBack("Vuelta a MainActivity desde SignUp")
                    } else {
                        Toast.makeText(baseContext, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        txtPasswordsignup1.requestFocus()
                    }
                } else {
                    Toast.makeText(baseContext, "Email incorrecto", Toast.LENGTH_SHORT).show()
                    txtEmailsignup.requestFocus()
                }
            } else {
                Toast.makeText(baseContext, "Error: Please fill out these fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Registra un nuevo usuario con el correo electrónico y la contraseña proporcionados.
     *
     * @param email El correo electrónico del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     */
    private fun signUp(email: String, password:String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendEmailVerification()
                    Toast.makeText(baseContext, "Account created successfully, check email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Error : " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Envía un correo electrónico de verificación al usuario registrado.
     */
    private fun sendEmailVerification() {
        val user = firebaseAuth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Verification sent succesfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Error : " + task.exception, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Maneja la acción de navegación hacia arriba en la ActionBar.
     *
     * Finaliza la actividad y notifica a la interfaz [MyInterface] sobre el retorno.
     *
     * @return Verdadero si la navegación hacia arriba se maneja correctamente.
     */
    override fun onSupportNavigateUp(): Boolean{
        finish()
        myInterface2!!.callBack("Vuelta a MainActivity desde SignUp")
        return true
    }

    /**
     * Verifica si el correo electrónico es válido según un patrón específico.
     *
     * @return Verdadero si el correo electrónico es válido.
     */
    private fun String.isEmailValid(): Boolean {
        val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
        return emailRegex.toRegex().matches(this)
    }

    //Contraseña alfanumérica
    /**
     * Verifica si la contraseña es válida, debe ser alfanumérica con al menos 6 caracteres.
     *
     * @return Verdadero si la contraseña es válida.
     */
    private fun String.isPasswordValid(): Boolean {
        val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$"
        return passwordRegex.toRegex().matches(this)
    }

    /**
     * Verifica si la cadena no es nula o vacía.
     *
     * @return Verdadero si la cadena no es nula o vacía.
     */
    private fun String?.isStringNotEmpty(): Boolean {
        return !this.isNullOrEmpty()
    }

}