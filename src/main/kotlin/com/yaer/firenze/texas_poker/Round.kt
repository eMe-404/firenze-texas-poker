package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.Action.BET

class Round(playersWithOrder: List<Player>) {
    var roundName = RoundName.PRE_FLOP
    var actionTakingPlayer: Player = playersWithOrder[0]
    var actionRequiredPlayers: List<Player> = playersWithOrder.subList(1, playersWithOrder.size)
    var actionCompletedPlayer: Player? = null
    var performableActions: List<Action> = listOf(BET)
}

