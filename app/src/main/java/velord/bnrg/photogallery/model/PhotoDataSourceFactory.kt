package velord.bnrg.photogallery.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


class PhotoDataSourceFactory : DataSource.Factory<Int, Photo>() {

    val photoDataSource = MutableLiveData<PhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val dataSource = PhotoDataSource()
        photoDataSource.postValue(dataSource)
        return dataSource
    }
}