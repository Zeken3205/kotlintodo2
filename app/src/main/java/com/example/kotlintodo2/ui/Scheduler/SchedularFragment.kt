package com.example.kotlintodo2.ui.Scheduler

import android.os.Bundle
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

class SchedularFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListener,
    TodoAdapter.ToDoAdapterClicksInterface {

    private lateinit var binding:FragmentSchedularBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
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
        databaseRef= FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())
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

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    // Convert each child snapshot to a Task object and add it to the list
                    val task = taskSnapshot.child("name").getValue(String::class.java)
                    val date = taskSnapshot.child("date").getValue(String::class.java)
                    val time = taskSnapshot.child("time").getValue(String::class.java)
                    val taskId = taskSnapshot.key.toString()
                    if (task!=null&&date!=null&&time!=null) {
                        mList.add(ToDoData(taskId, task, date, time))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onSaveTask(
        todo: String,
        popuptodotaskname: TextInputEditText,
        popupdate: EditText,
        popuptime: TextView
    ) {
        val newTaskRef = databaseRef.push()
        val taskMap = HashMap<String, Any>()
        taskMap["name"] = popuptodotaskname.text.toString()
        taskMap["date"] = cdate.toString()
        taskMap["time"] = popuptime.text.toString()
        newTaskRef.setValue(taskMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Do something else, like displaying a success message
                Toast.makeText(context,"Added", Toast.LENGTH_SHORT)
            } else {
                // Write operation failed, so display an error message
                Toast.makeText(context,"Failed to add", Toast.LENGTH_SHORT)
            }
            popuptodotaskname.text = null
            popupdate.text=null
            popuptime.text=null
            popupFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(
        toDoData: ToDoData,
        popuptodotaskname: TextInputEditText,
        popupdate: EditText,
        popuptime: TextView
    ) {
        val taskId = toDoData.taskid
        val name = popuptodotaskname.text.toString()
        val date = popupdate.text.toString()
        val time = popuptime.text.toString()

        val updates = HashMap<String, Any>()
        updates["name"] = name
        updates["date"] = date
        updates["time"] = time

        databaseRef.child(taskId).updateChildren(updates)
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
        databaseRef.child(toDoData.taskid).removeValue().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context,"Deleted", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(context,it.exception?.message, Toast.LENGTH_SHORT).show()
            }
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