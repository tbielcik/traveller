# Traveller API

You are working in a Traveller data company, and we would like you to develop REST API for managing Traveller data. 

### REST API should expose following operations:
#### Create Traveller, where the following data should be accepted:
  * First name, last name, date of birth, email, mobile number
  * Traveller’s document, which can be one of the types: passport or id card or driving license. Unique document is identified by unique combination of document type, document number and document issuing country.
  * Traveller can have multiple documents assigned, but only one is active at a time.
  * One unique document can be assigned to only one Traveller. 
  * Email, Mobile Number and Document are unique and can be assigned to only one Traveller
#### Get Traveller
* Should support search by email or mobile or document
* When Traveller has multiple documents assigned, Traveller can be retrieved only by the active one
* Deactivated Travellers can’t be retrieved
#### Update Traveller
* Accepts the same data as in case of Create Traveller operation
* Deactivated Travellers can’t be updated
#### Deactivate Traveller
* Operation should disable the Traveller, so it can’t be retrieved through any API operation, but the data will stay in Database.

