package com.yaer.firenze.texas_poker

import com.yaer.firenze.texas_poker.request.ActionRequest
import com.yaer.firenze.texas_poker.request.InitGameRequest
import jdk.nashorn.internal.ir.annotations.Ignore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyList
import org.mockito.kotlin.*

internal class TaxasPokerServiceTest {
    private val pokerRepository: PokerRepository = mock()
    private val pokerService = TaxasPokerService(pokerRepository)

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
        whenever(pokerRepository.retrieveCardsByName(any())).thenReturn(listOf(Cards.Dimand_1, Cards.Dimand_J))
        whenever(pokerRepository.retrieveCurrentRound()).thenReturn(Round(players))

        pokerService.initGame(initGameRequest)

        verify(pokerRepository, times(1)).save(eq(player1), anyList())
        verify(pokerRepository, times(1)).save(eq(player2), anyList())

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
        whenever(pokerRepository.retrieveCurrentRound()).thenReturn(Round(players))

        pokerService.initGame(initGameRequest)

        verify(pokerRepository, timeout(1)).initRound(players)
        val currentRound = pokerService.retrieveCurrentRoundStatus()
        verify(pokerRepository, timeout(1)).retrieveCurrentRound()
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
    @Ignore
    internal fun rule_bigBlind_bet_two_times_larger_then_smallBlind() {
        val actionRequest = ActionRequest(Action.BET, 2)
        val round = pokerService.takeAction(actionRequest)

        assertThat(round.roundName).isEqualTo(RoundName.PRE_FLOP)
        assertThat(round.actionCompletedPlayer?.role).isEqualTo(Role.BIG_BLIND)
        val pot = pokerService.retrievePotStatus()
        assertThat(pot.chips).isEqualTo(4)
    }
}