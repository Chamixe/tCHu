package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

public interface Player {

    public enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;

        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }

    abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    //, qui est appelée au début de la partie pour communiquer au joueur sa propre identité ownId, ainsi que les noms des différents joueurs, le sien inclus, qui se trouvent dans playerNames

    abstract void receiveInfo(String info);
    //, qui est appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie ; cette information est donnée sous la forme d'une chaîne de caractères, généralement produite par la classe Info définie à l'étape 3,

    abstract void updateState(PublicGameState newState, PlayerState ownState);
    //, qui est appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique de ce nouvel état, newState, ainsi que de son propre état, ownState

    abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);
    //, qui est appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués,

    abstract SortedBag<Ticket> chooseInitialTickets();
    //, qui est appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui a distribué initialement (via la méthode précédente) il garde,

    abstract TurnKind nextTurn();
    //, qui est appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour,

    abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    //, qui est appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie, afin de lui communiquer les billets tirés et de savoir lesquels il garde,

    abstract int drawSlot();
    //, qui est appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir d'où il désire les tirer : d'un des emplacements contenant une carte face visible — auquel cas la valeur retourne est comprise entre 0 et 4 inclus —, ou de la pioche — auquel cas la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1),

    abstract Route claimedRoute();
    //, qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route il s'agit,

    abstract SortedBag<Card> initialClaimCards();
    //, qui est appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir quelle(s) carte(s) il désire initialement utiliser pour cela,

    abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
    //, qui est appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles sont nécessaires, afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui étant passées en argument ; si le multiensemble retourné est vide, cela signifie que le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités.
}