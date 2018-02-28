package tabletop.velocic.com.worldforgerpgtools;

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

    private void onNewGeneratorCategoryNameClicked(View v)
    {
        TextView view = (TextView) v;
        Fragment generatorCategorySelectionFragment = GeneratorCategorySelectionFragment.newInstance(view.getText().toString());

        getActivity().getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, generatorCategorySelectionFragment)
            .addToBackStack(null)
            .commit();
    }
}
