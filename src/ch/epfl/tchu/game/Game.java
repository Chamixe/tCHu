package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * the main unwinding of the game
 *
 * @author Mamoun Chami (325917)
 * @author Ismael Berrada (327482)
 */

public final class Game {

    private Game() {
    }

    /**
     * the method that allows the game to advance
     *
     * @param players
     * @param playerNames
     * @param tickets
     * @param rng
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        /**
         * informs the players of the progress of the game
         */
        Map<PlayerId, Info> playerInfos = Map.of(PlayerId.PLAYER_1, new Info(playerNames.get(PlayerId.PLAYER_1)),
                PlayerId.PLAYER_2, new Info(playerNames.get(PlayerId.PLAYER_2)));

        /**
         * the beginning of the game : all the initialisations;the players, who plays first, the shuffled deck, the initial tickets.
         */
        //Beginning of the game
        PlayerId.ALL.forEach(player -> players.get(player).initPlayers(player, playerNames));
        GameState gameState = GameState.initial(tickets, rng);
        allReceiveInfo(players, playerInfos.get(gameState.currentPlayerId()).willPlayFirst());
        for (PlayerId playerId : PlayerId.ALL) {
            players.get(playerId).setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            allReceiveInfo(players, playerInfos.get(playerId).drewTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }
        allUpdateState(gameState, players);
        for (PlayerId playerId : PlayerId.ALL)
            gameState = gameState.withInitiallyChosenTickets(playerId, players.get(playerId).chooseInitialTickets());

        for (PlayerId playerId : PlayerId.ALL)
            allReceiveInfo(players, playerInfos.get(playerId).keptTickets(gameState.playerState(playerId).ticketCount()));

        //the middle of the game with the method playRound for each turn.
        //Midgame
        int lastTurnCounter = 0;
        while (lastTurnCounter <= 2) {
            gameState = playRound(gameState, playerInfos, players, rng);
            if (gameState.lastTurnBegins() || gameState.lastPlayer() != null)
                ++lastTurnCounter;
            gameState = gameState.forNextTurn();
        }
        //Endgame
        GameState finalGame = gameState;

        //Announces the longest trail
        Map<PlayerId, Trail> longestTrailMap = Map.of(PlayerId.PLAYER_1, Trail.longest(gameState.playerState(PlayerId.PLAYER_1).routes()),
                PlayerId.PLAYER_2, Trail.longest(gameState.playerState(PlayerId.PLAYER_2).routes()));
        PlayerId longestPlayerId;
        if (longestTrailMap.get(PlayerId.PLAYER_1).length() > longestTrailMap.get(PlayerId.PLAYER_2).length())
            longestPlayerId = PlayerId.PLAYER_1;
        else {
            if (longestTrailMap.get(PlayerId.PLAYER_1).length() < longestTrailMap.get(PlayerId.PLAYER_2).length())
                longestPlayerId = PlayerId.PLAYER_2;
            else longestPlayerId = null;
        }


        if (longestPlayerId == null)
            PlayerId.ALL.forEach(playerId -> players.get(playerId).receiveInfo(playerInfos.get(playerId).getsLongestTrailBonus(longestTrailMap.get(playerId))));
        else {
            Trail longestTrail = Trail.longest(gameState.playerState(longestPlayerId).routes());
            allReceiveInfo(players, playerInfos.get(longestPlayerId).getsLongestTrailBonus(longestTrail));
        }

        //Puts the points in the map
        Map<PlayerId, Integer> playerPoints = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(playerId -> playerPoints.put(playerId, finalGame.playerState(playerId).finalPoints() +
                (longestPlayerId == playerId || longestPlayerId == null ? Constants.LONGEST_TRAIL_BONUS_POINTS : 0)));

        //Announce the winner
        allUpdateState(gameState, players);
        int pointsPlayer1 = playerPoints.get(PlayerId.PLAYER_1);
        int pointsPlayer2 = playerPoints.get(PlayerId.PLAYER_2);

        if (pointsPlayer1 > pointsPlayer2)
            allReceiveInfo(players, playerInfos.get(PlayerId.PLAYER_1).won(pointsPlayer1, pointsPlayer2));
        if (pointsPlayer1 < pointsPlayer2)
            allReceiveInfo(players, playerInfos.get(PlayerId.PLAYER_2).won(pointsPlayer2, pointsPlayer1));
        if (pointsPlayer1 == pointsPlayer2)
            allReceiveInfo(players, Info.draw(new ArrayList<>(playerNames.values()), pointsPlayer1));

    }


    private static void allReceiveInfo(Map<PlayerId, Player> players, String info) {
        PlayerId.ALL.forEach(player -> players.get(player).receiveInfo(info));
    }

    private static void allUpdateState(GameState gameState, Map<PlayerId, Player> players) {
        PlayerId.ALL.forEach(playerId -> players.get(playerId).updateState(gameState, gameState.playerState(playerId)));
    }

    /**
     * this method represents a turn of the player
     *
     * @param gameState
     * @param playerInfos
     * @param players
     * @param rng
     * @return a new gameState after the player ended his turn
     */
    private static GameState playRound(GameState gameState, Map<PlayerId, Info> playerInfos, Map<PlayerId, Player> players,
                                       Random rng) {
        Player currentPlayer = players.get(gameState.currentPlayerId());
        Info currentPlayerInfo = playerInfos.get(gameState.currentPlayerId());
        allReceiveInfo(players, currentPlayerInfo.canPlay());
        allUpdateState(gameState, players);
        Player.TurnKind kind = currentPlayer.nextTurn();
        /**
         * at the beginning of his turn, the player has the choice between three different actions :
         */
        switch (kind) {
            /**
             * if he draws tickets he has to keep some of them
             */
            case DRAW_TICKETS:
                SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT));
                allReceiveInfo(players, currentPlayerInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));
                gameState = gameState.withChosenAdditionalTickets(gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT), chosenTickets);
                allReceiveInfo(players, currentPlayerInfo.keptTickets(chosenTickets.size()));
                break;

            /**
             * if he draws cards; if he draws the last card of the deck, the deck is recreated with the current discard.
             */
            case DRAW_CARDS:
                for (int i = 0; i < 2; i++) {
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                    if (i == 1) allUpdateState(gameState, players);
                    int cardSlot = currentPlayer.drawSlot();
                    if (cardSlot == Constants.DECK_SLOT) {
                        allReceiveInfo(players, currentPlayerInfo.drewBlindCard(gameState.topCard()));
                        gameState = gameState.withBlindlyDrawnCard();
                    } else {
                        allReceiveInfo(players, currentPlayerInfo.drewVisibleCard(gameState.cardState().faceUpCard(cardSlot)));
                        gameState = gameState.withDrawnFaceUpCard(cardSlot);
                    }
                }

                break;

            /**
             * if he claims route; first we see if it is an Overground route or an Underground route
             */
            case CLAIM_ROUTE:

                Route route = currentPlayer.claimedRoute();
                SortedBag<Card> initialClaimCards = currentPlayer.initialClaimCards();
                gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                if (route.level() == Route.Level.OVERGROUND) {
                    allReceiveInfo(players, currentPlayerInfo.claimedRoute(route, initialClaimCards));
                    gameState = gameState.withClaimedRoute(route, initialClaimCards);
                } else {
                    allReceiveInfo(players, currentPlayerInfo.attemptsTunnelClaim(route, initialClaimCards));
                    SortedBag.Builder<Card> additionalCardsBuilder = new SortedBag.Builder<>();

                    // extraction of the 3 cards at the top of the deck
                    for (int i = 0; i < 3; i++) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        additionalCardsBuilder.add(gameState.topCard());
                        gameState = gameState.withoutTopCard();
                    }
                    SortedBag<Card> additionalCards = additionalCardsBuilder.build();

                    // number of additional cards that the player will have to put down to seize the tunnel
                    int additionalCardsCount = route.additionalClaimCardsCount(initialClaimCards, additionalCards);
                    allReceiveInfo(players, currentPlayerInfo.drewAdditionalCards(additionalCards, additionalCardsCount));
                    if (additionalCardsCount >= 1) {
                        // the player chooses which cards he wants to add
                        List<SortedBag<Card>> possibleAdditionalCards = gameState.currentPlayerState().possibleAdditionalCards(additionalCardsCount, initialClaimCards);
                        SortedBag<Card> chosenCards = SortedBag.of();
                        if (!possibleAdditionalCards.isEmpty())
                            chosenCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);
                        if (chosenCards.isEmpty())
                            allReceiveInfo(players, currentPlayerInfo.didNotClaimRoute(route));
                        else {
                            SortedBag<Card> newCards = initialClaimCards.union(chosenCards);
                            gameState = gameState.withClaimedRoute(route, newCards);
                            allReceiveInfo(players, currentPlayerInfo.claimedRoute(route, newCards));
                        }
                    } else {
                        gameState = gameState.withClaimedRoute(route, initialClaimCards);
                        allReceiveInfo(players, currentPlayerInfo.claimedRoute(route, initialClaimCards));
                    }
                }
                gameState = gameState.withMoreDiscardedCards(initialClaimCards);

                break;
        }
        if (gameState.lastTurnBegins())
            allReceiveInfo(players, currentPlayerInfo.lastTurnBegins(gameState.playerState(gameState.currentPlayerId()).carCount()));
        return gameState;
    }

}