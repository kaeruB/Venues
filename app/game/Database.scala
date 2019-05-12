package game

import model.{Player, Venue}

object Database {
  var venues : List[Venue] = List.empty
  var players : List[Player] = populatePlayersList()

  def populatePlayersList() : List[Player] = {
    val p1 = Player("player1")
    val p2 = Player("player2")
    p1.money = 500
    p2.money = 2000
    List(p1, p2)
  }
}
