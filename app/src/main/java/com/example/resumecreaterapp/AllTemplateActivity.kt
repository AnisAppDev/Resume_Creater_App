package com.example.resumecreaterapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.resumecreaterapp.Adapter.TemplateAdapter
import com.example.resumecreaterapp.ResumeModel.Template
import com.example.resumecreaterapp.databinding.ActivityAllTemplateBinding

class AllTemplateActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAllTemplateBinding
    lateinit var templatelist : MutableList<Template>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //initialize list
        templatelist = mutableListOf()

        // add data
        templatelist.add(Template(R.drawable.one,1))
        templatelist.add(Template(R.drawable.two,2))
        templatelist.add(Template(R.drawable.three,3))
        templatelist.add(Template(R.drawable.four,4))

        binding.RecycleViewTemplate.setHasFixedSize(true)
        //show data as grid view
        binding.RecycleViewTemplate.layoutManager= StaggeredGridLayoutManager(2, GridLayoutManager. VERTICAL)

        val adapter = TemplateAdapter(templatelist,this)

        binding.RecycleViewTemplate.adapter = adapter


    }
}