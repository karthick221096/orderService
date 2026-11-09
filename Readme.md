
# PaymentService

Payment microservice for the e-commerce platform. It uses a payment gateway
(Stripe, Razorpay) instead of building payment infrastructure from scratch.

## Payment flow
1. The front end creates an order. The payment service gets the `orderId` and
   total amount from the order service.
2. The payment service asks the selected gateway for a payment link, sending the
   amount, a callback URL and the API key, and stores the payment as `Initiated`.
3. The user pays on the gateway page. The gateway redirects the browser to the
   callback URL. This redirect can be lost if the user closes the browser or loses
   the network.
4. The gateway also calls a webhook on this service directly (server to server)
   for payment and refund events, and the webhook updates the payment status.
5. APIs are idempotent, so paying the same order twice does not charge twice.
6. A scheduled reconciliation job asks the gateway for the last 24 hours of
   transactions. The gateway is the source of truth. Mismatches that cannot be
   resolved automatically are flagged for manual review.

## Structure
Controller, service layer, a `PaymentGateway` interface with Stripe and Razorpay
implementations, and an `Order` model.

## Configuration
- `STRIPE_API_KEY` environment variable for the Stripe secret key
- MySQL database `paymentservice`; provide the username and password through
  environment variables
- Service port: 8000

## Run
1. Start MySQL and create the database `paymentservice`.
2. Set `STRIPE_API_KEY` and the datasource variables.
3. `./mvnw spring-boot:run`
4. For local webhook testing, expose the port with a tunnel such as ngrok or localtunnel.
