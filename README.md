*API for connection Fakturoid API V3 invoicing application.*

API is seccured with OAuth 2.0 
for reaching refresh token, follow instructions in fakturoid api v3 documentation with use http client Token

The code pretend secure variables refresh_token, user_agent, client_authorization stored in environment variables.

Api pretend fetch tenant financial claims from the db into adapter ClaimDataDomain

Api has two invoicing way 
  - credits
  - at least 10 issues to prevent low invoicing costs
 
