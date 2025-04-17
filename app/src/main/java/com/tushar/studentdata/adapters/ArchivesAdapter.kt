package com.tushar.studentdata.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tushar.studentdata.R
import com.tushar.studentdata.databinding.ArchiveItemsBinding
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity

class ArchivesAdapter(private val list : ArrayList<ArchiveEntity>) : RecyclerView.Adapter<ArchivesAdapter.ViewHolder>() {
    class ViewHolder(val binding : ArchiveItemsBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val studentBinding = ArchiveItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(studentBinding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.binding.tvName.text = list[position].studentName
        holder.binding.tvRollNo.text = list[position].rollNo
        val image=list[position].img
        if (image!=null){
            val bitmap= BitmapFactory.decodeByteArray(image,0,image.size)
            holder.binding.imgStudent.setImageBitmap(bitmap)

    }else{
        holder.binding.imgStudent.setImageResource(R.drawable.student)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun getItem(position: Int): ArchiveEntity {
        return list[position]
    }


    fun updateList(newList:List<ArchiveEntity>){
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }
}