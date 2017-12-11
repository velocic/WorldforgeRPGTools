package tabletop.velocic.com.worldforgerpgtools;

import android.support.v4.app.Fragment;

public class GeneratorSelectionActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new GeneratorSelectionFragment();
    }
}
