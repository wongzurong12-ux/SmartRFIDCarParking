package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class RfidDetails(
    val rfidTagId: String = "",
    val status: String = "",
    val vehicleModel: String = "",
    val vehicleNumber: String = ""
)

@Composable
fun DeleteRfidTagScreen() {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val search = remember { mutableStateOf("") }
    val isFound = remember { mutableStateOf(false) }
    val details = remember { mutableStateOf(RfidDetails()) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    suspend fun fetchRfidDetails(rfidTagId: String): RfidDetails? {
        return try {
            val document = db.collection("rfidTag").document(rfidTagId).get().await()
            if (document.exists()) {
                document.toObject<RfidDetails>()
            } else {
                null
            }
        } catch (e: Exception) {
            error.value = e.message ?: "Error fetching data"
            null
        }
    }

    suspend fun deleteRfidTag(rfidTagId: String): Boolean {
        return try {
            db.collection("rfidTag").document(rfidTagId).delete().await()
            true
        } catch (e: Exception) {
            error.value = e.message ?: "Error deleting RFID tag"
            false
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
                .height(550.dp)
                .width(300.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { backDispatcher?.onBackPressed() },
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
                    text = "DELETE RFID TAG",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    label = { Text("Search RFID Tag") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    loading.value = true
                                    error.value = ""
                                    val result = runCatching { fetchRfidDetails(search.value) }
                                    result.onSuccess { fetchedDetails ->
                                        if (fetchedDetails != null) {
                                            details.value = fetchedDetails
                                            isFound.value = true
                                        } else {
                                            error.value = "RFID Tag not found"
                                        }
                                    }.onFailure {
                                        error.value = it.message ?: "Unknown error occurred"
                                    }
                                    loading.value = false
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.search_icon),
                                contentDescription = "Search Icon",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (loading.value) {
                    Text("Loading...")
                }

                if (error.value.isNotEmpty()) {
                    Text(error.value, color = Color.Red)
                }

                if (isFound.value) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "RFID Tag Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text("RFID Tag ID: ${details.value.rfidTagId}", fontSize = 16.sp)
                            Text("Status: ${details.value.status}", fontSize = 16.sp)
                            Text("Vehicle Model: ${details.value.vehicleModel}", fontSize = 16.sp)
                            Text("Vehicle Number: ${details.value.vehicleNumber}", fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDeleteDialog.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(130.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "DELETE", color = Color.White, fontSize = 16.sp)
                    }
                }

                if (showDeleteDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog.value = false },
                        title = { Text("Confirm Delete") },
                        text = { Text("Are you sure you want to delete this RFID tag?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val isDeleted = deleteRfidTag(details.value.rfidTagId)
                                        if (isDeleted) {
                                            isFound.value = false
                                            showDeleteDialog.value = false
                                            error.value = "RFID Tag deleted successfully."
                                        }
                                    }
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog.value = false }) {
                                Text("No")
                            }
                        }
                    )
                }
            }
        }
    }
}
