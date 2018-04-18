import controller.IdeaController;
import model.Idea;

import java.util.Set;

/*
 * Used for simple output on the progress of the project.
 */
public class ConsoleDisplay {
    public static void main(String[] args) {
        IdeaController ideaController = new IdeaController();
        Idea idea = ideaController.mindExample();

        System.out.println("Correct representation: ");
        System.out.println(idea + "\n");

        Set<Idea> ideas = ideaController.extractUniqueIdeas(idea);
        System.out.println("Extracted ideas: ");
        System.out.println("Parent / Idea / Child level ");
        for (Idea extractedIdea: ideas) {
            if (!extractedIdea.isMainIdea()) {
                System.out.println(extractedIdea.getParent().getTheme() + ": " + extractedIdea.getTheme() + ": " + extractedIdea.getChildLevel());
            } else {
                System.out.println("Main Idea: " + extractedIdea.getTheme() + ": " + extractedIdea.getChildLevel());
            }
        }
    }
}
