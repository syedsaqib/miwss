## The Gilded Rose Solution and Design
Short Description of solution and design of the application

### Surge Pricing
The core of pricing surge is based on a strategy design.
- <code>ItemPriceStrategy.apply</code> is the interface that "applies" pricing models.
- <code>NoOpPriceStrategy</code>: a default pricing strategy that keeps same price.<br/>
   This strategy would be applied when there is no bean of *ItemPriceStrategy* in system.
- <code>SurgePriceStrategy</code>: This strategy would be applied that reads N number of clicks within T minutes on a specific <code>Item</code> to increase price by P percent.<br/>
  - N is defined in system property: <code>gildedRose.item.clicks</code><br/>
  - T is defined in system property: <code>gildedRose.item.expireAfterMinutes</code><br/>
  - P is defined in system property: <code>gildedRose.pricing.increase-percent</code><br/>
  When this happens the price of <code>Item</code> is increased and updated in database using formula:
  <code>current_price * (1 + P/100)</code><br/>
  and updated to database.

### Surge Pricing Technical Design
<p>
To record number of clicks per T times (1 hour for instance) is tricky because it could give unnecessary delay on every time a customer views an item.
In order to resolve this the **clicks recoding is done asynchronously using spring's** <code>@Async</code>
</p>

#### Clicks recording data-structure:
<p>
Clicks recording is done using <code>Token Bucket (Leaky Bucket) structure</code>.<br/>
One close approximation is commons-collections4's <code>PassiveExpiringMap</code> that expires on its own after N milli-seconds.<br/>
Check class <code>ItemClickBucketProvider</code>
</p>

## Spring Security and user contexts
For this solution we are going to maintain a random generated UUID as a token that a REST caller
should send in <code>Authorization header</code> as <code>Bearer Token</code>
<p>For example: <code>curl -H "Authorization: Bearer {{token}}"</code></p><br/>
To get {{token}} above do: 
<pre>
POST /account/login
body: 
{
   "username": "customer1",
   "password": "abcd1234" 
}
</pre>

## Unit testing
Currently testing covers service package that is core of the solution.
- Some basic searching of <code>Items</code>
- <p><code>ItemClickSurgePricingTest</code> checks the price is updated 10% after 10 clicks. This gets ticky due to usage of spring async. Hence you would see some sleep(s) </p>
     
# TODO
- Found that apache-common's <code>PassiveExpiringMap</code> updates the expiring time on every put and design of that commons collection class is not extendable so need to find a better structure or build one!!
- We needed to update number of clicks keeping Item id as map's key 
- In a production grade application the <code>POST: /account/login</code> call should return a JWT token instead of a random UUID as we then have to maintain a **Token repository** that currently is an in-memory expiring hash map.<br/>
OR we can use **REDIS** or **hazelcast** to hold these tokens.
