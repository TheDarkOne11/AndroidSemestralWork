package budikpet.cvut.cz.semestralwork.data;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.URL;

/**
 * Retain fragment used for downloading feeds.
 */
public class LoaderFragment extends Fragment {
    private LoaderAsyncTask task;
    private TaskCallbacks callbacks;

    public interface TaskCallbacks {
        void onPreExecute();

        void onPostExecute(SyndFeed feed);
    }

    /**
     * Starts getting information about the feed.
     * @param url
     */
    public void execute(String url) {
        task = new LoaderAsyncTask();
        task.execute(url);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callbacks = (TaskCallbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        callbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    private class LoaderAsyncTask extends AsyncTask<String, Integer, SyndFeed> {

        @Override
        protected SyndFeed doInBackground(String... strings) {
            SyndFeed result = null;
            try {
                SyndFeedInput input = new SyndFeedInput();
                result = input.build(new XmlReader(
                        new URL(strings[0])));
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }

            // Send result to postExecute()
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            callbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(SyndFeed syndFeed) {
            super.onPostExecute(syndFeed);
            callbacks.onPostExecute(syndFeed);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
