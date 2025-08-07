package my.edu.tarc.carpark

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

@Composable
fun EditProfileScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }
    val email by loginViewModel.email.collectAsState()
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun uriToBase64(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveBase64ImageToFirestore(base64String: String, callback: (Boolean) -> Unit) {
        val imageMap = mapOf("profileImage" to base64String)
        db.collection("user")
            .whereEqualTo("userEmail", email)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val documentId = result.documents.first().id
                    db.collection("user").document(documentId).update(imageMap)
                        .addOnSuccessListener { callback(true) }
                        .addOnFailureListener { callback(false) }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener { callback(false) }
    }

    fun uploadImage(bitmap: Bitmap?, callback: (Boolean) -> Unit) {
        if (bitmap != null) {
            val base64String = bitmapToBase64(bitmap)
            saveBase64ImageToFirestore(base64String, callback)
        } else {
            callback(false)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            profileImageBitmap = bitmap

            uploadImage(bitmap) { success ->
                if (success) {
                    Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        profileImageBitmap = bitmap
        uploadImage(bitmap) { success ->
            if (success) {
                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
        }
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
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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

            Spacer(modifier = Modifier.weight(2f))
            Text(
                text = "Edit Profile",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(8f),
            )

        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
        ) {
            when {
                profileImageBitmap != null -> Image(
                    bitmap = profileImageBitmap!!.asImageBitmap(),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )

                profileImageUri != null -> Image(
                    painter = rememberAsyncImagePainter(model = profileImageUri),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )

                else -> Image(
                    painter = painterResource(id = R.drawable.tarumt_logo),
                    contentDescription = "Default Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Upload Photo")
            }
            Button(onClick = { cameraLauncher.launch() }) {
                Text("Take Photo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text("Name")
            TextField(
                value = userName,
                onValueChange = { userName = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Email (You cannot edit your email.)")
            TextField(
                value = email ?: "",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Phone Number")
            TextField(
                value = userPhone,
                onValueChange = { userPhone = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Vehicle Number")
            TextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        fun updateProfile() {
            val rfidTagMap = mapOf(
                "userName" to userName,
                "userPhone" to userPhone,
                "vehicleNumber" to vehicleNumber
            )
            val userMap = mapOf(
                "userName" to userName,
                "userPhone" to userPhone
            )

            db.collection("rfidTag")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val rfidDocumentId = result.documents.first().id
                        db.collection("rfidTag").document(rfidDocumentId).update(rfidTagMap)
                            .addOnSuccessListener {
                                db.collection("user")
                                    .whereEqualTo("userEmail", email)
                                    .get()
                                    .addOnSuccessListener { userResult ->
                                        if (!userResult.isEmpty) {
                                            val userDocumentId = userResult.documents.first().id
                                            db.collection("user").document(userDocumentId)
                                                .update(userMap)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Profile updated successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    navController.popBackStack()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Error updating user database",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "User profile not found in Firestore",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            context,
                                            "Error fetching user data: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Error updating RFID Tag database",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "RFID Tag not found in Firestore",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        context,
                        "Error fetching RFID Tag data: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        Button(
            onClick = {
                if (profileImageBitmap != null) {
                    val base64String = bitmapToBase64(profileImageBitmap!!)
                    saveBase64ImageToFirestore(base64String) { success ->
                        if (success) {
                            updateProfile()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to save profile picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (profileImageUri != null) {
                    val base64String = uriToBase64(profileImageUri!!)
                    saveBase64ImageToFirestore(base64String) { success ->
                        if (success) {
                            updateProfile()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to save profile picture",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    updateProfile()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF6CE95A)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .width(180.dp)
                .height(50.dp)
        ) {
            Text(text = "Save Changes", color = Color.Black, fontSize = 18.sp)
        }
    }
}