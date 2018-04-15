package budikpet.cvut.cz.semestralwork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DialogAddFeed extends AppCompatDialogFragment {
	private InteractionListener listener;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof InteractionListener) {
			listener = (InteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement InteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_feed, null);

		final EditText urlEditText = view.findViewById(R.id.addFeedUrl);

		builder.setView(view)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.saveFeed(urlEditText.getText().toString());
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});


		return builder.create();
	}

	public interface InteractionListener {
		void saveFeed(String url);
	}
}
