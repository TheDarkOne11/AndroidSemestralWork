package budikpet.cvut.cz.semestralwork.screens.configureFeeds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.Provider;
import budikpet.cvut.cz.semestralwork.data.articles.ArticleTable;

public class DialogRemoveFeed extends AppCompatDialogFragment {
	private long feedId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		feedId = getArguments().getLong(R.id.keyFeedId + "");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogRemoveView = inflater.inflate(R.layout.dialog_remove_feed, null);

		builder.setView(dialogRemoveView)
				.setPositiveButton(R.string.removeDialogDelete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Remove feed
						new RemoveFeed().execute(feedId);
					}
				})
				.setNegativeButton(R.string.dialogButtonCancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});


		return builder.create();
	}

	private void onPostExecute() {

	}

	/**
	 * Remove feed and its entries.
	 */
	private class RemoveFeed extends AsyncTask<Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... feedIds) {
			ContentResolver resolver = getActivity().getContentResolver();
			for (Long feedId : feedIds) {
				// Remove all feeds with given ids and their entries
				resolver.delete(ContentUris.withAppendedId(Provider.FEED_URI, feedId),
						null, null);

				// Remove its entries
				String selection = ArticleTable.FEED_ID + " == ?";
				String[] selectionArgs = {feedId + ""};
				resolver.delete(Provider.ARTICLE_URI, selection, selectionArgs);

			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(DialogRemoveFeed.this.getActivity(), R.string.DialogFeedDeleted, Toast.LENGTH_SHORT).show();
		}
	}
}
