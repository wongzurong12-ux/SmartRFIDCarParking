package my.edu.tarc.carpark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParkApp(navController: NavHostController, loginViewModel: LoginViewModel) {

    val backgroundColor = Color(0xFFF5F5DC)
    val customBackgroundColor = Color.White

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val adminRoutes = listOf(
        "adminDashboard",
        "adminLogin",
        "adminProfile",
        "adminNotification",
        "adminUser",
        "adminRfid",
        "adminParking",
        "adminReport",
        "addUser",
        "searchUser",
        "updateUser",
        "deleteUser",
        "addRfid",
        "updateRfid",
        "deleteRfid",
        "addParking",
        "updateParking",
        "searchParking",
        "deleteParking"
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tarumt_logo),
                            contentDescription = "TARUMT Logo",
                            modifier = Modifier.size(130.dp)
                        )
                        Row {
                            if (currentRoute in adminRoutes) {
                                Image(
                                    painter = painterResource(id = R.drawable.notification_icon),
                                    contentDescription = "Admin Notification",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable { navController.navigate("adminNotification") }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.profile_icon),
                                    contentDescription = "Admin Profile",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable { navController.navigate("adminProfile") }
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.notification_icon),
                                    contentDescription = "Notification",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable { navController.navigate("notification") }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.profile_icon),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable { navController.navigate("profile") }
                                )
                            }
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("notification") {
                        NotificationScreen(navController = navController, loginViewModel = loginViewModel)
                    }

                    composable("loginSelection") {
                        LoginSelectionScreen(
                            onUserLoginClick = { navController.navigate("userLogin")},
                            onAdminLoginClick = { navController.navigate("adminLogin") }
                        )
                    }
                    composable("account") {
                        RegisterAccountScreen(navController)
                    }
                    composable("tag") {
                        RegisterTagScreen(navController = navController)
                    }
                    composable("parking") {
                        ParkingScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("map"){
                        MapScreen()
                    }
                    composable("contact") {
                        ContactScreen(navController)
                    }
                    composable("profile"){
                        ProfileScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("editProfile"){
                        EditProfileScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("userLogin"){
                        UserLoginScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("reset"){
                        ResetPasswordScreen()
                    }
                    composable("adminLogin"){
                        AdminLoginScreen(navController = navController)
                    }
                    composable("rfidDetail"){
                        RFIDDetailScreen(navController, loginViewModel = loginViewModel)
                    }
                    composable("adminDashboard"){
                        AdminDashboardScreen(navController = navController, loginViewModel = loginViewModel)
                    }
                    composable("adminProfile"){
                        AdminProfileScreen()
                    }
                    composable("adminNotification"){
                        AdminNotificationScreen()
                    }
                    composable("adminUser"){
                        AdminUserScreen(navController)
                    }
                    composable("adminRfid"){
                        AdminRFIDTagScreen(navController)
                    }
                    composable("adminParking"){
                        AdminParkingScreen(navController)
                    }
                    composable("adminReport"){
                        AdminReportScreen()
                    }
                    composable("addUser"){
                        AddUserScreen(navController = navController)
                    }
                    composable("updateUser"){
                        UpdateUserScreen(navController = navController)
                    }
                    composable("searchUser"){
                        SearchUserScreen()
                    }
                    composable("deleteUser"){
                        DeleteUserScreen(navController = navController)
                    }
                    composable("addRfid"){
                        AddRfidTagScreen(navController = navController)
                    }
                    composable("updateRfid"){
                        UpdateRfidTagScreen(navController = navController)
                    }
                    composable("searchRfid"){
                        SearchRfidTagScreen()
                    }
                    composable("deleteRfid"){
                        DeleteRfidTagScreen()
                    }
                    composable("addParking"){
                        AddParkingScreen(navController = navController)
                    }
                    composable("updateParking"){
                        UpdateParkingScreen(navController = navController)
                    }
                    composable("searchParking"){
                        SearchParkingScreen()
                    }
                    composable("deleteParking"){
                        DeleteParkingScreen()
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute !in adminRoutes) {
                BottomAppBar(
                    containerColor = customBackgroundColor
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            iconRes = R.drawable.home_icon,
                            onClick = { navController.navigate("home") },
                            size = 60.dp
                        )
                        IconButton(
                            iconRes = R.drawable.parking_icon,
                            onClick = { navController.navigate("parking") },
                            size = 60.dp
                        )
                        IconButton(
                            iconRes = R.drawable.blockk_location,
                            onClick = { navController.navigate("map") },
                            size = 60.dp
                        )
                        IconButton(
                            iconRes = R.drawable.contact_icon,
                            onClick = { navController.navigate("contact") },
                            size = 60.dp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    val email by loginViewModel.email.collectAsState()
    var username by remember { mutableStateOf<String?>(null) }

    var isRfidRegistered by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.location_icon),
                contentDescription = "Location Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TARUMT, KL",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Image(
            painter = painterResource(id = R.drawable.tarumt),
            contentDescription = "TARUMT Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(vertical = 16.dp)
                .clickable { navController.navigate("map") }
        )

        LaunchedEffect(email) {
            if (email != null && isLoggedIn) {
                db.collection("user")
                    .whereEqualTo("userEmail", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            username =
                                document.getString("userName")
                            break
                        }
                    }
                    .addOnFailureListener {
                        username = null
                    }
            } else {
                username = null
            }
        }

        Text(
            text = if (isLoggedIn && username != null) {
                "WELCOME TO TARUMT, $username"
            } else {
                "WELCOME TO TARUMT"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            LaunchedEffect(isLoggedIn, email) {
                if (isLoggedIn && !email.isNullOrEmpty()) {
                    Firebase.firestore.collection("rfidTag")
                        .whereEqualTo("userEmail", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            isRfidRegistered = !documents.isEmpty
                            isLoading = false
                        }
                        .addOnFailureListener {
                            isRfidRegistered = false
                            isLoading = false
                        }
                } else {
                    isLoading = false
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            } else {
                when {
                    !isLoggedIn -> {
                        Button(
                            onClick = { navController.navigate("account") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.new_account_icon),
                                    contentDescription = "Register New Account",
                                    modifier = Modifier.size(120.dp)
                                )
                                Text(
                                    text = "REGISTER NEW ACCOUNT",
                                    fontSize = 18.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    isLoggedIn && !isRfidRegistered -> {
                        Button(
                            onClick = { navController.navigate("tag") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.rfid_tag_icon),
                                    contentDescription = "Register New RFID Tag",
                                    modifier = Modifier.size(120.dp)
                                )
                                Text(
                                    text = "REGISTER NEW RFID TAG",
                                    fontSize = 18.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    isLoggedIn && isRfidRegistered -> {
                        Button(
                            onClick = { navController.navigate("rfidDetail") },
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.rfid),
                                    contentDescription = "View RFID Detail",
                                    modifier = Modifier.size(120.dp)
                                )
                                Text(
                                    text = "VIEW RFID DETAIL",
                                    fontSize = 18.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            "Please log in to continue.",
                            modifier = Modifier.fillMaxSize(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconButton(iconRes: Int, onClick: () -> Unit, size: Dp) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick)
    )
}
