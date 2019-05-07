package model

case class Venue(name: String, price: Int) {
  var id: String = _
  var owner: Option[Player] = None
}
