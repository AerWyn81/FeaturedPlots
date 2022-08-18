package fr.aerwyn81.featuredplots.commands.list.explore;

import com.plotsquared.core.command.*;
import com.plotsquared.core.configuration.caption.StaticCaption;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.util.task.RunnableVal2;
import com.plotsquared.core.util.task.RunnableVal3;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.GuiType;
import fr.aerwyn81.featuredplots.handlers.LanguageHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@CommandDeclaration(
        command = "featured",
        permission = "plots.featured",
        description = "Explore featured plots",
        usage = "/plot featured",
        category = CommandCategory.TELEPORT,
        requiredType = RequiredType.PLAYER
)
public class PSExploreCommand extends Command {
    private final FeaturedPlots main;
    private final LanguageHandler languageHandler;

    public PSExploreCommand(FeaturedPlots main) {
        super(MainCommand.getInstance(), true);

        this.main = main;
        this.languageHandler = main.getLanguageHandler();
    }

    @Override
    public CompletableFuture<Boolean> execute(PlotPlayer<?> player, String[] args, RunnableVal3<Command, Runnable, Runnable> confirm, RunnableVal2<Command, CommandResult> whenDone) throws CommandException {
        var featuredPlots = main.getFeaturedPlotsManager().getFeaturedPlots();
        if (featuredPlots.keySet().size() == 0) {
            player.sendMessage(StaticCaption.of(languageHandler.getMessage("Messages.FeaturedPlotsEmpty")));
            return CompletableFuture.completedFuture(false);
        }

        main.getGuiManager().open((Player) player.getPlatformPlayer(), GuiType.Categories, new ArrayList<>(main.getFeaturedPlotsManager().getCategoryHandler().getCategories()));
        return CompletableFuture.completedFuture(true);
    }
}
