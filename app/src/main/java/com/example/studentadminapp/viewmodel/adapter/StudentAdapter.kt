package com.example.studentadminapp.viewmodel.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentadminapp.R
import com.example.studentadminapp.model.Student
import com.example.studentadminapp.view.ShowStudentActivity

class StudentAdapter(private var context: Context, private var sList: List<Student>) :
    RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = sList[position]
        holder.name.text = list.name
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShowStudentActivity::class.java)
            intent.putExtra("Name", list.name)
            intent.putExtra("Email", list.email)
            intent.putExtra("ID", list.id)
            intent.putExtra("Standard", list.standard)
            intent.putExtra("Password", list.password)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return sList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvNames)
    }
}