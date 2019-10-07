package velord.bnrg.photogallery.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

fun scope(): CoroutineScope =
    CoroutineScope(coroutineContext())


fun coroutineContext():  CoroutineContext =
    Job() + Dispatchers.Default