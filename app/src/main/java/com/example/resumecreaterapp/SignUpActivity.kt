package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.resumecreaterapp.ResumeModel.UserData
import com.example.resumecreaterapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    lateinit var binding : ActivitySignUpBinding
    lateinit var auth : FirebaseAuth
    lateinit var database : FirebaseDatabase
    lateinit var progress : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize progress dialog
        progress = ProgressDialog(this)
        progress.setMessage("User Creating")


        //firebase initializtion
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //go SignIn if already Account
        binding.TxtSignUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.TxtHaveAccountSignUp.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        //go SignIn After Complete SignIn
        binding.BtnSignUp.setOnClickListener {

            if(!binding.EdvNameSignUp.text.toString().isNullOrEmpty() && !binding.EdvEmailSignUp.text.toString().isNullOrEmpty()
                && !binding.EdvPasswordSignUp.text.toString().isNullOrEmpty() && !binding.EdvConfPassworrdSignUp.text.toString().isNullOrEmpty())
            {

                if(binding.EdvPasswordSignUp.text.toString().equals(binding.EdvConfPassworrdSignUp.text.toString()) ){

                    progress.show()
                    //create authenticate user signUp
                    auth.createUserWithEmailAndPassword(binding.EdvEmailSignUp.text.toString(),binding.EdvPasswordSignUp.text.toString())
                        .addOnCompleteListener {task ->

                            progress.dismiss()
                            if(task.isSuccessful)
                            {
                                // get user Id of user
                                val userId = task.getResult().user?.uid.toString()

                                //create model
                                val userdata = UserData(binding.EdvNameSignUp.text.toString(),
                                    binding.EdvEmailSignUp.text.toString(),
                                    binding.EdvPasswordSignUp.text.toString(),
                                    userId
                                )

                                //Store data in database
                                database.getReference().child("Users").child(userId).setValue(userdata)

                                Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this, SignInActivity::class.java))
                            }
                            else
                            {
                                Toast.makeText(this, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                            }


                        }
                }
                else
                {
                    Toast.makeText(this, "Password Not Match", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show()
            }


        }

    }
}