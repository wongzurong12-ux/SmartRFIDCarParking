package my.edu.tarc.carpark

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayInputStream
import java.io.InputStream

@Composable
fun ProfileScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val email by loginViewModel.email.collectAsState()

    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }

    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val imageBitmap = profileImageBitmap?.asImageBitmap()
    val defaultPainter = painterResource(id = R.drawable.tarumt_logo)


    if (!isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("loginSelection") { popUpTo("login") { inclusive = true } }
        }
        return
    }

    LaunchedEffect(email) {
        email?.let {
            db.collection("rfidTag")
                .whereEqualTo("userEmail", it)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents.first()
                        vehicleNumber = document.getString("vehicleNumber") ?: ""
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching RFID data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

            db.collection("user")
                .whereEqualTo("userEmail", it)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.documents.first()
                        userName = document.getString("userName") ?: ""
                        userPhone = document.getString("userPhone") ?: ""

                        // If there's a profile image in Base64, convert and set it
                        val base64String = document.getString("profileImage")
                        if (!base64String.isNullOrEmpty()) {
                            profileImageBitmap = base64ToBitmap(base64String)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

            IconButton(
                onClick = {
                    loginViewModel.logout()
                    navController.navigate("loginSelection")
                    Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    tint = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
        ) {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = defaultPainter,
                    contentDescription = "Default Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "My Profile",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF983))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = "User",
                    modifier = Modifier.size(60.dp),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = userName.ifEmpty { "Not Available" },
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = "Email",
                    modifier = Modifier.size(60.dp),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = email ?: "Not Available",
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "Phone",
                    modifier = Modifier.size(60.dp),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = userPhone.ifEmpty { "Not Available" },
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(32.dp))
                Image(
                    painter = painterResource(id = R.drawable.car),
                    contentDescription = "Car",
                    modifier = Modifier.size(60.dp),
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    text = vehicleNumber.ifEmpty { "Not Available" },
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = { navController.navigate("editProfile") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF87CEEB),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(150.dp)
                    ) {
                        Text(text = "Edit Profile")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { navController.navigate("rfidDetail") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6CE95A),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(180.dp)
                    ) {
                        Text(text = "View RFID Detail")
                    }
                }
            }
        }
    }
}

fun base64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}