package tabletop.velocic.com.worldforgerpgtools;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class DialogNewGeneratorName extends DialogFragment
{
    public static final String EXTRA_NEW_GENERATOR_NAME = "tabletop.velocic.com.worldforgerpgtools.DialogNewGeneratorName.newGeneratorName";
    private EditText newGeneratorName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_new_generator_name, null);

        newGeneratorName = view.findViewById(R.id.new_generator_name);

        return new AlertDialog.Builder(getActivity())
            .setView(view)
            .setTitle(R.string.title_new_generator_name)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    sendResult(Activity.RESULT_OK, newGeneratorName.getText().toString());
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    sendResult(Activity.RESULT_CANCELED, "");
                }
            })
            .setMessage(R.string.message_new_generator_name)
            .create();
    }

    private void sendResult(int resultCode, String newGeneratorName)
    {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NEW_GENERATOR_NAME, newGeneratorName);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
