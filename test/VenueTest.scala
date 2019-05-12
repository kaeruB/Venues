import game.{Database, Game}
import model.{Player, Venue}
import org.scalatest.{BeforeAndAfter, FunSuite}

class VenueTest extends FunSuite with BeforeAndAfter {
  var venue: Venue = _
  var venueWithoutId: Venue = _
  var venueWithoutId2: Venue = _

  val venuePrice = 10

  var venuesList: List[Venue] = List.empty

  var richPlayer: Player = _
  var ordinaryPlayer: Player = _
  var poorPlayer: Player = _

  val venueId = "VenueId"

  before {
    venue = Venue("venue1", "Venue", venuePrice)
    venueWithoutId = Venue(null, "venueWithoutId", venuePrice)
    venueWithoutId2 = Venue(null, "venueWithoutId2", venuePrice)

    richPlayer = Player("richPlayer")
    richPlayer.money = 100

    ordinaryPlayer = Player("poorPlayer")
    ordinaryPlayer.money = venuePrice

    poorPlayer = Player("poorPlayer")
    poorPlayer.money = 0
  }

  test("Game.hasEnoughMoney rich player") {
    assert(Game.hasEnoughMoney(richPlayer, venue))
  }

  test("Game.hasEnoughMoney ordinary player") {
    assert(Game.hasEnoughMoney(ordinaryPlayer, venue))
  }

  test("Game.hasEnoughMoney poor player") {
    assert(!Game.hasEnoughMoney(poorPlayer, venue))
  }

  test("Game.buyVenue rich player buys venue - has enough money") {
    Database.players = List.empty
    Database.players = richPlayer :: Database.players
    Database.venues.drop(Database.venues.length)
    Game.addVenue(venue.id, venue)
    val expectedMessageReturned = venue.name + " was bought by " + richPlayer.playerId + " for " + venue.price
    val msgReturned = Game.buyVenue(venue.id, richPlayer.playerId)
    assert( expectedMessageReturned == msgReturned)
  }

  test("Game.buyVenue rich player tries to buy the same venue (the venue cannot be sold again)") {
    Database.players = List.empty
    Database.players = richPlayer :: Database.players
    Database.venues = List.empty
    assert(Database.venues.isEmpty)
    Game.addVenue(venue.id, venue)
    Game.buyVenue(venue.id, richPlayer.playerId)
    val msgReturned = Game.buyVenue(venue.id, richPlayer.playerId)
    val expectedMessageReturned = venue.id + " is already bought by " + richPlayer.playerId
    assert(expectedMessageReturned == msgReturned)
  }

  test("Game.buyVenue poor player tries to buy a venue but has not enough money") {
    Database.players = List.empty
    Database.players = poorPlayer :: Database.players
    Database.venues = List.empty
    assert(Database.venues.isEmpty)
    Game.addVenue(venue.id, venue)
    Game.buyVenue(venue.id, poorPlayer.playerId)
    val msgReturned = Game.buyVenue(venue.id, poorPlayer.playerId)
    val expectedMessageReturned =  poorPlayer.playerId + " can't afford " + venue.name
    assert( expectedMessageReturned == msgReturned)
  }

  test("Game.putVenue") {
    Database.venues = List.empty
    assert(Database.venues.isEmpty)
    Game.addVenue(venueId, venueWithoutId)
    assert(Database.venues.length == 1)
    assert(Database.venues.count(x => x.id == venueId) == 1)
  }

  test("Game.putVenue for venue with id that already exists in Database - expected replacing the old one with a new one") {
    Database.venues = List.empty
    assert(Database.venues.isEmpty)
    Game.addVenue(venueId, venueWithoutId)
    Game.addVenue(venueId, venueWithoutId2)
    assert(Database.venues.length == 1 && Database.venues.filter(ven => ven.name == venueWithoutId2.name).head.name == venueWithoutId2.name)
  }

  test("Game.deleteVenue") {
    Database.venues.drop(Database.venues.length)
    Game.addVenue(venueId, venueWithoutId)
    Game.deleteVenue(venueId)
    assert(!Database.venues.exists(x => x.id == venueId))
  }
}
