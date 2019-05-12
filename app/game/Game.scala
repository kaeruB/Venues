package game

import model.{Player, Venue}

object Game {
  def hasEnoughMoney(player: Player, venue: Venue): Boolean = {
    if (player.money < venue.price) return false
    true
  }

  def addVenue(venueId: String, venueWithoutId: Venue): Unit = {
    this.synchronized {
      val newVenue: Venue = Venue(
        id = venueId,
        name = venueWithoutId.name,
        price = venueWithoutId.price
      )

      val venueToUpdate: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == venueId) else None
      if (venueToUpdate.isDefined)
        Database.venues = Database.venues.filter(x => x.id != venueId)
      Database.venues = newVenue :: Database.venues
    }
  }

  def deleteVenue(id: String): String = {
    val venueToDelete: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == id) else None
    if (venueToDelete.isDefined) {
      Database.venues = Database.venues.filter(_ != venueToDelete.get)
      id
    }
    else s"There is no venue with the following id: $id "
  }

  def buyVenue(venueId: String, playerId: String): String = {
    var message = ""

    this.synchronized {
      val buyer: Option[Player] = if (Database.players.nonEmpty) Database.players.find(_.playerId == playerId) else None
      val venue: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == venueId) else None
      val indexOfVenue: Option[Int] = if (venue.isDefined) Some(Database.venues.indexOf(venue.get)) else None

      if (buyer.isEmpty || venue.isEmpty) {
        if (buyer.isEmpty)
          message = s"There is no player with the following id: $playerId"
        if (venue.isEmpty)
          message = s"There is no venue with the following id: $venueId "
      }
      else if (hasEnoughMoney(buyer.get, venue.get) && venue.get.owner.isEmpty) {
        buyer.get.money = buyer.get.money - venue.get.price

        val updatedVenue = Venue(
          name = venue.get.name,
          price = venue.get.price,
          id = venue.get.id,
          owner = Some(playerId)
        )

        Database.venues = Database.venues.filter(x => x.id != venueId)
        Database.venues = updatedVenue :: Database.venues

        message = venue.get.name + s" was bought by $playerId for " + venue.head.price
      }
      else if (venue.get.owner.isDefined) {
        message = s"$venueId is already bought by " + venue.get.owner.get
      }
      else {
        message = s"$playerId can't afford " + venue.get.name
      }
    }
    message
  }
}
