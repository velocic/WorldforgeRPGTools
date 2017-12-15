package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCategory
{
    private String name;
    private String assetPath;
    private GeneratorCategory parent;
    private List<GeneratorCategory> childCategories;
    private List<String> generatorJsonDataPaths;
    private List<Generator> generators;

    public GeneratorCategory(String name, String assetPath)
    {
        this.name = name;
        this.assetPath = assetPath;
        this.parent = null;
        this.childCategories = new ArrayList<GeneratorCategory>();
        this.generatorJsonDataPaths = new ArrayList<String>();
        this.generators = new ArrayList<Generator>();
    }

    public GeneratorCategory(String name, String assetPath, GeneratorCategory parent)
    {
        this.name = name;
        this.assetPath = assetPath;
        this.parent = parent;
        this.childCategories = new ArrayList<GeneratorCategory>();
        this.generatorJsonDataPaths = new ArrayList<String>();
        this.generators = new ArrayList<Generator>();
    }

    public String getName()
    {
        return name;
    }

    public String getAssetPath()
    {
        return assetPath;
    }


    public void setParent(GeneratorCategory parent)
    {
        this.parent = parent;
    }

    public GeneratorCategory getParent()
    {
        return parent;
    }

    public void addChildCategory(GeneratorCategory child)
    {
        this.childCategories.add(child);
    }

    public List<GeneratorCategory> getChildCategories()
    {
        return childCategories;
    }

    public void addGenerator(Generator generator)
    {
        this.generators.add(generator);
    }

    public void addGeneratorJsonDataPath(String jsonPath)
    {
        generatorJsonDataPaths.add(jsonPath);
    }

    public List<String> getGeneratorJsonDataPaths()
    {
        return generatorJsonDataPaths;
    }

    public Generator getGenerator(String name)
    {
        for (Generator generator : generators) {
            if (generator.getName() == name) {
                return generator;
            }
        }

        Generator result = null;

        for (GeneratorCategory child : childCategories) {
            result = child.getGenerator(name);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public GeneratorCategory getCategory(String name)
    {
        for (GeneratorCategory category : childCategories) {
            if (category.getName() == name) {
                return category;
            }
        }

        GeneratorCategory result = null;

        for (GeneratorCategory category : childCategories) {
            result = category.getCategory(name);
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
