package model;


import java.util.HashMap;
import java.util.Map;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * The child defines its connection to its parent.
 */
public class Idea {
    private int id;
    private String theme;
    private Idea parent;
    private Map<Idea, IdeaConnectionType> children;
    private Map<Idea, IdeaConnectionType> acquaintances;
    private boolean isMainIdea;
    private static int numberOfIdeas = 0;

    Idea(String theme, boolean isMainIdea) {
        children = new HashMap<>();
        acquaintances = new HashMap<>();
        this.id = numberOfIdeas;
        this.theme = theme;
        this.isMainIdea = isMainIdea;
        numberOfIdeas++;
    }

    Idea(String theme) {
        this(theme, false);
    }

    public void addChild(Idea idea, IdeaConnectionType connectionType) {
        idea.setParent(this);
        children.put(idea, connectionType);
    }

    public void addAcquitance(Idea idea, IdeaConnectionType connectionType) {
        acquaintances.put(idea, connectionType);
    }

    public void setParent(Idea parent) {
        this.parent = parent;
    }

    public boolean getIsMainIdea() {
        return isMainIdea;
    }

    static int getNumberOfIdeas() {
        return numberOfIdeas;
    }

    public int getId() {
        return id;
    }

    public String getTheme() {
        return theme;
    }

    public Map<Idea, IdeaConnectionType> getChildren() {
        return children;
    }

    public Map<Idea, IdeaConnectionType> getAcquaintances() {
        return acquaintances;
    }

    public boolean isMainIdea() {
        return isMainIdea;
    }

    @Override
    public String toString() {
        String string = "Theme: " + theme + "\n";
        if (!children.isEmpty()) {
            if (children.keySet().size() == 1) {
                string += "Child: \n";
            } else {
                string += "Children: \n";
            }

            for (Idea idea : children.keySet()) {
                string += " - " + idea + "\n";
            }
        } else {
            string += "\n - No children. \n";
        }

        if (!acquaintances.isEmpty()){
            string += "Acquaintances: \n";
            for (Idea idea : acquaintances.keySet()) {
                string += " - " + idea;
            }
        } else {
            string += "\n - No acquaintances. \n";
        }

        return string;
    }
}
