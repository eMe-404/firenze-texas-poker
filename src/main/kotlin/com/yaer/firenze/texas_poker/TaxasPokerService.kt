package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.request.ActionRequest
import com.yaer.firenze.texas_poker.request.InitGameRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class TaxasPokerService {
    lateinit var pot: Pot
    lateinit var leftCards: Stack<Cards>
    lateinit var roundDetails: Round
    lateinit var playerCardsMap: HashMap<Player, List<Cards>>
    lateinit var playerInfo: HashMap<String, Player>


    fun takeAction(actionRequest: ActionRequest): Round {
        val previousRound = roundDetails.copy()
        roundDetails.actionRequiredPlayers = listOf(previousRound.actionCompletedPlayer!!)
        roundDetails.actionTakingPlayer = previousRound.actionRequiredPlayers[0]
        roundDetails.actionCompletedPlayer = previousRound.actionTakingPlayer
        if (actionRequest.action == Action.BET) {
            pot.chips += actionRequest.chips
        }
        return roundDetails
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
        roundDetails = Round(players)
        playerCardsMap = HashMap()
        playerInfo = HashMap()
        saveAllPlayers(players)
    }

    private fun saveAllPlayers(players: List<Player>) {
        players.forEach {
            val defaultCards = generateBasic2Cards()
            playerCardsMap[it] = defaultCards
            playerInfo[it.name] = it
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
        val player = playerInfo[playerName]
        return playerCardsMap[player]!!
    }

    fun retrieveCurrentRoundStatus(): Round {
        return roundDetails
    }
}