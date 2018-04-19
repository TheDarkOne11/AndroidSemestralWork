package budikpet.cvut.cz.semestralwork.data;

import android.os.SystemClock;
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
import java.util.ArrayList;

/**
 * Retain fragment used for downloading feeds.
 */
public class FeedDataLoader extends Fragment {
    private LoaderAsyncTask task;
    private TaskCallbacks callbacks;
    private boolean running = false;

    public interface TaskCallbacks {
        void onPreExecute();

		/**
		 * Processes all downloaded feeds.
		 * @param feeds
		 */
		void onPostExecute(ArrayList<SyndFeed> feeds);
    }

    /**
     * Starts getting information about the feed.
     * @param url
     */
    public void execute(String... url) {
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

	public boolean isRunning() {
		return running;
	}

	private class LoaderAsyncTask extends AsyncTask<String, Integer, ArrayList<SyndFeed>> {

        @Override
        protected ArrayList<SyndFeed> doInBackground(String... urls) {
			ArrayList<SyndFeed> result = new ArrayList<>();
            try {
                SyndFeedInput input = new SyndFeedInput();

                for(String url : urls) {
					result.add(input.build(new XmlReader(new URL(url))));
				}

            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }

//            SystemClock.sleep(5000);

            // Send result to postExecute()
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            running = true;
            callbacks.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<SyndFeed> syndFeed) {
            super.onPostExecute(syndFeed);
            running = false;
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
