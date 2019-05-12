# Solution

## Structure
Routes defined in app/conf/routes, application controller in app/controllers/VenueController.


## Execution
The application can be started by executing:
```
> sbt run
```
in project folder. 
By default, it will be started at localhost:9000, to start it on 8080, please use: 
```
> sbt "run 8080"
```

## Business logic tests

### Unit tests

To run unit tests:
```
> sbt test
```

### User tests
Example flow on Windows (needed \" to work in a Cmdr...) 
```
> curl http://localhost:9000/venues
```
[]
```
> curl -XPUT -H "Content-Type: application/json" http://localhost:9000/venue/1 -d "{\"name\":\"Rynek Glowny\",\"price\":1000}" 
```
1
```
> curl http://localhost:9000/venues
```
[{"id":"1","name":"Rynek Glowny","price":1000}]
```
> curl -XPUT -H "Content-Type: application/json" http://localhost:9000/venue/2 -d "{\"name\":\"Krzemienica\",\"price\":100}" 
```
2
```
> curl http://localhost:9000/venues
```
[{"id":"2","name":"Krzemienica","price":100},{"id":"1","name":"Rynek Glowny","price":1000}]
```
> curl -XDELETE http://localhost:9000/venues/2
```
2
```
> curl http://localhost:9000/venues
```
[{"id":"1","name":"Rynek Glowny","price":1000}]

```
> curl -XPOST -H "Content-Type: application/json" http://localhost:9000/venue/2/buy -d "{\"playerId\":\"player2\"}" 
```
There is no venue with the following id: 2
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:9000/venue/1/buy -d "{\"playerId\":\"player1\"}"
```
player1 can't afford Rynek Glowny

```
> curl -XPOST -H "Content-Type: application/json" http://localhost:9000/venue/1/buy -d "{\"playerId\":\"player2\"}"  
```
Rynek Glowny was bought by player2 for 1000
```
> curl http://localhost:9000/venues
```
[{"id":"1","name":"Rynek Glowny","price":1000,"owner":"player2"}]


_________________________________________________________________________________________________________________________________


# Buy Venues
This project is a simple proof-of-concept application that allows adding `venues` and buying them for money.

## Usage
The application can be started by executing `sbt run`.

### Creating/updating a venue
You can use `PUT` and provide your own `UUID` to be sure that only one venue is created.
```
> curl -XPUT -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1 -d '{
  "name": "Rynek Główny",
  "price": 1000
}' "687e8292-1afd-4cf7-87db-ec49a3ed93b1"
```

### Getting all venues
```
> curl http://localhost:8080/venues
[
  {
    "id": "687e8292-1afd-4cf7-87db-ec49a3ed93b1",
    "name": "Rynek Główny",
    "price": 1000
  }
]
```

### Deleting venues
```
> curl -XDELETE "http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1"
"687e8292-1afd-4cf7-87db-ec49a3ed93b1"
```

### Hardcoded players
For now, two players are hardcoded and they are:
- `id=player1`, `money=500`
- `id=player2`, `money=2000`

Each restart of the application resets the state to the above.

### Buying a venue

#### Scenario 1: Buying a venue when player can't afford it
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player1"
}'
"player1 can't afford Rynek Główny"
```

#### Scenario 2: Buying a venue when player can afford it
```
> curl -XPOST -H "Content-Type: application/json" http://localhost:8080/venues/687e8292-1afd-4cf7-87db-ec49a3ed93b1/buy -d '{
  "playerId": "player2"
}'
"Rynek Główny was bought by player2 for 1000"
```

```
> curl http://localhost:8080/venues
[
  {
    "id": "687e8292-1afd-4cf7-87db-ec49a3ed93b1",
    "name": "Rynek Główny",
    "price": 1000,
    "owner:" "player2"
  }
]
```

## Recruitment task
Your task is to implement this proof-of-concept application in Scala. 
You can choose any appraoch and libraries as you wish, as long as the above `curl`s work as required. 
This file can be changed during the course of implementation to document the project. 
The application doesn't have to persist data between restarts, 
so each restart will revert the application to the state with no venues and 2 hardcoded players.
