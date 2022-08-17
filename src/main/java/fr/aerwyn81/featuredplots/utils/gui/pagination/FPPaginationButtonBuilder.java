package fr.aerwyn81.featuredplots.utils.gui.pagination;

import fr.aerwyn81.featuredplots.utils.gui.FPMenu;
import fr.aerwyn81.featuredplots.utils.gui.ItemGUI;

/**
 * All credits of this code go to @SamJakob (SpiGUI)
 * https://github.com/SamJakob/SpiGUI
 */
public interface FPPaginationButtonBuilder {
    ItemGUI buildPaginationButton(FPPaginationButtonType type, FPMenu inventory);
}