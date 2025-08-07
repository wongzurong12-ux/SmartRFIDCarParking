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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

@Composable
fun RegisterAccountScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val nameError = remember { mutableStateOf("") }
    val roleError = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf("") }
    val phoneError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }
    val confirmPasswordError = remember { mutableStateOf("") }

    val isPasswordVisible = remember { mutableStateOf(false) }
    val isConfirmPasswordVisible = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val generatedUserId = remember { mutableStateOf("") }

    val roles = listOf("Staff", "Student", "Visitor")
    val expanded = remember { mutableStateOf(false) }
    val selectedRole = remember { mutableStateOf("Select a role") }

    val auth = FirebaseAuth.getInstance()

    var loading by remember { mutableStateOf(false) }


    fun validateFields(): Boolean {
        nameError.value = ""
        roleError.value = ""
        emailError.value = ""
        phoneError.value = ""
        passwordError.value = ""
        confirmPasswordError.value = ""

        var isValid = true

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

        val passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()-_=+<>?]).{8,}"
        if (password.value.isEmpty() || !password.value.matches(passwordPattern.toRegex())) {
            passwordError.value =
                "Password must be at least 8 characters long, contain uppercase and lowercase letters, numbers, and special characters."
            isValid = false
        }

        if (confirmPassword.value != password.value) {
            confirmPasswordError.value = "Passwords do not match."
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
                    text = "\t\t\t\t\t\t WELCOME TO \n" +
                            "TARUMT RFID CAR PARK",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
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

                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password_icon),
                            contentDescription = "Password Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isPasswordVisible.value = !isPasswordVisible.value
                        }) {
                            Icon(
                                painter = painterResource(id = if (isPasswordVisible.value) R.drawable.visible else R.drawable.not_visible),
                                contentDescription = "Toggle Password Visibility",
                                tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (passwordError.value.isNotEmpty()) {
                    Text(passwordError.value, color = Color.Red, fontSize = 12.sp)
                }

                TextField(
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.password_icon),
                            contentDescription = "Confirm Password Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    },
                    visualTransformation = if (isConfirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {
                            isConfirmPasswordVisible.value = !isConfirmPasswordVisible.value
                        }) {
                            Icon(
                                painter = painterResource(id = if (isConfirmPasswordVisible.value) R.drawable.visible else R.drawable.not_visible),
                                contentDescription = "Toggle Confirm Password Visibility",
                                tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(4.dp, Color.Black)
                )
                if (confirmPasswordError.value.isNotEmpty()) {
                    Text(confirmPasswordError.value, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                fun generateUserId(onUserIdGenerated: (String) -> Unit) {
                    db.collection("metadata").document("lastUserId").get()
                        .addOnSuccessListener { document ->
                            val lastUserId = document.getString("lastUserId") ?: "U1000"
                            val numericId = lastUserId.substring(1).toInt() + 1
                            val newUserId = "U${numericId.toString().padStart(4, '0')}"

                            db.collection("metadata").document("lastUserId")
                                .set(mapOf("lastUserId" to newUserId))
                                .addOnSuccessListener {
                                    onUserIdGenerated(newUserId)
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Failed to generate user ID: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                Button(
                    onClick = {
                        if (validateFields()) {
                            loading = true
                            generateUserId { newUserId ->
                                generatedUserId.value = newUserId

                                val emails = email.value
                                val rawPassword = password.value
                                val hashedPassword = hashPassword(rawPassword)

                                auth.createUserWithEmailAndPassword(
                                    emails,
                                    rawPassword
                                )
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = hashMapOf(
                                                "userId" to newUserId,
                                                "userName" to name.value,
                                                "userRole" to selectedRole.value,
                                                "userEmail" to emails,
                                                "userPhone" to phone.value,
                                                "userPassword" to hashedPassword // Store the hashed password
                                            )


                                            db.collection("user")
                                                .document(newUserId)
                                                .set(user)
                                                .addOnSuccessListener {
                                                    loading = false
                                                    Toast.makeText(
                                                        context,
                                                        "Registration Successful! Your User ID is $newUserId",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.navigate("userLogin") {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    loading = false
                                                    Toast.makeText(
                                                        context,
                                                        "Registration Failed: ${it.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            loading = false
                                            Toast.makeText(
                                                context,
                                                "Registration Failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
                if (loading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

fun hashPassword(password: String): String {
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}