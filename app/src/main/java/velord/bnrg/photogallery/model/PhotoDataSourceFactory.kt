package velord.bnrg.photogallery.model

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource


class PhotoDataSourceFactory(private val f: () -> List<Photo>) : DataSource.Factory<Int, Photo>() {

    val photoDataSource = MutableLiveData<PhotoDataSource>()

    override fun create(): DataSource<Int, Photo> {
        val dataSource = PhotoDataSource(f)
        photoDataSource.postValue(dataSource)
        return dataSource
    }
}