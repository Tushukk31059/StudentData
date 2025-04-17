package com.tushar.studentdata

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.method.Touch
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.tushar.studentdata.adapters.ArchivesAdapter
import com.tushar.studentdata.adapters.StudentAdapter
import com.tushar.studentdata.dao.StudentDAO
import com.tushar.studentdata.databases.StudentDatabase
import com.tushar.studentdata.databinding.ActivityMainBinding
import com.tushar.studentdata.databinding.LayoutDialogBinding
import com.tushar.studentdata.domains.ArchiveDomain
import com.tushar.studentdata.domains.StudentDomain
import com.tushar.studentdata.entities.ArchiveEntity
import com.tushar.studentdata.entities.StudentEntity
import com.tushar.studentdata.repository.StudentRepository
import com.tushar.studentdata.viewmodels.StudentViewModel
import com.tushar.studentdata.viewmodels.VMFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var view: RecyclerView
    private lateinit var archiveList: ArrayList<ArchiveDomain>
    private lateinit var adapter: StudentAdapter
    private lateinit var archivesAdapter: ArchivesAdapter
    private lateinit var viewModel: StudentViewModel
    private lateinit var studentDatabase: StudentDatabase
    private var isMain = true
    private var animMap = mutableMapOf<Int, Drawable>()
    private var animatorSet = mutableSetOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        studentDatabase = StudentDatabase.createDatabase(this)
        val repo = StudentRepository(studentDatabase.studentDAO(), studentDatabase.archiveDAO())
        adapter = StudentAdapter(arrayListOf())
        archivesAdapter = ArchivesAdapter(arrayListOf())
        viewModel = ViewModelProvider(this, VMFactory(repo))[StudentViewModel::class.java]
        archiveList = ArrayList()
        val dialogBinding=LayoutDialogBinding.inflate(layoutInflater)
        var byteArray:ByteArray?
        var selectedImg: ByteArray?=null

        val mediaLauncher=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
                uri->
            if (uri!=null){
                dialogBinding.imgAdd.setImageURI(uri)
                byteArray=uriToByteArray(uri)
                selectedImg=byteArray
                if(byteArray!=null){
                    val bitmapObj=BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)
                    dialogBinding.imgAdd.setImageBitmap(bitmapObj)
                }
            }
        }

        binding.btnFAB.setOnClickListener {
            val dialog=Dialog(this)
            val dialogBinding=LayoutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)
            val window=dialog.window
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)


            dialogBinding.imgAdd.setOnClickListener {
                mediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            dialogBinding.btnSubmit.setOnClickListener {
                val name=dialogBinding.etName.text.toString().trim()
                val rollNo=dialogBinding.etRoll.text.toString().trim().toInt()
                if (dialogBinding.etName.text.isEmpty()){
                    dialogBinding.til1.error="Enter Name"
                }else if (dialogBinding.etRoll.text.isEmpty()){
                    dialogBinding.til2.error="Enter Roll Number"

                }else if(selectedImg==null){
                    lifecycleScope.launch {
                        studentDatabase.studentDAO()
                            .insertStudent(StudentEntity(studentName = name, rollNo = rollNo, img = null ))

                    }

                }
                else{
                    lifecycleScope.launch {
                        studentDatabase.studentDAO()
                            .insertStudent(StudentEntity(studentName = name, rollNo = rollNo, img = selectedImg ))

                    }
                }
                selectedImg=null
                    dialog.dismiss()

            }
            dialog.show()
        }
//        lifecycleScope.launch {
//            studentDatabase.studentDAO()
//                .insertStudent(StudentEntity(studentName = "Rahul", rollNo = 48))
//            studentDatabase.studentDAO()
//                .insertStudent(StudentEntity(studentName = "dcf", rollNo = 44))
//            studentDatabase.studentDAO()
//                .insertStudent(StudentEntity(studentName = "ghj", rollNo = 46))
//            studentDatabase.studentDAO()
//                .insertStudent(StudentEntity(studentName = "ful", rollNo = 41))
//        }
        mainAdapterCalled()
        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isMain = false
                binding.txt.text = "Archived Data"
                view = binding.recLayoutA.recyclerA
                binding.recLayoutA.recyclerA.adapter = archivesAdapter
                binding.recLayoutA.recyclerA.layoutManager = LinearLayoutManager(this)
                binding.recLayoutM.root.visibility = View.GONE
                binding.recLayoutA.root.visibility = View.VISIBLE
                viewModel.archive.observe(this) {
                    archivesAdapter.updateList(it)
                }

                swipe(view)
            } else {
                mainAdapterCalled()
            }
        }

    }

    private fun uriToByteArray(uri: Uri):ByteArray? {
        contentResolver.openInputStream(uri).use {
            return it?.readBytes()
        }

    }

    private fun swipe(view: RecyclerView) {
        val swipeDel = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
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
                val position = viewHolder.adapterPosition
                if (isMain) {

                    val stuObj = adapter.getItem(position)
                    when (direction) {
                        ItemTouchHelper.LEFT -> {
                            viewModel.delStudent(stuObj.id)
                            Snackbar.make(binding.root,"Deleted",Snackbar.LENGTH_SHORT)
                                .setAction("Undo"){
                                    viewModel.insertStudent(stuObj)
                                }
                                .show()
                            animatorSet.remove(position*2)
                            animMap.remove(position*2)
                            animatorSet.remove(position*2+1)
                            animMap.remove(position*2+1)
                        }

                        ItemTouchHelper.RIGHT -> {
                            viewModel.insertArchive(stuObj)
                            viewModel.delStudent(stuObj.id)
                            Snackbar.make(binding.root,"Added to Archives",Snackbar.LENGTH_SHORT)
                                .setAction("Undo"){
                                    viewModel.delArchive(stuObj.id)
                                    viewModel.insertStudent(stuObj)
                                }
                                .show()
                            animatorSet.remove(position*2)
                            animMap.remove(position*2)
                            animatorSet.remove(position*2+1)
                            animMap.remove(position*2+1)
                        }
                    }
                } else {
                    val archiveObj = archivesAdapter.getItem(position)
                    viewModel.delArchive(archiveObj.id)
                    Snackbar.make(binding.root,"Deleted",Snackbar.LENGTH_SHORT)
                        .setAction("Undo"){
                            viewModel.insertArchive(archiveObj.toStudentEntity())
                        }
                        .show()
                    animatorSet.remove(position*2)
                    animMap.remove(position*2)
                    animatorSet.remove(position*2+1)
                    animMap.remove(position*2+1)

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

                val leftSwipeBg =
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.left_swipe_bg)
                val rightSwipeBg =
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.right_swipw_bg)
                val delImg = animMap.getOrPut(viewHolder.adapterPosition*2){ AppCompatResources.getDrawable(this@MainActivity, R.drawable.avd_anim2)!!.mutate()}
                val arcImg = animMap.getOrPut(viewHolder.adapterPosition*2+1){ AppCompatResources.getDrawable(this@MainActivity, R.drawable.avd_anim)!!.mutate()}
                val position = viewHolder.adapterPosition
                if (dX < 0) {
                    val height = delImg.intrinsicHeight
                    val width = delImg.intrinsicWidth
                    val itemView = viewHolder.itemView
                    val margin = itemView.height.minus(height.toInt()) / 2
                    val imgTop = itemView.top + margin
                    val imgBottom = itemView.bottom - margin
                    leftSwipeBg?.setBounds(
                        itemView.right + dX.toInt() - 20,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    leftSwipeBg?.draw(c)
                    delImg.setBounds(
                        itemView.right - margin - width.toInt(),
                        imgTop,
                        itemView.right - margin,
                        imgBottom
                    )
                    delImg.draw(c)
                    if (isCurrentlyActive && animatorSet.add(position * 2)) {
                        if (delImg is AnimatedVectorDrawable)
                            delImg.start()
                    }
                }
                if (dX > 0) {
                    val height = arcImg.intrinsicHeight
                    val width = arcImg.intrinsicWidth
                    val itemView = viewHolder.itemView
                    val margin = itemView.height.minus(height.toInt()) / 2
                    val imgTop = itemView.top + margin
                    val imgBottom = itemView.bottom - margin
                    rightSwipeBg?.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt() + 20,
                        itemView.bottom
                    )
                    rightSwipeBg?.draw(c)
                    arcImg.setBounds(
                        itemView.left + margin,
                        imgTop,
                        itemView.left + margin + width.toInt(),
                        imgBottom
                    )
                    arcImg.draw(c)
                    if (isCurrentlyActive && animatorSet.add(position * 2 + 1)) {
                        if (arcImg is AnimatedVectorDrawable)
                            arcImg.start()
                    }
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

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                animatorSet.remove(viewHolder.adapterPosition * 2)
                animatorSet.remove(viewHolder.adapterPosition * 2 + 1)

                super.clearView(recyclerView, viewHolder)
            }
        })

        swipeDel.attachToRecyclerView(view)
    }

    private fun mainAdapterCalled() {
        isMain = true

        binding.txt.text = "Student Data"
        binding.recLayoutM.recyclerM.adapter = adapter
        binding.recLayoutM.recyclerM.layoutManager = LinearLayoutManager(this)
        view = binding.recLayoutM.recyclerM
        binding.recLayoutM.root.visibility = View.VISIBLE
        binding.recLayoutA.root.visibility = View.GONE
        viewModel.students.observe(this) {
            adapter.updateList(it)
        }
        swipe(view)
    }

    fun materialDatePicker() {
//        val calendar =
//            Calendar.getInstance()
//            // Calculate the minimum and maximum selectable dates
//
//        val simpleDateFormat = SimpleDateFormat("dd / MMMM / yyyy", Locale.getDefault())
//        val calConstraintsBuilder = CalendarConstraints.Builder()
//        val minDate = simpleDateFormat.parse("12 / December / 2024")
//        val maxDate = simpleDateFormat.parse("12 / March / 2025")
//        calConstraintsBuilder.setStart(minDate!!.time)
//        calConstraintsBuilder.setEnd(maxDate!!.time)
//        val validator = CompositeDateValidator.allOf(
//            listOf(DateValidatorPointForward.from(minDate.time), DateValidatorPointBackward.before(maxDate.time))
//        )
//        calConstraintsBuilder.setValidator(validator)
//        val calConstraints = calConstraintsBuilder.build()
//        val datePicker = MaterialDatePicker.Builder.datePicker()
//            .setCalendarConstraints(calConstraints)
//            .build()
//        datePicker.show(supportFragmentManager,null)

    }
    fun ArchiveEntity.toStudentEntity():StudentEntity{
        return StudentEntity(
            id = this.id,
            studentName = this.studentName,
            rollNo = this.rollNo.toInt(),
            img = this.img
        )
    }
}