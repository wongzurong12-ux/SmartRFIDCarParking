package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NotificationScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    val notificationText = remember { mutableStateOf("Notification Fetching...") }
    val subtitleText = remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val email by loginViewModel.email.collectAsState()

    val expireTag = remember { mutableStateOf("") }  // State to store the expiration date

    if (!isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("loginSelection") { popUpTo("login") { inclusive = true } }
        }
        return
    }

    LaunchedEffect(email) {
        email?.let { userEmail ->
            // Fetch the tag ID from Firestore using email
            db.collection("rfidTag")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents.first()
                        val storedTagID = document.getString("tagId") ?: ""
                        val fetchedExpireTag = document.getString("expirationDate") ?: "Not Available"
                        expireTag.value = fetchedExpireTag  // Update expiration date state

                        // Fetch the latest tag ID from Realtime Database
                        database.child("AccessLog").child("LastUID").get()
                            .addOnSuccessListener { dataSnapshot ->
                                val detectedTagID = dataSnapshot.value.toString()

                                if (storedTagID == detectedTagID) {
                                    database.child("AccessLog").get()
                                        .addOnSuccessListener { accessSnapshot ->
                                            val entryTime =
                                                accessSnapshot.child("EntryTime").value?.toString()
                                            val exitTime =
                                                accessSnapshot.child("ExitTime").value?.toString()

                                            if (entryTime != null && exitTime == null) {
                                                subtitleText.value = "Car Entry Time"
                                                notificationText.value =
                                                    "Your car entered at $entryTime at TARUMT Block K car park."
                                            } else if (exitTime != null && entryTime != null) {
                                                subtitleText.value = "Car Exit Time"
                                                notificationText.value =
                                                    "Your car exited at $exitTime at TARUMT Block K car park."
                                                database.child("AccessLog").child("EntryTime")
                                                    .removeValue()
                                                database.child("AccessLog").child("ExitTime")
                                                    .removeValue()

                                            } else {
                                                notificationText.value =
                                                    "No recent entry or exit records found."
                                                database.child("AccessLog").child("EntryTime")
                                                    .removeValue()
                                                database.child("AccessLog").child("ExitTime")
                                                    .removeValue()
                                            }
                                        }
                                        .addOnFailureListener {
                                            notificationText.value =
                                                "Failed to fetch entry and exit times: ${it.message}"
                                        }
                                }
                            }
                            .addOnFailureListener {
                                notificationText.value = "Failed to fetch detected RFID tag."
                            }
                    } else {
                        notificationText.value = "User email not found in Firestore."
                    }
                }
                .addOnFailureListener { exception ->
                    notificationText.value = "Error fetching RFID data: ${exception.message}"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    backDispatcher?.onBackPressed()
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    modifier = Modifier.size(35.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(2f))
            Text(
                text = "Notifications",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(8f),
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFF983), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .height(500.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFDA00), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .height(80.dp)
                            .width(320.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = subtitleText.value,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = notificationText.value,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFDA00), shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .height(80.dp)
                            .width(320.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "RFID Tag Expire",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your RFID Tag will be expired on ${expireTag.value}.",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}