package velord.bnrg.photogallery.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


data class PhotoDataSourceFactory(
    private val dataSource: PhotoDataSource = PhotoDataSource()
) : DataSource.Factory<Int, Photo>() {

    val photoDataSource = MutableLiveData<PhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        photoDataSource.postValue(dataSource)
        return dataSource
    }
}