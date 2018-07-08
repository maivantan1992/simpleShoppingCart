# assignment

## Building for docker

To build the assignment application, run:

    ./mvnw verify -Pprod dockerfile:build
    
## Using Docker to run

You can use Docker Compose to run in `src/main/docker`

    docker-compose up -d
    
## Using Swagger
Access swagger dashboard at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## API Using

### Assumption
The application not have user, role management, spring security so all endpoint are open to access.

Each user should only have 1 cart that strict belong to user session info, but with this context each cart is hold an id instead of belong to 1 user.

For this small assginment, I skip to take care about history tracking, audit, concurrency access resource.

### API User Guide

You can use the payload from swagger dashboard but for create do not input id value, use id when update.

Use `POST /api/shopping-carts` to init shopping cart before put item to cart.

 

