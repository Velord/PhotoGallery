package velord.bnrg.photogallery.view.fragment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import velord.bnrg.photogallery.model.worker.PollWorker


//This class will be a generic fragment that hides foreground notifications
abstract class VisibleFragment : Fragment() {

    private val onShowNotification = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            // If we receive this, we're visible, so cancel the notification
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(PollWorker.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(
            onShowNotification,
            filter,
            PollWorker.PERM_PRIVATE,
            null
        )
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }
}