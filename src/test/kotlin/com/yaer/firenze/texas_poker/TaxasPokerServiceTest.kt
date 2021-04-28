package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.request.ActionRequest
import com.yaer.firenze.texas_poker.request.InitGameRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TaxasPokerServiceTest {
    private val pokerService = TaxasPokerService()

    @Test
    internal fun rule_there_are_at_lease_two_player_to_start_the_game() {
        val players = listOf(Player("A", role = null))
        val initGameRequest = InitGameRequest(players)

        assertThrows<IllegalArgumentException> { pokerService.initGame(initGameRequest) }
    }

    @Test
    internal fun rule_when_game_start_each_player_cat_get_two_basic_card() {
        val player1 = Player("One", null)
        val player2 = Player("Two", null)
        val players = listOf(player1, player2)
        val initGameRequest = InitGameRequest(players)

        pokerService.initGame(initGameRequest)

        val playerOneCards = pokerService.retrievePlayerCards("One")
        val playerTwoCards = pokerService.retrievePlayerCards("Two")
        assertThat(playerOneCards.size).isEqualTo(2)
        assertThat(playerTwoCards.size).isEqualTo(2)
    }

    @Test
    internal fun should_return_initial_round_status_when_init_game() {
        val player1 = Player("One", null)
        val player2 = Player("Two", null)
        val players = listOf(player1, player2)
        val initGameRequest = InitGameRequest(players)

        pokerService.initGame(initGameRequest)

        val currentRound = pokerService.retrieveCurrentRoundStatus()
        assertThat(currentRound.roundName).isEqualTo(RoundName.PRE_FLOP)
        assertThat(currentRound.actionTakingPlayer).isEqualTo(player1)
        assertThat(currentRound.actionRequiredPlayers).containsOnly(player2)
        assertThat(currentRound.performableActions).containsExactlyInAnyOrder(Action.BET)
    }

    @Test
    internal fun should_return_pot_status_when_init_game() {
        val player1 = Player("One", null)
        val player2 = Player("Two", null)
        val players = listOf(player1, player2)
        val initGameRequest = InitGameRequest(players)

        pokerService.initGame(initGameRequest)


        val pot = pokerService.retrievePotStatus()
        assertThat(pot.chips).isEqualTo(0)
    }

    @Test
    internal fun rule_bigBlind_bet_two_times_larger_then_smallBlind() {
        val player1 = Player("One", Role.SMALL_BLIND)
        val player2 = Player("Two", Role.BIG_BLIND)
        val player3 = Player("Three", null)
        val players = listOf(player1, player2, player3)
        val initGameRequest = InitGameRequest(players)
        pokerService.initGame(initGameRequest)
        val currentRound = Round(players)
        currentRound.actionCompletedPlayer = player1
        currentRound.actionTakingPlayer = player2
        currentRound.performableActions = listOf(Action.RAISE)
        currentRound.actionRequiredPlayers = listOf(player3)
        pokerService.roundDetails = currentRound
        pokerService.pot.chips = 2

        val actionRequest = ActionRequest(Action.BET, null)
        val round = pokerService.takeAction(actionRequest)

        assertThat(round.roundName).isEqualTo(RoundName.PRE_FLOP)
        val pot = pokerService.retrievePotStatus()
        assertThat(pot.chips).isEqualTo(6)
    }
}