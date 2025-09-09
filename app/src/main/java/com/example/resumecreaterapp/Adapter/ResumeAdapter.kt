package com.example.resumecreaterapp.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.resumecreaterapp.CreateResmeActivity
import com.example.resumecreaterapp.ForthTemplateActivity
import com.example.resumecreaterapp.MainActivity
import com.example.resumecreaterapp.Objects.TemplateCl
import com.example.resumecreaterapp.R
import com.example.resumecreaterapp.ResumeModel.Resume
import com.example.resumecreaterapp.ResumeModel.ResumeData
import com.example.resumecreaterapp.SecondTemplateActivity
import com.example.resumecreaterapp.ThirdTemplateActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ResumeAdapter(private var resumes: List<ResumeData>, val context : Context) : RecyclerView.Adapter<ResumeAdapter.resumeViewHolder>(){
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var key: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): resumeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resume_item,parent,false)
        return resumeViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: resumeViewHolder,
        position: Int
    ) {
        val resume = resumes[position]

        holder.photo.setImageURI(resume.image?.toUri())
        holder.name.text = resume.name
        holder.phone.text = resume.phone

        holder.rel_lay.setOnClickListener {

            // initialize template id for mainActivity
            TemplateCl.TEMPLATE_ID = resume.tempId

            //check and pass data according of template id
            if(resume.tempId == 1) {
                val intent = Intent(context, CreateResmeActivity::class.java)
                intent.putExtra("status", "adapter")
                intent.putExtra("key", resume.id)
                context.startActivity(intent)
            }
            else if(resume.tempId == 2) {
                val intent = Intent(context, SecondTemplateActivity::class.java)
                intent.putExtra("status", "adapter")
                intent.putExtra("key", resume.id)
                context.startActivity(intent)
            }
            else if(resume.tempId == 3) {
                val intent = Intent(context, ThirdTemplateActivity::class.java)
                intent.putExtra("status", "adapter")
                intent.putExtra("key", resume.id)
                context.startActivity(intent)
            }
            else if(resume.tempId == 4) {
                val intent = Intent(context, ForthTemplateActivity::class.java)
                intent.putExtra("status", "adapter")
                intent.putExtra("key", resume.id)
                context.startActivity(intent)
            }

        }

        holder.delete.setOnClickListener {

            //  Initialize Firebase reference here
            database = FirebaseDatabase.getInstance().getReference()
            auth = FirebaseAuth.getInstance()

            //get CurrentUserId
            var userId = auth.currentUser?.uid.toString()

            //get key
            key = resume.id.toString()

            val Alertdialog : Dialog= Dialog(context)
            Alertdialog.setContentView(R.layout.custome_delete_alert_dialog)
            Alertdialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cancel : TextView= Alertdialog.findViewById(R.id.TextView_cancel)
            val delete : TextView= Alertdialog.findViewById(R.id.TextView_delete)

            Alertdialog.show()

            cancel.setOnClickListener {
                Alertdialog.dismiss()
            }
            delete.setOnClickListener {
                database.child("Resumes").child(userId).child(key).removeValue()
                    .addOnSuccessListener {

                        Alertdialog.dismiss()
                        Toast.makeText(context, "Resume deleted successfully", Toast.LENGTH_SHORT).show()
                        refreshData(resumes)
                    }
                    .addOnFailureListener { error ->
                        Alertdialog.dismiss()
                        Toast.makeText(context, "Update failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }


        }
    }

//    fun refreshData()
//    {
//        notifyDataSetChanged()
//    }

    fun refreshData(newResum : List<ResumeData>)
    {
        resumes = newResum
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
       return resumes.size
    }

    class resumeViewHolder(itemview:View): RecyclerView.ViewHolder(itemview){

        val photo : ImageView = itemview.findViewById(R.id.ImageView_photo)
        val delete : ImageView = itemview.findViewById(R.id.ImageView_delete_button)
        val name : TextView = itemview.findViewById(R.id.TextView_name_Recycleview)
        val phone : TextView = itemview.findViewById(R.id.TextView_phone_recycleview)
        val rel_lay : ConstraintLayout = itemview.findViewById(R.id.constraint_cardview)
    }
}
