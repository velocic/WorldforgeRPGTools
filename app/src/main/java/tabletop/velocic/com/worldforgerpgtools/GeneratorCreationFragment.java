package tabletop.velocic.com.worldforgerpgtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer.GeneratorImporter;

public class GeneratorCreationFragment extends android.support.v4.app.Fragment
{
    public static final String BACK_STACK_GENERATOR_CREATION_FRAGMENT = "tabletop.velocic.com.worldforgerpgtools.GeneratorCreationFragment";

    private static final int REQUEST_NEW_CATEGORY_PATH = 0;

    private EditText newGeneratorName;
    private TextView newGeneratorCategoryName;
    private Button createNewResultEntryButton;
    private Button submitGeneratorButton;

    public static GeneratorCreationFragment newInstance()
    {
        GeneratorCreationFragment fragment = new GeneratorCreationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_generator, container, false);

        newGeneratorName = (EditText) view.findViewById(R.id.edit_text_create_generator_name);
        newGeneratorCategoryName = (TextView) view.findViewById(R.id.edit_text_create_generator_category);
        createNewResultEntryButton = (Button) view.findViewById(R.id.button_add_generator_possible_result);
        submitGeneratorButton = (Button) view.findViewById(R.id.button_submit_new_generator);

        newGeneratorCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onNewGeneratorCategoryNameClicked(v);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_NEW_CATEGORY_PATH) {
            //Can't set the TextView directly, as this function is called before
            //onResume, and the TextView state gets lost.
            setArguments(data.getExtras());
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Bundle fragmentArgs = getArguments();

        if (fragmentArgs != null) {
            newGeneratorCategoryName.setText(fragmentArgs.getString(GeneratorCategorySelectionFragment.EXTRA_SELECTED_CATEGORY));
        }
    }

    private void onNewGeneratorCategoryNameClicked(View v)
    {
        TextView view = (TextView) v;
        Fragment generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(view.getText().toString());

        generatorCategorySelectionFragment.setTargetFragment(GeneratorCreationFragment.this, REQUEST_NEW_CATEGORY_PATH);

        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, generatorCategorySelectionFragment)
            .addToBackStack(BACK_STACK_GENERATOR_CREATION_FRAGMENT)
            .commit();
    }
}
