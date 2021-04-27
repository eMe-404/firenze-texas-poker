package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.request.InitGameRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaxasPokerService(private val pokerRepository: PokerRepository) {
    private lateinit var pot: Pot
    private lateinit var leftCards: Stack<Cards>


    fun takeAction(action: Action): Round {
        TODO("Not yet implemented")
    }

    fun retrievePotStatus(): Pot {
        return pot
    }

    fun initGame(initGameRequest: InitGameRequest) {
        val players = initGameRequest.players

        if (players.size < 2) {
            throw IllegalArgumentException("this game need at least 2 players to start!")

        }
        initGameCards()
        pot = Pot()
        saveAllPlayers(players)
    }

    private fun saveAllPlayers(players: List<Player>) {
        players.forEach {
            val defaultCards = generateBasic2Cards()
            pokerRepository.save(it, defaultCards)
        }
    }

    private fun initGameCards() {
        val cardsStack = Stack<Cards>()
        val allAvailableCards = Cards.values()
        allAvailableCards.shuffle()
        cardsStack.addAll(allAvailableCards)
        leftCards = cardsStack
    }

    private fun generateBasic2Cards(): List<Cards> {
        return listOf(leftCards.pop(), leftCards.pop())
    }

    fun retrievePlayerCards(playerName: String): List<Cards> {
        return pokerRepository.retrieveCardsByName(playerName)
    }
}