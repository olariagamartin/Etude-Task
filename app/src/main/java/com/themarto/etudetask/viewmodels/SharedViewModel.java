package com.themarto.etudetask.viewmodels;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    /* when a configuration change occurs, if the "timeline" screen is
       set as home page, the activity restarts in the "timeline" page, no matter
       if the configuration change occurs when the user is in another page
       ¡¡That's a problem!!
       So, I want to set the start page only when the app is launched and not
       when a configuration change occurs, so this is what this variable is for.
       I check if the start page was already set before; this will mean that the
       activity is recreated because of configuration change, therefore, the start page
       is not set in this case.*/
    private boolean startPageLoaded = false;

    public boolean isStartPageLoaded() {
        return startPageLoaded;
    }

    public void setStartPageLoaded(boolean startPageLoaded) {
        this.startPageLoaded = startPageLoaded;
    }
}
