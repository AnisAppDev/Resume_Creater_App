package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.resumecreaterapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var  binding: ActivitySignInBinding
    lateinit var auth : FirebaseAuth
    lateinit var progress : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize progress dialog
        progress = ProgressDialog(this)
        progress.setMessage("Loading...")


        //firebase initializtion
        auth = FirebaseAuth.getInstance()



        // go SignUp if not Account
        binding.TxtSignIn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.TxtDontAccountSignIn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }


        // go AllRecycleActivity if Login Complete
        binding.BtnSignIn.setOnClickListener {

            progress.show()
            auth.signInWithEmailAndPassword(binding.EdvEmailSignIn.text.toString(),binding.EdvPasswordSignIn.text.toString())
                .addOnCompleteListener { task ->
                    progress.dismiss()

                    if(task.isSuccessful)
                    {
                        startActivity(Intent(this, AllResumeActivity::class.java))
                    }
                    else
                    {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser != null)
        {
            startActivity(Intent(this, AllResumeActivity::class.java))
        }
    }
}
