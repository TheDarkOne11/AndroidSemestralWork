package budikpet.cvut.cz.semestralwork.feeds;

import android.app.FragmentManager;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;

/**
 * Created by Petr on 13.04.18.
 * Contains all operations with RSS feeds.
 */
public class FeedHandler implements LoaderFragment.TaskCallbacks {
    LoaderFragment loaderFragment;

    protected void init(FragmentManager fm) {
        // Create new task fragment
        String tag = "loaderFragment";
        loaderFragment = (LoaderFragment) fm.findFragmentByTag(tag);

        if (loaderFragment == null) {
            loaderFragment = new LoaderFragment();
            fm.beginTransaction().add(loaderFragment, tag).commit();
        }
    }

    public void update(String url) {
//        loaderFragment.execute("http://servis.idnes.cz/rss.aspx?c=zpravodaj");
        loaderFragment.execute(url);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onCancelled() {

    }

    /**
     * Handles parsing of feed information and saving entries to database.
     * @param feed is current feed to be parsed.
     */
    @Override
    public void onPostExecute(SyndFeed feed) {
        String tmp  = "FEED";

        for(Object curr : feed.getEntries()) {
            SyndEntry entry = (SyndEntry) curr;
            Log.i(tmp, entry.getTitle());
            Log.i(tmp, entry.getLink());
            break;
        }

    }
}