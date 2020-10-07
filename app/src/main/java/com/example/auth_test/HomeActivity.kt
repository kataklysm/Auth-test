package com.example.auth_test

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}
class HomeActivity : AppCompatActivity() {
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
            throw RuntimeException("Forzado de error")
        }

    }
}