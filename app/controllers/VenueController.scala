package controllers

import game.{Database, TransactionsController}
import javax.inject.Inject
import model.{Player, Venue}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

class VenueController  @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  implicit val venueWrites = new Writes[Venue] {
    def writes(venue : Venue) = Json.obj(
      "id" -> venue.id,
        "name" -> venue.name,
        "price" -> venue.price,
        "owner" -> getOwnerOrNull(venue.owner)
    )
  }

  implicit val venueReads: Reads[Venue] = (
    (__ \ "name").read[String] and
    (__ \ "price").read[Int]
  )(Venue.apply _)

  implicit val playerIdReads: Reads[String] = (__ \ "playerId").read[String]

  def getVenues() = Action { implicit request  =>
    Ok(Json.toJson(Database.venues))
  }

  def putVenue(id: String) = Action { implicit request =>
    val venue = Json.fromJson[Venue](request.body.asJson.get).get
    venue.id = id

    if (venueExists(id)) Database.venues = Database.venues.filter(_.id != id)
    Database.venues = venue :: Database.venues

    Ok(venue.id)
  }

  def deleteVenue(id: String) = Action { implicit  request =>
    var venueToDeleteId = id
    if (venueExists(id)) Database.venues = Database.venues.filter(_.id != id)
    else venueToDeleteId = s"There is no venue with the following id: $id "
    Ok(venueToDeleteId)
  }

  def buyVenue(venueId: String) = Action { implicit request =>
    val playerId: String = playerIdReads.reads(request.body.asJson.get).get
    val player: List[Player] = if (Database.players.nonEmpty) Database.players.filter(_.playerId == playerId) else List.empty
    val venue: List[Venue] = if (Database.venues.nonEmpty) Database.venues.filter(_.id == venueId) else List.empty

    var message = ""
    if (player.isEmpty || venue.isEmpty) {
      if (player.isEmpty)
        message = s"There is no player with the following id: $playerId"
      if (venue.isEmpty)
        message = s"There is no venue with the following id: $venueId "
    }
    else if (TransactionsController.isRichEnough(player.head, venue.head)) {
      player.head.money = player.head.money - venue.head.price
      venue.head.owner = Option(player.head)
      message = venue.head.name + s" was bought by $playerId for " + venue.head.price
    }
    else {
      message = s"$playerId can't afford " + venue.head.name
    }

    Ok(message)
  }

  def getOwnerOrNull(owner: Option[Player]): String = {
    if (owner.isDefined) return owner.get.playerId
    null
  }

  def venueExists(id: String): Boolean = {
    if (Database.venues.exists(_.id == id)) return true
    false
  }
}
