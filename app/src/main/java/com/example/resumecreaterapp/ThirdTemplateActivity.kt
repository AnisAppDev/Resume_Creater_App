package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.resumecreaterapp.Objects.PdfUtils
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.databinding.ActivityThirdTemplateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ThirdTemplateActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var progress : ProgressDialog
    lateinit var database: DatabaseReference
    lateinit var resume : ResumeData
    lateinit var binding : ActivityThirdTemplateBinding
    lateinit var key : String
    private var currentViewForExport: android.view.View? = null

    // Launcher for saving PDF
    private val pdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    currentViewForExport?.let { view ->
                        PdfUtils.savePdf(this, view, it)
                    }
                }
            }
        }

    // Launcher for saving PNG
    private val pngLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    currentViewForExport?.let { view ->
                        PdfUtils.savePng(this, view, it)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityThirdTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize progress dialog
        progress = ProgressDialog(this)
        progress.setMessage("Loading....")

        //  Initialize Firebase reference here
        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        //get CurrentUserId
        var userId = auth.currentUser?.uid.toString()

        val status = intent.getStringExtra("status")
         key = intent.getStringExtra("key").toString()


        // now attach Firebase listener
        progress.show()
        database.child("Resumes").child(userId).child(key).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //resumedata.clear()

                if (snapshot.exists()) {
                    progress.dismiss()

                    val resumes = snapshot.getValue(ResumeData::class.java)
                    resumes?.id = snapshot.key
                    if (resumes != null) {

                        resume= resumes

                        // Fill UI
                        binding.ImageViewCadidatePhotoThird.setImageURI(resume.image?.toUri())
                        binding.TextViewCandidteNameThird.text = resume.name
                        binding.TextViewPhoneNoThird.text = resume.phone
                        binding.TextViewEmialThird.text = resume.mail
                        binding.TextViewWhatsappNoThird.text = resume.whatsapp
                        binding.TextViewCandidateBioThird.text = resume.bio
                        binding.TextViewLanguagesThird.text = resume.language
                        binding.TextViewDegreeThird.text = resume.degree
                        binding.TextViewDegreeInstitudeDateThird.text = resume.degreeDate
                        binding.TextViewDegreeInstitudeNameThird.text = resume.institudeName
                        binding.TextViewDegreeInstitudeCgpaThird.text = "CGPA :${resume.cgpa}"
                        binding.TextViewPercentageThird.text = "Percentage :${resume.percentage}"
                        binding.TextViewCompanyNameThird.text = resume.companyName
                        binding.TextViewOccupationThird.text = resume.companyRole
                        binding.TextViewWorkCompanyWorkThird.text = resume.companyWork
                        binding.TextViewSkillThird.text = resume.skills
                        binding.TextViewHobbiesThird.text = resume.hobby
                        binding.TextViewTechSkillThird.text = resume.techSkills

                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                progress.dismiss()
                Toast.makeText(
                    this@ThirdTemplateActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


        // Edit button
        binding.buttonEditThird.setOnClickListener {
            if (status.equals("main")) {
                val returnIntent = Intent()
                returnIntent.putExtra("status", "manual")
                setResult(RESULT_OK, returnIntent)
                finish()
            }
            if (status.equals("adapter")) {
                val returnIntent = Intent(this, MainActivity::class.java)
                returnIntent.putExtra("status", "db")
                returnIntent.putExtra("key", key)
                setResult(RESULT_OK, returnIntent)
                startActivity(returnIntent)
            }
        }
        //  Download PDF button
        binding.buttonDownloadThird.setOnClickListener {
            val cardView = binding.CardViewMainThird
            currentViewForExport = cardView // save view reference

            // Round candidate photo before export
            PdfUtils.applyRoundedImage(binding.ImageViewCadidatePhotoThird, 50f)

            // Show options (pass launchers)
            PdfUtils.showExportOptions(
                this,
                cardView,
                fileName = "Resume",
                onPdfIntentReady = { intent -> pdfLauncher.launch(intent) },
                onPngIntentReady = { intent -> pngLauncher.launch(intent) }
            )
        }


    }

    // Handle back press
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ThirdTemplateActivity, AllResumeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}