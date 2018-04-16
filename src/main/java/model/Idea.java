package model;


import java.util.HashMap;
import java.util.Map;

/**
 * The thought as a keyword expressing a fraction of a theme/subject.
 * This thought is either the parent of all other thoughts in the mind map,
 * or it is a child to another thought.
 *
 * A map tracks the connection types each child (Idea) has to its parent.
 */
public class Idea {
    private int id;
    private String theme;
    private Idea parent;
    private int childLevel;
    private Map<Idea, IdeaConnectionType> children;
    private Map<Idea, IdeaConnectionType> acquaintances;
    private boolean isMainIdea;
    private Bubble bubble;

    private static int numberOfIdeas = 0;

    /**
     * Create an Idea with its theme, if it is main idea and instructions for display in a Bubble.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     * @param bubble instructions for displaying the Idea.
     */
    public Idea(String theme, boolean isMainIdea, Bubble bubble) {
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
        this.bubble = bubble;
        numberOfIdeas++;
    }

    /**
     * Create an Idea with its theme and if it is main idea.
     * @param theme the semantic content of the Idea as a String
     * @param isMainIdea the central Idea of the mindmap
     */
    public Idea(String theme, boolean isMainIdea) {
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
        bubble = new Bubble();
        numberOfIdeas++;
    }

    public Idea(String theme) {
        this(theme, false);
    }

    /**
     * Adds  a child to the current Idea
     * @param child as the Idea that branches off the current Idea (parent).
     * @param connectionType specifies what connection the child should have to the parent Idea.
     */
    public void addChild(Idea child, IdeaConnectionType connectionType) {
        child.setParent(this);
        child.childLevel = this.childLevel + 1;
        children.put(child, connectionType);
    }

    /**
     * Adds a child to the current Idea with default IdeaConnectionType.BRANCH to its parent.
     * @param child the Idea to be added to a parent.
     */
    public void addChild(Idea child) {
        addChild(child, IdeaConnectionType.BRANCH);
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

    public Bubble getBubble() {
        return bubble;
    }

    public void setBubble(Bubble bubble) {
        this.bubble = bubble;
    }

    @Override
    public String toString() {
        String string = theme + " <" + bubble.getType() + ">\n";

        String spaces = "";
        for (int i = 0; i <= childLevel; i++) {
            spaces += "  ";
        }

        if (!acquaintances.isEmpty()){
            for (Idea idea : acquaintances.keySet()) {
                string += spaces + "(" + acquaintances.get(idea) + ": " + idea.theme + ") <" + idea.bubble.getType() + ">\n";
            }
        }

        if (!children.isEmpty()) {
            for (Idea idea : children.keySet()) {
                string += spaces + children.get(idea) + ": " + idea; // recursion to get each child and its children in turn.
            }
        }
        return string;
    }
}
