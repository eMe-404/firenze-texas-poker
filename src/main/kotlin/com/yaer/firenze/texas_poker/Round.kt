package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.Action.BET

class Round(val sortedPlayers: List<Player>) {
    var roundName = RoundName.PRE_FLOP
    var actionTakingPlayer: Player = sortedPlayers[0]
    var actionRequiredPlayers: List<Player> = sortedPlayers.subList(1, sortedPlayers.size)
    var actionCompletedPlayer: Player? = null
    var performableActions: List<Action> = listOf(BET)
}

