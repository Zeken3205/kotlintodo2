package com.example.kotlintodo2.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodo2.databinding.FragmentHomeBinding
import com.example.kotlintodo2.ui.AddTodoPopupFragment
import com.example.kotlintodo2.ui.AddTodoPopupFragment.Companion.TAG
import com.example.kotlintodo2.utils.ToDoData
import com.example.kotlintodo2.utils.TodoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlintodo2.ui.NotificationReceiver
import java.time.LocalTime

import java.util.*

class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextButtonClickListener,
    TodoAdapter.ToDoAdapterClicksInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popupFragment: AddTodoPopupFragment?=null
    private lateinit var adapter: TodoAdapter
    private lateinit var mList:MutableList<ToDoData>
    private lateinit var timestamp: Timestamp
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHomeBinding.inflate(inflater,container,false)
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
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager= LinearLayoutManager(context)
        mList= mutableListOf()
        adapter= TodoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter=adapter
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

    }

    private fun getDataFromFirebase() {
        val collectionRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)
        val query = collectionRef.orderBy("timestamp")
             // sort by due_date field

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.w(TAG, "Listen failed", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val taskList = mutableListOf<ToDoData>()
                for (doc in snapshot.documents) {
                    val task = doc.getString("name")
                    val date = doc.getString("date")
                    val time = doc.getString("time")
                    val completed = doc.getBoolean("completed") ?: false
                    val timestamp=doc.getTimestamp("timestamp")

                    val taskId = doc.id

                    if (task != null && date != null && time != null && date == formattedDate) {
                        taskList.add(ToDoData(taskId, task, date, time, completed))
                    }

                }
                // Sort the task list so completed tasks are at the bottom
                //taskList.sortBy { !it.completed }

                mList.clear()
                mList.addAll(taskList)
                adapter.notifyDataSetChanged()
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleNotification(context: Context) {
        val collectionRef =
            db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")
        val query = collectionRef.orderBy("timestamp")

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.w(TAG, "Listen failed", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val firstDocument = snapshot.documents.firstOrNull()
                if (firstDocument != null) {

                    timestamp = firstDocument.getTimestamp("timestamp")!!

                }
            }
        }

        if (mList.isNotEmpty()) {
            if (timestamp == getCurrentTimestamp()) {
                val task = mList[0];

                if (task != null) {
                    val name = task.task
                    val time = task.time as LocalTime

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.set(Calendar.HOUR_OF_DAY, time.hour)
                    calendar.set(Calendar.MINUTE, time.minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(context, NotificationReceiver::class.java).apply {
                        putExtra("name", name)
                        putExtra("time", time.toString())
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }

            }
        }
    }

    private fun getCurrentTimestamp(): Timestamp {
        val calendar = Calendar.getInstance()
        val timestamp = Timestamp(calendar.time)
        return timestamp
    }



    override fun onSaveTask(todo: String, popuptodotaskname: TextInputEditText, popupdate: String, popuptime: String) {

        val collectionRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks")

        val newTaskRef = collectionRef.document()

        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
        val date = dateFormat.parse(popupdate)

        val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.ENGLISH)
        timeFormat.timeZone = TimeZone.getDefault()
        val time = timeFormat.parse(popuptime)

        val dateTime = Calendar.getInstance()
        dateTime.timeZone = TimeZone.getDefault()
        dateTime.time = time
        dateTime.set(Calendar.YEAR, date.year + 1900)
        dateTime.set(Calendar.MONTH, date.month)
        dateTime.set(Calendar.DAY_OF_MONTH, date.date)

        val timestamp = Timestamp(dateTime.time)

        val taskMap = hashMapOf(
            "name" to popuptodotaskname.text.toString(),
            "date" to popupdate,
            "time" to popuptime,
            "timestamp" to timestamp
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
                popupFragment!!.dismiss()
            }
    }


    override fun onUpdateTask(
        toDoData: ToDoData,
        popuptodotaskname: TextInputEditText,
        popupdate: String,
        popuptime: String
    ) {
        val taskId = toDoData.taskid
        val name = popuptodotaskname.text.toString()


        val taskRef = db.collection("users").document(auth.currentUser?.uid.toString()).collection("tasks").document(taskId)

        val batch = db.batch()
        batch.update(taskRef, "name", name)


        batch.commit()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                }
                popuptodotaskname.text = null
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
        popupFragment=AddTodoPopupFragment.newInstance(toDoData.taskid,toDoData.task,toDoData.date,toDoData.time,toDoData.completed)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager,AddTodoPopupFragment.TAG)
    }

}