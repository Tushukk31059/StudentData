package com.tushar.studentdata

import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.text.method.Touch
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tushar.studentdata.adapters.ArchivesAdapter
import com.tushar.studentdata.adapters.StudentAdapter
import com.tushar.studentdata.dao.StudentDAO
import com.tushar.studentdata.databases.StudentDatabase
import com.tushar.studentdata.databinding.ActivityMainBinding
import com.tushar.studentdata.domains.ArchiveDomain
import com.tushar.studentdata.domains.StudentDomain
import com.tushar.studentdata.entities.StudentEntity
import com.tushar.studentdata.repository.StudentRepository
import com.tushar.studentdata.viewmodels.StudentViewModel
import com.tushar.studentdata.viewmodels.VMFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: RecyclerView
    private lateinit var archiveList : ArrayList<ArchiveDomain>
    private lateinit var adapter: StudentAdapter
    private lateinit var archivesAdapter: ArchivesAdapter
    private lateinit var viewModel:StudentViewModel
    private lateinit var studentDatabase:StudentDatabase
    private var isMain=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        studentDatabase=StudentDatabase.createDatabase(this)
        val repo=StudentRepository(studentDatabase.studentDAO(),studentDatabase.archiveDAO())
        adapter=StudentAdapter(arrayListOf())
        archivesAdapter= ArchivesAdapter(arrayListOf())
        viewModel=ViewModelProvider(this,VMFactory(repo))[StudentViewModel::class.java]
        archiveList=ArrayList()
        lifecycleScope.launch {
        studentDatabase.studentDAO().insertStudent(StudentEntity(studentName = "Rahul", rollNo = 48))
        studentDatabase.studentDAO().insertStudent(StudentEntity(studentName = "dcf", rollNo = 44))
        studentDatabase.studentDAO().insertStudent(StudentEntity(studentName = "ghj", rollNo = 46))
        studentDatabase.studentDAO().insertStudent(StudentEntity(studentName = "ful", rollNo = 41))
        }
        mainAdapterCalled()
        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isMain=false
                binding.txt.text="Archived Data"
                view=binding.recLayoutA.recyclerA
                binding.recLayoutA.recyclerA.adapter = archivesAdapter
                binding.recLayoutA.recyclerA.layoutManager = LinearLayoutManager(this)
                binding.recLayoutM.root.visibility= View.GONE
                binding.recLayoutA.root.visibility=View.VISIBLE
                viewModel.archive.observe(this){
                    archivesAdapter.updateList(it)
                }

                swipe(view)
            }else{
                mainAdapterCalled()
        }
        }

    }
    private fun swipe(view: RecyclerView){
        val swipeDel= ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position=viewHolder.adapterPosition
                if (isMain){

                    val stuObj=adapter.getItem(position)
                    when(direction){
                        ItemTouchHelper.LEFT->{
                            viewModel.delStudent(stuObj.id)
                    }ItemTouchHelper.RIGHT->{
                    viewModel.insertArchive(stuObj)
                    }
                    }
                }else{
                    val archiveObj=archivesAdapter.getItem(position)
                    viewModel.delArchive(archiveObj.id)

                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val leftSwipeBg = AppCompatResources.getDrawable(this@MainActivity,R.drawable.left_swipe_bg)
                val rightSwipeBg= AppCompatResources.getDrawable(this@MainActivity,R.drawable.right_swipw_bg)
                val delImg= AppCompatResources.getDrawable(this@MainActivity,R.drawable.avd_anim2)
                val arcImg= AppCompatResources.getDrawable(this@MainActivity,R.drawable.avd_anim)

                if (dX<0){
                    val height=delImg?.intrinsicHeight
                    val width=delImg?.intrinsicWidth
                    val itemView= viewHolder.itemView
                    val margin = itemView.height.minus(height!!.toInt())/2
                    val imgTop=itemView.top+margin
                    val imgBottom=itemView.bottom-margin
                    leftSwipeBg?.setBounds(itemView.right+dX.toInt()-20,itemView.top,itemView.right,itemView.bottom)
                    leftSwipeBg?.draw(c)
                    delImg.setBounds(itemView.right-margin-width!!.toInt(),imgTop,itemView.right-margin,imgBottom)
                    delImg.draw(c)
                    if (delImg is AnimatedVectorDrawable)
                        delImg.start()
                }
                if (dX>0){
                    val height=arcImg?.intrinsicHeight
                    val width=arcImg?.intrinsicWidth
                    val itemView= viewHolder.itemView
                    val margin = itemView.height.minus(height!!.toInt())/2
                    val imgTop=itemView.top+margin
                    val imgBottom=itemView.bottom-margin
                    rightSwipeBg?.setBounds(itemView.left,itemView.top,itemView.left+dX.toInt()+20,itemView.bottom)
                    rightSwipeBg?.draw(c)
                    arcImg.setBounds(itemView.left+margin,imgTop,itemView.left+margin+width!!.toInt(),imgBottom)
                    arcImg.draw(c)
                    if (arcImg is AnimatedVectorDrawable)
                        arcImg.start()
                }

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })

        swipeDel.attachToRecyclerView(view)
    }
    private fun mainAdapterCalled(){
        isMain=true

        binding.txt.text="Student Data"
        binding.recLayoutM.recyclerM.adapter = adapter
        binding.recLayoutM.recyclerM.layoutManager=LinearLayoutManager(this)
        view=binding.recLayoutM.recyclerM
        binding.recLayoutM.root.visibility= View.VISIBLE
        binding.recLayoutA.root.visibility=View.GONE
        viewModel.students.observe(this){
            adapter.updateList(it)
        }
        swipe(view)
    }
 }