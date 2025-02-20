import {set} from 'lodash'
import StackUtils from 'Main/game/Stack/StackUtils'
import Phase from 'Main/game/Turn/Phase'
import PlayerUtils from 'Main/game/PlayerInfo/PlayerUtils'

export default class UserInterfaceUtils {
  static setMessage(state, message, closable = true) {
    set(state, 'userInterface.message', {text: message, closable: closable})
  }

  static unsetMessage(state) {
    UserInterfaceUtils.setMessage(state, undefined)
  }

  static computeStatusMessage(state) {
    if (state.turn.winner) {
      UserInterfaceUtils.setMessage(state, state.turn.winner + ' Win!')

    } else if (!PlayerUtils.isCurrentPlayerActive(state)) {
      UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.WAIT_FOR_AN_OPPONENT_TO_PERFORM_ITS_ACTION)

    } else {
      if (!StackUtils.isStackEmpty(state)) {
        UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.PLAY_ANY_INSTANT_OR_ABILITIES_OR_CONTINUE_STACK_NOT_EMPTY)

      } else {
        if (Phase.isMainPhase(state.turn.currentPhase)) {
          UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.PLAY_ANY_SPELL_OR_ABILITIES_OR_CONTINUE)

        } else if (state.turn.currentPhase === 'DB' && PlayerUtils.isPlayerAbleToBlock(state)) {
          UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.CHOOSE_CREATURES_YOU_WANT_TO_BLOCK_WITH)

        } else if (state.turn.currentPhase === 'DA' && PlayerUtils.isPlayerAbleToAttack(state)) {
          UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.CHOOSE_CREATURES_YOU_WANT_TO_ATTACK_WITH)

        } else if (state.turn.triggeredNonStackAction) {
          UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.CHOOSE_A_CARD_TO_DISCARD)

        } else {
          UserInterfaceUtils.setStatusMessage(state, UserInterfaceUtils.PLAY_ANY_INSTANT_OR_ABILITIES_OR_CONTINUE)
        }
      }
    }
  }

  static setStatusMessage(state, statusMessage) {
    set(state, 'userInterface.statusMessage', statusMessage)
  }

  static setPlayableAbilities(state, cardId, possibleAbilities, position) {
    set(state, 'userInterface.playableAbilities', {cardId: cardId, possibleAbilities: possibleAbilities, position: position})
  }

  static unsetPlayableAbilities(state) {
    set(state, 'userInterface.playableAbilities', {})
  }
}

UserInterfaceUtils.CHOOSE_A_CARD_TO_DISCARD = 'Choose a card to discard.'
UserInterfaceUtils.CHOOSE_CREATURES_YOU_WANT_TO_BLOCK_WITH = 'Choose creatures you want to block with.'
UserInterfaceUtils.CHOOSE_CREATURES_YOU_WANT_TO_ATTACK_WITH = 'Choose creatures you want to attack with.'
UserInterfaceUtils.PLAY_ANY_SPELL_OR_ABILITIES_OR_CONTINUE = 'Play any spell or ability or continue (SPACE).'
UserInterfaceUtils.PLAY_ANY_INSTANT_OR_ABILITIES_OR_CONTINUE = 'Play any instant or ability or continue (SPACE).'
UserInterfaceUtils.PLAY_ANY_INSTANT_OR_ABILITIES_OR_CONTINUE_STACK_NOT_EMPTY = 'Play any instant or ability or resolve the top spell in the stack (SPACE).'
UserInterfaceUtils.WAIT_FOR_AN_OPPONENT_TO_PERFORM_ITS_ACTION = 'Wait for opponent to perform its action...'