package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SearchRfidTagScreen() {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val search = remember { mutableStateOf("") }
    val rfidData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val isLoading = remember { mutableStateOf(false) }

    // Firestore instance
    val db = FirebaseFirestore.getInstance()

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
                .width(350.dp)
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
                    text = "SEARCH RFID TAG",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Search TextField
                TextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    label = { Text("Search RFID Tag ID") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )

                // Search Button
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading.value = true
                        // Query Firestore to fetch the RFID tag data
                        db.collection("rfidTag").document(search.value)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // If RFID tag exists, populate the fields
                                    rfidData.value = document.data
                                } else {
                                    rfidData.value = null
                                }
                                isLoading.value = false
                            }
                            .addOnFailureListener {
                                isLoading.value = false
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

                // Show loading state while waiting for Firestore data
                if (isLoading.value) {
                    CircularProgressIndicator()
                }

                // If RFID data is found, display it
                rfidData.value?.let { data ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Using Card to display fields with rounded corners
                        DisplayRfidField("RFID Tag ID:", data["rfidTagId"].toString())
                        DisplayRfidField("Tag ID:", data["tagId"].toString()) // Added the new field
                        DisplayRfidField("Status:", data["status"].toString())
                        DisplayRfidField("Expiration Date:", data["expirationDate"].toString())
                        DisplayRfidField("User Email:", data["userEmail"].toString())
                        DisplayRfidField("User ID:", data["userId"].toString())
                        DisplayRfidField("User Name:", data["userName"].toString())
                        DisplayRfidField("Vehicle Model:", data["vehicleModel"].toString())
                        DisplayRfidField("Vehicle Number:", data["vehicleNumber"].toString())
                    }
                }

                // If no data found
                if (rfidData.value == null && !isLoading.value && search.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "No RFID tag found with ID: ${search.value}", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DisplayRfidField(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF0F0F0))
            .shadow(4.dp, RoundedCornerShape(16.dp)), // Apply shadow for elevation
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}