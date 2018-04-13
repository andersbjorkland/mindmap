package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of all the thoughts being a part of a mind map.
 */
public class Mind {
    private List<Idea> ideas = new ArrayList<>();
    private Idea mainIdea;

    /* as ideas are added to the mind map, get reference to main idea,
     * and add idea to a list of ideas.
     */
    public void addIdea(Idea idea) {
        if (idea.getIsMainIdea()) {
            mainIdea = idea;
        }

        ideas.add(idea);
    }
}
