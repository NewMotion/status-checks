


Library to add consistent checks on application health

## Modules

### core

contains core functionality 

The type of a check is

```scala
type Check = (String) => Future[Result]
```
where the String parameter is a human readable name for the service or app to check

you can use it by extending your code with the `StatusCheck` trait or by using the `StatusCheck` object

an example check is

```scala
val checks:Future[Results] = Checks("my.app.newmotion.com")
    .internal("db.psql.my-app",checkDb)
    .internal("actor-system", checkActorSystem
    .external("rabbitmq", checkRabbitMq)
    .external("some-other-service.newmotion.com", checkSomeOtherService)
    .run()
    
def checkDb():(String) => Future[Result] = 
        (name) => {
          db.run(sqlu"select 1").map(r => Result(name, r.equals(1), Map()))
        }   
```

### akka_http_spray_json

uses the core module

Routes for `/status` and `/heartbeat`

```scala
    def routes: Route = normalRoutes ++ statusRoutes(checks) 
```

### other combinations of servers and json serializers

coming soon