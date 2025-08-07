package my.edu.tarc.carpark

import android.app.DatePickerDialog
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

@Composable
fun AddRfidTagScreen(navController: NavHostController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val rfidId = remember { mutableStateOf("") }
    val tagId = remember { mutableStateOf("") }
    val rfidStatus = listOf("ACTIVE", "INACTIVATE")
    val expanded = remember { mutableStateOf(false) }
    val selectedStatus = remember { mutableStateOf(rfidStatus[0]) }
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var isValidRfidId by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFDA00), shape = RoundedCornerShape(30.dp))
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
                    text = "ADD RFID TAG",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                // RFID Input Section
                Text(
                    text = "RFID TAG ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                TextField(
                    value = rfidId.value,
                    onValueChange = { rfidId.value = it },
                    label = { Text("Enter RFID Tag ID") },
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

                // Search Button
                Button(
                    onClick = {
                        isLoading = true
                        db.collection("rfidTag").document(rfidId.value)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    isValidRfidId = true
                                    isLoading = false
                                } else {
                                    isValidRfidId = false
                                    isLoading = false
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "SEARCH RFID TAG", color = Color.Black, fontSize = 18.sp)
                }

                if (isValidRfidId) {
                    // Display all fields after successful search
                    Column {
                        // Tag ID Input Section
                        Text(
                            text = "TAG ID",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        TextField(
                            value = tagId.value,
                            onValueChange = { tagId.value = it },
                            label = { Text("Enter Tag ID") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.rfid_tag),
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
                                            .clickable { expanded.value = true },
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

                        Spacer(modifier = Modifier.height(16.dp))

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
                                        "$selectedDay/$selectedMonth/$selectedYear"
                                    showDatePicker = false
                                },
                                year,
                                month,
                                day
                            ).apply {
                                datePicker.minDate = calendar.timeInMillis
                            }.show()
                        }

                        Text(
                            text = "RFID TAG EXPIRATION DATE",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        TextField(
                            value = selectedDate,
                            onValueChange = { },
                            label = { Text("Select Expiration Date") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.vehicle_icon),
                                    contentDescription = "Vehicle Model Icon",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar_icon),
                                    contentDescription = "Calendar Icon",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            showDatePicker = true
                                        }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(4.dp, Color.Black)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        // Add RFID Tag Button
                        Button(
                            onClick = {
                                val tagData = hashMapOf(
                                    "rfidTagId" to rfidId.value,
                                    "tagId" to tagId.value,
                                    "status" to selectedStatus.value,
                                    "expirationDate" to selectedDate
                                )

                                db.collection("rfidTag")
                                    .document(rfidId.value)
                                    .update(tagData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        navController.navigate("adminDashboard") {
                                            popUpTo("addRfidTagScreen") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener {
                                        // Handle failure
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "ADD RFID TAG", color = Color.Black, fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}