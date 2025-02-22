package application;

import application.browser.MtgBrowser;
import com.aa.mtg.cards.CardInstanceFactory;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

import static com.aa.mtg.game.player.PlayerType.OPPONENT;
import static com.aa.mtg.game.player.PlayerType.PLAYER;
import static com.aa.mtg.game.turn.phases.Main1Phase.M1;
import static com.aa.mtg.game.turn.phases.UpkeepPhase.UP;
import static java.lang.Integer.parseInt;

public abstract class AbstractApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplicationTest.class);

    private static AtomicInteger GAME_ID = new AtomicInteger(0);

    @LocalServerPort
    private int port;

    protected MtgBrowser browser;

    public abstract void setupGame();

    @Before
    public void setupWithRetries() {
        RuntimeException lastException = null;

        for (int i = 0; i < getNumOfRetries(); i++) {
            try {
                lastException = null;
                LOGGER.info("Setup (attempt {})", i);
                setup();
                LOGGER.info("Setup succeeded");
                break;

            } catch (RuntimeException e) {
                sleep5Secs();
                lastException = e;
                LOGGER.info("Setup failed: {}", e.getMessage());
            }
        }

        if (lastException != null) {
            throw lastException;
        }
    }

    private void setup() {
        setupGame();

        // When player1 joins the game is waiting for opponent
        browser = new MtgBrowser(port, GAME_ID.incrementAndGet());
        browser.getMessageHelper().hasMessage("Waiting for opponent...");

        // When player2 joins the game both players see the table with the cards
        browser.openSecondTab();

        // Make sure player1 is Pippo and player2 is Pluto
        browser.player1().getPlayerInfoHelper(PLAYER).toHaveName();
        browser.player2().getPlayerInfoHelper(PLAYER).toHaveName();
        if (browser.player1().getPlayerInfoHelper(PLAYER).getPlayerName().equals("Pluto")) {
            browser.swapTabs();
        }
        browser.player1().getPlayerInfoHelper(PLAYER).toHaveName("Pippo");
        browser.player2().getPlayerInfoHelper(PLAYER).toHaveName("Pluto");

        // Message disappears
        browser.player1().getMessageHelper().hasNoMessage();
        browser.player2().getMessageHelper().hasNoMessage();

        // Status and Phase are
        browser.player1().getPhaseHelper().is(UP, PLAYER);
        browser.player1().getStatusHelper().hasMessage("Play any instant or ability or continue (SPACE).");
        browser.player2().getPhaseHelper().is(UP, OPPONENT);
        browser.player2().getStatusHelper().hasMessage("Wait for opponent to perform its action...");

        // Player1 continues
        browser.player1().getActionHelper().clickContinue();

        // Status and Phase are
        browser.player1().getPhaseHelper().is(UP, OPPONENT);
        browser.player1().getStatusHelper().hasMessage("Wait for opponent to perform its action...");
        browser.player2().getPhaseHelper().is(UP, PLAYER);
        browser.player2().getStatusHelper().hasMessage("Play any instant or ability or continue (SPACE).");

        // Player2 continues
        browser.player2().getActionHelper().clickContinue();

        // Status is
        browser.player1().getPhaseHelper().is(M1, PLAYER);
        browser.player1().getStatusHelper().hasMessage("Play any spell or ability or continue (SPACE).");
        browser.player2().getPhaseHelper().is(M1, OPPONENT);
        browser.player2().getStatusHelper().hasMessage("Wait for opponent to perform its action...");
    }

    @After
    public void cleanup() {
        browser.dumpContent();
        browser.close();
    }

    @Configuration
    public static class InitGameTestConfiguration {
        @Bean
        public InitTestServiceDecorator initTestServiceDecorator(CardInstanceFactory cardInstanceFactory) {
            InitTestServiceDecorator initTestServiceDecorator = new InitTestServiceDecorator();
            initTestServiceDecorator.setCardInstanceFactory(cardInstanceFactory);
            return initTestServiceDecorator;
        }
    }

    @SneakyThrows
    private void sleep5Secs() {
        Thread.sleep(3000);
    }

    private int getNumOfRetries() {
        try {
            return parseInt(System.getProperty("test.gameSetup.retries"));
        } catch (Exception e) {
            return 10;
        }
    }
}
