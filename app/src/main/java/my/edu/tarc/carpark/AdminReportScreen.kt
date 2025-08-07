package my.edu.tarc.carpark

import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Log
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import java.io.OutputStream
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun AdminReportScreen() {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val usersList = remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(true) {
        try {
            val usersSnapshot = firestore.collection("user").get().await()
            val users = usersSnapshot.documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }
            usersList.value = users
        } catch (e: Exception) {
            Log.e("AdminReportScreen", "Error fetching user data", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(onClick = {
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.back_icon),
                    contentDescription = "Back",
                    modifier = Modifier.size(35.dp),
                    tint = Color.Black
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.admin_icon),
                contentDescription = "Admin Icon",
                modifier = Modifier.size(250.dp),
                tint = Color.Black
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(100.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.admin_report),
                contentDescription = "Report Icon",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(32.dp))
            Text(
                "MONTHLY USER REPORT",
                fontSize = 30.sp,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFDA00), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("User Name", "User Email", "Phone", "Role").forEach { header ->
                        Text(
                            header,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                usersList.value.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            user.userName,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            user.userEmail,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            user.userPhone,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        Text(
                            user.userRole,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }

                Button(
                    onClick = {
                        generatePDFReport(usersList.value, context)
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Generate Report")
                }
            }
        }
    }
}



fun generatePDFReport(usersList: List<User>, context: Context) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()
    paint.textSize = 16f

    var yPos = 40f
    canvas.drawText("User Report", 250f, yPos, paint)
    yPos += 30f

    canvas.drawText("User Name", 40f, yPos, paint)
    canvas.drawText("User Email", 150f, yPos, paint)
    canvas.drawText("Phone", 300f, yPos, paint)
    canvas.drawText("Role", 450f, yPos, paint)
    yPos += 30f

    paint.textSize = 10f

    for (user in usersList) {
        canvas.drawText(user.userName, 40f, yPos, paint) // User Name
        canvas.drawText(user.userEmail, 100f, yPos, paint) // User Email
        canvas.drawText(user.userPhone, 300f, yPos, paint) // User Phone
        canvas.drawText(user.userRole, 450f, yPos, paint) // User Role

        yPos += 40f
    }

    pdfDocument.finishPage(page)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentResolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "UserReport.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // Scoped storage
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        if (uri != null) {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()

                Toast.makeText(context, "Report saved to Downloads", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "UserReport.pdf")
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            Toast.makeText(context, "Report saved to Downloads", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("PDF", "Error saving PDF: ${e.message}")
            Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
        }
    }
}