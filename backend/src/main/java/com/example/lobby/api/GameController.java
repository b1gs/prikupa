package com.example.lobby.api;

import com.example.lobby.domain.Game;
import com.example.lobby.domain.Player;
import com.example.lobby.domain.Room;
import com.example.lobby.enums.TurnType;
import com.example.lobby.messaging.ChatMessage;
import com.example.lobby.messaging.TurnMessage;
import com.example.lobby.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final PlayerService playerService;
    private final GameService gameService;

    @MessageMapping("{roomId}/startGame")
    @Transactional
    public void startGame(@Payload ChatMessage message, Principal principal, @DestinationVariable Long roomId) throws JsonProcessingException {
        Room room = roomService.getRoom(roomId);
        if (!principal.getName().equals(room.getOwner().getUsername())) {
            throw new IllegalArgumentException("You are not the owner");
        }
        for (Player p : room.getPlayers()) {
            if (!p.isReady()) {
                throw new IllegalArgumentException("Not all playerTurnMap is ready");
            }
        }
        Set<Player> players = room.getPlayers();
        Game game = gameService.create(players, room);

        for (Player p : players) {
            message.setUsername("CARDS_MESSAGE");
            message.setCurrentTurnPlayerNumber(game.getCurrentTurnPlayerNumber());
            message.setMessage(objectMapper.writeValueAsString(p.getPlayerCards()));
            simpMessagingTemplate.convertAndSend("/user/" + p.getUsername() + "/exchange/amq.direct/chat.message", message);
        }
    }

    @MessageMapping("{roomId}/turn")
    @SendTo("topic/{roomId}/turn")
    public void turn(@DestinationVariable Long roomId, @Payload TurnMessage message, Principal principal) {
        if (gameService.isPlayerTurn(roomId, message)) {
            if (TurnType.TURN == message.getTurnType()){
                gameService.makeTurn(message);
            }else {
                gameService.pickUpCards(message);
            }
        } else {
            throw new IllegalArgumentException("It is not you turn now!! Player: " + principal.getName());
        }
    }

}