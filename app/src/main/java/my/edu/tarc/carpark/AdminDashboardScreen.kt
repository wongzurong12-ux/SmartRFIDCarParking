package my.edu.tarc.carpark

import android.widget.Toast
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AdminDashboardScreen(navController: NavHostController, loginViewModel: LoginViewModel) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = {
                        loginViewModel.adminLogout()
                        navController.navigate("loginSelection")
                        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Logout",
                        tint = Color.Black
                    )
                }
            }}

        Icon(
            painter = painterResource(id = R.drawable.admin_icon),
            contentDescription = "Admin Icon",
            modifier = Modifier.size(250.dp),
            tint = Color.Black
        )
        Text(
            text = "WELCOME TO ADMIN PORTAL",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))


        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("adminUser") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF983),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_user),
                    contentDescription = "User Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "USER",
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("adminRfid") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF983),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_rfid),
                    contentDescription = "RFID Tag Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "RFID TAG",
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("adminParking") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF983),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_parking),
                    contentDescription = "Parking Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "PARKING",
                    fontSize = 20.sp
                )
            }

            Button(
                onClick = { navController.navigate("adminReport") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF983),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_report),
                    contentDescription = "Report Icon",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "REPORT",
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

    }
}