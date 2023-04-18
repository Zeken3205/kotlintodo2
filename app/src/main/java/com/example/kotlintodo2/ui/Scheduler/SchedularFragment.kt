package com.example.kotlintodo2.ui.Scheduler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodo2.databinding.FragmentSchedularBinding
import com.example.kotlintodo2.ui.AddTodoPopupFragment
import com.example.kotlintodo2.utils.ToDoData
import com.example.kotlintodo2.utils.TodoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SchedularFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListener,
    TodoAdapter.ToDoAdapterClicksInterface {

    private lateinit var binding:FragmentSchedularBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var navController: NavController
    private var popupFragment: AddTodoPopupFragment?=null
    private lateinit var adapter: TodoAdapter
    private lateinit var mList:MutableList<ToDoData>
    private lateinit var cdate:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentSchedularBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun init(view:View)
    {
        navController= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.recyclerview2.setHasFixedSize(true)
        binding.recyclerview2.layoutManager= LinearLayoutManager(context)
        mList= mutableListOf()
        adapter= TodoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerview2.adapter=adapter

    }

    private fun registerEvents()
    {
        binding.addtaskbtn.setOnClickListener {
            if (popupFragment!=null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment= AddTodoPopupFragment();
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                AddTodoPopupFragment.TAG
            )
        }

        binding.calendarView.setOnDateChangeListener { calendarView, i, i2, i3 ->
            if(i2<10)
            {
                cdate="$i3/0${i2+1}/$i"
            }
            else{
                cdate="$i3/${i2+1}/$i"
            }

        }

    }

    private fun getDataFromFirebase() {
        val collectionRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")

        collectionRef.orderBy("date")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.w(AddTodoPopupFragment.TAG, "Listen failed", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val taskList = mutableListOf<ToDoData>()
                    for (doc in snapshot.documents) {
                        val task = doc.getString("name")
                        val date = doc.getString("date")
                        val time = doc.getString("time")
                        val taskId = doc.id

                        if (task != null && date != null && time != null) {
                            taskList.add(ToDoData(taskId, task, date, time))
                        }
                    }
                    mList.clear()
                    mList.addAll(taskList)
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(AddTodoPopupFragment.TAG, "Current data: null")
                }
            }
    }



    override fun onSaveTask(todo: String, popuptodotaskname: TextInputEditText, popupdate: EditText, popuptime: EditText) {

        val collectionRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")


        val newTaskRef = collectionRef.document()
        val taskMap = hashMapOf(
            "name" to popuptodotaskname.text.toString(),
            "date" to cdate,
            "time" to popuptime.text.toString()
        )

        newTaskRef.set(taskMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Do something else, like displaying a success message
                    Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show()
                } else {
                    // Write operation failed, so display an error message
                    Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                }
                popuptodotaskname.text = null
                popupdate.text = null
                popuptime.text = null
                popupFragment!!.dismiss()
            }
    }

    override fun onUpdateTask(
        toDoData: ToDoData,
        popuptodotaskname: TextInputEditText,
        popupdate: EditText,
        popuptime: EditText
    ) {
        val taskId = toDoData.taskid
        val name = popuptodotaskname.text.toString()
        val date = popupdate.text.toString()
        val time = popuptime.text.toString()

        val taskRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks").document(taskId)

        val batch = db.batch()
        batch.update(taskRef, "name", name)
        batch.update(taskRef, "date", date)
        batch.update(taskRef, "time", time)

        batch.commit()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                }
                popuptodotaskname.text = null
                popupdate.text = null
                popuptime.text = null
                popupFragment?.dismiss()
            }
    }



    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        val taskId = toDoData.taskid
        val documentRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks").document(taskId)
        documentRef.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {
        if(popupFragment!=null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        popupFragment=AddTodoPopupFragment.newInstance(toDoData.taskid,toDoData.task,toDoData.date,toDoData.time)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager,AddTodoPopupFragment.TAG)
    }

}