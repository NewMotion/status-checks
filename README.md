


Library to add consistent checks on application health

## Modules

### core

contains core functionality 

The type of a check is

```scala
type Check = () => Future[Result]
```

you can use it by extending your code with the `StatusCheck` trait or by using the `StatusCheck` object

an example check is

```scala
val checks:Future[Results] = Checks("my.app.newmotion.com")
    .internal("db.psql.my-app", Importance.Critical, checkDb)
    .internal("actor-system", Importance.Critical, checkActorSystem
    .external("rabbitmq", Importance.Minor, checkRabbitMq)
    .external("some-other-service.newmotion.com", Importance.Major, checkSomeOtherService)
    .run()
    
def checkDb() = () => {
          db.run(sqlu"select 1").map(r => Result(r.equals(1), Map()))
        }   
```

the last argument in the `Result` is a `Map[String, String]` to allow passing on extra information like, response times etc


### akka_http_spray_json

uses the core module

Routes for `/status` and `/heartbeat`

```scala
    def routes: Route = normalRoutes ++ statusRoutes(checks) 
```

### other combinations of servers and json serializers

coming soon