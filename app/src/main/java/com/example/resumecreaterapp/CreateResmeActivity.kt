package com.example.resumecreaterapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.resumecreaterapp.Objects.PdfUtils
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.databinding.ActivityCreateResmeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CreateResmeActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateResmeBinding
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var progress: ProgressDialog
    lateinit var resume: ResumeData
    lateinit var key: String

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
        binding = ActivityCreateResmeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progress = ProgressDialog(this)
        progress.setMessage("Loading....")

        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()

        val status = intent.getStringExtra("status")
        val id = intent.getStringExtra("key")

        progress.show()
        database.child("Resumes").child(userId).child(id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        progress.dismiss()
                        val resumes = snapshot.getValue(ResumeData::class.java)
                        key = snapshot.key.toString()
                        if (resumes != null) {
                            resume = resumes

                            // Fill UI
                            binding.ImageViewCadidatePhoto.setImageURI(resume.image?.toUri())
                            binding.TextViewCandidteName.text = resume.name
                            binding.TextViewPhoneNo.text = resume.phone
                            binding.TextViewEmail.text = resume.mail
                            binding.TextViewWhatsappNo.text = resume.whatsapp
                            binding.TextViewCandidateBio.text = resume.bio
                            binding.TextViewLanguagesKhow.text = resume.language
                            binding.TextViewDegree.text = resume.degree
                            binding.TextViewDegreeDate.text = resume.degreeDate
                            binding.TextViewDegreeInstitudeName.text = resume.institudeName
                            binding.TextViewDegreeCgpa.text = "CGPA :${resume.cgpa}"
                            binding.TextViewDegreePercentage.text =
                                "Percentage :${resume.percentage}"
                            binding.TextViewWorkExperienceCompany.text = resume.companyName
                            binding.TextViewWorkExperiencePost.text = resume.companyRole
                            binding.TextViewWorkExperienceWork.text = resume.companyWork
                            binding.TextViewSkill.text = resume.skills
                            binding.TextViewHobbies.text = resume.hobby
                            binding.TextViewTechSkill.text = resume.techSkills
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progress.dismiss()
                    Toast.makeText(
                        this@CreateResmeActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // Edit button
        binding.buttonEdit.setOnClickListener {
            if (status == "main") {
                val returnIntent = Intent()
                returnIntent.putExtra("status", "manual")
                returnIntent.putExtra("key", key)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
            if (status == "adapter") {
                val returnIntent = Intent(this, MainActivity::class.java)
                returnIntent.putExtra("status", "db")
                returnIntent.putExtra("key", key)
                setResult(RESULT_OK, returnIntent)
                startActivity(returnIntent)
            }
        }

        // Download button
        binding.buttonDownload.setOnClickListener {
            val cardView = binding.cardviewMain
            currentViewForExport = cardView // save view reference

            // Round candidate photo before export
            PdfUtils.applyRoundedImage(binding.ImageViewCadidatePhoto, 50f)

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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@CreateResmeActivity, AllResumeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
