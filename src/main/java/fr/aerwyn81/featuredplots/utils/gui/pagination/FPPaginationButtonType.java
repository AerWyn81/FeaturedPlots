package fr.aerwyn81.featuredplots.utils.gui.pagination;

/**
 * All credits of this code go to @SamJakob (SpiGUI)
 * https://github.com/SamJakob/SpiGUI
 */
public enum FPPaginationButtonType {

    PREV_BUTTON(3),
    CURRENT_BUTTON(4),
    NEXT_BUTTON(5),
    UNASSIGNED(0);

    private final int slot;

    FPPaginationButtonType(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public static FPPaginationButtonType forSlot(int slot) {
        for (FPPaginationButtonType buttonType : FPPaginationButtonType.values()) {
            if (buttonType.slot == slot) return buttonType;
        }

        return FPPaginationButtonType.UNASSIGNED;
    }

}
