package com.aa.mtg.game.turn.action._continue;

import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.ability.Ability;
import com.aa.mtg.cards.ability.action.AbilityAction;
import com.aa.mtg.cards.ability.target.Target;
import com.aa.mtg.game.message.MessageException;
import com.aa.mtg.game.player.Player;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.AbilityActionFactory;
import com.aa.mtg.game.turn.action.enter.EnterCardIntoBattlefieldService;
import com.aa.mtg.game.turn.action.leave.PutIntoGraveyardService;
import com.aa.mtg.game.turn.action.target.TargetCheckerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aa.mtg.cards.ability.trigger.TriggerType.CAST;

@Component
public class ResolveService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolveService.class);

    private final ContinueTurnService continueTurnService;
    private final AbilityActionFactory abilityActionFactory;
    private final EnterCardIntoBattlefieldService enterCardIntoBattlefieldService;
    private final PutIntoGraveyardService putIntoGraveyardService;
    private final TargetCheckerService targetCheckerService;

    @Autowired
    public ResolveService(ContinueTurnService continueTurnService, AbilityActionFactory abilityActionFactory,
                          EnterCardIntoBattlefieldService enterCardIntoBattlefieldService, PutIntoGraveyardService putIntoGraveyardService, TargetCheckerService targetCheckerService) {
        this.continueTurnService = continueTurnService;
        this.abilityActionFactory = abilityActionFactory;
        this.enterCardIntoBattlefieldService = enterCardIntoBattlefieldService;
        this.putIntoGraveyardService = putIntoGraveyardService;
        this.targetCheckerService = targetCheckerService;
    }

    public void resolve(GameStatus gameStatus, String triggeredNonStackAction, List<Integer> targetCardIds, Map<Integer, List<Object>> targetsIdsForCardIds) {
        if (!gameStatus.getStack().isEmpty()) {
            CardInstance stackItemToResolve = gameStatus.getStack().peek();

            if (stackItemToResolve.getTriggeredAbilities().isEmpty()) {
                gameStatus.getStack().remove();
                resolveCardInstanceFromStack(gameStatus, stackItemToResolve);

            } else {
                Player playerWhoCastedTheSpell = gameStatus.getPlayerByName(stackItemToResolve.getController());
                if (gameStatus.getTurn().getCurrentPhaseActivePlayer().equals(playerWhoCastedTheSpell.getName())) {
                    if (targetCheckerService.checkIfValidTargetsArePresentForSpellOrAbilityTargetRequisites(stackItemToResolve, gameStatus)) {
                        targetCheckerService.checkSpellOrAbilityTargetRequisites(stackItemToResolve, gameStatus, targetsIdsForCardIds, "THAT_TARGETS_GET");
                    } else {
                        gameStatus.getStack().remove();
                        return;
                    }

                } else {
                    gameStatus.getStack().remove();
                    resolveTriggeredAbility(gameStatus, stackItemToResolve);
                }
            }

            gameStatus.getTurn().setCurrentPhaseActivePlayer(gameStatus.getNonActivePlayer().getName());

        } else if (gameStatus.getTurn().getTriggeredNonStackAction().equals(triggeredNonStackAction)) {
            resolveTriggeredNonStackAction(gameStatus, triggeredNonStackAction, targetCardIds);

        } else {
            String message = "Cannot resolve triggeredNonStackAction " + triggeredNonStackAction + " as current triggeredNonStackAction is " + gameStatus.getTurn().getTriggeredNonStackAction();
            throw new MessageException(message);
        }
    }

    private void resolveCardInstanceFromStack(GameStatus gameStatus, CardInstance cardToResolve) {
        performAbilitiesActions(gameStatus, cardToResolve, cardToResolve.getAbilitiesByTriggerType(CAST));

        if (cardToResolve.isPermanent()) {
            enterCardIntoBattlefieldService.enter(gameStatus, cardToResolve);

        } else {
            cardToResolve.resetAllModifiers();
            putIntoGraveyardService.putIntoGraveyard(gameStatus, cardToResolve);
        }
    }

    private void resolveTriggeredAbility(GameStatus gameStatus, CardInstance stackItemToResolve) {
        performAbilitiesActions(gameStatus, stackItemToResolve, stackItemToResolve.getTriggeredAbilities());
        stackItemToResolve.getTriggeredAbilities().clear();
    }

    private void resolveTriggeredNonStackAction(GameStatus gameStatus, String triggeredNonStackAction, List<Integer> cardIds) {
        switch (triggeredNonStackAction) {
            case "DISCARD_A_CARD": {
                CardInstance cardInstance = gameStatus.getCurrentPlayer().getHand().extractCardById(cardIds.get(0));
                putIntoGraveyardService.putIntoGraveyard(gameStatus, cardInstance);
                gameStatus.getTurn().setTriggeredNonStackAction(null);
                break;
            }
        }
        continueTurnService.continueTurn(gameStatus);
    }

    private void performAbilitiesActions(GameStatus gameStatus, CardInstance cardToResolve, List<Ability> abilities) {
        for (Ability ability : abilities) {
            performAbilityAction(gameStatus, cardToResolve, ability);
        }

        cardToResolve.getModifiers().setTargets(new ArrayList<>());
    }

    private void performAbilityAction(GameStatus gameStatus, CardInstance cardToResolve, Ability ability) {
        AbilityAction abilityAction = abilityActionFactory.getAbilityAction(ability.getAbilityType());
        if (abilityAction != null) {
            try {
                checkTargets(gameStatus, cardToResolve, ability);
                abilityAction.perform(cardToResolve, gameStatus, ability);

            } catch (MessageException e) {
                LOGGER.info("{}: Target is now invalid during resolution, dropping the action. [{}] ", cardToResolve.getIdAndName(), e.getMessage());
            }
        }
    }

    private void checkTargets(GameStatus gameStatus, CardInstance cardToResolve, Ability ability) {
        for (int i = 0; i < ability.getTargets().size(); i++) {
            Target target = ability.getTargets().get(i);
            Object targetId = getTargetIdAtIndex(cardToResolve, i);
            targetCheckerService.check(gameStatus, cardToResolve, target, targetId);
        }
    }

    private Object getTargetIdAtIndex(CardInstance cardToResolve, int i) {
        if (i < cardToResolve.getModifiers().getTargets().size()) {
            return cardToResolve.getModifiers().getTargets().get(i);
        }
        return null;
    }
}
