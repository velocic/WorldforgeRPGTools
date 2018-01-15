package tabletop.velocic.com.worldforgerpgtools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class NumGeneratorResultsFragment extends android.support.v4.app.DialogFragment
{
    private NumberPicker numberPicker;
    private String generatorPath;

    public static final String ARG_GENERATOR_PATH = "generator_path";
    public static final String EXTRA_NUM_GENERATOR_RESULTS = "tabletop.velocic.com.worldforgerpgtools.num_generator_results";
    public static final String EXTRA_GENERATOR_PATH = "tabletop.velocic.com.worldforgerpgtools.generator_path";

    public static NumGeneratorResultsFragment newInstance(String generatorPath)
    {
        Bundle args = new Bundle();
        args.putString(ARG_GENERATOR_PATH, generatorPath);

        NumGeneratorResultsFragment fragment = new NumGeneratorResultsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = LayoutInflater.from(getActivity())
            .inflate(R.layout.dialog_num_generator_results, null);

        numberPicker = view.findViewById(R.id.picker_num_generator_results);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);

        Bundle fragmentArgs = getArguments();

        if (fragmentArgs != null) {
            generatorPath = fragmentArgs.getString(ARG_GENERATOR_PATH);
        }

        return new AlertDialog.Builder(getActivity())
            .setView(view)
            .setTitle(R.string.num_generator_results_dialog_title)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    sendResult(Activity.RESULT_OK, numberPicker.getValue());
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    sendResult(Activity.RESULT_CANCELED, 0);
                }
            })
            .create();
    }

    private void sendResult(int resultCode, int numResultsToGenerate)
    {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NUM_GENERATOR_RESULTS, numResultsToGenerate);
        intent.putExtra(EXTRA_GENERATOR_PATH, generatorPath);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
