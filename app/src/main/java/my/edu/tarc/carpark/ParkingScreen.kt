package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun ParkingScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("loginSelection") { popUpTo("login") { inclusive = true } }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make entire screen scrollable
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
                onClick = { backDispatcher?.onBackPressed() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    modifier = Modifier.size(35.dp),
                    tint = Color.Black
                )
            }

            Text(
                text = "Parking Availability",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFF983), shape = RoundedCornerShape(16.dp))
                    .padding(32.dp)
                    .height(500.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter
            ){
            // Directly call DisplayCarStatus without nesting it inside a Box
            DisplayCarStatus()
                }
        }
    }
}


@Composable
fun DisplayCarStatus() {
    var parkingData by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val database = Firebase.database
    val parkingRef = database.getReference("ParkingData")

    parkingRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val dataList = mutableListOf<Pair<String, String>>()
            for (parkingSnapshot in snapshot.children) {
                val parkingId = parkingSnapshot.key ?: "Unknown ID"
                val status =
                    parkingSnapshot.child("Status").getValue(String::class.java) ?: "Unknown Status"
                dataList.add(parkingId to status)
            }
            parkingData = dataList
        }

        override fun onCancelled(error: DatabaseError) {
            parkingData = listOf("Error" to error.message)
        }
    })

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        parkingData.forEach { (id, status) ->
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFDA00), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Block K Image
                    Image(
                        painter = painterResource(id = R.drawable.block_k),
                        contentDescription = "Block K Car Park Image",
                        modifier = Modifier
                            .size(130.dp)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Column for Location and Parking Status
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Location Icon
                            Icon(
                                painter = painterResource(id = R.drawable.location_icon),
                                contentDescription = "Location Icon",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Location Text
                            Text(
                                text = "TARUMT, KL BLOCK K CAR PARK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Parking Space ID: $id",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Status: $status",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Adds spacing between boxes
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}