Run with: 

    lein run

Or on the JVM with: 

    lein uberjar 
    java -jar target/api-example.jar
    
API:

    # create user
    curl -X POST -H "Content-Type:application/json" -d '{"user-id":"250fc6a9-074b-4a99-abcb-7e40f235ab83", "name":"jenny"}' localhost:3000/users
    
    # get user
    curl localhost:3000/users/250fc6a9-074b-4a99-abcb-7e40f235ab83

