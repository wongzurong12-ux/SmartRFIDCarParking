package my.edu.tarc.carpark

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
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

@Composable
fun AddParkingScreen(navController : NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val parkingId = remember { mutableStateOf("") }
    val parkingArea = "BLOCK K"
    val parkingType = listOf("YELLOW", "RED")
    val expanded = remember { mutableStateOf(false) }
    val selectedType = remember { mutableStateOf(parkingType[0]) }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    fun generateParkingSpaceId(
        onParkingSpaceIdGenerated: (String) -> Unit
    ) {
        db.collection("metadata").document("lastParkingSpaceId").get()
            .addOnSuccessListener { document ->
                val lastParkingSpaceId = document.getString("lastParkingSpaceId") ?: "P000"
                val numericId = lastParkingSpaceId.substring(1).toInt() + 1
                val newParkingSpaceId = "P${numericId.toString().padStart(3, '0')}"

                db.collection("metadata").document("lastParkingSpaceId")
                    .set(mapOf("lastParkingSpaceId" to newParkingSpaceId))
                    .addOnSuccessListener {
                        onParkingSpaceIdGenerated(newParkingSpaceId)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Failed to generate parking space ID: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    LaunchedEffect(Unit) {
        generateParkingSpaceId { id ->
            parkingId.value = id
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
                    text = "ADD PARKING",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "PARKING SPACE ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                TextField(
                    value = parkingId.value,
                    onValueChange = { },
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.parking_icon),
                            contentDescription = "Parking Icon",
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

                Text(
                    text = "PARKING AREA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                TextField(
                    value = parkingArea,
                    onValueChange = { },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.admin_parking),
                            contentDescription = "Parking Area Icon",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PARKING TYPE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedType.value,
                        onValueChange = { },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.vehicle_icon),
                                contentDescription = "Parking Type Icon",
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
                        parkingType.forEach { statusOption ->
                            DropdownMenuItem(
                                text = { Text(statusOption) },
                                onClick = {
                                    selectedType.value = statusOption
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.popBackStack()

                        val parkingData = mapOf(
                            "spaceId" to parkingId.value,
                            "parkingArea" to parkingArea,
                            "spaceType" to selectedType.value
                        )

                        db.collection("parkingSpace")
                            .document(parkingId.value)
                            .set(parkingData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Parking Space Added Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    context,
                                    "Failed to Add Parking Space: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "ADD", color = Color.Black, fontSize = 18.sp)
                }
            }
        }
    }
}