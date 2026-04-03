package control;

public enum Difficulty {
    EASY(1, "Easy"),
    MEDIUM(2, "Medium"),
    HARD(3, "Hard");

    private final int value;
    private final String label;

    Difficulty(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts a user-selected integer (1-3) to the corresponding Difficulty.
     * @param choice the integer choice from user input
     * @return the matching Difficulty
     * @throws IllegalArgumentException if the choice does not map to any difficulty
     */
    public static Difficulty fromChoice(int choice) {
        for (Difficulty d : values()) {
            if (d.value == choice) return d;
        }
        throw new IllegalArgumentException("Invalid difficulty choice: " + choice);
    }
}
