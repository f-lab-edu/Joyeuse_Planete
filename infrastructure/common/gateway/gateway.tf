resource "aws_apigatewayv2_api" "joyeuse_planete" {
  name          = "Joyeuse_Planete_API_GATEWAY"
  description   = "API Gateway for Joyeuse_Planete"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "foods_root" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.food_lb_ip}/api/v1/foods"
}

resource "aws_apigatewayv2_route" "foods_root" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/foods"
  target    = "integrations/${aws_apigatewayv2_integration.foods_root.id}"
}

resource "aws_apigatewayv2_integration" "foods" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.food_lb_ip}/api/v1/foods/{proxy}"
}

resource "aws_apigatewayv2_route" "foods" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/foods/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.foods.id}"
}

resource "aws_apigatewayv2_integration" "orders_root" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.orders_lb_ip}/api/v1/orders"
}

resource "aws_apigatewayv2_route" "orders_root" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/orders"
  target    = "integrations/${aws_apigatewayv2_integration.orders_root.id}"
}

resource "aws_apigatewayv2_integration" "orders" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.orders_lb_ip}/api/v1/orders/{proxy}"
}

resource "aws_apigatewayv2_route" "orders" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/orders/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.orders.id}"
}

resource "aws_apigatewayv2_integration" "payment_root" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.payments_lb_ip}/api/v1/payment"
}

resource "aws_apigatewayv2_route" "payment_root" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/payment"
  target    = "integrations/${aws_apigatewayv2_integration.payment_root.id}"
}

resource "aws_apigatewayv2_integration" "payment" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.payments_lb_ip}/api/v1/payment/{proxy}"
}

resource "aws_apigatewayv2_route" "payment" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/payment/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.payment.id}"
}

resource "aws_apigatewayv2_integration" "notifications_root" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.notifications_lb_ip}/api/v1/notifications"
}

resource "aws_apigatewayv2_route" "notifications_root" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/notifications"
  target    = "integrations/${aws_apigatewayv2_integration.notifications_root.id}"
}

resource "aws_apigatewayv2_integration" "notifications" {
  api_id             = aws_apigatewayv2_api.joyeuse_planete.id
  integration_type   = "HTTP_PROXY"
  integration_method = "ANY"
  integration_uri    = "http://${var.notifications_lb_ip}/api/v1/notifications/{proxy}"
}

resource "aws_apigatewayv2_route" "notifications" {
  api_id    = aws_apigatewayv2_api.joyeuse_planete.id
  route_key = "ANY /api/v1/notifications/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.notifications.id}"
}

resource "aws_apigatewayv2_deployment" "joyeuse_planete" {
  api_id   = aws_apigatewayv2_api.joyeuse_planete.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_apigatewayv2_route.foods.id,
      aws_apigatewayv2_route.foods_root.id,
      aws_apigatewayv2_route.orders_root.id,
      aws_apigatewayv2_route.orders.id,
      aws_apigatewayv2_route.payment_root.id,
      aws_apigatewayv2_route.payment.id,
      aws_apigatewayv2_route.notifications_root.id,
      aws_apigatewayv2_route.notifications.id
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_apigatewayv2_stage" "default" {
  api_id        = aws_apigatewayv2_api.joyeuse_planete.id
  name          = "$default"
  deployment_id = aws_apigatewayv2_deployment.joyeuse_planete.id
  auto_deploy   = true
}

output "joyeuse_planete_api_gateway_ip" {
  value       = aws_apigatewayv2_api.joyeuse_planete.api_endpoint
  description = "Base URL of the Joyeuse Planete API Gateway"
}