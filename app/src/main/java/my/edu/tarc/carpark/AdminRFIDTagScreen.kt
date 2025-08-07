package my.edu.tarc.carpark

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AdminRFIDTagScreen(navController: NavController) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

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
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = {
                        backDispatcher?.onBackPressed()
                    },
                    modifier = Modifier.offset(y = (-100).dp)
                ) {
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
                modifier = Modifier
                    .height(100.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_rfid),
                    contentDescription = "Rfid Tag Icon",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    "RFID TAG",
                    fontSize = 30.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.create),
                            contentDescription = "Create Icon",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    navController.navigate("addRfid")
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "CREATE",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search Icon",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    navController.navigate("searchRfid")
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "SEARCH",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.update),
                            contentDescription = "Update Icon",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    navController.navigate("updateRfid")
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "UPDATE",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .height(150.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    navController.navigate("deleteRfid")
                                }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "DELETE",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}