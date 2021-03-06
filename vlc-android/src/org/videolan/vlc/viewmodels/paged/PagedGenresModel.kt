package org.videolan.vlc.viewmodels.paged

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import org.videolan.medialibrary.Medialibrary
import org.videolan.medialibrary.media.Genre
import org.videolan.vlc.util.EmptyMLCallbacks
import org.videolan.vlc.util.Settings

class PagedGenresModel(context: Context): MLPagedModel<Genre>(context), Medialibrary.GenresCb by EmptyMLCallbacks {

    init {
        sort = Settings.getInstance(context).getInt(sortKey, Medialibrary.SORT_ALPHA)
        desc = Settings.getInstance(context).getBoolean("${sortKey}_desc", false)
    }

    override fun onMedialibraryReady() {
        super.onMedialibraryReady()
        medialibrary.addGenreCb(this)
    }

    override fun onCleared() {
        super.onCleared()
        medialibrary.removeGenreCb(this)
    }

    override fun getAll() = medialibrary.getGenres(sort, desc)

    override fun getPage(loadSize: Int, startposition: Int) = if (filter == null) medialibrary.getPagedGenres(sort, desc, loadSize, startposition)
    else medialibrary.searchGenre(filter, sort, desc, loadSize, startposition)

    override fun getTotalCount() = if (filter == null) medialibrary.genresCount else medialibrary.getGenresCount(filter)

    override fun onGenresAdded() {
        refresh()
    }

    override fun onGenresDeleted() {
        refresh()
    }

    class Factory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PagedGenresModel(context.applicationContext) as T
        }
    }
}