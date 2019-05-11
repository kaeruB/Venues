package model

case class Venue(id: String = null, name: String, price: Int, owner: Option[String] = None)
