package com.yaer.firenze.texas_poker

import org.springframework.stereotype.Repository

@Repository
class PokerRepository {
    private lateinit var currentRound: Round
    private lateinit var playerCardsMap: HashMap<Player, List<Cards>>

//    fun retrieveCardsByName(playerName: String): List<Cards> {
//        return playerCardsMap[playerName]!!
//    }
//
//    fun save(player: Player, defaultCards: List<Cards>) {
//        playerCardsMap[player] = defaultCards
//    }
//
//    fun initRound(players: List<Player>) {
//        currentRound = Round(players)
//    }
//
//    fun retrieveRound(): Round {
//        return currentRound
//    }

}
