package velord.bnrg.photogallery.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

val replaceFragment: (FragmentManager, Fragment, Int) -> Unit =
    { fm, fragment, containerId ->
        fm.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

val initFragment: (FragmentManager, Fragment, Int) -> Unit =
    {fm,  fragment, containerId ->
        val currentFragment =
            fm.findFragmentById(containerId)

        if (currentFragment == null)
            addFragment(fm, fragment, containerId)
    }

private val addFragment: (FragmentManager, Fragment, Int) -> Unit =
    { fm,  fragment, containerId ->
        fm.beginTransaction()
            .add(containerId, fragment)
            .commit()
    }