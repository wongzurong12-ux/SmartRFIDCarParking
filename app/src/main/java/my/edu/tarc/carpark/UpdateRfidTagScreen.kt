package my.edu.tarc.carpark

import android.app.DatePickerDialog
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

@Composable
fun UpdateRfidTagScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val rfidId = remember { mutableStateOf("") }
    val vehicle = remember { mutableStateOf("") }
    val expirationDate = remember { mutableStateOf("") }
    val tagId = remember { mutableStateOf("") }  // New field for tagId

    val rfidStatus = listOf("ACTIVE", "INACTIVATE")
    val expanded = remember { mutableStateOf(false) }
    val selectedStatus = remember { mutableStateOf(rfidStatus[0]) }

    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val isEditingVehicle = remember { mutableStateOf(false) }

    val search = remember { mutableStateOf("") }
    val firestore = FirebaseFirestore.getInstance()

    // Search functionality to fetch RFID data from Firestore
    fun searchRfidTag() {
        val docRef = firestore.collection("rfidTag").document(search.value)
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val rfid = document.getString("rfidTagId") ?: ""
                val vehicleModel = document.getString("vehicleModel") ?: ""
                val status = document.getString("status") ?: ""
                val expiration = document.getString("expirationDate") ?: ""
                val tag = document.getString("tagId") ?: ""  // Retrieve tagId

                rfidId.value = rfid
                vehicle.value = vehicleModel
                selectedStatus.value = status
                expirationDate.value = expiration
                tagId.value = tag  // Set the retrieved tagId
            } else {
                // Handle case where RFID tag is not found
                rfidId.value = ""
                vehicle.value = ""
                selectedStatus.value = rfidStatus[0]
                expirationDate.value = ""
                tagId.value = ""  // Reset tagId
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(0xFFFFDA00),
                    shape = RoundedCornerShape(30.dp)
                )
                .border(4.dp, Color.Black, shape = RoundedCornerShape(30.dp))
                .padding(32.dp)
                .height(600.dp)
                .width(300.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            backDispatcher?.onBackPressed()
                        },
                        modifier = Modifier.size(35.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_icon),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.tarumt_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )
                Text(
                    text = "UPDATE RFID TAG",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                TextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    label = { Text("Search RFID Tag") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Black
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { searchRfidTag() },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "SEARCH", color = Color.Black, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // RFID ID field (Non-editable)
                TextField(
                    value = rfidId.value,
                    onValueChange = { },
                    label = { Text("RFID Tag ID") },
                    readOnly = true,  // Ensures the field is non-editable
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.vehicle_icon),
                            contentDescription = "RFID Tag Id Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // New Tag ID field
                TextField(
                    value = tagId.value,  // Display the tagId value here
                    onValueChange = { tagId.value = it },  // Allow editing of the tagId field
                    label = { Text("Tag ID") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.vehicle_icon),  // Adjust as necessary
                            contentDescription = "Tag Id Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // RFID Tag Status
                Text(
                    text = "RFID TAG STATUS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedStatus.value,
                        onValueChange = { },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.rfid_tag),
                                contentDescription = "RFID Tag Status Icon",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown),
                                contentDescription = "Dropdown Icon",
                                modifier = Modifier.size(24.dp)
                                    .clickable {
                                        expanded.value = true
                                    },
                                tint = Color.Black
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(4.dp, Color.Black)
                            .clickable { expanded.value = true }
                    )

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        rfidStatus.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    selectedStatus.value = statusOption
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Model field
                TextField(
                    value = vehicle.value,
                    onValueChange = { vehicle.value = it },
                    label = { Text("Vehicle Model") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.vehicle_icon),
                            contentDescription = "Vehicle Model Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Expiration Date field
                if (showDatePicker) {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val context = LocalContext.current
                    DatePickerDialog(
                        context,
                        { _, selectedYear, selectedMonth, selectedDay ->
                            selectedDate =
                                "$selectedDay/${selectedMonth + 1}/$selectedYear"
                            showDatePicker = false
                        },
                        year,
                        month,
                        day
                    ).apply {
                        datePicker.minDate =
                            calendar.timeInMillis
                    }.show()
                }

                TextField(
                    value = expirationDate.value,  // This should be the selected date
                    onValueChange = { expirationDate.value = it },
                    label = { Text("Expiration Date") },
                    readOnly = false,  // Remove this line to make the field editable
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar_icon),
                            contentDescription = "Expiration Date Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Save button to update RFID data
                Button(
                    onClick = {
                        navController.popBackStack()
                        // Code to update the Firestore document
                        val updatedData = mapOf(
                            "status" to selectedStatus.value,
                            "vehicleModel" to vehicle.value,
                            "expirationDate" to expirationDate.value,
                            "tagId" to tagId.value

                        )
                        val docRef = firestore.collection("rfidTag").document(rfidId.value)
                        docRef.update(updatedData)
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "UPDATE", color = Color.Black, fontSize = 18.sp)
                }
            }
        }
    }
}