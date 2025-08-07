package my.edu.tarc.carpark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RFIDDetailScreen(navController: NavController, loginViewModel: LoginViewModel) {

    val email by loginViewModel.email.collectAsState()

    val db = FirebaseFirestore.getInstance()

    var tagId by remember { mutableStateOf("") }
    var tagStatus by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var tagExpiryDate by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }

    LaunchedEffect(email) {
        email?.let {
            db.collection("rfidTag")
                .whereEqualTo("userEmail", it)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents.first()
                        tagId = document.getString("tagId") ?: ""
                        tagStatus = document.getString("status") ?: ""
                        vehicleModel = document.getString("vehicleModel") ?: ""
                        tagExpiryDate = document.getString("expirationDate") ?: ""
                    } else {
                        emailError = "User Email not found."
                    }
                }
                .addOnFailureListener { exception ->
                    emailError = "Error fetching user data: ${exception.message}"
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
                    Color(0xFFFFF983),
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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
                                navController.navigate("home")
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
                }

                Image(
                    painter = painterResource(id = R.drawable.tarumt_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "RFID Details",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "RFID Tag ID")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.rfid),
                        contentDescription = "RFID Tag ID",
                        modifier = Modifier.size(60.dp),
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = tagId.ifEmpty { "Not Available" },
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "RFID Tag Status")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.rfid_tag),
                        contentDescription = "RFID Tag Status",
                        modifier = Modifier.size(60.dp),
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = tagStatus.ifEmpty { "Not Available" },
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Vehicle Model")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.vehicle_icon),
                        contentDescription = "Vehicle Icon",
                        modifier = Modifier.size(60.dp),
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = vehicleModel.ifEmpty { "Not Available" },
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "RFID Tag Expiration Date")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(32.dp))
                    Image(
                        painter = painterResource(id = R.drawable.vehicle_icon),
                        contentDescription = "RFID Tag Expiration Date",
                        modifier = Modifier.size(60.dp),
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        text = tagExpiryDate.ifEmpty { "Not Available" },
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}