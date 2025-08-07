package my.edu.tarc.carpark

import android.content.Context
import android.util.Log
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext


@Composable
fun AddUserScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val nameError = remember { mutableStateOf("") }
    val roleError = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf("") }
    val phoneError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }

    val isPasswordVisible = remember { mutableStateOf(false) }

    val roles = listOf("Select a role", "Student", "Staff")
    val selectedRole = remember { mutableStateOf(roles[0]) }
    val expanded = remember { mutableStateOf(false) }

    fun validateFields(): Boolean {
        nameError.value = ""
        roleError.value = ""
        emailError.value = ""
        phoneError.value = ""
        passwordError.value = ""

        var isValid = true

        if (name.value.isEmpty() || name.value.length < 3 || !name.value.matches("^[a-zA-Z0-9_]+$".toRegex())) {
            nameError.value =
                "Username must be at least 3 characters and can only contain letters, numbers, and underscores."
            isValid = false
        }

        if (selectedRole.value == "Select a role") {
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

        return isValid
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
                    text = "ADD NEW USER",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Username",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
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
                            .border(4.dp, Color.Black),
                        isError = nameError.value.isNotEmpty()
                    )
                    if (nameError.value.isNotEmpty()) {
                        Text(text = nameError.value, color = Color.Red, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "User Role",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    TextField(
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
                                    .clickable {
                                        expanded.value = !expanded.value
                                    },
                                tint = Color.Black
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(4.dp, Color.Black),
                        readOnly = true
                    )

                    if (roleError.value.isNotEmpty()) {
                        Text(roleError.value, color = Color.Red, fontSize = 12.sp)
                    }

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

                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Email",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
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
                            .border(4.dp, Color.Black),
                        isError = emailError.value.isNotEmpty()
                    )
                    if (emailError.value.isNotEmpty()) {
                        Text(text = emailError.value, color = Color.Red, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Phone Number",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
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
                            .border(4.dp, Color.Black),
                        isError = phoneError.value.isNotEmpty()
                    )
                    if (phoneError.value.isNotEmpty()) {
                        Text(text = phoneError.value, color = Color.Red, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(4.dp, Color.Black),
                        visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible.value = !isPasswordVisible.value
                            }) {
                                Icon(
                                    painter = painterResource(id = if (isPasswordVisible.value) R.drawable.not_visible else R.drawable.visible),
                                    contentDescription = "Visibility Icon",
                                    tint = Color.Black
                                )
                            }
                        },
                        isError = passwordError.value.isNotEmpty()
                    )
                    if (passwordError.value.isNotEmpty()) {
                        Text(text = passwordError.value, color = Color.Red, fontSize = 12.sp)
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                fun generateUserId(context: Context, onUserIdGenerated: (String) -> Unit) {
                    val db = FirebaseFirestore.getInstance()

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
                            generateUserId(context) { newUserId ->
                                val user = hashMapOf(
                                    "userId" to newUserId,
                                    "userName" to name.value,
                                    "userRole" to selectedRole.value,
                                    "userEmail" to email.value,
                                    "userPhone" to phone.value,
                                    "userPassword" to password.value
                                )
                                val emails = email.value
                                val passwords = password.value

                                auth.createUserWithEmailAndPassword(emails, passwords)
                                db.collection("user")

                                    .document(newUserId)
                                    .set(user)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(
                                            "AddUserScreen",
                                            "User added with ID: ${documentReference}"
                                        )

                                        navController.popBackStack()
                                        navController.navigate("adminDashboard")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("AddUserScreen", "Error adding user", e)
                                    }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                ) {
                    Text(text = "Add User", color = Color.White, fontSize = 20.sp)
                }
            }
        }
    }
}




