package com.aa.mtg.cards.ability.type;

public enum AbilityType {
    DEATHTOUCH("Deathtouch."),
    DRAW_X_CARDS("Draw %s cards."),
    ADD_X_LIFE("%s %s life."),
    EACH_PLAYERS_ADD_X_LIFE("Each player %s %s life."),
    ENCHANTED_CREATURE_GETS("Enchanted creature gets %s."),
    ENTERS_THE_BATTLEFIELD_TAPPED("Enters the battlefield tapped."),
    EQUIPPED_CREATURE_GETS("Equipped creature gets %s."),
    FLYING("Flying."),
    HASTE("Haste."),
    LIFELINK("Lifelink."),
    REACH("Reach."),
    SELECTED_PERMANENTS_GET("%s get %s"),
    SHUFFLE_GRAVEYARD_INTO_LIBRARY_FOR_TARGET_PLAYER("Shuffle graveyard into library."),
    TAP_ADD_MANA("Tap add %s mana."),
    THAT_TARGET_CONTROLLER_GETS("That target controller gets %s."),
    THAT_TARGETS_GET("That targets get %s."),
    TRAMPLE("Trample."),
    VIGILANCE("Vigilance.");

    private String text;

    AbilityType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static AbilityType abilityType(String text) {
        if (text == null) {
            return null;
        }

        return AbilityType.valueOf(text);
    }
}