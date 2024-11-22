**API for connection Fakturoid API V3 invoicing application.**

API is seccured with OAuth 2.0 
for reaching refresh token, follow instructions in fakturoid api v3 documentation with use http client Token.
https://www.fakturoid.cz/api/v3/authorization

The code assume secure variables refresh_token, user_agent, client_authorization stored in environment variables.

Api pretend fetch tenant financial claims from the db into adapter ClaimDataDomain.

Api has two invoicing systems:

**buffer system**
- waiting for at least 10 items to create invoice.
- send invoice if the the limit is not reached in the invoicing period (year)   

**credit system**
- assume that sales upload or directly fill up saver proforma invoice
  saver
  - saver dealed with customer and saleas has to be proforma with "validated saver"
  - if the limit is reached or new client is switched into the credit system final invoice with text in first line "Saver" is created 
  - credit s - look in fetched invoice for first invoice with key word "saver" in string, calculate rest of credits, send proforma "do not pay" invoice when 50%, 75% and 100% credits consumed.




