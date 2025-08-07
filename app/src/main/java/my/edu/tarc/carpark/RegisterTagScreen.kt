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
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterTagScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val userId = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val vehicleNumber = remember { mutableStateOf("") }
    val vehicleModel = remember { mutableStateOf("") }

    val roles = listOf("Staff", "Student", "Visitor")
    val expanded = remember { mutableStateOf(false) }
    val selectedRole = remember { mutableStateOf("Select a role") }

    val userIdError = remember { mutableStateOf("") }
    val nameError = remember { mutableStateOf("") }
    val roleError = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf("") }
    val phoneError = remember { mutableStateOf("") }
    val vehicleNumberError = remember { mutableStateOf("") }
    val vehicleModelError = remember { mutableStateOf("") }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val generatedRfidId = remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        userIdError.value = ""
        nameError.value = ""
        roleError.value = ""
        emailError.value = ""
        phoneError.value = ""
        vehicleNumberError.value = ""
        vehicleModelError.value = ""

        var isValid = true

        if (userId.value.isEmpty() || userId.value.length < 5 || !userId.value.matches("^[a-zA-Z0-9]+$".toRegex())) {
            userIdError.value =
                "User ID must be at least 5 characters and can only contain letters and numbers."
            isValid = false
        }

        if (name.value.isEmpty() || name.value.length < 3 || !name.value.matches("^[a-zA-Z0-9_]+$".toRegex())) {
            nameError.value =
                "Username must be at least 3 characters and can only contain letters, numbers, and underscores."
            isValid = false
        }

        if (selectedRole.value.isEmpty() || selectedRole.value == "Select a role") {
            roleError.value = "Please select a valid user role."
            isValid = false
        }

        if (email.value.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value)
                .matches()
        ) {
            emailError.value = "A valid email address is required."
            isValid = false
        }

        if (phone.value.isEmpty() || !phone.value.matches("^\\+?[0-9]{10,15}$".toRegex())) {
            phoneError.value = "A valid phone number (10 to 15 digits) is required."
            isValid = false
        }

        if (vehicleNumber.value.isEmpty() || vehicleNumber.value.length < 5 || !vehicleNumber.value.matches(
                "^[A-Z0-9-]+$".toRegex()
            )
        ) {
            vehicleNumberError.value =
                "Vehicle number must be at least 5 characters long and contain only uppercase letters, numbers, and hyphens."
            isValid = false
        }


        if (vehicleModel.value.isEmpty() || vehicleModel.value.length < 2 || !vehicleModel.value.matches(
                "^[a-zA-Z0-9 ]+$".toRegex()
            )
        ) {
            vehicleModelError.value =
                "Vehicle model must be at least 2 characters long and contain only letters and numbers."
            isValid = false
        }

        return isValid
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFF983), shape = RoundedCornerShape(30.dp))
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
                    text = "\t\t\t\t\t\t WELCOME TO \nTARUMT RFID CAR PARK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = userId.value,
                    onValueChange = { userId.value = it },
                    label = { Text("User ID") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.user_icon),
                            contentDescription = "User ID Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (nameError.value.isNotEmpty()) {
                    Text(nameError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.user_icon),
                            contentDescription = "Username Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (nameError.value.isNotEmpty()) {
                    Text(nameError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedRole.value,
                        onValueChange = { },
                        label = { Text("User Role") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.user_icon),
                                contentDescription = "User Role Icon",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.dropdown),
                                contentDescription = "Dropdown Icon",
                                modifier = Modifier
                                    .size(24.dp)
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
                        roles.forEach { roleOption ->
                            DropdownMenuItem(
                                text = { Text(roleOption) },
                                onClick = {
                                    selectedRole.value = roleOption
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
                if (roleError.value.isNotEmpty()) {
                    Text(roleError.value, color = Color.Red, fontSize = 12.sp)
                }

                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.email_icon),
                            contentDescription = "Email Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (emailError.value.isNotEmpty()) {
                    Text(emailError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = phone.value,
                    onValueChange = { phone.value = it },
                    label = { Text("Phone Number") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.phone_icon),
                            contentDescription = "Phone Number Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (phoneError.value.isNotEmpty()) {
                    Text(phoneError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = vehicleNumber.value,
                    onValueChange = { vehicleNumber.value = it },
                    label = { Text("Vehicle Number") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.vehicle_icon),
                            contentDescription = "Vehicle Number Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (vehicleNumberError.value.isNotEmpty()) {
                    Text(vehicleNumberError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = vehicleModel.value,
                    onValueChange = { vehicleModel.value = it },
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
                if (vehicleModelError.value.isNotEmpty()) {
                    Text(vehicleModelError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                fun generateRfidId(onRfidIdGenerated: (String) -> Unit) {

                    db.collection("metadata").document("lastRfidId")
                        .get()
                        .addOnSuccessListener { document ->
                            val lastRfidId = document.getString("lastRfidId") ?: "TAG1000"
                            val lastNumber = lastRfidId.removePrefix("TAG").toInt()
                            val newNumber = lastNumber + 1
                            val newRfidId = "TAG$newNumber"

                            db.collection("metadata").document("lastRfidId")
                                .set(mapOf("lastRfidId" to newRfidId))
                                .addOnSuccessListener {
                                    onRfidIdGenerated(newRfidId)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Failed to generate TAG ID: ${it.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Failed to retrieve last TAG ID: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                Button(
                    onClick = {
                        if (validateFields()) {
                            loading = true
                            generateRfidId { newRfidId ->
                                generatedRfidId.value = newRfidId

                                val rfid = hashMapOf(
                                    "rfidTagId" to newRfidId,
                                    "userId" to userId.value,
                                    "userName" to name.value,
                                    "userRole" to selectedRole.value,
                                    "userEmail" to email.value,
                                    "userPhone" to phone.value,
                                    "vehicleNumber" to vehicleNumber.value,
                                    "vehicleModel" to vehicleModel.value
                                )

                                db.collection("rfidTag")
                                    .document(newRfidId)
                                    .set(rfid)
                                    .addOnSuccessListener {
                                        loading = false
                                        showDialog = true
                                    }
                                    .addOnFailureListener {
                                        loading = false
                                        Toast.makeText(
                                            context,
                                            "Registration Failed: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Register", color = Color.Black, fontSize = 18.sp)
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = "Registration Successful!") },
                        text = {
                            Text(
                                text = "Your Tag ID is ${generatedRfidId.value}. " +
                                        "Please collect your Rfid Card at Block B security room A108."
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("rfidDetail")
                                }
                            ) {
                                Text("View RFID Details")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    navController.navigate("home")
                                }
                            ) {
                                Text("Go to Home")
                            }
                        }
                    )
                }
                if (loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}