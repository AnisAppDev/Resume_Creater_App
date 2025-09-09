package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
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
import com.example.resumecreaterapp.CreateResmeActivity
import com.example.resumecreaterapp.Objects.PdfUtils
import com.example.resumecreaterapp.Objects.TemplateCl
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.databinding.ActivitySecondTemplateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SecondTemplateActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var progress : ProgressDialog
    lateinit var database: DatabaseReference
    lateinit var resume : ResumeData
    lateinit var key : String
    lateinit var binding : ActivitySecondTemplateBinding
    private var tempPdfDocument: PdfDocument? = null   // store document temporarily for saving

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
        binding = ActivitySecondTemplateBinding.inflate(layoutInflater)
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

                if (snapshot.exists()) {
                    progress.dismiss()

                    val resumes = snapshot.getValue(ResumeData::class.java)
                    resumes?.id = snapshot.key
                    if (resumes != null) {

                        resume= resumes

                        // Fill UI
                        binding.ImageViewCadidatePhotoSecond.setImageURI(resume.image?.toUri())
                        binding.TextViewCandidteNameSecond.text = resume.name
                        binding.TextViewPhoneNoSecond.text = resume.phone
                        binding.TextViewEmailSecond.text = resume.mail
                        binding.TextViewWhatsappNoSecond.text = resume.whatsapp
                        binding.TextViewSummarySecond.text = resume.bio
                        binding.TextViewLanguagesKhowSecond.text = resume.language
                        binding.TextViewDegreeSecond.text = resume.degree
                        binding.TextViewDegreeDateSecond.text = resume.degreeDate
                        binding.TextViewInstitudeNameSecond.text = resume.institudeName
                        binding.TextViewCgpaSecond.text = "CGPA :${resume.cgpa}"
                        binding.TextViePercentageSecond.text = "Percentage :${resume.percentage}"
                        binding.TextViewCompanyNameSecond.text = resume.companyName
                        binding.TextViewOccuopationSecond.text = resume.companyRole
                        binding.TextViewCompanyWorkSecond.text = resume.companyWork
                        binding.TextViewSkillSecond.text = resume.skills
                        binding.TextViewHobbiesSecond.text = resume.hobby
                        binding.TextViewTechSkillSecond.text = resume.techSkills

                    }


                }
            }



            override fun onCancelled(error: DatabaseError) {
                progress.dismiss()
                Toast.makeText(
                    this@SecondTemplateActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Edit button
        binding.buttonEdit.setOnClickListener {
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
        binding.buttonDownload.setOnClickListener {
            val cardView = binding.CardViewMainSecond
            currentViewForExport = cardView // save view reference

            // Round candidate photo before export
            PdfUtils.applyRoundedImage(binding.ImageViewCadidatePhotoSecond, 50f)

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
        val intent = Intent(this@SecondTemplateActivity, AllResumeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
