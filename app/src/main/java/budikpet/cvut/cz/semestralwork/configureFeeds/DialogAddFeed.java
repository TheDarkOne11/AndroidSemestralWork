package budikpet.cvut.cz.semestralwork.configureFeeds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.FeedReaderContentProvider;
import budikpet.cvut.cz.semestralwork.data.feeds.FeedTable;

public class DialogAddFeed extends AppCompatDialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_feed, null);

		final EditText urlEditText = view.findViewById(R.id.addFeedUrl);

		builder.setView(view)
				.setPositiveButton(R.string.dialogAddFeed, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Use feed loading service to add new feed
						ContentValues cv = new ContentValues();
						cv.put(FeedTable.URL, urlEditText.getText().toString());
						cv.put(FeedTable.HEADING, "DUMMY_HEADING");
						getActivity().getContentResolver().insert(FeedReaderContentProvider.FEED_URI, cv);
					}
				})
				.setNegativeButton(R.string.dialogButtonCancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});


		return builder.create();
	}
}
