package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resumecreaterapp.Adapter.ResumeAdapter
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.databinding.ActivityAllResumeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AllResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllResumeBinding

    private lateinit var resumeAdapter: ResumeAdapter
    lateinit var progress : ProgressDialog

    private var doubleBackToExitPressedOnce = false

    lateinit var resumedata: ArrayList<ResumeData>

    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingButtonAddResume.setOnClickListener {
            startActivity(Intent(this, AllTemplateActivity::class.java))
        }

        //initialize progress dialog
        progress = ProgressDialog(this)
        progress.setMessage("Loading....")

        //  Initialize Firebase reference here
        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        resumedata = ArrayList()

        //get CurrentUserId
        var userId = auth.currentUser?.uid.toString()

        resumeAdapter = ResumeAdapter(resumedata, this)
        binding.RecycleViewResume.layoutManager = LinearLayoutManager(this)
        binding.RecycleViewResume.adapter = resumeAdapter

        // now attach Firebase listener
        progress.show()
        database.child("Resumes").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resumedata.clear()

                if (snapshot.exists()) {
                    progress.dismiss()
                    for (childsnap in snapshot.children) {
                        val resumes = childsnap.getValue(ResumeData::class.java)
                        //its assigned forsefully id if in firebase in every resumes id null
                        //its assigned parent node id
                        //resumes?.id = childsnap.key
                        if (resumes != null) {
                            resumedata.add(resumes)
                        }
                    }
                    resumeAdapter.refreshData(resumedata)
                }
                else
                {
                    progress.dismiss()
                    Toast.makeText(this@AllResumeActivity, "Not Resume Created", Toast.LENGTH_SHORT).show()
                }
            }



            override fun onCancelled(error: DatabaseError) {
                progress.dismiss()
                Toast.makeText(
                    this@AllResumeActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        // logout Current user
        binding.ImageViewButtonLogOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding.TextViewLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
        }


        //  Use OnBackPressedDispatcher instead of overriding onBackPressed()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Exit to home screen
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        this@AllResumeActivity,
                        "Press back again to exit",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reset flag after 2 seconds
                    Handler(Looper.getMainLooper()).postDelayed(
                        { doubleBackToExitPressedOnce = false },
                        2000
                    )
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        // Refresh the resume list when activity comes back into view
        resumeAdapter.refreshData(resumedata)
    }

}
