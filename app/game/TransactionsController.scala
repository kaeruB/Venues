package game

import model.{Player, Venue}

object TransactionsController {
  def isRichEnough(player: Player, venue: Venue): Boolean = {
    if (player.money < venue.price) return false
    true
  }
}
