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
    private int childLevel;
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
        if (isMainIdea) {
            childLevel = 0;
        } else {
            childLevel = -1;
        }
        numberOfIdeas++;
    }

    Idea(String theme) {
        this(theme, false);
    }

    public void addChild(Idea idea, IdeaConnectionType connectionType) {
        idea.setParent(this);
        idea.childLevel = this.childLevel + 1;
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
        String string = theme + "\n";

        String spaces = "";
        for (int i = 0; i <= childLevel; i++) {
            spaces += "  ";
        }

        if (!acquaintances.isEmpty()){
            for (Idea idea : acquaintances.keySet()) {
                string += spaces + "(" + acquaintances.get(idea) + ": " + idea.theme + ")\n";
            }
        }

        if (!children.isEmpty()) {
            for (Idea idea : children.keySet()) {
                string += spaces + children.get(idea) + ": " + idea;
            }
        }
        return string;
    }
}
