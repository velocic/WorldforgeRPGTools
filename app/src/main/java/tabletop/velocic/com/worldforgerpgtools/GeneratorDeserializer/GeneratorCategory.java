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
        int firstSlashIndex = assetPath.indexOf("/");
        String firstPathComponent = "";

        if (firstSlashIndex != -1) {
            firstPathComponent = assetPath.substring(0, firstSlashIndex);
        }

        if (firstPathComponent.equals(GeneratorImporter.GENERATOR_DATA_FOLDER)) {
            return assetPath.substring(firstSlashIndex + 1, assetPath.length()) + "/";
        }

        return assetPath + "/";
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

    public GeneratorCategory getChildCategory(int index)
    {
        return childCategories.get(index);
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

    public int getNumChildCategories()
    {
        return childCategories.size();
    }

    public int getNumGenerators()
    {
        return generators.size();
    }

    public Generator getGenerator(String name)
    {
        for (Generator generator : generators) {
            if (generator.getName().equals(name)) {
                return generator;
            }
        }

        return null;
    }

    public Generator getGenerator(int index)
    {
        return generators.get(index);
    }

    public List<Generator> getGenerators()
    {
        return generators;
    }

    public GeneratorCategory getCategory(String name)
    {
        for (GeneratorCategory category : childCategories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }

        return null;
    }

    public GeneratorCategory getCategoryFromFullPath(String fullQualifiedPath, GeneratorCategory node)
    {
        if (node == null || fullQualifiedPath.equals(node.getName()) || fullQualifiedPath.equals("")) {
            return node;
        }

        int slashCharIndex = fullQualifiedPath.indexOf("/");

        //Nothing left to reduce on the path, and the current node does not match somehow
        if (slashCharIndex == -1) {
            return null;
        }

        String nextCategoryName = fullQualifiedPath.substring(0, slashCharIndex);
        String reducedPath = fullQualifiedPath.substring(slashCharIndex + 1);

        GeneratorCategory nextCategory = node.getCategory(nextCategoryName);
        return getCategoryFromFullPath(reducedPath, nextCategory);
    }

    public Generator getGeneratorFromFullPath(String fullQualifiedPath, GeneratorCategory node)
    {
        if (node == null) {
            return null;
        }

        int slashCharIndex = fullQualifiedPath.indexOf("/");

        //No more path to reduce. Return the generator with name matching fullQualifiedPath
        if (slashCharIndex == -1) {
            return node.getGenerator(fullQualifiedPath);
        }

        //Get the next generator along the path down the tree and recurse
        String nextCategoryName = fullQualifiedPath.substring(0, slashCharIndex);
        String reducedPath = fullQualifiedPath.substring(slashCharIndex + 1);

        return getGeneratorFromFullPath(reducedPath, node.getCategory(nextCategoryName));
    }
}
