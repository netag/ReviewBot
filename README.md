#Review Bot

Trying out Facebook Messenger API & Kotlin


## Running
* Needs Java11  

```bash
# setup your facebook app creds
export APP_ID=???
export APP_SECRET=???
export APP_WEBHOOK_VERIFY_TOKEN=???

./scripts/server
```

### Configure a page
```bash
curl --request POST 'http://localhost:8080/page' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "<page name>",
    "id": "<page id>",
    "accessToken": "<page access token>"
}'
```


### Send a Trigger

More triggers can be found [here](src/main/kotlin/reviewbot/events/MessageTrigger.kt)

```bash
curl --request POST 'http://localhost:8080/trigger' \
--header 'Content-Type: application/json' \
--data-raw '{
    "to": "<user psid>",
    "for": "<page id>",
    "trigger": {
        "type": "reviewbot.events.Trigger.SaySomething",
        "something": "<some random message>"

    }
}'
```


### Webhook
Webhook endpoint `http://localhost:8080/webhook`, verification token is up to you.  
Setup your app to use webhooks, and subscribe a page to messaging events.


### Latest Reviews (per user & page)
```bash
curl --request GET 'http://localhost:8080/reviews?psid=<user_psid>&pageId=<page_id>&limit=<limit>'
```
Example Response:  

```json
[
    {
        "_id": "6060d45f9a2a3b7d003fc9df",
        "createdAt": 1616958559802,
        "updatedAt": 1616958586316,
        "recipient": "<psid>",
        "page": "<pageId>",
        "triggeredBy": {
            "type": "reviewbot.events.Trigger.SaySomething",
            "something": "buz"
        },
        "messageId": "mid1",
        "responses": [
            {
                "mid": "mid2",
                "timestamp": 1616958576770,
                "text": "typing typing"
            },
            {
                "mid": "mid3",
                "timestamp": 1616958585740,
                "text": "ajdfakjfnadkfnakdfnasdfa"
            }
        ]
    },
    {
        "_id": "6060d4319a2a3b7d003fc9de",
        "createdAt": 1616958513774,
        "updatedAt": 1616958530993,
        "recipient": "<psid>",
        "page": "<pageId>",
        "triggeredBy": {
            "type": "reviewbot.events.Trigger.SaySomething",
            "something": "best message ever"
        },
        "messageId": "mid4",
        "responses": [
            {
                "mid": "mid5",
                "timestamp": 1616958530459,
                "text": "40000000"
            }
        ]
    }
]

```


## Libs used
* Http Server & Client [ktor](https://ktor.io/)
* DI [koin](https://start.insert-koin.io/#/)
* JSON [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
* Functional [arrow](https://arrow-kt.io/)
* MongoDB [kmongo](https://litote.org/kmongo/)
* Testing [kotest](https://github.com/kotest/kotest)
