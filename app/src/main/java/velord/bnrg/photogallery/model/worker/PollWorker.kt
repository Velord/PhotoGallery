package velord.bnrg.photogallery.model.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.application.NOTIFICATION_CHANNEL_ID
import velord.bnrg.photogallery.application.sharedPreferences.QueryPreferences
import velord.bnrg.photogallery.model.photo.Photo
import velord.bnrg.photogallery.repository.FlickrRepository
import velord.bnrg.photogallery.repository.api.FlickrApi
import velord.bnrg.photogallery.view.activity.MainActivity

private const val TAG = "PollWorker"

class PollWorker(
    private val context: Context,
    workerParams: WorkerParameters
): Worker(context, workerParams) {

    val repository = FlickrRepository(FlickrApi.invoke())

    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val items: List<Photo> = fetchPhoto(query)

        if (items.isEmpty()) Result.success()

        val resultId = items.first().id
        if (resultId == lastResultId)
            Log.i(TAG, "Got an old result: $resultId")
        else {
            Log.i(TAG, "Got a new result: $resultId")
            gotANewResult(resultId)
        }

        return Result.success()
    }


    private fun fetchPhoto(query: String) =
        if (query.isEmpty()) {
            runBlocking {
                repository.fetchInterestingnessPhotos(1)
            }
        } else {
            runBlocking {
                repository.fetchSearchPhotos(query)
            }
        }

    private fun gotANewResult(resultId: String) {
        QueryPreferences.setLastResultId(context, resultId)

        val intent = MainActivity.newIntent(context)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, 0)

        val resources = context.resources
        val notification = buildNotification(context, resources, pendingIntent)

        showBackgroundNotification(0, notification)
    }

    private fun buildNotification(context: Context,
                                  resources: Resources,
                                  pendingIntent: PendingIntent) =
        NotificationCompat
            .Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker(resources.getString(R.string.new_pictures_title))
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(resources.getString(R.string.new_pictures_title))
            .setContentText(resources.getString(R.string.new_pictures_text))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

    private fun showBackgroundNotification(
        requestCode: Int,
        notification: Notification
    ) {
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(NOTIFICATION, notification)
        }

        context.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }


    companion object {
        const val ACTION_SHOW_NOTIFICATION =
            "velord.bnrg.photogallery.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "velord.bnrg.photogallery.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val NOTIFICATION = "NOTIFICATION"
    }
}