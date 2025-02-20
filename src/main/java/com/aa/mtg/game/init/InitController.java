package com.aa.mtg.game.init;

import com.aa.mtg.game.deck.DeckRetrieverService;
import com.aa.mtg.game.event.Event;
import com.aa.mtg.game.event.EventSender;
import com.aa.mtg.game.init.test.InitTestService;
import com.aa.mtg.game.message.MessageEvent;
import com.aa.mtg.game.player.PlayerFactory;
import com.aa.mtg.game.security.SecurityHelper;
import com.aa.mtg.game.security.SecurityToken;
import com.aa.mtg.game.status.GameStatus;
import com.aa.mtg.game.status.GameStatusFactory;
import com.aa.mtg.game.status.GameStatusRepository;
import com.aa.mtg.game.status.GameStatusUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class InitController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitController.class);

    private final SecurityHelper securityHelper;
    private final EventSender eventSender;
    private final GameStatusFactory gameStatusFactory;
    private final PlayerFactory playerFactory;
    private final GameStatusUpdaterService gameStatusUpdaterService;
    private final InitTestService initTestService;
    private final GameStatusRepository gameStatusRepository;
    private final DeckRetrieverService deckRetrieverService;

    @Autowired
    public InitController(SecurityHelper securityHelper, EventSender eventSender, GameStatusFactory gameStatusFactory, PlayerFactory playerFactory, GameStatusUpdaterService gameStatusUpdaterService, GameStatusRepository gameStatusRepository, DeckRetrieverService deckRetrieverService, @Autowired(required = false) InitTestService initTestService) {
        this.securityHelper = securityHelper;
        this.eventSender = eventSender;
        this.gameStatusFactory = gameStatusFactory;
        this.playerFactory = playerFactory;
        this.gameStatusUpdaterService = gameStatusUpdaterService;
        this.gameStatusRepository = gameStatusRepository;
        this.deckRetrieverService = deckRetrieverService;
        this.initTestService = initTestService;
    }

    @MessageMapping("/game/init")
    void init(SimpMessageHeaderAccessor headerAccessor) {
        SecurityToken token = securityHelper.extractSecurityToken(headerAccessor);
        LOGGER.info("Init request received for sessionId '{}', gameId '{}'.", token.getSessionId(), token.getGameId());

        if (!gameStatusRepository.contains(token.getGameId())) {
            GameStatus gameStatus = gameStatusFactory.create(token.getGameId());
            String playerName = "Pippo";
            gameStatus.setPlayer1(playerFactory.create(token.getSessionId(), playerName));
            gameStatus.getPlayer1().getLibrary().addCards(deckRetrieverService.retrieveDeckForUser(token, playerName, gameStatus));
            gameStatus.getPlayer1().drawHand();
            gameStatusRepository.save(token.getGameId(), gameStatus);
            eventSender.sendToPlayer(gameStatus.getPlayer1(), new Event("INIT_WAITING_OPPONENT"));

        } else {
            GameStatus gameStatus = gameStatusRepository.getUnsecure(token.getGameId());
            if (gameStatus.getPlayer2() == null) {
                String playerName = "Pluto";
                gameStatus.setPlayer2(playerFactory.create(token.getSessionId(), playerName));
                gameStatus.getPlayer2().getLibrary().addCards(deckRetrieverService.retrieveDeckForUser(token, playerName, gameStatus));
                gameStatus.getPlayer2().drawHand();

                gameStatus.getTurn().init(gameStatus.getPlayer1().getName());

                eventSender.sendToPlayer(gameStatus.getPlayer1(), new Event("OPPONENT_JOINED"));

                if (initTestService != null && gameStatusRepository.getUnsecure(token.getGameId()).getPlayer2() != null) {
                    initTestService.initGameStatusForTest(gameStatusRepository.getUnsecure(token.getGameId()));
                }

                eventSender.sendToPlayer(gameStatus.getPlayer1(), new Event("INIT_PLAYER", InitPlayerEvent.createForPlayer(gameStatus.getPlayer1())));
                eventSender.sendToPlayer(gameStatus.getPlayer1(), new Event("INIT_OPPONENT", InitPlayerEvent.createForOpponent(gameStatus.getPlayer2())));

                eventSender.sendToPlayer(gameStatus.getPlayer2(), new Event("INIT_PLAYER", InitPlayerEvent.createForPlayer(gameStatus.getPlayer2())));
                eventSender.sendToPlayer(gameStatus.getPlayer2(), new Event("INIT_OPPONENT", InitPlayerEvent.createForOpponent(gameStatus.getPlayer1())));

                gameStatusUpdaterService.sendUpdateTurn(gameStatus);

            } else {
                eventSender.sendToPlayer(gameStatus.getPlayer2(), new Event("MESSAGE", new MessageEvent("Game is full.", true)));
            }
        }
    }

}
