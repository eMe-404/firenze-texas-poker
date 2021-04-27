package com.yaer.firenze.texas_poker

import org.springframework.stereotype.Repository

@Repository
class PokerRepository {
    private lateinit var playerCardsMap: HashMap<String, List<Cards>>

    fun retrieveCardsByName(playerName: String): List<Cards> {
        return playerCardsMap[playerName]!!
    }

    fun save(player: Player, defaultCards: List<Cards>) {
        playerCardsMap[player.name] = defaultCards
    }

}
