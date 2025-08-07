package my.edu.tarc.carpark

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MapScreen() {
    val context = LocalContext.current

    val latitude = 3.2167
    val longitude = 101.7252

    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(TARUMT Block K)")

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.google.android.apps.maps"))
    }

    context.startActivity(intent)
}
