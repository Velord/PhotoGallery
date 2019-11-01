package velord.bnrg.photogallery.model.pagedListDataSource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import velord.bnrg.photogallery.model.photo.Photo


data class PhotoDataSourceFactory(
    private val dataSource: PhotoDataSource = PhotoDataSource()
) : DataSource.Factory<Int, Photo>() {

    val photoDataSource = MutableLiveData<PhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        photoDataSource.postValue(dataSource)
        return dataSource
    }
}