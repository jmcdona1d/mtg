package application.cleanup;

import application.AbstractApplicationTest;
import application.InitTestServiceDecorator;
import com.aa.mtg.MtgApplication;
import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.game.init.test.InitTestService;
import com.aa.mtg.game.status.GameStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static application.browser.BattlefieldHelper.SECOND_LINE;
import static com.aa.mtg.cards.Cards.ISLAND;
import static com.aa.mtg.cards.Cards.MOUNTAIN;
import static com.aa.mtg.cards.modifiers.PowerToughness.powerToughness;
import static com.aa.mtg.cards.modifiers.TappedModifier.TAPPED;
import static com.aa.mtg.cards.sets.Ixalan.AIR_ELEMENTAL;
import static com.aa.mtg.cards.sets.Ixalan.GRAZING_WHIPTAIL;
import static com.aa.mtg.cards.sets.Ixalan.HUATLIS_SNUBHORN;
import static com.aa.mtg.game.player.PlayerType.OPPONENT;
import static com.aa.mtg.game.player.PlayerType.PLAYER;
import static com.aa.mtg.game.turn.phases.EndTurnPhase.ET;
import static com.aa.mtg.game.turn.phases.Main1Phase.M1;
import static com.aa.mtg.game.turn.phases.Main2Phase.M2;
import static com.aa.mtg.game.turn.phases.UpkeepPhase.UP;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MtgApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({CleanupTest.InitGameTestConfiguration.class})
public class CleanupTest extends AbstractApplicationTest {

    @Autowired
    private InitTestServiceDecorator initTestServiceDecorator;

    public void setupGame() {
        initTestServiceDecorator.setInitTestService(new CleanupTest.InitTestServiceForTest());
    }

    @Test
    public void cleanupWhenPassingTurns() {
        // Battlefields is
        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(HUATLIS_SNUBHORN).hasDamage(1);
        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(HUATLIS_SNUBHORN).isTapped();

        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(GRAZING_WHIPTAIL).hasSummoningSickness();
        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(GRAZING_WHIPTAIL).hasPowerAndToughness("4/5");

        browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(AIR_ELEMENTAL).hasDamage(1);
        browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(AIR_ELEMENTAL).isTapped();

        // Phase is
        browser.player1().getPhaseHelper().is(M1, PLAYER);

        // When Player1 clicks continue
        browser.player1().getActionHelper().clickContinue();

        // Phase is
        browser.player1().getPhaseHelper().is(M2, PLAYER);

        // When Player1 clicks continue
        browser.player1().getActionHelper().clickContinue();

        // Phase is
        browser.player1().getPhaseHelper().is(ET, OPPONENT);
        browser.player2().getPhaseHelper().is(ET, PLAYER);
        browser.player2().getActionHelper().clickContinue();
        browser.player1().getPhaseHelper().is(UP, OPPONENT);

        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(HUATLIS_SNUBHORN).doesNotHaveDamage();
        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(HUATLIS_SNUBHORN).isTapped();

        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(GRAZING_WHIPTAIL).hasSummoningSickness();
        browser.player1().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(GRAZING_WHIPTAIL).hasPowerAndToughness("3/4");

        browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(AIR_ELEMENTAL).isNotTapped();
        browser.player2().getBattlefieldHelper(PLAYER, SECOND_LINE).getFirstCard(AIR_ELEMENTAL).doesNotHaveDamage();
    }

    static class InitTestServiceForTest extends InitTestService {
        @Override
        public void initGameStatus(GameStatus gameStatus) {
            // Current Player
            addCardToCurrentPlayerLibrary(gameStatus, ISLAND);

            addCardToCurrentPlayerBattlefield(gameStatus, HUATLIS_SNUBHORN);
            addCardToCurrentPlayerBattlefield(gameStatus, GRAZING_WHIPTAIL);

            CardInstance huatlisShubhorn = gameStatus.getCurrentPlayer().getBattlefield().getCards().get(0);
            huatlisShubhorn.getModifiers().setTapped(TAPPED);
            huatlisShubhorn.getModifiers().dealDamage(1);

            CardInstance grazingWhiptail = gameStatus.getCurrentPlayer().getBattlefield().getCards().get(1);
            grazingWhiptail.getModifiers().setSummoningSickness(true);
            grazingWhiptail.getModifiers().addExtraPowerToughnessUntilEndOfTurn(powerToughness("1/1"));

            // Non Current Player
            addCardToNonCurrentPlayerLibrary(gameStatus, MOUNTAIN);

            addCardToNonCurrentPlayerBattlefield(gameStatus, AIR_ELEMENTAL);
            CardInstance nestRobber = gameStatus.getNonCurrentPlayer().getBattlefield().getCards().get(0);
            nestRobber.getModifiers().dealDamage(1);
            nestRobber.getModifiers().setTapped(TAPPED);
        }
    }
}
