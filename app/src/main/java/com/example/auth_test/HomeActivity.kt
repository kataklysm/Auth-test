package com.example.auth_test

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}
class HomeActivity : AppCompatActivity() {

    private  val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //SetUp
        val bundle:Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        bundle?.getString("provider")
        setUp(email?:"",provider?:"")

        //Guardar datos

        val prefs : SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

        //Remote Config
        btnError.visibility = View.INVISIBLE
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val showErrorButton = Firebase.remoteConfig.getBoolean("show_error_button")
                val errorButtonText = Firebase.remoteConfig.getString("error_button_text")
                if(showErrorButton){
                    btnError.visibility = View.VISIBLE
                }
                btnError.text = errorButtonText
            }
        }

    }

    private fun setUp(email: String, provider: String){
        title = "Inicio"
        tvEmail.text = email
        tvProveedor.text = provider

        btnLogOut.setOnClickListener {
            //Borrar datos

            val prefs:SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            if(provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

        btnError.setOnClickListener {
            FirebaseCrashlytics.getInstance().setUserId(email)
            FirebaseCrashlytics.getInstance().setCustomKey("Provider", provider)
            //Forzado de error
           // throw RuntimeException("Forzado de error")
        }

        btnSave.setOnClickListener {

            db.collection("users").document(email).set(
                hashMapOf("provider " to provider,
                "address" to tvAddress.text.toString(),
                "phone" to tvPhone.text.toString())
            )
        }
        btnGet.setOnClickListener {
            db.collection("users").document(email).get().addOnSuccessListener {
                tvAddress.setText(it.get("address") as String?)
                tvPhone.setText(it.get("phone") as String?)
            }
        }
        btnDelete.setOnClickListener {
            db.collection("users").document(email).delete()

        }

    }
}