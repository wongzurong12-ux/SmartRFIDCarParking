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
import androidx.compose.runtime.*
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
import com.google.firebase.firestore.FirebaseFirestore

import androidx.navigation.NavController

@Composable
fun UpdateUserScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val userName = remember { mutableStateOf("") }
    val userRole = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }
    val userPhone = remember { mutableStateOf("") }
    val userPassword = remember { mutableStateOf("") }

    val isPasswordVisible = remember { mutableStateOf(false) }
    val isEditingName = remember { mutableStateOf(false) }
    val isEditingRole = remember { mutableStateOf(false) }
    val isEditingEmail = remember { mutableStateOf(false) }
    val isEditingPhone = remember { mutableStateOf(false) }
    val isEditingPassword = remember { mutableStateOf(false) }

    val search = remember { mutableStateOf("") }

    fun fetchUserData(userId: String) {
        db.collection("user").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName.value = document.getString("userName") ?: ""
                    userRole.value = document.getString("userRole") ?: ""
                    userEmail.value = document.getString("userEmail") ?: ""
                    userPhone.value = document.getString("userPhone") ?: ""
                    userPassword.value = document.getString("userPassword") ?: ""
                } else {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserData(userId: String) {
        val updatedData: Map<String, Any> = hashMapOf(
            "userName" to userName.value,
            "userRole" to userRole.value,
            "userEmail" to userEmail.value,
            "userPhone" to userPhone.value,
            "userPassword" to userPassword.value
        )

        db.collection("user").document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
            }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFFFDA00), shape = RoundedCornerShape(30.dp))
                .border(4.dp, Color.Black, shape = RoundedCornerShape(30.dp))
                .padding(32.dp)
                .height(550.dp)
                .width(300.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                TextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    label = { Text("Search by User ID") },
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { fetchUserData(search.value) },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "Search", color = Color.Black, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Username", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    EditableTextField(value = userName, label = "Username", iconId = R.drawable.user_icon, isEditing = isEditingName)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "User Role", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    EditableTextField(value = userRole, label = "User Role", iconId = R.drawable.user_icon, isEditing = isEditingRole)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Email", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    EditableTextField(value = userEmail, label = "Email", iconId = R.drawable.email_icon, isEditing = isEditingEmail)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Phone Number", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    EditableTextField(value = userPhone, label = "Phone Number", iconId = R.drawable.phone_icon, isEditing = isEditingPhone)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(text = "Password", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    EditableTextField(value = userPassword, label = "Password", iconId = R.drawable.password_icon, isEditing = isEditingPassword, isPassword = true, isPasswordVisible = isPasswordVisible, isReadOnly = true)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { updateUserData(search.value) },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "UPDATE", color = Color.Black, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun EditableTextField(
    value: MutableState<String>,
    label: String,
    iconId: Int,
    isEditing: MutableState<Boolean>,
    isPassword: Boolean = false,
    isPasswordVisible: MutableState<Boolean>? = null,
    isReadOnly: Boolean = false
) {
    TextField(
        value = value.value,
        onValueChange = { value.value = it },
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "$label Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        },
        trailingIcon = {
            Image(
                painter = painterResource(id = if (isEditing.value) R.drawable.save_icon else R.drawable.edit_icon),
                contentDescription = if (isEditing.value) "Save" else "Edit",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { isEditing.value = !isEditing.value }
            )
        },
        visualTransformation = if (isPassword && !isPasswordVisible!!.value) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(4.dp, Color.Black),
        readOnly = isReadOnly || !isEditing.value
    )
}