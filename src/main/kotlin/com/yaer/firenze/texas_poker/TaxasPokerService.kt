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
        if (roundDetails.roundName == RoundName.PRE_FLOP) {
            if (actionRequest.action == Action.FOLD) {
                throw IllegalArgumentException("in ${RoundName.PRE_FLOP} can not able to take ${Action.FOLD}")
            }


            if (roundDetails.actionTakingPlayer.role == Role.BIG_BLIND) {
                pot.chips += pot.chips * 2
            }
        }
        return updateRoundDetails(actionRequest)
    }

    private fun updateRoundDetails(actionRequest: ActionRequest): Round {
        val oldRound = roundDetails
        val updatedRound = Round(playerCardsMap.keys.toList())
        val oldRoundActionRequiredPlayers = oldRound.actionRequiredPlayers
        updatedRound.roundName = oldRound.roundName
        updatedRound.actionTakingPlayer = oldRoundActionRequiredPlayers[0]
        val subList = mutableListOf<Player>()
        subList.addAll(oldRoundActionRequiredPlayers.subList(1, oldRoundActionRequiredPlayers.size))
        subList.add(oldRound.actionCompletedPlayer!!)
        updatedRound.actionRequiredPlayers = subList
        updatedRound.actionCompletedPlayer = oldRound.actionTakingPlayer
        if (oldRound.eligibleForFold) {
            updatedRound.performableActions = listOf(Action.CHECK, Action.RAISE, Action.FOLD, Action.CALL)
        } else {
            updatedRound.performableActions = listOf(Action.RAISE, Action.FOLD, Action.CALL)
        }
        this.roundDetails = updatedRound
        this.pot.chips += actionRequest.chips ?: 0
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