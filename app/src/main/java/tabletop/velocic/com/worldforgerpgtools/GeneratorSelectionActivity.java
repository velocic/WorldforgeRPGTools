package tabletop.velocic.com.worldforgerpgtools;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class GeneratorSelectionActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new GeneratorSelectionFragment();
    }
}
