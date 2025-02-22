package integration.mtg.game.turn.action.life;

import com.aa.mtg.cards.CardInstance;
import com.aa.mtg.cards.CardInstanceFactory;
import com.aa.mtg.cards.sets.CoreSet2019;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.turn.action.life.AddXLifeAction;
import integration.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static com.aa.mtg.cards.ability.Abilities.GAIN_3_LIFE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LifeTestConfiguration.class)
public class AddXLifeActionTest {

    @Autowired
    private AddXLifeAction addXLifeAction;

    @Autowired
    private CardInstanceFactory cardInstanceFactory;

    @Autowired
    private TestUtils testUtils;

    @Test
    public void addLifeToPlayer() {
        // Given
        GameStatus gameStatus = testUtils.testGameStatus();
        CardInstance cardInstance = cardInstanceFactory.create(gameStatus, 1, CoreSet2019.LICHS_CARESS, "player-name", "player-name");

        // When
        addXLifeAction.perform(cardInstance, gameStatus, GAIN_3_LIFE);

        // Then
        assertThat(gameStatus.getPlayerByName("player-name").getLife()).isEqualTo(23);
    }
}