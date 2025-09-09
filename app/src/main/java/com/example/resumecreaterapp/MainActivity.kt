package com.example.resumecreaterapp

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.resumecreaterapp.Objects.TemplateCl
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    var uri : Uri? = null
    lateinit var status: String


    //related firebase
    lateinit var database : DatabaseReference
    lateinit var auth : FirebaseAuth
    lateinit var key :String
    lateinit var progress : ProgressDialog
    lateinit var resume : ResumeData
    var templateId: Int ?=null
    //Global variables (accessible everywhere in this class)

    var name: String = ""
    var phone: String = ""
    var mail: String = ""
    var whatsapp: String = ""
    var bio: String = ""
    var language: String = ""
    var degree: String = ""
    var degreeDate: String = ""
    var institudeName: String = ""
    var cgpa: String = ""
    var percentage: String = ""
    var companyName: String = ""
    var companyRole: String = ""
    var companyWork: String = ""
    var skill: String = ""
    var hobby: String = ""
    var techSkill: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
// remove enableEdgeToEdge() because it conflicts with adjustResize
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //initialize progress dialog
        progress = ProgressDialog(this)
        progress.setMessage("Loading....")

        //  Initialize Firebase reference here
        database = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        //get CurrentUserId
        var userId = auth.currentUser?.uid.toString()

        // get id from CreateResumeActivity through intent
            key = intent.getStringExtra("key").toString()

          status = intent.getStringExtra("status").toString()

        //initialize template id here

         templateId = intent.getIntExtra("TemplateId",0)

        //ToolTip for Image
        binding.BtnToolTipForImageView.setOnClickListener {
            showToolTip(this)
        }

        if(status.equals("db") || status.equals("manual") )
        {
            //show progress
            progress.show()

            //get data from firebase
            database.child("Resumes").child(userId).child(key.toString()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    if (snapshot.exists()) {
                        progress.dismiss()

                        val resumes = snapshot.getValue(ResumeData::class.java)
                        key = snapshot.key.toString()
                        if (resumes != null) {

                            resume= resumes


                            // Fill ul , data from data Firebase
                            //initialize template id from firebase
                            templateId = resume.tempId
                            binding.ImageViewSelectImage.setImageURI(resume.image?.toUri())
                            //initialize uri from firebase for orientation
                            uri=resume.image?.toUri()
                            binding.textViewImagePath.text = resume.image?.toUri()?.lastPathSegment
                            binding.EditViewCandidateName.setText(resume.name)
                            binding.EditViewPhoneNoCandidate.setText(resume.phone)
                            binding.EditViewEmailCandidate.setText(resume.mail)
                            binding.EditViewWhatsappNo.setText(resume.whatsapp)
                            binding.EditViewBoiDataCanidate.setText(resume.bio)
                            binding.EditViewLangueageKhnown.setText(resume.language)
                            binding.AutoCompleteDegreeList.setText(resume.degree)
                            binding.TextViewDate.setText(resume.degreeDate)
                            binding.EditViewInstitudeName.setText(resume.institudeName)
                            binding.EditViewCgpa.setText(resume.cgpa)
                            binding.EditViewPersentage.setText(resume.percentage)
                            binding.EditViewCompanyName.setText(resume.companyName)
                            binding.EditViewCompanyRole.setText(resume.companyRole)
                            binding.EditViewCompanyWork.setText(resume.companyWork)
                            binding.EditViewSkills.setText(resume.skills)
                            binding.EditViewHobby.setText(resume.hobby)
                            binding.EditViewTechSkill.setText(resume.techSkills)

                        }


                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    progress.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }



        // adpter for degree list
        val degreeList = arrayOf(
            // Undergraduate Degrees
            "B.A.", "B.Sc.", "B.Com.", "BBA", "BMS", "BCA", "B.Tech.", "B.E.", "B.Arch.", "B.Plan",
            "B.Pharm.", "BDS", "MBBS", "BAMS", "BHMS", "BUMS", "BPT", "B.V.Sc.", "BFA", "B.Ed.",
            "LLB", "BHM", "BHMCT", "B.Sc. Nursing", "B.Lib.Sc.", "B.Des.", "B.Mus.", "B.P.Ed.",

            // Postgraduate Degrees
            "M.A.", "M.Sc.", "M.Com.", "MBA", "MCA", "M.Tech.", "M.E.", "M.Arch.", "M.Plan", "M.Pharm.",
            "MDS", "MD", "MS", "MPT", "M.V.Sc.", "MFA", "M.Ed.", "LLM", "MHM", "M.Lib.Sc.",
            "M.Des.", "M.Mus.", "M.P.Ed.", "MPA",

            // Doctoral & Higher Research Degrees
            "Ph.D.", "D.Litt.", "D.Sc.", "LLD", "Post-Doctoral Fellowship"
        )

        var adapt = ArrayAdapter(this,android.R.layout.simple_list_item_1,degreeList)
        binding.AutoCompleteDegreeList.setAdapter(adapt)

        // calendar view for date
        binding.TextViewDate.setOnClickListener {
            // Get today's date as default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val calendarSelected = Calendar.getInstance()
                    calendarSelected.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
                    binding.TextViewDate.text = dateFormat.format(calendarSelected.time)

                },
                year,
                month,
                day
            )
            datePickerDialog.datePicker.calendarViewShown = false
            datePickerDialog.datePicker.spinnersShown = true
            datePickerDialog.show()
        }


        binding.ImageViewSelectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(10)
        }

        binding.buttonSaveInfo.setOnClickListener {

            // storing only two word of name
            val fullName = binding.EditViewCandidateName.text.toString()
            name = fullName.split(" ").take(2).joinToString(" ")
            phone = binding.EditViewPhoneNoCandidate.text.toString()
            mail = binding.EditViewEmailCandidate.text.toString()
            whatsapp = binding.EditViewWhatsappNo.text.toString()
            bio = binding.EditViewBoiDataCanidate.text.toString()
            language = binding.EditViewLangueageKhnown.text.toString()
            degree = binding.AutoCompleteDegreeList.text.toString()
            degreeDate = binding.TextViewDate.text.toString()
            institudeName = binding.EditViewInstitudeName.text.toString()
            cgpa = binding.EditViewCgpa.text.toString()
            percentage = binding.EditViewPersentage.text.toString()
            companyName = binding.EditViewCompanyName.text.toString()
            companyRole = binding.EditViewCompanyRole.text.toString()
            companyWork = binding.EditViewCompanyWork.text.toString()
            skill = binding.EditViewSkills.text.toString()
            hobby = binding.EditViewHobby.text.toString()
            techSkill = binding.EditViewTechSkill.text.toString()

            if( uri.toString().isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty() && mail.isNotEmpty()
                && whatsapp.isNotEmpty() && bio.isNotEmpty() && language.isNotEmpty() && degree.isNotEmpty() && degreeDate.isNotEmpty()
                && institudeName.isNotEmpty() && cgpa.isNotEmpty() && percentage.isNotEmpty() && companyName.isNotEmpty()
                && companyRole.isNotEmpty() && companyWork.isNotEmpty() && skill.isNotEmpty() &&  hobby.isNotEmpty() &&  techSkill.isNotEmpty() )
            {


            if(status.equals("manual") || status.equals("db"))
           {
               //update in Firebase
               val resume = ResumeData(
               id = key,
               tempId = templateId,
               image = uri.toString(),
               name = name,
               phone = phone,
               mail = mail,
               whatsapp = whatsapp,
               bio = bio,
               language = language,
               degree = degree,
               degreeDate = degreeDate,
               institudeName = institudeName,
               cgpa = cgpa,
               percentage = percentage,
               companyName = companyName,
               companyRole = companyRole,
               companyWork = companyWork,
               skills = skill,
               hobby = hobby,
               techSkills = techSkill
               )


               database.child("Resumes").child(userId).child(key).setValue(resume)
                   .addOnSuccessListener {
                       Toast.makeText(this@MainActivity, "Resume updated successfully", Toast.LENGTH_SHORT).show()
                   }
                   .addOnFailureListener { error ->
                       Toast.makeText(this@MainActivity, "Update failed: ${error.message}", Toast.LENGTH_SHORT).show()
                   }

           }
            else {
               //store in Firebase

                key = database.push().key.toString()   // auto-generated unique ID
                if (key != null) {
                    val dataFirebase = ResumeData(
                        id = key,
                        TemplateCl.TEMPLATE_ID,
                        uri.toString(),
                        name,
                        phone,
                        mail,
                        whatsapp,
                        bio,
                        language,
                        degree,
                        degreeDate,
                        institudeName,
                        cgpa,
                        percentage,
                        companyName,
                        companyRole,
                        companyWork,
                        skill,
                        hobby,
                        techSkill
                    )

                    val userId = auth.currentUser?.uid.toString()

                database = FirebaseDatabase.getInstance().getReference()
                database.child("Resumes").child(userId).child(key).setValue(dataFirebase)
                    .addOnSuccessListener {
                    Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }

                }

           }

            // Start activity with requestCode = 200
                if(templateId == 1)
                {
                    var intent = Intent(this, CreateResmeActivity::class.java)
                    intent.putExtra("status", "main")
                    intent.putExtra("key", key)
                    startActivityForResult(intent, 200)
                }
                else if (templateId == 2)
                {
                    var intent = Intent(this, SecondTemplateActivity::class.java)
                    intent.putExtra("status", "main")
                    intent.putExtra("key", key)
                    startActivityForResult(intent, 200)
                }
                else if (templateId == 3)
                {
                    var intent = Intent(this, ThirdTemplateActivity::class.java)
                    intent.putExtra("status", "main")
                    intent.putExtra("key", key)
                    startActivityForResult(intent, 200)
                }
                else if (templateId == 4)
                {
                    var intent = Intent(this, ForthTemplateActivity::class.java)
                    intent.putExtra("status", "main")
                    intent.putExtra("key", key)
                    startActivityForResult(intent, 200)
                }
                else
                {
                    Toast.makeText(this, "Your Not Selected Template", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        showToolTip(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save image URI if selected
        outState.putString("selected_image_uri", uri?.toString())

        // Save selected date text
        outState.putString("selected_date", binding.TextViewDate.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore image
        val uriString = savedInstanceState.getString("selected_image_uri")
        if (!uriString.isNullOrEmpty()) {
            uri = Uri.parse(uriString)
            binding.ImageViewSelectImage.setImageURI(uri)
            binding.textViewImagePath.text = uri?.lastPathSegment
        }

        // Restore date
        val savedDate = savedInstanceState.getString("selected_date")
        if (!savedDate.isNullOrEmpty()) {
            binding.TextViewDate.text = savedDate
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 10 && resultCode == RESULT_OK && data != null)
        {
            try
            {
                uri = data.data
                if (uri != null)
                {
                    binding.ImageViewSelectImage.setImageURI(uri)
                    val fileName = uri?.lastPathSegment
                    binding.textViewImagePath.text = fileName ?: "No Name"
                }
                else
                {
                    Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                Toast.makeText(this, "Failed to set image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == 200 && resultCode == RESULT_OK && data != null)
        {
            //  Handle result coming back from templates
            //  here back activity status is assigned manual
            status = data.getStringExtra("status").toString()
        }
        else if (resultCode == ImagePicker.RESULT_ERROR)
        {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun MainActivity.showToolTip(context: Context) {

    SimpleTooltip.Builder(context)
        .anchorView(binding.ImageViewSelectImage)
        .text("Note: The image is stored only on your device. If you delete or move it, the image may not appear later in your resume.")
        .gravity(Gravity.BOTTOM)
        .animated(true)
        .transparentOverlay(false)
        .backgroundColor(Color.parseColor("#FFFFFFFF"))
        .arrowColor(Color.parseColor("#FFFFFFFF"))
        .arrowWidth(35F)

        .build()
        .show()
}
