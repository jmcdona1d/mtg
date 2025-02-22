package com.aa.mtg.cards.sets;

import static com.aa.mtg.cards.ability.Abilities.DESTROY_TARGET_CREATURE_WITH_POWER_LESS_OR_EQUAL_3;
import static com.aa.mtg.cards.ability.Abilities.FLYING;
import static com.aa.mtg.cards.ability.Abilities.OTHER_KNIGHTS_YOU_CONTROL_GET_PLUS_1_1;
import static com.aa.mtg.cards.ability.Abilities.PAY_1_EQUIP_CREATURE_GETS_PLUS_1_0_AND_HASTE;
import static com.aa.mtg.cards.ability.Abilities.TAP_ADD_BLUE_MANA;
import static com.aa.mtg.cards.ability.Abilities.TAP_ADD_GREEN_MANA;
import static com.aa.mtg.cards.ability.Abilities.TARGET_CREATURE_GETS_PLUS_2_1_AND_DEATHTOUCH_UNTIL_END_OF_TURN;
import static com.aa.mtg.cards.properties.Rarity.COMMON;
import static com.aa.mtg.cards.properties.Rarity.UNCOMMON;
import static com.aa.mtg.cards.properties.Subtype.EQUIPMENT;
import static com.aa.mtg.cards.properties.Subtype.FAERIE;
import static com.aa.mtg.cards.properties.Subtype.GRIFFIN;
import static com.aa.mtg.cards.properties.Subtype.HUMAN;
import static com.aa.mtg.cards.properties.Subtype.KNIGHT;
import static com.aa.mtg.cards.properties.Type.ARTIFACT;
import static com.aa.mtg.cards.properties.Type.CREATURE;
import static com.aa.mtg.cards.properties.Type.INSTANT;
import static com.aa.mtg.cards.properties.Type.SORCERY;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

import com.aa.mtg.cards.Card;
import com.aa.mtg.cards.properties.Color;
import com.aa.mtg.cards.properties.Cost;
import java.util.ArrayList;
import java.util.List;

public class ThroneOfEldraine implements MtgSet {

    public static final String ELD = "ELD";

    public static Card CRYSTAL_SLIPPER = new Card("Crystal Slipper", singleton(Color.RED), asList(Cost.RED, Cost.COLORLESS), singletonList(ARTIFACT), singletonList(EQUIPMENT), COMMON, "Equipped creature gets +1/+0 and has haste. Equip 1", 0, 0, singletonList(PAY_1_EQUIP_CREATURE_GETS_PLUS_1_0_AND_HASTE));
    public static Card INSPIRING_VETERAN = new Card("Inspiring Veteran", asSet(Color.RED, Color.WHITE), asList(Cost.RED, Cost.WHITE), singletonList(CREATURE), asList(HUMAN, KNIGHT), UNCOMMON, "Other Knights you control get +1/+1.", 2, 2, singletonList(OTHER_KNIGHTS_YOU_CONTROL_GET_PLUS_1_1));
    public static Card KNIGHT_OF_THE_KEEP = new Card("Knight of the Keep", singleton(Color.WHITE), asList(Cost.WHITE, Cost.COLORLESS, Cost.COLORLESS), singletonList(CREATURE), asList(HUMAN, KNIGHT), COMMON, "", 3, 2, emptyList());
    public static Card LASH_OF_THORNS = new Card("Lash of Thorns", singleton(Color.BLACK), singletonList(Cost.BLACK), singletonList(INSTANT), emptyList(), COMMON, "Target creature gets +2/+1 and gains deathtouch until end of turn.", 0, 0, singletonList(TARGET_CREATURE_GETS_PLUS_2_1_AND_DEATHTOUCH_UNTIL_END_OF_TURN));
    public static Card MARALEAF_PIXIE = new Card("Maraleaf Pixie", asSet(Color.GREEN, Color.BLUE), asList(Cost.GREEN, Cost.BLUE), singletonList(CREATURE), singletonList(FAERIE), UNCOMMON, "Flying. TAP: Add GREEN or BLUE.", 2, 2, asList(FLYING, TAP_ADD_GREEN_MANA, TAP_ADD_BLUE_MANA));
    public static Card OKOS_ACCOMPLICES = new Card("Oko's Accomplices", singleton(Color.BLUE), asList(Cost.BLUE, Cost.COLORLESS, Cost.COLORLESS), singletonList(CREATURE), singletonList(FAERIE), COMMON, "Flying", 2, 3, singletonList(FLYING));
    public static Card PRIZED_GRIFFIN = new Card("Prized Griffin", singleton(Color.WHITE), asList(Cost.WHITE, Cost.COLORLESS, Cost.COLORLESS, Cost.COLORLESS, Cost.COLORLESS), singletonList(CREATURE), singletonList(GRIFFIN), COMMON, "Flying", 3, 4, singletonList(FLYING));
    public static Card REAVE_SOUL = new Card("Reave Soul", singleton(Color.BLACK), asList(Cost.BLACK, Cost.COLORLESS), singletonList(SORCERY), emptyList(), COMMON, "Destroy target creature with power 3 or less.", 0, 0, singletonList(DESTROY_TARGET_CREATURE_WITH_POWER_LESS_OR_EQUAL_3));

    private static ThroneOfEldraine instance;

    private List<Card> cards = new ArrayList<>();

    private ThroneOfEldraine() {
        cards.add(CRYSTAL_SLIPPER);
        cards.add(INSPIRING_VETERAN);
        cards.add(KNIGHT_OF_THE_KEEP);
        cards.add(LASH_OF_THORNS);
        cards.add(MARALEAF_PIXIE);
        cards.add(OKOS_ACCOMPLICES);
        cards.add(PRIZED_GRIFFIN);
        cards.add(REAVE_SOUL);
    }

    @Override
    public String getName() {
        return "Throne of Eldraine";
    }

    @Override
    public String getCode() {
        return ELD;
    }

    @Override
    public List<Card> getCards() {
        return cards;
    }

    public static ThroneOfEldraine throneOfEldraine() {
        if (instance == null) {
            instance = new ThroneOfEldraine();
        }
        return instance;
    }
}
