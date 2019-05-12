package controllers

import game.{Database, Game}
import javax.inject.Inject
import model.Venue
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

  def getVenues = Action { implicit request  =>
    Ok(Json.toJson(Database.venues))
  }

  def putVenue(id: String) = Action { implicit request =>
    val venue = Json.fromJson[Venue](request.body.asJson.get).get
    Game.addVenue(id, venue)
    Ok(id)
  }

  def deleteVenue(id: String) = Action { implicit  request =>
    val venueToDeleteId: String = Game.deleteVenue(id)
    Ok(venueToDeleteId)
  }

  def buyVenue(venueId: String) = Action { implicit request =>
    val playerId: String = playerIdReads.reads(request.body.asJson.get).get
    val message = Game.buyVenue(venueId, playerId)
    Ok(message)
  }
}
