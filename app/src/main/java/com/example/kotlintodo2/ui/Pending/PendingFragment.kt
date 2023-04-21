package com.example.kotlintodo2.ui.Pending

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodo2.databinding.FragmentPendingBinding
import com.example.kotlintodo2.ui.AddTodoPopupFragment
import com.example.kotlintodo2.utils.ToDoData
import com.example.kotlintodo2.utils.TodoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*


class PendingFragment : Fragment(),TodoAdapter.ToDoAdapterClicksInterface,
    AddTodoPopupFragment.DialogNextButtonClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var navController: NavController
    private lateinit var binding: FragmentPendingBinding
    private var popupFragment: AddTodoPopupFragment?=null
    private lateinit var adapter: TodoAdapter
    private lateinit var mList:MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentPendingBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
    }
    private fun init(view:View)
    {
        navController= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.recyclerview3.setHasFixedSize(true)
        binding.recyclerview3.layoutManager= LinearLayoutManager(context)
        mList= mutableListOf()
        adapter= TodoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerview3.adapter=adapter
    }
    private fun getDataFromFirebase() {
        val collectionRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val currentParsedDate = dateFormat.parse(formattedDate)

        collectionRef.orderBy("timestamp")
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
                        val completed = doc.getBoolean("completed")
                        val taskId = doc.id
                        val dateparsed = date?.let { dateFormat.parse(it) }
                        if (dateparsed != null) {
                            if (task != null && date!= null && time != null && dateparsed.before(currentParsedDate)) {
                                if (completed == true) {
                                    // Delete the task
                                    val documentRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks").document(taskId)
                                    documentRef.delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Deleted completed task", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Failed to delete completed task: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    // Add the task to the list
                                    taskList.add(ToDoData(taskId, task, date, time))
                                }
                            }
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
        popupFragment=AddTodoPopupFragment.newInstance(toDoData.taskid,toDoData.task,toDoData.date,toDoData.time,toDoData.completed)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager,AddTodoPopupFragment.TAG)
    }

    override fun onSaveTask(
        todo: String,
        popuptodotaskname: TextInputEditText,
        popupdate: String,
        popuptime: String
    ) {
        TODO("Not yet implemented")
    }

    override fun onUpdateTask(
        toDoData: ToDoData,
        popuptodotaskname: TextInputEditText,
        popupdate: String,
        popuptime: String
    ) {
        TODO("Not yet implemented")
    }
}