package fr.aerwyn81.featuredplots.events;

import com.plotsquared.core.plot.Plot;
import fr.aerwyn81.featuredplots.FeaturedPlots;
import fr.aerwyn81.featuredplots.data.FPlot;
import fr.aerwyn81.featuredplots.utils.chat.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public record OnWorldEvent(FeaturedPlots main) implements Listener {

    /**
     * Used to assign each plot of PlotSquared when it is loaded along with the world
     *
     * @param e {@link WorldLoadEvent} world load event
     */
    @EventHandler
    public void OnWorldLoad(WorldLoadEvent e) {
        var worldName = e.getWorld().getName();

        for (FPlot plot : main.getFeaturedPlotsManager().getPlotHandler().getPlotsByWorld(worldName)) {
            var psPlot = Plot.fromString(null, plot.getConfigPlotArea() + ";" + plot.getConfigPlotId());
            if (psPlot == null) {
                FeaturedPlots.log.sendMessage(MessageUtils.colorize(String.format("&3[FeaturedPlots] &cCannot find plot %s in the world %s", plot.getName(), plot.getConfigWorld())));
                continue;
            }

            plot.setPlot(psPlot);
        }
    }
}
