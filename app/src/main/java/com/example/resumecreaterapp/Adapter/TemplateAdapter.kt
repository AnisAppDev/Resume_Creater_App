package com.example.resumecreaterapp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.resumecreaterapp.MainActivity
import com.example.resumecreaterapp.R
import com.example.resumecreaterapp.ResumeModel.Template
import com.example.resumecreaterapp.Objects.TemplateCl


class TemplateAdapter(val templateList: List<Template>, val context: Context): RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TemplateAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TemplateAdapter.ViewHolder,
        position: Int
    ) {

        val template = templateList[position]

        holder.id.text = template.Template_Id.toString()
        holder.image.setImageResource(template.Image)

        holder.relative.setOnClickListener {

            TemplateCl.TEMPLATE_ID = template.Template_Id
            var intent = Intent(context, MainActivity::class.java)
            intent.putExtra("TemplateId",template.Template_Id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return templateList.size
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val image : ImageView = itemView.findViewById(R.id.ImageView_template_photo)
        val id : TextView = itemView.findViewById(R.id.TextView_template_id)
        val relative : RelativeLayout = itemView.findViewById(R.id.RelativeLayout_main)
    }
}