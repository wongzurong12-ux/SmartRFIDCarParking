package my.edu.tarc.carpark

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

@Composable
fun DeleteUserScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val search = remember { mutableStateOf("") }
    val userDetail = remember { mutableStateOf<User?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()

    fun searchUserById(userId: String) {
        db.collection("user")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    userDetail.value = null
                } else {
                    val user = result.documents.first().toObject<User>()
                    userDetail.value = user
                }
            }
            .addOnFailureListener {
                userDetail.value = null
            }
    }

    fun deleteUser(userId: String) {

        db.collection("metadata").document("lastUserId")
            .get()
            .addOnSuccessListener { document ->
                val lastUserId = document.getString("lastUserId") ?: "U1000"
                val numericId = lastUserId.substring(1).toInt()
                val newLastUserId = "U${(numericId - 1).toString().padStart(4, '0')}"

                db.collection("metadata").document("lastUserId")
                    .set(mapOf("lastUserId" to newLastUserId))
                    .addOnSuccessListener {
                        db.collection("user").document(userId)
                            .delete()
                            .addOnSuccessListener {
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to delete user: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update last user ID: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch last user ID: ${it.message}", Toast.LENGTH_SHORT).show()
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
                    text = "DELETE USER",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

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
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { searchUserById(search.value) },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "SEARCH", color = Color.Black, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                userDetail.value?.let { user ->
                    Text(text = "User Details:", fontWeight = FontWeight.Bold)
                    Text("Name: ${user.userName}")
                    Text("Email: ${user.userEmail}")
                    Text("Role: ${user.userRole}")
                    Text("Phone: ${user.userPhone}")

                    Button(
                        onClick = { showDialog.value = true },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF0000)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(130.dp)
                            .height(50.dp)
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "DELETE", color = Color.Black, fontSize = 18.sp)
                    }
                } ?: run {
                    Text("No user found or invalid userId")
                }
            }
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Confirm Deletion") },
                text = { Text("Are you sure you want to delete this user?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            deleteUser(search.value)
                            showDialog.value = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog.value = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}