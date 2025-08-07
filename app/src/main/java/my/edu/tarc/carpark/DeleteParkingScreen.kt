package my.edu.tarc.carpark

import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext

@Composable
fun DeleteParkingScreen() {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val search = remember { mutableStateOf("") }
    val spaceIdFound = remember { mutableStateOf("") }
    val spaceTypeFound = remember { mutableStateOf("") }
    val isDialogVisible = remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

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
                    text = "DELETE PARKING",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = search.value,
                    onValueChange = { search.value = it },
                    label = { Text("Search") },
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
                    onClick = {
                        searchParkingSpace(db, search.value, spaceIdFound, spaceTypeFound, context)
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(text = "SEARCH", color = Color.Black, fontSize = 18.sp)
                }

                if (spaceIdFound.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Found: ${spaceIdFound.value} - ${spaceTypeFound.value}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { isDialogVisible.value = true },
                        colors = ButtonDefaults.buttonColors(Color(0xFFFF0000)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(130.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "DELETE", color = Color.Black, fontSize = 18.sp)
                    }
                }
            }
        }

        if (isDialogVisible.value) {
            AlertDialog(
                onDismissRequest = { isDialogVisible.value = false },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this parking space?") },
                confirmButton = {
                    Button(
                        onClick = {
                            deleteParkingSpace(db, spaceIdFound.value, context, spaceIdFound, spaceTypeFound)
                            isDialogVisible.value = false
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { isDialogVisible.value = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}

fun searchParkingSpace(
    db: FirebaseFirestore,
    searchQuery: String,
    spaceIdFound: MutableState<String>,
    spaceTypeFound: MutableState<String>,
    context: android.content.Context
) {
    db.collection("parkingSpace")
        .whereEqualTo("spaceId", searchQuery)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                spaceIdFound.value = ""
                spaceTypeFound.value = ""
                Toast.makeText(context, "No parking space found", Toast.LENGTH_SHORT).show()
            } else {
                val document = querySnapshot.documents.first()
                spaceIdFound.value = document.getString("spaceId") ?: ""
                spaceTypeFound.value = document.getString("spaceType") ?: ""
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error searching parking space", Toast.LENGTH_SHORT).show()
        }
}

fun deleteParkingSpace(
    db: FirebaseFirestore,
    spaceId: String,
    context: android.content.Context,
    spaceIdFound: MutableState<String>,
    spaceTypeFound: MutableState<String>
) {
    if (spaceId.isNotEmpty()) {
        Log.d("DeleteParking", "Attempting to delete space with ID: $spaceId")

        db.collection("parkingSpace")
            .whereEqualTo("spaceId", spaceId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Toast.makeText(context, "Parking space not found", Toast.LENGTH_SHORT).show()
                } else {
                    val documentId = querySnapshot.documents.first().id

                    db.collection("parkingSpace").document(documentId).delete()
                        .addOnSuccessListener {
                            Log.d("DeleteParking", "Parking space deleted successfully.")
                            Toast.makeText(context, "Parking space deleted", Toast.LENGTH_SHORT).show()

                            spaceIdFound.value = ""
                            spaceTypeFound.value = ""
                        }
                        .addOnFailureListener { exception ->
                            Log.e("DeleteParking", "Error deleting parking space: ${exception.message}")
                            Toast.makeText(context, "Error deleting parking space", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error deleting parking space", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Invalid parking space ID", Toast.LENGTH_SHORT).show()
    }
}
