package velord.bnrg.photogallery.receiver

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import velord.bnrg.photogallery.model.worker.PollWorker

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "Received broadcastL ${intent!!.action}")

        if (resultCode != Activity.RESULT_OK)
        // A foreground activity canceled the broadcast
            return

        val requestCode =  intent!!.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val notification: Notification =
            intent.getParcelableExtra(PollWorker.NOTIFICATION)

        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(requestCode, notification)
    }
}