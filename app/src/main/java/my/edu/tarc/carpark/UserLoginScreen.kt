package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import android.widget.Toast
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect

@Composable
fun UserLoginScreen(navController: NavController, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var failedAttempts by remember { mutableStateOf(0) }
    var lockoutTime by remember { mutableStateOf(0L) }

    val isLockedOut = remember(lockoutTime) {
        lockoutTime > System.currentTimeMillis()
    }


    fun validateFields(): Boolean {
        emailError.value = ""
        passwordError.value = ""

        var isValid = true

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) {
            emailError.value = "A valid email address is required."
            isValid = false
        }

        val passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*()-_=+<>?]).{8,}"
        if (password.isEmpty() || !password.matches(passwordPattern.toRegex())) {
            passwordError.value =
                "Password must be at least 8 characters long, contain uppercase and lowercase letters, numbers, and special characters."
            isValid = false
        }

        return isValid
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage.isNotEmpty()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(lockoutTime) {
        if (isLockedOut) {
            val remainingTime = lockoutTime - System.currentTimeMillis()
            if (remainingTime > 0) {
                kotlinx.coroutines.delay(remainingTime)
            }
            failedAttempts = 0
            lockoutTime = 0L
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
                .verticalScroll(rememberScrollState())
                .width(300.dp),
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
                }

                Image(
                    painter = painterResource(id = R.drawable.tarumt_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "User Login",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
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
                    value = password,
                    onValueChange = { password = it },
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

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = Color.Blue,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                navController.navigate("reset")
                            }
                            .padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        if (isLockedOut) {
                            return@Button
                        }

                        if (validateFields()) {
                            scope.launch {
                                loading = true
                                val userEmail = handleUserLogin(email, password) { message ->
                                    errorMessage = message
                                }
                                loading = false

                                if (userEmail != null) {
                                    loginViewModel.login(userEmail)

                                    successMessage = "Login successful!"
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    failedAttempts = 0
                                } else {
                                    failedAttempts++
                                    if (failedAttempts >= 5) {
                                        lockoutTime = System.currentTimeMillis() + 5 * 60 * 1000
                                        errorMessage = "Too many failed attempts. Please wait 5 minutes before trying again."
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp),
                    enabled = !isLockedOut
                ) {
                    Text(
                        text = if (isLockedOut) "Locked" else "Login",
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

suspend fun handleUserLogin(email: String, password: String, updateErrorMessage: (String) -> Unit): String? {
    return try {
        val auth = FirebaseAuth.getInstance()

        val result = auth.signInWithEmailAndPassword(email, password).await()

        result.user?.email
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        updateErrorMessage("Invalid credentials. Please check your email or password.")
        null
    } catch (e: FirebaseAuthInvalidUserException) {
        updateErrorMessage("User not found. Please check your email.")
        null
    } catch (e: Exception) {
        updateErrorMessage("An error occurred. Please try again later.")
        null
    }
}
