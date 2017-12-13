package tabletop.velocic.com.worldforgerpgtools.GeneratorDeserializer;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCategory
{
    private String name;
    private GeneratorCategory parent;
    private List<GeneratorCategory> childCategories;
    private List<Generator> generators;

    public GeneratorCategory(String name)
    {
        this.name = name;
        this.parent = null;
        this.childCategories = new ArrayList<GeneratorCategory>();
        this.generators = new ArrayList<Generator>();
    }

    public GeneratorCategory(String name, GeneratorCategory parent)
    {
        this.name = name;
        this.parent = parent;
        this.childCategories = new ArrayList<GeneratorCategory>();
        this.generators = new ArrayList<Generator>();
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

    public void addGenerator(Generator generator)
    {
        this.generators.add(generator);
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
                return result
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
                return result
            }
        }

        return null;
    }
}
