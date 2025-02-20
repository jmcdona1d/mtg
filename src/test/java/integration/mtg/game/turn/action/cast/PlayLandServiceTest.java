package integration.mtg.game.turn.action.cast;

import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.CardInstanceFactory;
import com.aa.mtg.cards.sets.Dominaria;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.cast.PlayLandService;
import com.aa.mtg.game.turn.action.enter.EnterCardIntoBattlefieldService;
import integration.TestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.aa.mtg.cards.Cards.SWAMP;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CastTestConfiguration.class)
public class PlayLandServiceTest {

    @Autowired
    private PlayLandService playLandService;

    @Autowired
    private CardInstanceFactory cardInstanceFactory;

    @Autowired
    private EnterCardIntoBattlefieldService enterCardIntoBattlefieldService;
    
    @Autowired
    private TestUtils testUtils;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void playLand() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstance card = cardInstanceFactory.create(gameStatus, 100, SWAMP, "player-name");
        gameStatus.getPlayer1().getHand().addCard(card);

        // When
        playLandService.playLand(gameStatus, card.getId());

        // Then
        verify(enterCardIntoBattlefieldService).enter(gameStatus, card);
    }

    @Test
    public void playLandErrorNotALand() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstance card = cardInstanceFactory.create(gameStatus, 100, Dominaria.BEFUDDLE, "player-name");
        gameStatus.getPlayer1().getHand().addCard(card);

        // Expect
        thrown.expectMessage("Playing \"100 - Befuddle\" as land.");

        // When
        playLandService.playLand(gameStatus, card.getId());
    }

    @Test
    public void playLandMultipleLand() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstance card1 = cardInstanceFactory.create(gameStatus, 100, SWAMP, "player-name");
        gameStatus.getPlayer1().getHand().addCard(card1);
        CardInstance card2 = cardInstanceFactory.create(gameStatus, 101, SWAMP, "player-name");
        gameStatus.getPlayer1().getHand().addCard(card2);

        // When
        playLandService.playLand(gameStatus, card1.getId());

        // Then
        verify(enterCardIntoBattlefieldService).enter(gameStatus, card1);

        // Expect
        thrown.expectMessage("You already played a land this turn.");

        // When
        playLandService.playLand(gameStatus, card2.getId());
    }

    @Test
    public void playLandNotInMainPhase() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        gameStatus.getTurn().setCurrentPhase("FS");
        CardInstance card = cardInstanceFactory.create(gameStatus, 100, SWAMP, "player-name");
        gameStatus.getPlayer1().getHand().addCard(card);

        // Expect
        thrown.expectMessage("You can only play lands during main phases.");

        // When
        playLandService.playLand(gameStatus, card.getId());
    }
}