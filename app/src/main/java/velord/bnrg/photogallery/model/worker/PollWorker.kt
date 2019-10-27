package velord.bnrg.photogallery.model.worker

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.runBlocking
import velord.bnrg.photogallery.R
import velord.bnrg.photogallery.model.NOTIFICATION_CHANNEL_ID
import velord.bnrg.photogallery.model.Photo
import velord.bnrg.photogallery.model.QueryPreferences
import velord.bnrg.photogallery.repository.FlickrRepository
import velord.bnrg.photogallery.repository.api.FlickrApi
import velord.bnrg.photogallery.view.MainActivity

private const val TAG = "PollWorker"

class PollWorker(
    val context: Context,
    workerParams: WorkerParameters
): Worker(context, workerParams) {

    val repository = FlickrRepository(FlickrApi.invoke())

    override fun doWork(): Result {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastresultId(context)
        val items: List<Photo> = if (query.isEmpty()) {
            runBlocking {
                repository.fetchInterestingnessPhotos(1)
            }
        } else {
            runBlocking {
                repository.fetchSearchPhotos(query)
            }
        }

        if (items.isEmpty()) Result.success()

        val resultId = items.first().id
        if (resultId == lastResultId)
            Log.i(TAG, "Got an old result: $resultId")
        else {
            Log.i(TAG, "Got a new result: $resultId")
            QueryPreferences.setLastResultId(context, resultId)

            val intent = MainActivity.newIntent(context)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0)

            val resources = context.resources
            val notification = NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat
                .from(context)
                .notify(0, notification)
        }

        return Result.success()
    }
}