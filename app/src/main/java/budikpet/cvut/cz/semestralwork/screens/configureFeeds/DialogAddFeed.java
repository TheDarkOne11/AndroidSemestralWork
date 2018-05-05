package budikpet.cvut.cz.semestralwork.screens.configureFeeds;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import budikpet.cvut.cz.semestralwork.R;
import budikpet.cvut.cz.semestralwork.data.sync.SyncService;

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
						// Start SyncService, add url to update new feed
						Intent intent = new Intent(getContext(), SyncService.class);
						intent.putExtra(R.id.keyFeedId + "", urlEditText.getText().toString());
						getActivity().startService(intent);
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
