package net.ratdik.multikino.domain;

public enum Jezyk {
    POLSKI("Polski"),
    ANGIELSKI("Angielski"),
    FRANCUSKI("Francuski"),
    NIEMIECKI("Niemiecki"),
    HISZPANSKI("Hiszpański"),
    WŁOSKI("Włoski"),
    KOREANSKI("Koreański"),
    ROSYJSKI("Rosyjski"),
    AMERIKA("Amerykański");

    private final String displayName;

    Jezyk(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}