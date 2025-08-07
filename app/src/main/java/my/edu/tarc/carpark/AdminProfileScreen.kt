package my.edu.tarc.carpark

import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminProfileScreen() {
    val firestore = FirebaseFirestore.getInstance()

    val adminId = "A1001"

    val adminData = remember { mutableStateOf<Admin?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(adminId) {
        firestore.collection("admin")
            .whereEqualTo("adminId", adminId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val admin = querySnapshot.documents.first().toObject(Admin::class.java)
                    adminData.value = admin
                }
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting document: ", exception)
                isLoading.value = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
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
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "My Profile",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading.value) {
            Text(text = "Loading...")
        } else {
            adminData.value?.let { admin ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFDA00))
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
                            contentDescription = "Admin ID",
                            modifier = Modifier.size(60.dp),
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(
                            text = admin.adminId ?: "Admin ID",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(32.dp))
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Admin Name",
                            modifier = Modifier.size(60.dp),
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(
                            text = admin.adminName ?: "Admin Name",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            text = admin.adminEmail ?: "user@example.com",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                            text = admin.adminPhone ?: "0123456789",
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

data class Admin(
    val adminId: String? = null,
    val adminName: String? = null,
    val adminEmail: String? = null,
    val adminPhone: String? = null
)
