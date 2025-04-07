package com.tushar.studentdata.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tushar.studentdata.databinding.ListItemBinding
import com.tushar.studentdata.entities.StudentEntity

class StudentAdapter(private val list : ArrayList<StudentEntity>) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {
    class ViewHolder(val binding : ListItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val studentBinding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(studentBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.binding.tvName.text = list[position].studentName
        holder.binding.tvRollNo.text = list[position].rollNo.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun getItem(position: Int):StudentEntity{
        return list[position]
    }


fun updateList(newList:List<StudentEntity>){
    this.list.clear()
    this.list.addAll(newList)
    notifyDataSetChanged()
}
}