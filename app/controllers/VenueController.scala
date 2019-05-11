package controllers

import game.{Database, TransactionsController}
import javax.inject.Inject
import model.{Player, Venue}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

class VenueController  @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  implicit val venueWrites: OWrites[Venue] = Json.writes[Venue]

  implicit val venueReads: Reads[Venue] = (
    (__ \ "name").read[String] and
    (__ \ "price").read[Int]
  )(Venue.apply(null, _, _))

  implicit val playerIdReads: Reads[String] = (__ \ "playerId").read[String]

  def getVenues() = Action { implicit request  =>
    Ok(Json.toJson(Database.venues))
  }

  def putVenue(id: String) = Action { implicit request =>
    val venue = Json.fromJson[Venue](request.body.asJson.get).get
    val newVenue: Venue = Venue (
      id = id,
      name = venue.name,
      price = venue.price
    )

    val venueToUpdate: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == id) else None
    if (venueToUpdate.isDefined)
      Database.venues = Database.venues.filter(x => x.id != id)
    Database.venues = newVenue :: Database.venues

    Ok(id)
  }

  def deleteVenue(id: String) = Action { implicit  request =>
    var venueToDeleteId = id
    val venueToDelete: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == id) else None
    if (venueToDelete.isDefined)
      Database.venues = Database.venues.filter(_ != venueToDelete.get)
    else venueToDeleteId = s"There is no venue with the following id: $id "
    Ok(venueToDeleteId)
  }

  def buyVenue(venueId: String) = Action { implicit request =>
    val playerId: String = playerIdReads.reads(request.body.asJson.get).get
    val buyer: Option[Player] = if (Database.players.nonEmpty) Database.players.find(_.playerId == playerId) else None
    val venue: Option[Venue] = if (Database.venues.nonEmpty) Database.venues.find(_.id == venueId) else None
    val indexOfVenue: Option[Int] = if (venue.isDefined) Some(Database.venues.indexOf(venue.get)) else None

    var message = ""
    if (buyer.isEmpty || venue.isEmpty) {
      if (buyer.isEmpty)
        message = s"There is no player with the following id: $playerId"
      if (venue.isEmpty)
        message = s"There is no venue with the following id: $venueId "
    }
    else if (TransactionsController.isRichEnough(buyer.get, venue.get)) {
      buyer.get.money = buyer.get.money - venue.get.price

      val updatedVenue = Venue (
        name = venue.get.name,
        price = venue.get.price,
        id = venue.get.id,
        owner = Some(playerId)
      )

      Database.venues = Database.venues.filter(x => x.id != venueId)
      Database.venues = updatedVenue :: Database.venues

      message = venue.get.name + s" was bought by $playerId for " + venue.head.price
    }
    else {
      message = s"$playerId can't afford " + venue.get.name
    }

    Ok(message)
  }

  def getOwnerOrNull(owner: Option[Player]): String = {
    if (owner.isDefined) return owner.get.playerId
    null
  }
}
