package com.aa.mtg.game.turn.action.permanent;

import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.ability.Ability;
import com.aa.mtg.cards.modifiers.PowerToughness;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.counters.CountersService;
import com.aa.mtg.game.turn.action.damage.DealDamageToCreatureService;
import com.aa.mtg.game.turn.action.damage.DealDamageToPlayerService;
import com.aa.mtg.game.turn.action.leave.DestroyPermanentService;
import com.aa.mtg.game.turn.action.leave.ReturnPermanentToHandService;
import com.aa.mtg.game.turn.action.tap.TapPermanentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.aa.mtg.cards.ability.Abilities.*;

@Component
public class PermanentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermanentService.class);

    private final DealDamageToCreatureService dealDamageToCreatureService;
    private final DealDamageToPlayerService dealDamageToPlayerService;
    private final DestroyPermanentService destroyPermanentService;
    private final TapPermanentService tapPermanentService;
    private final ReturnPermanentToHandService returnPermanentToHandService;
    private final GainControlPermanentService gainControlPermanentService;
    private final CountersService countersService;

    @Autowired
    public PermanentService(DealDamageToCreatureService dealDamageToCreatureService, DealDamageToPlayerService dealDamageToPlayerService, DestroyPermanentService destroyPermanentService,
                            TapPermanentService tapPermanentService, ReturnPermanentToHandService returnPermanentToHandService, GainControlPermanentService gainControlPermanentService, CountersService countersService) {
        this.dealDamageToCreatureService = dealDamageToCreatureService;
        this.dealDamageToPlayerService = dealDamageToPlayerService;
        this.destroyPermanentService = destroyPermanentService;
        this.tapPermanentService = tapPermanentService;
        this.returnPermanentToHandService = returnPermanentToHandService;
        this.gainControlPermanentService = gainControlPermanentService;
        this.countersService = countersService;
    }

    public void thatPermanentGets(CardInstance cardInstance, GameStatus gameStatus, List<String> parameters, CardInstance target) {
        for (String parameter : parameters) {
            thatPermanentGets(cardInstance, gameStatus, parameter, target);
        }
    }

    private void thatPermanentGets(CardInstance cardInstance, GameStatus gameStatus, String parameter, CardInstance target) {
        PowerToughness powerToughness = powerToughnessFromParameter(parameter);
        target.getModifiers().addExtraPowerToughnessUntilEndOfTurn(powerToughness);

        Optional<Ability> ability = abilityFromParameter(parameter);
        ability.ifPresent(value -> target.getModifiers().getAbilitiesUntilEndOfTurn().add(value));

        int damage = damageFromParameter(parameter);
        dealDamageToCreatureService.dealDamageToCreature(gameStatus, target, damage, false);

        int controllerDamage = controllerDamageFromParameter(parameter);
        dealDamageToPlayerService.dealDamageToPlayer(gameStatus, controllerDamage, gameStatus.getPlayerByName(cardInstance.getController()));

        if (destroyedFromParameter(parameter)) {
            destroyPermanentService.destroy(gameStatus, target.getId());
        }

        if (tappedDoesNotUntapNextTurnFromParameter(parameter)) {
            tapPermanentService.tapDoesNotUntapNextTurn(gameStatus, target.getId());
        }

        if (untappedFromParameter(parameter)) {
            tapPermanentService.untap(gameStatus, target.getId());
        }

        if (returnToOwnerHandFromParameter(parameter)) {
            returnPermanentToHandService.returnPermanentToHand(gameStatus, target.getId());
        }

        if (controlledFromParameter(parameter)) {
            gainControlPermanentService.gainControlUntilEndOfTurn(gameStatus, target, cardInstance.getController());
        }

        int counters = plus1CountersFromParameter(parameter);
        countersService.addPlus1Counters(gameStatus, target, counters);

        LOGGER.info("PermanentService: {} target {} which gets {}", cardInstance.getIdAndName(), target.getIdAndName(), parameter);
    }
}
